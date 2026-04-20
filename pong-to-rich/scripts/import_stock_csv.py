"""
주식 일봉 CSV → MySQL 삽입 스크립트

용도: fetch_stock_csv.py로 생성한 CSV를 stocks / stock_prices 테이블에 삽입
실행: python import_stock_csv.py [--data-dir ./data] [--host localhost] [--port 3306]
                                  [--user root] [--password yourpw] [--db pong_to_rich]

의존성:
    pip install pandas pymysql
"""

import argparse
import glob
import os
import pymysql
import pandas as pd
from datetime import date


def parse_args():
    parser = argparse.ArgumentParser(description="주식 일봉 CSV → MySQL 삽입")
    parser.add_argument("--data-dir", type=str, default="./data",        help="CSV 폴더 (기본값: ./data)")
    parser.add_argument("--host",     type=str, default="localhost",      help="DB 호스트 (기본값: localhost)")
    parser.add_argument("--port",     type=int, default=3306,             help="DB 포트 (기본값: 3306)")
    parser.add_argument("--user",     type=str, default="root",           help="DB 유저 (기본값: root)")
    parser.add_argument("--password", type=str, required=True,            help="DB 비밀번호")
    parser.add_argument("--db",       type=str, default="pong_to_rich",   help="DB 이름 (기본값: pong_to_rich)")
    return parser.parse_args()


def get_or_create_stock(cursor, code: str, name: str) -> int:
    cursor.execute("SELECT id FROM stocks WHERE code = %s AND market = 'KRX'", (code,))
    row = cursor.fetchone()
    if row:
        return row[0]

    cursor.execute(
        "INSERT INTO stocks (code, name, market) VALUES (%s, %s, 'KRX')",
        (code, name)
    )
    return cursor.lastrowid


def import_csv(conn, csv_path: str):
    df = pd.read_csv(csv_path, dtype={'stock_code': str})
    if df.empty:
        print(f"  → 빈 파일 스킵: {csv_path}")
        return 0

    ticker = df["stock_code"].iloc[0]
    name   = df["stock_name"].iloc[0]

    with conn.cursor() as cursor:
        stock_id = get_or_create_stock(cursor, ticker, name)

        insert_count = 0
        skip_count   = 0

        for _, row in df.iterrows():
            # 중복 체크 (stock_id + trade_date UNIQUE)
            cursor.execute(
                "SELECT 1 FROM stock_prices WHERE stock_id = %s AND trade_date = %s",
                (stock_id, row["trade_date"])
            )
            if cursor.fetchone():
                skip_count += 1
                continue

            cursor.execute(
                """INSERT INTO stock_prices
                   (stock_id, trade_date, open_price, high_price, low_price, close_price, volume)
                   VALUES (%s, %s, %s, %s, %s, %s, %s)""",
                (
                    stock_id,
                    row["trade_date"],
                    row["open_price"],
                    row["high_price"],
                    row["low_price"],
                    row["close_price"],
                    int(row["volume"]),
                )
            )
            insert_count += 1

        conn.commit()

    print(f"  → {name} ({ticker}): 삽입 {insert_count}건 / 스킵(중복) {skip_count}건")
    return insert_count


def main():
    args = parse_args()

    csv_files = sorted(glob.glob(os.path.join(args.data_dir, "*.csv")))
    if not csv_files:
        print(f"[오류] CSV 파일 없음: {args.data_dir}")
        return

    print(f"[시작] {len(csv_files)}개 CSV → {args.host}:{args.port}/{args.db}")

    conn = pymysql.connect(
        host=args.host,
        port=args.port,
        user=args.user,
        password=args.password,
        database=args.db,
        charset="utf8mb4",
    )

    total = 0
    try:
        for csv_path in csv_files:
            print(f"\n[처리] {os.path.basename(csv_path)}")
            total += import_csv(conn, csv_path)
    finally:
        conn.close()

    print(f"\n[완료] 총 {total}건 삽입")


if __name__ == "__main__":
    main()
