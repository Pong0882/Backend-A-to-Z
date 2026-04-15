"""
더미 유저 생성 스크립트

용도: k6 부하 테스트용 테스트 유저 N명을 회원가입 API로 생성
실행: python seed_users.py [--count 100] [--base-url http://localhost:8080]

의존성:
    pip install requests
"""

import argparse
import sys
import requests


def parse_args():
    parser = argparse.ArgumentParser(description="더미 유저 생성 스크립트")
    parser.add_argument("--count", type=int, default=100, help="생성할 유저 수 (기본값: 100)")
    parser.add_argument("--base-url", type=str, default="http://localhost:8080", help="API 서버 URL (기본값: http://localhost:8080)")
    parser.add_argument("--password", type=str, default="Test1234!", help="테스트 유저 비밀번호 (기본값: Test1234!)")
    return parser.parse_args()


def seed_users(count: int, base_url: str, password: str):
    url = f"{base_url}/api/auth/signup"
    print(f"[시작] 더미 유저 {count}명 생성 ({url})")

    success = 0
    skip = 0
    fail = 0

    for i in range(1, count + 1):
        email = f"test{i:03d}@pongtest.com"
        res = requests.post(url, json={"email": email, "password": password})

        if res.status_code == 200:
            success += 1
        elif res.status_code == 409:  # 이미 존재하는 이메일
            skip += 1
        else:
            fail += 1
            print(f"[실패] {email} — {res.status_code}: {res.text}")

        if i % 10 == 0:
            print(f"  진행: {i}/{count}")

    print(f"\n[완료] 성공: {success}명 / 스킵(중복): {skip}명 / 실패: {fail}명")
    print(f"  이메일 형식: test001@pongtest.com ~ test{count:03d}@pongtest.com")
    print(f"  비밀번호: {password}")


if __name__ == "__main__":
    args = parse_args()
    seed_users(args.count, args.base_url, args.password)
