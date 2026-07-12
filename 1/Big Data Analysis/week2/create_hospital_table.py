import duckdb

# Connect to your DuckDB database (create if not exists)
conn = duckdb.connect('my_database.duckdb')

# Drop the table if it exists
conn.execute("DROP TABLE IF EXISTS hospital_data")

# Create table from CSV
conn.execute("""
CREATE TABLE hospital_data AS
SELECT *
FROM read_csv_auto('/Users/enisogdum/Desktop/Erasmus+/Erasmus Courses/Big Data Analysis/2/mydata.csv')
""")

print("CSV loaded into table 'hospital_data'")