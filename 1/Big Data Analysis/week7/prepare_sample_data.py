#!/usr/bin/env python3
"""
Prepare Sample Data for DataBricks Community Edition
Creates a smaller, manageable subset of the hospital dataset for upload to DataBricks.
Target size: ~100MB (~100,000 rows)

OPTIMIZED VERSION: Uses chunked reading to handle large files efficiently
"""

import pandas as pd
import os
import random

# Configuration
INPUT_FILE = "../2/mydata.csv"
OUTPUT_FILE = "sample_data.csv"
SAMPLE_SIZE = 100000  # Number of rows to sample
CHUNK_SIZE = 50000    # Read 50k rows at a time
RANDOM_SEED = 42

def main():
    random.seed(RANDOM_SEED)
    
    print("=" * 60)
    print("DataBricks Sample Data Preparation (Optimized)")
    print("=" * 60)
    
    # Check if input file exists
    if not os.path.exists(INPUT_FILE):
        print(f"❌ Error: Input file not found: {INPUT_FILE}")
        return
    
    print(f"\n📂 Reading data from: {INPUT_FILE}")
    print(f"   Target sample size: {SAMPLE_SIZE:,} rows")
    print(f"   Using chunked reading for efficiency...")
    
    # Count total rows efficiently
    print("\n⏳ Counting total rows...")
    total_rows = sum(1 for _ in open(INPUT_FILE)) - 1  # Subtract header
    print(f"   Total rows in dataset: {total_rows:,}")
    
    # Calculate sampling probability
    sample_prob = SAMPLE_SIZE / total_rows
    print(f"   Sampling probability: {sample_prob:.4f}")
    
    # Read and sample data in chunks (reservoir sampling approach)
    print("\n⏳ Reading and sampling data in chunks...")
    sampled_rows = []
    chunks_processed = 0
    
    # Read CSV in chunks
    chunk_iterator = pd.read_csv(INPUT_FILE, chunksize=CHUNK_SIZE)
    
    for chunk in chunk_iterator:
        chunks_processed += 1
        
        # Sample from this chunk
        chunk_sample = chunk.sample(frac=sample_prob, random_state=RANDOM_SEED + chunks_processed)
        sampled_rows.append(chunk_sample)
        
        # Progress indicator
        rows_processed = chunks_processed * CHUNK_SIZE
        print(f"   Processed {min(rows_processed, total_rows):,} / {total_rows:,} rows...", end='\r')
    
    print(f"\n   ✅ Processed all {total_rows:,} rows")
    
    # Combine all sampled chunks
    print("\n⏳ Combining sampled data...")
    sampled_df = pd.concat(sampled_rows, ignore_index=True)
    
    # If we got more than needed, take exact sample size
    if len(sampled_df) > SAMPLE_SIZE:
        print(f"   Trimming from {len(sampled_df):,} to {SAMPLE_SIZE:,} rows...")
        sampled_df = sampled_df.sample(n=SAMPLE_SIZE, random_state=RANDOM_SEED)
    
    print(f"\n✅ Sampled {len(sampled_df):,} rows")
    
    # Show distribution by State
    print("\n📊 Distribution by State:")
    state_counts = sampled_df['State'].value_counts().sort_index()
    for state, count in state_counts.items():
        percentage = (count / len(sampled_df)) * 100
        print(f"   {state}: {count:,} ({percentage:.1f}%)")
    
    # Save to CSV
    print(f"\n💾 Saving sample data to: {OUTPUT_FILE}")
    sampled_df.to_csv(OUTPUT_FILE, index=False)
    
    # Get file size
    file_size_bytes = os.path.getsize(OUTPUT_FILE)
    file_size_mb = file_size_bytes / (1024 * 1024)
    
    print(f"\n✅ Sample data created successfully!")
    print(f"   File size: {file_size_mb:.2f} MB")
    print(f"   Rows: {len(sampled_df):,}")
    print(f"   Columns: {len(sampled_df.columns)}")
    
    print("\n" + "=" * 60)
    print("Ready for upload to DataBricks Community Edition!")
    print("=" * 60)

if __name__ == "__main__":
    main()
