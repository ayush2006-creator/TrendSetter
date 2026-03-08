from pytrends.request import TrendReq
def fetch_google_trending_keywords(keyword: str,region:str="IN"):
        pytrends = TrendReq(
        hl="en-IN",      # language: English India
        tz=330 ,
        backoff_factor=0.2              # timezone offset (India)
    )
 # 1️⃣ Build payload
        pytrends.build_payload(
            kw_list=[keyword],
            geo=region,
            timeframe="now 7-d"
        )

        # 2️⃣ Fetch related queries
        related_queries = pytrends.related_queries()

        if (
            keyword not in related_queries
            or related_queries[keyword]["top"] is None
        ):
            return []

        df = related_queries[keyword]["top"]

        # 3️⃣ Convert to clean Python objects
        trends = []
        for _, row in df.iterrows():
            trends.append({
                "keyword": row["query"],
                "score": int(row["value"])
            })

        return trends      