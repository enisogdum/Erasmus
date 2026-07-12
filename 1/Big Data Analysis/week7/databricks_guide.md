# DataBricks Community Edition - Complete Guide

## Overview
This guide walks you through using **DataBricks Community Edition** to process the hospital dataset. You'll learn how to create an account, upload data, and perform SQL and PySpark operations in a cloud-based environment.

---

## 1. Account Setup

### Creating Your DataBricks Community Edition Account

1. **Navigate to DataBricks Community Edition**
   - Visit: [https://community.cloud.databricks.com/](https://community.cloud.databricks.com/)
   - Click **"Sign up for Community Edition"**

2. **Register Your Account**
   - Provide your email address
   - Create a strong password
   - Complete the registration form
   - Verify your email address

3. **First Login**
   - Log in with your credentials
   - You'll be directed to your DataBricks workspace

> [!TIP]
> Community Edition is completely free and includes:
> - 15GB cluster storage
> - Single-node cluster (limited compute)
> - Full access to notebooks and DBFS
> - No credit card required

---

## 2. Workspace Navigation

### Understanding the DataBricks Interface

**Key Components:**
- **Workspace**: Where you organize notebooks, folders, and libraries
- **Data**: Manage tables, databases, and DBFS (DataBricks File System)
- **Compute**: Create and manage clusters
- **Jobs**: Schedule and automate workflows

### Creating Your First Cluster

1. Click **"Compute"** in the left sidebar
2. Click **"Create Cluster"**
3. Configure your cluster:
   - **Cluster Name**: `hospital-data-cluster`
   - **Cluster Mode**: Standard (default for Community Edition)
   - **Databricks Runtime Version**: Select latest LTS version (e.g., 13.3 LTS)
   - **Node Type**: Automatically set for Community Edition
4. Click **"Create Cluster"**
5. Wait 3-5 minutes for cluster to start (status will show green when ready)

> [!IMPORTANT]
> Community Edition clusters auto-terminate after 2 hours of inactivity. Always save your work in notebooks!

---

## 3. Data Upload

### Method 1: Upload via UI (Recommended for Sample Data)

1. **Navigate to Data**
   - Click **"Data"** in the left sidebar
   - Click **"Create Table"**

2. **Upload File**
   - Click **"Upload File"**
   - Select `sample_data.csv` from your local machine
   - Click **"Create Table with UI"**

3. **Configure Table**
   - **Table Name**: `hospital_data`
   - **Database**: `default`
   - Check **"First row is header"**
   - Verify column types (DataBricks auto-detects)
   - Click **"Create Table"**

### Method 2: Upload to DBFS (Programmatic)

```python
# In a DataBricks notebook
dbutils.fs.put("/FileStore/tables/sample_data.csv", open("/path/to/sample_data.csv").read())
```

### Verify Upload

```sql
-- Check table exists
SHOW TABLES;

-- View first 10 rows
SELECT * FROM hospital_data LIMIT 10;

-- Get row count
SELECT COUNT(*) as total_rows FROM hospital_data;
```

---

## 4. SQL Analysis in DataBricks

### Creating a SQL Notebook

1. Click **"Workspace"** → **"Create"** → **"Notebook"**
2. **Name**: `Hospital Data SQL Analysis`
3. **Default Language**: SQL
4. **Cluster**: Select your cluster
5. Click **"Create"**

### Example SQL Queries

See [sql_analysis.sql](file:///Users/enisogdum/Desktop/Erasmus+/Erasmus%20Courses/Big%20Data%20Analysis/8/sql_analysis.sql) for complete examples.

**Quick Examples:**

```sql
-- Patient count by state
SELECT State, COUNT(*) as patient_count
FROM hospital_data
GROUP BY State
ORDER BY patient_count DESC;

-- Age distribution
SELECT 
  CASE 
    WHEN Age < 18 THEN 'Child'
    WHEN Age BETWEEN 18 AND 65 THEN 'Adult'
    ELSE 'Senior'
  END as age_group,
  COUNT(*) as count
FROM hospital_data
GROUP BY age_group;
```

### Visualization

- After running a query, click the **chart icon** below the results
- Choose from bar charts, pie charts, line graphs, etc.
- DataBricks automatically creates interactive visualizations

---

## 5. PySpark Processing in DataBricks

### Creating a PySpark Notebook

1. Click **"Workspace"** → **"Create"** → **"Notebook"**
2. **Name**: `Hospital Data PySpark Processing`
3. **Default Language**: Python
4. **Cluster**: Select your cluster
5. Click **"Create"**

### Example PySpark Operations

See [pyspark_processing.py](file:///Users/enisogdum/Desktop/Erasmus+/Erasmus%20Courses/Big%20Data%20Analysis/8/pyspark_processing.py) for complete examples.

**Quick Examples:**

```python
# Read table into DataFrame
df = spark.table("hospital_data")

# Show schema
df.printSchema()

# Basic statistics
df.describe().show()

# Filter and aggregate
from pyspark.sql.functions import col, count

state_summary = df.groupBy("State").agg(
    count("*").alias("patient_count")
).orderBy(col("patient_count").desc())

display(state_summary)
```

> [!TIP]
> Use `display()` instead of `show()` in DataBricks for interactive tables and automatic visualizations!

---

## 6. Delta Lake Operations

### Why Delta Lake?

- **ACID transactions**: Reliable data operations
- **Time travel**: Query historical versions
- **Schema enforcement**: Prevent data corruption
- **Optimized performance**: Better than plain Parquet

### Creating a Delta Table

```python
# Write DataFrame to Delta format
df.write.format("delta").mode("overwrite").save("/delta/hospital_data")

# Create table from Delta files
spark.sql("""
  CREATE TABLE hospital_data_delta
  USING DELTA
  LOCATION '/delta/hospital_data'
""")
```

### Time Travel Example

```python
# Query previous version
df_v0 = spark.read.format("delta").option("versionAsOf", 0).load("/delta/hospital_data")

# Query as of specific timestamp
df_yesterday = spark.read.format("delta") \
    .option("timestampAsOf", "2024-11-30") \
    .load("/delta/hospital_data")
```

---

## 7. Best Practices

### Performance Optimization

1. **Cache frequently used DataFrames**
   ```python
   df.cache()
   df.count()  # Trigger caching
   ```

2. **Partition large tables**
   ```python
   df.write.partitionBy("State").format("delta").save("/delta/hospital_partitioned")
   ```

3. **Use Delta Lake for production workloads**
   - Better performance than CSV/Parquet
   - Built-in optimization commands

### Resource Management

- **Stop clusters when not in use** to conserve resources
- **Use notebook auto-save** - DataBricks saves automatically
- **Export important notebooks** as backups (File → Export)

### Collaboration

- **Share notebooks** with team members via workspace permissions
- **Use version control** by integrating with Git repositories
- **Document your code** with markdown cells

---

## 8. Key Differences: Local Spark vs DataBricks

| Feature | Local Spark (Tasks 6-7) | DataBricks |
|---------|------------------------|------------|
| **Setup** | Manual installation | Cloud-based, instant |
| **Cluster Management** | Manual configuration | Automated, managed |
| **Visualization** | External tools needed | Built-in interactive charts |
| **Collaboration** | File sharing | Real-time collaboration |
| **Storage** | Local filesystem | DBFS + cloud storage |
| **Scalability** | Limited to local resources | Cloud-scale (paid tiers) |
| **Delta Lake** | Manual setup | Native integration |

---

## 9. Common Issues & Solutions

### Cluster Won't Start
- **Solution**: Wait a few minutes, Community Edition has limited resources
- Try terminating and recreating the cluster

### Table Not Found
- **Solution**: Ensure cluster is attached to notebook
- Verify table creation with `SHOW TABLES`

### Out of Memory
- **Solution**: Process data in smaller chunks
- Use `.limit()` for testing queries
- Cache only necessary DataFrames

### Upload Fails
- **Solution**: Ensure file is < 2GB for Community Edition
- Use the sample data (~100MB) instead of full dataset
- Try DBFS upload method instead of UI

---

## 10. Next Steps

After completing this guide, you should be able to:
- ✅ Create and manage DataBricks clusters
- ✅ Upload and query data using SQL
- ✅ Process data with PySpark DataFrames
- ✅ Use Delta Lake for reliable storage
- ✅ Create visualizations and dashboards

### Further Learning
- Explore DataBricks Academy (free courses)
- Try MLflow for machine learning workflows
- Experiment with streaming data using Structured Streaming
- Integrate with cloud storage (AWS S3, Azure Blob)

---

## Resources

- **DataBricks Documentation**: [docs.databricks.com](https://docs.databricks.com/)
- **Community Forums**: [community.databricks.com](https://community.databricks.com/)
- **Apache Spark Docs**: [spark.apache.org](https://spark.apache.org/docs/latest/)
- **Delta Lake Guide**: [delta.io](https://delta.io/)

---

*Guide created for Big Data Analysis - Task 8*
