import duckdb

# Full path to your CSV file
file_path = r"/Users/enisogdum/Desktop/Erasmus+/Erasmus Courses/Big Data Analysis/2/mydata.csv"

# Connect to DuckDB database (creates 'my_database.duckdb' if not exists)
conn = duckdb.connect(database='my_database.duckdb', read_only=False)

# Load CSV into a table called my_table
conn.execute(f"""
CREATE TABLE my_table AS
SELECT *
FROM read_csv_auto('{file_path}')
""")

# Verify how many rows loaded
result = conn.execute("SELECT COUNT(*) FROM my_table").fetchall()
print("Number of rows in table:", result[0][0])

# Optional: preview first 5 rows
preview = conn.execute("SELECT * FROM my_table LIMIT 5").fetchall()
print("Preview of table:")
for row in preview:
    print(row)