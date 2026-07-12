-- Step 4a: Create database
CREATE DATABASE IF NOT EXISTS hospital_db;
USE hospital_db;

-- Step 4b: Create original Hive table pointing to CSV folder
CREATE EXTERNAL TABLE hospital_data_original (
    Patient_Name STRING,
    Age INT,
    Gender STRING,
    City STRING,
    Symptom_6 STRING,
    Symptom_7 STRING,
    -- ... include all other non-partition columns
    Misc_99 STRING,
    State STRING,
    Date STRING
)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
STORED AS TEXTFILE
LOCATION '/Users/enisogdum/Desktop/Erasmus+/Erasmus Courses/Big Data Analysis/2/';

-- Step 4c: Create partitioned table (partition by State and Date)
CREATE TABLE hospital_data_partitioned (
    Patient_Name STRING,
    Age INT,
    Gender STRING,
    City STRING,
    Symptom_6 STRING,
    Symptom_7 STRING,
    -- ... include all other non-partition columns
    Misc_99 STRING
)
PARTITIONED BY (State STRING, Date STRING)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
STORED AS TEXTFILE;

-- Step 4d: Enable dynamic partitioning
SET hive.exec.dynamic.partition = true;
SET hive.exec.dynamic.partition.mode = nonstrict;

-- Step 4e: Load data into partitioned table
INSERT INTO TABLE hospital_data_partitioned PARTITION (State, Date)
SELECT
    Patient_Name,
    Age,
    Gender,
    City,
    Symptom_6,
    Symptom_7,
    -- ... all other non-partition columns
    Misc_99,
    State,
    Date
FROM hospital_data_original;

-- Step 4f: Verify partitions
SHOW PARTITIONS hospital_data_partitioned;