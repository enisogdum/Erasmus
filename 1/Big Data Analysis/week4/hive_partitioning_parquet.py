import os
import shutil
import pandas as pd
from pathlib import Path
import uuid

# Configuration
ROOT = Path("/Users/enisogdum/Desktop/Erasmus+/Erasmus Courses/Big Data Analysis")
CSV_SOURCE = ROOT / "2/mydata.csv"
OUTPUT_ROOT = ROOT / "4/hive_partitioned_parquet"
CHUNK_SIZE = 100_000  # Adjust based on memory

def prepare_output_dir(path: Path) -> None:
    """Recreate the output directory."""
    if path.exists():
        try:
            shutil.rmtree(path)
        except OSError as e:
            print(f"Error removing {path}: {e}")
            # Try to proceed anyway or handle specifically if needed
            pass
    path.mkdir(parents=True, exist_ok=True)

def write_partition(state: str, date: str, data: pd.DataFrame, chunk_id: int) -> None:
    partition_dir = OUTPUT_ROOT / f"State={state}" / f"Date={date}"
    partition_dir.mkdir(parents=True, exist_ok=True)
    
    # Parquet files are immutable, so we write a new file for each chunk.
    # Naming convention: part-{chunk_id}-{uuid}.parquet
    filename = f"part-{chunk_id:05d}-{uuid.uuid4().hex[:8]}.parquet"
    file_path = partition_dir / filename
    
    # Write to parquet using pyarrow engine (default)
    # index=False because Hive partitions usually don't include the pandas index
    data.to_parquet(file_path, index=False, engine='pyarrow', compression='snappy')

def export_hive_partitioned_parquet() -> None:
    print(f"Starting Parquet export from {CSV_SOURCE} to {OUTPUT_ROOT}")
    prepare_output_dir(OUTPUT_ROOT)

    # Read CSV in chunks
    chunk_iter = pd.read_csv(CSV_SOURCE, chunksize=CHUNK_SIZE)
    
    for chunk_idx, chunk in enumerate(chunk_iter, start=1):
        # Group by partition columns
        grouped = chunk.groupby(["State", "Date"])
        
        for (state, date), df in grouped:
            # Drop the partition columns from the dataframe as they are in the folder structure
            # (Standard Hive behavior, though sometimes kept. We'll drop to save space/redundancy)
            df_to_write = df.drop(columns=["State", "Date"])
            write_partition(state, date, df_to_write, chunk_idx)
            
        print(f"Processed chunk {chunk_idx}")

    print(f"✅ Hive-partitioned Parquet exported under {OUTPUT_ROOT}")

if __name__ == "__main__":
    export_hive_partitioned_parquet()
