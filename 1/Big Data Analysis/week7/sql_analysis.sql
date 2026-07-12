-- ============================================================================
-- DataBricks SQL Analysis - Hospital Sample Data
-- ============================================================================
-- This notebook contains SQL queries for analyzing the hospital sample dataset
-- in DataBricks Community Edition.
--
-- Prerequisites:
-- 1. Upload sample_data.csv to DataBricks (489 MB, 100,000 rows)
-- 2. Create table named 'hospital_data' from sample_data.csv
--    (You can name it 'hospital_data' when creating the table in DataBricks UI)
-- 3. Attach this notebook to a running cluster
--
-- Note: The file is named 'sample_data.csv' but you'll create a table 
--       called 'hospital_data' from it in DataBricks.
-- ============================================================================

-- ----------------------------------------------------------------------------
-- SECTION 1: Data Exploration
-- ----------------------------------------------------------------------------

-- Cell 1: Verify table exists and show all tables
SHOW TABLES;

-- Cell 2: View table schema
DESCRIBE hospital_data;

-- Cell 3: Preview first 10 rows
SELECT * FROM hospital_data LIMIT 10;

-- Cell 4: Get total row count
SELECT COUNT(*) as total_rows FROM hospital_data;

-- Cell 5: Check for null values in key columns
SELECT 
  COUNT(*) as total_rows,
  COUNT(Patient_Name) as non_null_names,
  COUNT(Age) as non_null_ages,
  COUNT(State) as non_null_states,
  COUNT(Date) as non_null_dates
FROM hospital_data;


-- ----------------------------------------------------------------------------
-- SECTION 2: State-Based Analysis
-- ----------------------------------------------------------------------------

-- Cell 6: Patient count by state
SELECT 
  State,
  COUNT(*) as patient_count,
  ROUND(COUNT(*) * 100.0 / SUM(COUNT(*)) OVER (), 2) as percentage
FROM hospital_data
GROUP BY State
ORDER BY patient_count DESC;

-- Cell 7: Average age by state
SELECT 
  State,
  ROUND(AVG(Age), 2) as avg_age,
  MIN(Age) as min_age,
  MAX(Age) as max_age
FROM hospital_data
GROUP BY State
ORDER BY avg_age DESC;

-- Cell 8: Gender distribution by state
SELECT 
  State,
  Gender,
  COUNT(*) as count
FROM hospital_data
GROUP BY State, Gender
ORDER BY State, count DESC;


-- ----------------------------------------------------------------------------
-- SECTION 3: Temporal Analysis
-- ----------------------------------------------------------------------------

-- Cell 9: Patient visits by year
SELECT 
  YEAR(Date) as year,
  COUNT(*) as visit_count
FROM hospital_data
GROUP BY YEAR(Date)
ORDER BY year;

-- Cell 10: Patient visits by month (recent year)
SELECT 
  MONTH(Date) as month,
  COUNT(*) as visit_count
FROM hospital_data
WHERE YEAR(Date) = (SELECT MAX(YEAR(Date)) FROM hospital_data)
GROUP BY MONTH(Date)
ORDER BY month;

-- Cell 11: Date range of the dataset
SELECT 
  MIN(Date) as earliest_date,
  MAX(Date) as latest_date,
  DATEDIFF(MAX(Date), MIN(Date)) as days_span
FROM hospital_data;


-- ----------------------------------------------------------------------------
-- SECTION 4: Age Demographics
-- ----------------------------------------------------------------------------

-- Cell 12: Age group distribution
SELECT 
  CASE 
    WHEN Age < 18 THEN '0-17 (Child)'
    WHEN Age BETWEEN 18 AND 30 THEN '18-30 (Young Adult)'
    WHEN Age BETWEEN 31 AND 50 THEN '31-50 (Adult)'
    WHEN Age BETWEEN 51 AND 65 THEN '51-65 (Middle Age)'
    ELSE '65+ (Senior)'
  END as age_group,
  COUNT(*) as count,
  ROUND(AVG(Age), 1) as avg_age_in_group
FROM hospital_data
GROUP BY age_group
ORDER BY 
  CASE 
    WHEN age_group LIKE '0-17%' THEN 1
    WHEN age_group LIKE '18-30%' THEN 2
    WHEN age_group LIKE '31-50%' THEN 3
    WHEN age_group LIKE '51-65%' THEN 4
    ELSE 5
  END;

-- Cell 13: Gender distribution across age groups
SELECT 
  CASE 
    WHEN Age < 18 THEN '0-17'
    WHEN Age BETWEEN 18 AND 30 THEN '18-30'
    WHEN Age BETWEEN 31 AND 50 THEN '31-50'
    WHEN Age BETWEEN 51 AND 65 THEN '51-65'
    ELSE '65+'
  END as age_group,
  Gender,
  COUNT(*) as count
FROM hospital_data
GROUP BY age_group, Gender
ORDER BY age_group, count DESC;


-- ----------------------------------------------------------------------------
-- SECTION 5: Symptom Analysis
-- ----------------------------------------------------------------------------

-- Cell 14: Most common symptoms (from Symptom_1 column)
SELECT 
  Symptom_1,
  COUNT(*) as frequency,
  ROUND(COUNT(*) * 100.0 / SUM(COUNT(*)) OVER (), 2) as percentage
FROM hospital_data
WHERE Symptom_1 IS NOT NULL
GROUP BY Symptom_1
ORDER BY frequency DESC
LIMIT 10;

-- Cell 15: Symptom distribution by state (Symptom_1)
SELECT 
  State,
  Symptom_1,
  COUNT(*) as count
FROM hospital_data
WHERE Symptom_1 IS NOT NULL
GROUP BY State, Symptom_1
ORDER BY State, count DESC;


-- ----------------------------------------------------------------------------
-- SECTION 6: City Analysis
-- ----------------------------------------------------------------------------

-- Cell 16: Top 10 cities by patient count
SELECT 
  City,
  State,
  COUNT(*) as patient_count
FROM hospital_data
GROUP BY City, State
ORDER BY patient_count DESC
LIMIT 10;

-- Cell 17: Number of unique cities per state
SELECT 
  State,
  COUNT(DISTINCT City) as unique_cities,
  COUNT(*) as total_patients,
  ROUND(COUNT(*) / COUNT(DISTINCT City), 2) as avg_patients_per_city
FROM hospital_data
GROUP BY State
ORDER BY unique_cities DESC;


-- ----------------------------------------------------------------------------
-- SECTION 7: Advanced Queries
-- ----------------------------------------------------------------------------

-- Cell 18: Monthly trends by state (pivot-style)
SELECT 
  State,
  COUNT(CASE WHEN MONTH(Date) = 1 THEN 1 END) as Jan,
  COUNT(CASE WHEN MONTH(Date) = 2 THEN 1 END) as Feb,
  COUNT(CASE WHEN MONTH(Date) = 3 THEN 1 END) as Mar,
  COUNT(CASE WHEN MONTH(Date) = 4 THEN 1 END) as Apr,
  COUNT(CASE WHEN MONTH(Date) = 5 THEN 1 END) as May,
  COUNT(CASE WHEN MONTH(Date) = 6 THEN 1 END) as Jun
FROM hospital_data
WHERE YEAR(Date) = (SELECT MAX(YEAR(Date)) FROM hospital_data)
GROUP BY State
ORDER BY State;

-- Cell 19: Patients by state and age group (cross-tabulation)
SELECT 
  State,
  COUNT(CASE WHEN Age < 18 THEN 1 END) as children,
  COUNT(CASE WHEN Age BETWEEN 18 AND 65 THEN 1 END) as adults,
  COUNT(CASE WHEN Age > 65 THEN 1 END) as seniors
FROM hospital_data
GROUP BY State
ORDER BY State;

-- Cell 20: Weekend vs Weekday visits
SELECT 
  CASE 
    WHEN DAYOFWEEK(Date) IN (1, 7) THEN 'Weekend'
    ELSE 'Weekday'
  END as day_type,
  COUNT(*) as visit_count,
  ROUND(COUNT(*) * 100.0 / SUM(COUNT(*)) OVER (), 2) as percentage
FROM hospital_data
GROUP BY day_type;


-- ----------------------------------------------------------------------------
-- SECTION 8: Performance Optimization Examples
-- ----------------------------------------------------------------------------

-- Cell 21: Create a cached view for frequent queries
CREATE OR REPLACE TEMP VIEW state_summary_cached AS
SELECT 
  State,
  COUNT(*) as patient_count,
  AVG(Age) as avg_age,
  COUNT(DISTINCT City) as city_count
FROM hospital_data
GROUP BY State;

CACHE TABLE state_summary_cached;

-- Cell 22: Query the cached view
SELECT * FROM state_summary_cached ORDER BY patient_count DESC;

-- Cell 23: Create a partitioned table (if you have write permissions)
-- Note: This creates a new table partitioned by State
CREATE TABLE IF NOT EXISTS hospital_data_partitioned
USING PARQUET
PARTITIONED BY (State)
AS SELECT * FROM hospital_data;

-- Cell 24: Query with partition pruning (faster!)
SELECT COUNT(*) 
FROM hospital_data_partitioned 
WHERE State = 'NY';


-- ----------------------------------------------------------------------------
-- SECTION 9: Data Quality Checks
-- ----------------------------------------------------------------------------

-- Cell 25: Check for duplicate patient records
SELECT 
  Patient_Name,
  Date,
  COUNT(*) as duplicate_count
FROM hospital_data
GROUP BY Patient_Name, Date
HAVING COUNT(*) > 1
ORDER BY duplicate_count DESC
LIMIT 10;

-- Cell 26: Age data quality check
SELECT 
  COUNT(*) as total_rows,
  COUNT(CASE WHEN Age < 0 THEN 1 END) as negative_ages,
  COUNT(CASE WHEN Age > 120 THEN 1 END) as unrealistic_ages,
  COUNT(CASE WHEN Age IS NULL THEN 1 END) as null_ages
FROM hospital_data;

-- Cell 27: Date range validation
SELECT 
  COUNT(*) as total_rows,
  COUNT(CASE WHEN Date > CURRENT_DATE() THEN 1 END) as future_dates,
  COUNT(CASE WHEN Date < '1900-01-01' THEN 1 END) as very_old_dates,
  COUNT(CASE WHEN Date IS NULL THEN 1 END) as null_dates
FROM hospital_data;


-- ----------------------------------------------------------------------------
-- SECTION 10: Summary Statistics
-- ----------------------------------------------------------------------------

-- Cell 28: Comprehensive summary report
SELECT 
  'Total Patients' as metric,
  CAST(COUNT(*) as STRING) as value
FROM hospital_data

UNION ALL

SELECT 
  'Date Range',
  CONCAT(CAST(MIN(Date) as STRING), ' to ', CAST(MAX(Date) as STRING))
FROM hospital_data

UNION ALL

SELECT 
  'Average Age',
  CAST(ROUND(AVG(Age), 2) as STRING)
FROM hospital_data

UNION ALL

SELECT 
  'Number of States',
  CAST(COUNT(DISTINCT State) as STRING)
FROM hospital_data

UNION ALL

SELECT 
  'Number of Cities',
  CAST(COUNT(DISTINCT City) as STRING)
FROM hospital_data;


-- ============================================================================
-- End of SQL Analysis
-- ============================================================================
-- 
-- Tips for DataBricks:
-- 1. Click the chart icon below results to create visualizations
-- 2. Use Cmd/Ctrl + Enter to run individual cells
-- 3. Use display() in Python cells for interactive tables
-- 4. Save your notebook frequently (auto-save is enabled)
-- ============================================================================
