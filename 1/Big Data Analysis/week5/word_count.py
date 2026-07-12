from pyspark.sql import SparkSession
import os
import shutil

# Initialize Spark Session
spark = SparkSession.builder \
    .appName("Task6_WordCount") \
    .master("local[*]") \
    .getOrCreate()

# Paths
ROOT_DIR = "/Users/enisogdum/Desktop/Erasmus+/Erasmus Courses/Big Data Analysis"
CSV_PATH = os.path.join(ROOT_DIR, "2/mydata.csv")
OUTPUT_DIR = os.path.join(ROOT_DIR, "6/word_count_output")

# Clean output directory if it exists
if os.path.exists(OUTPUT_DIR):
    shutil.rmtree(OUTPUT_DIR)

print(f"Reading data from {CSV_PATH}...")

# 1. Read the CSV file (we can use DataFrame reader to load it efficiently, then convert column to RDD)
# The task asks to "select column with text data... save as RDD".
# We'll use "Symptom_6" as our text column.
df = spark.read.option("header", "true").csv(CSV_PATH)
text_rdd = df.select("Symptom_6").rdd.map(lambda row: row[0])

# Filter out None values just in case
text_rdd = text_rdd.filter(lambda x: x is not None)

print("Performing Word Count on 'Symptom_6' column...")

# 2. Word Count using RDD transformations
# flatMap: Split lines into words
# map: Create (word, 1) tuples
# reduceByKey: Sum counts per word
word_counts = text_rdd \
    .flatMap(lambda line: line.split(" ")) \
    .map(lambda word: (word, 1)) \
    .reduceByKey(lambda a, b: a + b)

# 3. Save the result
print(f"Saving results to {OUTPUT_DIR}...")
word_counts.saveAsTextFile(OUTPUT_DIR)

# 4. Print a sample
print("Sample results:")
for word, count in word_counts.take(10):
    print(f"{word}: {count}")

spark.stop()
