package com.pongtorich.pong_to_rich.security;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * JPA AttributeConverter — BrokerAccount.appkey/appsecret DB 저장 시 AES-256 암복호화
 *
 * AES/CBC/PKCS5Padding 방식 사용:
 * - CBC: 앞 블록 암호문이 다음 블록에 영향을 줘서 ECB보다 패턴 노출 방지
 * - IV(초기화 벡터): 같은 평문도 매번 다른 암호문이 되도록. 암호문 앞 16바이트에 포함해서 저장
 * - 키: 반드시 32바이트(256bit) — .env의 ENCRYPT_SECRET_KEY
 *
 * 현재 보안 수준 및 한계:
 * - DB 유출 시 평문 노출 방지 ✅ (가장 흔한 위협)
 * - 서버 전체 침투 시 ENCRYPT_SECRET_KEY도 같이 노출 → 복호화 가능 ❌
 * - 관리자(서버 운영자)는 키를 알고 있으므로 원칙적으로 복호화 가능 ❌
 *
 * TODO: 실서비스 전환 시 아래 중 하나로 교체 필요
 *   1. 사용자 비밀번호 기반 암호화 (PBKDF2)
 *      - 암호화 키를 사용자 PW에서 파생 → 서버/관리자도 복호화 불가
 *      - 단점: 비밀번호 분실 시 appkey 영구 복호화 불가 → 재입력 유도 필요
 *   2. AWS KMS / HashiCorp Vault
 *      - 암호화 키를 외부 키 관리 서비스에서 관리 → 서버에 키 없음
 *      - IAM 권한으로 접근 제어 → 관리자도 키 직접 열람 불가
 *      - 비용 발생, 인프라 복잡도 증가
 */
@Converter
@Component
public class AesEncryptor implements AttributeConverter<String, String>, ApplicationContextAware {

    private static final Logger log = LoggerFactory.getLogger(AesEncryptor.class);
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final int IV_LENGTH = 16;

    // Hibernate가 Spring Context 초기화 전에 Converter를 생성하므로
    // @Value 주입이 안 됨 → ApplicationContextAware로 Context 준비 후 키를 지연 로딩
    private static ApplicationContext applicationContext;
    private SecretKeySpec secretKey;

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        applicationContext = ctx;
    }

    private SecretKeySpec getSecretKey() {
        if (secretKey == null) {
            String key = applicationContext.getEnvironment().getProperty("encrypt.secret-key");
            if (key == null) throw new IllegalStateException("encrypt.secret-key is not configured");
            byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
            log.info("[AesEncryptor] encrypt.secret-key loaded, length={}bytes, value='{}'", keyBytes.length, key);
            if (keyBytes.length != 32) {
                throw new IllegalArgumentException("encrypt.secret-key must be exactly 32 bytes (AES-256)");
            }
            secretKey = new SecretKeySpec(keyBytes, "AES");
        }
        return secretKey;
    }

    @Override
    public String convertToDatabaseColumn(String plainText) {
        if (plainText == null) return null;
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);

            // 매 암호화마다 랜덤 IV 생성 → 같은 평문도 항상 다른 암호문
            byte[] iv = new byte[IV_LENGTH];
            new java.security.SecureRandom().nextBytes(iv);
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(), new IvParameterSpec(iv));

            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            // IV(16) + 암호문을 합쳐서 Base64 인코딩 → DB 저장
            byte[] combined = new byte[IV_LENGTH + encrypted.length];
            System.arraycopy(iv, 0, combined, 0, IV_LENGTH);
            System.arraycopy(encrypted, 0, combined, IV_LENGTH, encrypted.length);

            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new IllegalStateException("AES encryption failed", e);
        }
    }

    @Override
    public String convertToEntityAttribute(String cipherText) {
        if (cipherText == null) return null;
        try {
            byte[] combined = Base64.getDecoder().decode(cipherText);

            // 앞 16바이트 = IV, 나머지 = 암호문
            byte[] iv = new byte[IV_LENGTH];
            byte[] encrypted = new byte[combined.length - IV_LENGTH];
            System.arraycopy(combined, 0, iv, 0, IV_LENGTH);
            System.arraycopy(combined, IV_LENGTH, encrypted, 0, encrypted.length);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), new IvParameterSpec(iv));

            return new String(cipher.doFinal(encrypted), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("AES decryption failed", e);
        }
    }
}
