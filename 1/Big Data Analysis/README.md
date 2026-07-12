# 📊 Big Data Analysis — Course Summary

> **Erasmus+ Programme** | Summer 2025
> Tools & Technologies: Python · DuckDB · Apache Spark · Delta Lake · Databricks · SQL · Pandas · PyArrow

---

## Table of Contents

1. [week1 — Synthetic Dataset Generation](#week-1--synthetic-dataset-generation)
2. [week2 — Columnar Storage & DuckDB](#week-2--columnar-storage--duckdb)
3. [week3 — Hive-Style Partitioning (CSV)](#week-3--hive-style-partitioning-csv)
4. [week4 — Hive-Style Partitioning (Parquet)](#week-4--hive-style-partitioning-parquet)
5. [week6 — Apache Spark & MapReduce (Word Count)](#week-6--apache-spark--mapreduce-word-count)
6. [week7 — Delta Lake Pipeline](#week-7--delta-lake-pipeline)
7. [week8 — Databricks Cloud Analytics](#week-8--databricks-cloud-analytics)
8. [Key Concepts Cheatsheet](#key-concepts-cheatsheet)

---

## week1 — Synthetic Dataset Generation

### Concept
Before analysing big data, you need big data. week1 focuses on programmatically generating a realistic, large-scale synthetic medical dataset using Python's standard library — no external services required.

### Goal
Create a CSV file of **10 million rows × 100 columns** (~12 GB) that simulates a hospital patient database, to be used as the common input for all subsequent tasks.

### Dataset Schema

| Column Range | Content | Example values |
|---|---|---|
| `Date` | Random date 2018–2025 | `2021-07-14` |
| `Patient_Name` | Random first + last name | `Alice Johnson` |
| `Age` | Integer 0–100 | `42` |
| `Gender` | Categorical | `M`, `F`, `Other` |
| `City` / `State` | US city/state | `Houston`, `TX` |
| `Symptom_6`–`Symptom_29` | Random medical sentences | `fever cough headache pain` |
| `Treatment_30`–`Treatment_59` | Random treatment sentences | `medication therapy report test` |
| `Test_Report_60`–`Test_Report_79` | Longer random sentences | |
| `Misc_80`–`Misc_99` | Random single words | |

### Tools & Libraries
- `csv` — chunked CSV writing
- `random` — data randomisation
- `datetime` / `timedelta` — date generation

### Key Technique — Chunked Writing
To avoid loading 10M rows into memory at once, data is written in **chunks of 100,000 rows**:

```python
chunk_size = 100_000
with open(output_file, mode='w', newline='', encoding='utf-8') as f:
    writer = csv.writer(f)
    writer.writerow(columns)
    for start in range(0, num_rows, chunk_size):
        chunk = [generate_row() for _ in range(chunk_size)]
        writer.writerows(chunk)
```

### Takeaway
> Generating realistic synthetic data at scale is the foundation of any big data pipeline. Chunked I/O prevents memory overflow when handling billions of records.

---

## week2 — Columnar Storage & DuckDB

### Concept
[DuckDB](https://duckdb.org/) is an embedded, in-process analytical database (like SQLite, but for analytics). It is optimised for OLAP workloads and can query CSV, Parquet, and JSON files directly without importing them.

### Why DuckDB?
- **Column-oriented storage** → scans only the needed columns (much faster than row-store for analytics)
- **Zero-copy CSV reading** via `read_csv_auto()`
- **Runs fully in-process** — no server to set up
- **SQL-compatible** — standard SQL queries work out of the box

### Files & What They Do

| File | Purpose |
|---|---|
| `load_csv_to_duckdb.py` | Loads `mydata.csv` into a DuckDB table and previews rows |
| `connect_duckdb.py` | Minimal connection snippet — opens `my_database.duckdb` |
| `create_hospital_table.py` | Drops & recreates `hospital_data` table from CSV |
| `analyze_partition_candidates.py` | Analyses cardinality of every column to suggest partition keys |
| `export_partition_summary.py` | Same analysis, but exports results to `partitioning_summary.csv` |

### Key SQL Pattern

```python
conn = duckdb.connect(database='my_database.duckdb', read_only=False)
conn.execute("""
    CREATE TABLE my_table AS
    SELECT * FROM read_csv_auto('mydata.csv')
""")
```

### Partitioning Analysis Logic
A column is a **good partition candidate** if:
- `DISTINCT values < 50% of total rows` → low cardinality (good for grouping)
- `NULL count < 10% of total rows` → data is mostly complete

Columns identified as good candidates: **`State`**, **`Date`**, **`Gender`**, **`City`**

### Takeaway
> DuckDB bridges the gap between local scripting and cloud data warehouses. It handles billions of rows efficiently on a laptop with plain SQL, making it ideal for data exploration and partitioning analysis.

---

## week3 — Hive-Style Partitioning (CSV)

### Concept
**Hive partitioning** organises data on disk into a folder hierarchy based on the values of selected columns. This is the de-facto standard layout used by Hive, Spark, Presto, Athena, and DuckDB.

### Partition Layout

```
hive_partitioned_csv/
├── State=NY/
│   ├── Date=2021-03-14/
│   │   └── part-00000.csv
│   └── Date=2022-11-05/
│       └── part-00000.csv
├── State=TX/
│   └── ...
└── ...
```

### Files & What They Do

| File | Purpose |
|---|---|
| `hive_partition_setup.sql` | HiveQL DDL — creates external table, partitioned table, enables dynamic partitioning, and loads data |
| `split_csv_by_state.py` | Simple split: reads CSV in chunks and writes one file per US state |
| `hive_partitioning.py` | Full Hive-style export: partitions by **State × Date** into a proper directory tree |

### Core Export Logic

```python
def write_partition(state: str, date: str, data: pd.DataFrame):
    partition_dir = OUTPUT_ROOT / f"State={state}" / f"Date={date}"
    partition_dir.mkdir(parents=True, exist_ok=True)
    file_path = partition_dir / "part-00000.csv"
    write_header = not file_path.exists()
    data.to_csv(file_path, mode="a", index=False, header=write_header)
```

### HiveQL Key Concepts

```sql
-- Enable dynamic partitioning
SET hive.exec.dynamic.partition = true;
SET hive.exec.dynamic.partition.mode = nonstrict;

-- Load with automatic partition detection
INSERT INTO TABLE hospital_data_partitioned PARTITION (State, Date)
SELECT ..., State, Date FROM hospital_data_original;
```

### Takeaway
> Hive-style partitioning is fundamental to big data query performance. Queries with a `WHERE State='NY'` filter skip all other partitions entirely (partition pruning), reducing I/O by orders of magnitude.

---

## week4 — Hive-Style Partitioning (Parquet)

### Concept
week4 repeats the Hive partitioning layout from week3 but replaces the output format: **CSV → Parquet**. Parquet is a binary, columnar file format that is far more efficient for analytical workloads.

### CSV vs Parquet Comparison

| Property | CSV | Parquet |
|---|---|---|
| Format | Text (row-based) | Binary (columnar) |
| Size | ~12 GB raw | ~2–4 GB (with Snappy) |
| Read speed | Slow (parse every cell) | Fast (skip unused columns) |
| Schema | None (inferred) | Embedded metadata |
| Compression | None / gzip | Snappy / Zstd / Brotli |
| Ecosystem | Universal | Spark, Hive, Athena, DuckDB |

### Key Technique — PyArrow + Snappy Compression

```python
data.to_parquet(
    file_path,
    index=False,
    engine='pyarrow',
    compression='snappy'  # Fast compress/decompress, good ratio
)
```

### Output Layout

```
hive_partitioned_parquet/
├── State=AZ/
│   ├── Date=2018-01-02/
│   │   └── part-00001-a3f8b2c1.parquet
│   └── ...
└── ...
```

> **Note:** Partition columns (`State`, `Date`) are dropped from the Parquet file content since they are already encoded in the folder path — standard Hive behaviour.

### Takeaway
> Parquet is the industry-standard format for data lakes. Combining Parquet with Hive partitioning gives you the best of both worlds: efficient storage and fast partition-pruned queries.

---

## week6 — Apache Spark & MapReduce (Word Count)

### Concept
**Apache Spark** is the dominant distributed data processing engine. week6 introduces Spark's **RDD (Resilient Distributed Dataset)** API by implementing the classic **Word Count** — the "Hello World" of big data.

### MapReduce Pipeline

```
Input CSV column (Symptom_6)
        │
    flatMap  ──────→  Split each line into individual words
        │
      map  ──────────→  (word, 1)  for each word
        │
  reduceByKey  ───────→  Sum counts:  (word, total_count)
        │
  saveAsTextFile  ─────→  word_count_output/
```

### Implementation

```python
spark = SparkSession.builder.appName("Task6_WordCount").master("local[*]").getOrCreate()

df = spark.read.option("header", "true").csv(CSV_PATH)
text_rdd = df.select("Symptom_6").rdd.map(lambda row: row[0])
text_rdd = text_rdd.filter(lambda x: x is not None)

word_counts = text_rdd \
    .flatMap(lambda line: line.split(" ")) \
    .map(lambda word: (word, 1)) \
    .reduceByKey(lambda a, b: a + b)

word_counts.saveAsTextFile(OUTPUT_DIR)
```

### Key Concepts

| Term | Meaning |
|---|---|
| **RDD** | Distributed, fault-tolerant collection of elements |
| **Transformation** | Lazy operation that builds the DAG (`flatMap`, `map`, `filter`) |
| **Action** | Triggers execution (`saveAsTextFile`, `count`, `take`) |
| **`local[*]`** | Run Spark in local mode using all available CPU cores |
| **Partition** | A slice of the RDD processed in parallel on one core/node |

### Takeaway
> The MapReduce paradigm (flatMap → map → reduceByKey) underpins virtually every distributed computation. Spark's RDD API makes it explicit and understandable before moving to higher-level DataFrame APIs.

---

## week7 — Delta Lake Pipeline

### Concept
**Delta Lake** is an open-source storage layer built on top of Parquet that adds ACID transactions, schema enforcement, and time travel to data lakes.

### The Pipeline: CSV → DataFrame → Delta → SQL

```
mydata.csv
    ↓  spark.read.csv()
   DF1  (Spark DataFrame)
    ↓  .write.format("delta").save()
    D1  (Delta table on disk)
    ↓  spark.read.format("delta").load()
   DF2  (new DataFrame from Delta)
    ↓  createOrReplaceTempView() + spark.sql()
  Result  (patient count per State)
```

### Spark Session with Delta Extensions

```python
builder = SparkSession.builder \
    .appName("Task7_DeltaLake") \
    .master("local[*]") \
    .config("spark.sql.extensions", "io.delta.sql.DeltaSparkSessionExtension") \
    .config("spark.sql.catalog.spark_catalog", "org.apache.spark.sql.delta.catalog.DeltaCatalog")

spark = configure_spark_with_delta_pip(builder).getOrCreate()
```

### SQL Query on Delta Data

```python
df2.createOrReplaceTempView("hospital_data")

result = spark.sql("""
    SELECT State, COUNT(*) as Patient_Count
    FROM hospital_data
    GROUP BY State
    ORDER BY Patient_Count DESC
""")
result.show()
```

### Delta Lake vs Plain Parquet

| Feature | Plain Parquet | Delta Lake |
|---|---|---|
| ACID transactions | ❌ | ✅ |
| Schema enforcement | ❌ | ✅ |
| Time travel | ❌ | ✅ |
| Concurrent writes | ❌ (unsafe) | ✅ |
| Audit log | ❌ | ✅ (`_delta_log/`) |
| Upserts / Deletes | ❌ | ✅ (`MERGE INTO`) |

### Takeaway
> Delta Lake makes data lakes as reliable as traditional databases. Its ACID guarantees are essential when multiple jobs write to the same dataset simultaneously — a core requirement in production big data pipelines.

---

## week8 — Databricks Cloud Analytics

### Concept
**Databricks** is a unified data and AI platform that hosts managed Apache Spark clusters, notebooks, Delta Lake, and MLflow in the cloud. week8 moves the entire pipeline from a local machine to a cloud environment.

### Key Components

| Component | Description |
|---|---|
| **Workspace** | Notebooks, folders, and libraries |
| **Compute** | Managed Spark clusters (auto-terminate after 2 h on Community Edition) |
| **DBFS** | Distributed filesystem — accessible like a local path |
| **Data** | Managed tables, databases, and external data sources |
| **Jobs** | Scheduled and automated notebook/script workflows |

### Local Spark vs Databricks

| Feature | Local Spark (Weeks 6–7) | Databricks |
|---|---|---|
| Setup | Manual installation | Cloud-based, instant |
| Cluster management | Manual | Fully automated |
| Visualisation | External tools | Built-in interactive charts |
| Collaboration | File sharing | Real-time shared notebooks |
| Delta Lake | Manual setup | Native, first-class support |
| Scalability | Limited to laptop | Cloud-scale (paid tiers) |

### PySpark in Databricks

```python
# Read hospital_data table (already uploaded via UI)
df = spark.table("hospital_data")

# Aggregate by state
from pyspark.sql.functions import col, count

state_summary = df.groupBy("State").agg(
    count("*").alias("patient_count")
).orderBy(col("patient_count").desc())

display(state_summary)   # use display() instead of show() for rich output
```

### Sample Data Workflow
1. Run `prepare_sample_data.py` to generate a smaller `sample_data.csv` (~100 MB)
2. Upload via Databricks UI → create a managed `hospital_data` table
3. Run SQL notebooks (`sql_analysis.sql`) and PySpark notebooks (`pyspark_processing.py`)
4. Create visualisations (bar charts, pie charts) directly in the notebook

### Takeaway
> Databricks removes the operational overhead of Spark cluster management, enabling data engineers and scientists to focus on analysis. It is the cloud-native successor to on-premise Hadoop/Hive clusters.

---

## Key Concepts Cheatsheet

### Data Formats

| Format | Best for | Storage model |
|---|---|---|
| **CSV** | Simplicity, portability | Row-based, uncompressed |
| **Parquet** | Analytical queries, data lakes | Columnar, compressed |
| **Delta** | ACID workloads, streaming | Parquet + transaction log |

### Partitioning Rules of Thumb

| Guideline | Reason |
|---|---|
| Partition by **low-cardinality** columns (`State`, `Date`, `Gender`) | Manageable number of folders |
| **Don't** partition by high-cardinality columns (`Name`, UUID) | Too many tiny files → performance penalty |
| Target partition size **128 MB – 1 GB** | Optimal for Spark / HDFS block reads |
| Drop partition columns **from file content** | They are encoded in the path already |

### Spark Execution Model

```
Driver Program
    │ SparkContext / SparkSession
    │
    ├── Transformation (lazy) ──────→ builds DAG
    │       flatMap, map, filter, groupBy, join ...
    │
    └── Action (eager) ─────────────→ triggers execution
            count(), show(), saveAsTextFile(), collect()
```

### Full Technology Stack

```
┌─────────────────────────────────────────┐
│            Databricks (week8)          │  ← Cloud platform
├─────────────────────────────────────────┤
│          Delta Lake (week7)            │  ← ACID storage layer
├─────────────────────────────────────────┤
│       Apache Spark (Weeks 6–8)          │  ← Distributed compute
├─────────────────────────────────────────┤
│  Parquet / CSV Partitioning (Wks 3–4)   │  ← Storage layout
├─────────────────────────────────────────┤
│          DuckDB (week2)                │  ← Local OLAP engine
├─────────────────────────────────────────┤
│     Synthetic Dataset (week1)          │  ← Data source
└─────────────────────────────────────────┘
```

---

*Summary prepared for the Erasmus+ Big Data Analysis course · Summer 2025*
