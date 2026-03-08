from app.db.database import SessionLocal
from app.trends.service import fetch_and_store_trends

def main():
    db = SessionLocal()
    try:
        count = fetch_and_store_trends(
            db=db,
            niche="dance",
            region="IN"
        )
        print(f"Stored {count} trending keywords")
    finally:
        db.close()

if __name__ == "__main__":
    main()