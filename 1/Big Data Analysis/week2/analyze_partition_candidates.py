import duckdb

# 1️⃣ Connect to your DuckDB database
conn = duckdb.connect('my_database.duckdb')

# 2️⃣ Drop and recreate the table from your CSV (ensures the table exists)
csv_path = '/Users/enisogdum/Desktop/Erasmus+/Erasmus Courses/Big Data Analysis/2/mydata.csv'
conn.execute("DROP TABLE IF EXISTS hospital_data")
conn.execute(f"""
CREATE TABLE hospital_data AS
SELECT *
FROM read_csv_auto('{csv_path}')
""")
print("✅ CSV loaded into table 'hospital_data'")

# 3️⃣ Get all column names and types
columns_info = conn.execute("PRAGMA table_info('hospital_data')").fetchall()
print("\nColumns and types:\n")
for col in columns_info:
    print(f"Index: {col[0]}, Name: {col[1]}, Type: {col[2]}")

# 4️⃣ Analyze each column for partitioning
print("\nColumn summary for partitioning:\n")
partition_candidates = []

for col in columns_info:
    col_name = col[1]
    try:
        unique, nulls, total = conn.execute(f"""
            SELECT
                COUNT(DISTINCT "{col_name}") AS unique_values,
                SUM(CASE WHEN "{col_name}" IS NULL THEN 1 ELSE 0 END) AS null_count,
                COUNT(*) AS total_rows
            FROM hospital_data
        """).fetchone()

        print(f"{col_name}: unique={unique}, NULLs={nulls}, total={total}")

        # Simple rule for partitioning candidates
        if unique < 0.5 * total and nulls < 0.1 * total:
            partition_candidates.append(col_name)

    except Exception as e:
        print(f"{col_name}: Error - {e}")

# 5️⃣ Print recommended partitioning columns
print("\n✅ Recommended partitioning candidates (low-medium unique values, few NULLs):")
print(partition_candidates)
