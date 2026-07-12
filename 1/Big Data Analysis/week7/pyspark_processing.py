# ============================================================================
# DataBricks PySpark Processing - Hospital Sample Data
# ============================================================================
# This notebook contains PySpark code for processing the hospital sample dataset
# in DataBricks Community Edition.
#
# Prerequisites:
# 1. Upload sample_data.csv to DataBricks (489 MB, 100,000 rows)
# 2. Create table named 'hospital_data' from sample_data.csv
#    (You can name it 'hospital_data' when creating the table in DataBricks UI)
# 3. Attach this notebook to a running cluster
#
# Note: The file is named 'sample_data.csv' but you'll create a table 
#       called 'hospital_data' from it in DataBricks.
# ============================================================================

# ----------------------------------------------------------------------------
# SECTION 1: Setup and Data Loading
# ----------------------------------------------------------------------------

# Cell 1: Import required libraries
from pyspark.sql import SparkSession
from pyspark.sql.functions import *
from pyspark.sql.types import *
from pyspark.sql.window import Window

# Cell 2: Verify Spark session (already available in DataBricks)
print(f"Spark Version: {spark.version}")
print(f"Application ID: {spark.sparkContext.applicationId}")

# Cell 3: Load data from table
df = spark.table("hospital_data")
print(f"✅ Loaded {df.count():,} rows")

# Cell 4: Display schema
df.printSchema()

# Cell 5: Preview data (use display() for interactive table in DataBricks)
display(df.limit(10))

# Cell 6: Get basic statistics
display(df.describe())


# ----------------------------------------------------------------------------
# SECTION 2: Data Exploration
# ----------------------------------------------------------------------------

# Cell 7: Column count and row count
num_columns = len(df.columns)
num_rows = df.count()
print(f"Dataset dimensions: {num_rows:,} rows × {num_columns} columns")

# Cell 8: Check for null values across all columns
null_counts = df.select([
    count(when(col(c).isNull(), c)).alias(c) 
    for c in df.columns
])
display(null_counts)

# Cell 9: Unique values in categorical columns
categorical_cols = ['State', 'Gender', 'City']
for col_name in categorical_cols:
    unique_count = df.select(col_name).distinct().count()
    print(f"{col_name}: {unique_count} unique values")

# Cell 10: Show sample values for State and Gender
display(df.groupBy("State", "Gender").count().orderBy("count", ascending=False))


# ----------------------------------------------------------------------------
# SECTION 3: State-Based Analysis
# ----------------------------------------------------------------------------

# Cell 11: Patient count by state with percentage
state_summary = df.groupBy("State").agg(
    count("*").alias("patient_count")
).withColumn(
    "percentage",
    round((col("patient_count") / sum("patient_count").over(Window.partitionBy())) * 100, 2)
).orderBy(col("patient_count").desc())

display(state_summary)

# Cell 12: Average age by state
age_by_state = df.groupBy("State").agg(
    round(avg("Age"), 2).alias("avg_age"),
    min("Age").alias("min_age"),
    max("Age").alias("max_age"),
    round(stddev("Age"), 2).alias("stddev_age")
).orderBy("avg_age", ascending=False)

display(age_by_state)

# Cell 13: Gender distribution by state
gender_by_state = df.groupBy("State", "Gender").agg(
    count("*").alias("count")
).orderBy("State", "count", ascending=False)

display(gender_by_state)

# Cell 14: Top 5 cities per state (using window functions)
window_spec = Window.partitionBy("State").orderBy(col("city_count").desc())

top_cities = df.groupBy("State", "City").agg(
    count("*").alias("city_count")
).withColumn(
    "rank",
    row_number().over(window_spec)
).filter(col("rank") <= 5).orderBy("State", "rank")

display(top_cities)


# ----------------------------------------------------------------------------
# SECTION 4: Temporal Analysis
# ----------------------------------------------------------------------------

# Cell 15: Convert Date column to proper date type (if needed)
df_with_date = df.withColumn("Date", to_date(col("Date")))

# Cell 16: Extract date components
df_temporal = df_with_date.withColumn("year", year("Date")) \
    .withColumn("month", month("Date")) \
    .withColumn("day_of_week", dayofweek("Date")) \
    .withColumn("quarter", quarter("Date"))

display(df_temporal.select("Date", "year", "month", "day_of_week", "quarter").limit(10))

# Cell 17: Patient visits by year
visits_by_year = df_temporal.groupBy("year").agg(
    count("*").alias("visit_count")
).orderBy("year")

display(visits_by_year)

# Cell 18: Monthly trends (recent year)
max_year = df_temporal.agg(max("year")).collect()[0][0]

monthly_trends = df_temporal.filter(col("year") == max_year).groupBy("month").agg(
    count("*").alias("visit_count")
).orderBy("month")

display(monthly_trends)

# Cell 19: Day of week analysis
day_names = {1: "Sunday", 2: "Monday", 3: "Tuesday", 4: "Wednesday", 
             5: "Thursday", 6: "Friday", 7: "Saturday"}

dow_analysis = df_temporal.groupBy("day_of_week").agg(
    count("*").alias("visit_count")
).orderBy("day_of_week")

display(dow_analysis)


# ----------------------------------------------------------------------------
# SECTION 5: Age Demographics
# ----------------------------------------------------------------------------

# Cell 20: Create age groups
df_age_groups = df.withColumn(
    "age_group",
    when(col("Age") < 18, "0-17 (Child)")
    .when((col("Age") >= 18) & (col("Age") <= 30), "18-30 (Young Adult)")
    .when((col("Age") >= 31) & (col("Age") <= 50), "31-50 (Adult)")
    .when((col("Age") >= 51) & (col("Age") <= 65), "51-65 (Middle Age)")
    .otherwise("65+ (Senior)")
)

# Cell 21: Age group distribution
age_group_dist = df_age_groups.groupBy("age_group").agg(
    count("*").alias("count"),
    round(avg("Age"), 1).alias("avg_age_in_group")
).orderBy("age_group")

display(age_group_dist)

# Cell 22: Age statistics by state
age_stats = df.groupBy("State").agg(
    round(avg("Age"), 2).alias("avg_age"),
    round(percentile_approx("Age", 0.25), 0).alias("age_25th_percentile"),
    round(percentile_approx("Age", 0.50), 0).alias("age_median"),
    round(percentile_approx("Age", 0.75), 0).alias("age_75th_percentile")
).orderBy("avg_age", ascending=False)

display(age_stats)


# ----------------------------------------------------------------------------
# SECTION 6: Word Count Analysis (RDD Operations)
# ----------------------------------------------------------------------------

# Cell 23: Word count on Symptom_1 column using RDDs
symptom_rdd = df.select("Symptom_1").rdd.flatMap(lambda x: x[0].split() if x[0] else [])

word_counts = symptom_rdd.map(lambda word: (word.lower(), 1)) \
    .reduceByKey(lambda a, b: a + b) \
    .sortBy(lambda x: x[1], ascending=False)

# Convert back to DataFrame for display
word_count_df = spark.createDataFrame(
    word_counts.take(20), 
    ["word", "count"]
)

display(word_count_df)

# Cell 24: Word count using DataFrame API (more efficient)
symptom_words = df.select(
    explode(split(col("Symptom_1"), " ")).alias("word")
).filter(col("word") != "")

word_freq = symptom_words.groupBy(lower(col("word")).alias("word")).agg(
    count("*").alias("frequency")
).orderBy(col("frequency").desc())

display(word_freq.limit(20))


# ----------------------------------------------------------------------------
# SECTION 7: Advanced Transformations
# ----------------------------------------------------------------------------

# Cell 25: Create a patient profile summary
patient_summary = df.select(
    col("Patient_Name"),
    col("Age"),
    col("Gender"),
    col("State"),
    col("City"),
    concat_ws(", ", col("Symptom_1"), col("Symptom_2"), col("Symptom_3")).alias("symptoms"),
    col("Date")
).withColumn(
    "age_category",
    when(col("Age") < 18, "Minor").otherwise("Adult")
)

display(patient_summary.limit(20))

# Cell 26: Pivot table - Gender distribution across states
gender_pivot = df.groupBy("State").pivot("Gender").count().fillna(0)
display(gender_pivot)

# Cell 27: Calculate running totals by state (window function)
window_running = Window.partitionBy("State").orderBy("Date").rowsBetween(Window.unboundedPreceding, Window.currentRow)

running_total = df_with_date.withColumn(
    "running_patient_count",
    count("*").over(window_running)
).select("State", "Date", "running_patient_count")

display(running_total.orderBy("State", "Date").limit(100))


# ----------------------------------------------------------------------------
# SECTION 8: Delta Lake Operations
# ----------------------------------------------------------------------------

# Cell 28: Write data to Delta Lake format
delta_path = "/delta/hospital_data"

df.write.format("delta") \
    .mode("overwrite") \
    .save(delta_path)

print(f"✅ Data written to Delta Lake at {delta_path}")

# Cell 29: Read from Delta Lake
df_delta = spark.read.format("delta").load(delta_path)
print(f"✅ Read {df_delta.count():,} rows from Delta Lake")

# Cell 30: Create Delta table
spark.sql(f"""
    CREATE TABLE IF NOT EXISTS hospital_data_delta
    USING DELTA
    LOCATION '{delta_path}'
""")

print("✅ Delta table 'hospital_data_delta' created")

# Cell 31: Query Delta table using SQL
delta_query_result = spark.sql("""
    SELECT State, COUNT(*) as patient_count
    FROM hospital_data_delta
    GROUP BY State
    ORDER BY patient_count DESC
""")

display(delta_query_result)

# Cell 32: Write partitioned Delta table
partitioned_delta_path = "/delta/hospital_data_partitioned"

df.write.format("delta") \
    .mode("overwrite") \
    .partitionBy("State") \
    .save(partitioned_delta_path)

print(f"✅ Partitioned data written to {partitioned_delta_path}")

# Cell 33: Optimize Delta table (compaction)
spark.sql(f"OPTIMIZE delta.`{delta_path}`")
print("✅ Delta table optimized")

# Cell 34: Show Delta table history (time travel)
history = spark.sql(f"DESCRIBE HISTORY delta.`{delta_path}`")
display(history)


# ----------------------------------------------------------------------------
# SECTION 9: Performance Optimization
# ----------------------------------------------------------------------------

# Cell 35: Cache frequently used DataFrame
df.cache()
df.count()  # Trigger caching
print("✅ DataFrame cached in memory")

# Cell 36: Check cache status
print(f"Is cached: {df.is_cached}")
print(f"Storage level: {df.storageLevel}")

# Cell 37: Repartition for better parallelism
df_repartitioned = df.repartition(8, "State")
print(f"Partitions before: {df.rdd.getNumPartitions()}")
print(f"Partitions after: {df_repartitioned.rdd.getNumPartitions()}")

# Cell 38: Broadcast join example (for small lookup tables)
# Create a small state info table
state_info = spark.createDataFrame([
    ("NY", "New York", "Northeast"),
    ("CA", "California", "West"),
    ("TX", "Texas", "South"),
    ("IL", "Illinois", "Midwest"),
    ("AZ", "Arizona", "Southwest")
], ["State", "StateName", "Region"])

# Broadcast join
from pyspark.sql.functions import broadcast

df_with_region = df.join(broadcast(state_info), "State", "left")
display(df_with_region.select("State", "StateName", "Region", "Patient_Name", "Age").limit(20))


# ----------------------------------------------------------------------------
# SECTION 10: Data Quality and Validation
# ----------------------------------------------------------------------------

# Cell 39: Check for duplicates
duplicate_check = df.groupBy("Patient_Name", "Date").agg(
    count("*").alias("duplicate_count")
).filter(col("duplicate_count") > 1).orderBy(col("duplicate_count").desc())

print(f"Duplicate records found: {duplicate_check.count()}")
display(duplicate_check.limit(10))

# Cell 40: Age validation
age_validation = df.select(
    count("*").alias("total_rows"),
    count(when(col("Age") < 0, True)).alias("negative_ages"),
    count(when(col("Age") > 120, True)).alias("unrealistic_ages"),
    count(when(col("Age").isNull(), True)).alias("null_ages")
)

display(age_validation)

# Cell 41: Date range validation
date_validation = df_with_date.select(
    count("*").alias("total_rows"),
    count(when(col("Date") > current_date(), True)).alias("future_dates"),
    count(when(col("Date") < lit("1900-01-01"), True)).alias("very_old_dates"),
    count(when(col("Date").isNull(), True)).alias("null_dates")
)

display(date_validation)


# ----------------------------------------------------------------------------
# SECTION 11: Visualization Examples
# ----------------------------------------------------------------------------

# Cell 42: Prepare data for bar chart (State distribution)
state_chart_data = df.groupBy("State").count().orderBy("count", ascending=False)
display(state_chart_data)
# Tip: Click the chart icon and select "Bar Chart"

# Cell 43: Prepare data for pie chart (Gender distribution)
gender_chart_data = df.groupBy("Gender").count()
display(gender_chart_data)
# Tip: Click the chart icon and select "Pie Chart"

# Cell 44: Prepare data for line chart (Temporal trends)
temporal_chart_data = df_temporal.groupBy("year", "month").count().orderBy("year", "month")
display(temporal_chart_data)
# Tip: Click the chart icon and select "Line Chart"

# Cell 45: Age distribution histogram
age_histogram = df.select("Age")
display(age_histogram)
# Tip: Click the chart icon and select "Histogram"


# ----------------------------------------------------------------------------
# SECTION 12: Export Results
# ----------------------------------------------------------------------------

# Cell 46: Save analysis results to CSV (in DBFS)
output_path = "/FileStore/tables/state_analysis_results.csv"

state_summary.coalesce(1).write.format("csv") \
    .mode("overwrite") \
    .option("header", "true") \
    .save(output_path)

print(f"✅ Results saved to {output_path}")

# Cell 47: Save to Parquet (more efficient)
parquet_output = "/FileStore/tables/hospital_analysis.parquet"

df.write.format("parquet") \
    .mode("overwrite") \
    .save(parquet_output)

print(f"✅ Data saved to Parquet at {parquet_output}")


# ============================================================================
# End of PySpark Processing
# ============================================================================
# 
# Key Takeaways:
# 1. Use display() instead of show() for interactive tables in DataBricks
# 2. Delta Lake provides ACID transactions and time travel
# 3. Window functions enable advanced analytics
# 4. Caching improves performance for repeated operations
# 5. Broadcast joins optimize small table joins
# 6. DataBricks visualizations are built-in (click chart icon)
# 
# Next Steps:
# - Experiment with MLlib for machine learning
# - Try Structured Streaming for real-time data
# - Explore Delta Lake time travel features
# - Create dashboards with multiple visualizations
# ============================================================================
