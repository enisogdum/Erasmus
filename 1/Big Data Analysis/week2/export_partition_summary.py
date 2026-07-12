import duckdb
import csv

# 1️⃣ Connect to DuckDB database
conn = duckdb.connect('my_database.duckdb')

# 2️⃣ Load the CSV into DuckDB table (drop if exists)
csv_path = '/Users/enisogdum/Desktop/Erasmus+/Erasmus Courses/Big Data Analysis/2/mydata.csv'
conn.execute("DROP TABLE IF EXISTS hospital_data")
conn.execute(f"""
CREATE TABLE hospital_data AS
SELECT *
FROM read_csv_auto('{csv_path}')
""")
print("✅ CSV loaded into table 'hospital_data'")

# 3️⃣ Get all columns
columns_info = conn.execute("PRAGMA table_info('hospital_data')").fetchall()

# 4️⃣ Prepare results for CSV
results = []

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

        # Check if this column is a partitioning candidate
        candidate = 'YES' if unique < 0.5 * total and nulls < 0.1 * total else 'NO'

        results.append([col_name, col[2], unique, nulls, total, candidate])

    except Exception as e:
        results.append([col_name, col[2], 'Error', 'Error', 'Error', 'NO'])

# 5️⃣ Write results to CSV
output_file = 'partitioning_summary.csv'
with open(output_file, mode='w', newline='', encoding='utf-8') as f:
    writer = csv.writer(f)
    # Header
    writer.writerow(['Column', 'Type', 'Unique_Values', 'Null_Count', 'Total_Rows', 'Partition_Candidate'])
    # Rows
    writer.writerows(results)

print(f"✅ Partitioning summary saved to '{output_file}'")