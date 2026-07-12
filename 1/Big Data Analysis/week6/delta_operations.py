from pyspark.sql import SparkSession
from delta import *
import os
import shutil

# Initialize Spark Session with Delta support
builder = SparkSession.builder \
    .appName("Task7_DeltaLake") \
    .master("local[*]") \
    .config("spark.sql.extensions", "io.delta.sql.DeltaSparkSessionExtension") \
    .config("spark.sql.catalog.spark_catalog", "org.apache.spark.sql.delta.catalog.DeltaCatalog") \
    .config("spark.driver.memory", "4g")

spark = configure_spark_with_delta_pip(builder).getOrCreate()

# Paths
ROOT_DIR = "/Users/enisogdum/Desktop/Erasmus+/Erasmus Courses/Big Data Analysis"
CSV_PATH = os.path.join(ROOT_DIR, "2/mydata.csv")
DELTA_PATH = os.path.join(ROOT_DIR, "7/delta_table")

# Clean output directory if it exists
if os.path.exists(DELTA_PATH):
    shutil.rmtree(DELTA_PATH)

print("1. Reading CSV as Dataframe DF1...")
df1 = spark.read.option("header", "true").csv(CSV_PATH)

print("2. Writing Dataframe DF1 as Delta D1...")
df1.write.format("delta").save(DELTA_PATH)

print("3. Reading Delta D1 as new Dataframe DF2...")
df2 = spark.read.format("delta").load(DELTA_PATH)

print("4. Running SQL query for DF2...")
df2.createOrReplaceTempView("hospital_data")

# Example SQL Query: Count of patients per State
result = spark.sql("""
    SELECT State, COUNT(*) as Patient_Count 
    FROM hospital_data 
    GROUP BY State 
    ORDER BY Patient_Count DESC
""")

print("Query Result:")
result.show()

spark.stop()
