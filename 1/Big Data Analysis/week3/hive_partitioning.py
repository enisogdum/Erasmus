import os
import shutil
import errno
from pathlib import Path

import pandas as pd


ROOT = Path("/Users/enisogdum/Desktop/Erasmus+/Erasmus Courses/Big Data Analysis")
CSV_SOURCE = ROOT / "2/mydata.csv"
OUTPUT_ROOT = ROOT / "3/hive_partitioned_csv"
CHUNK_SIZE = 100_000


def _handle_remove_error(func, path, exc_info):
    """Force-remove read-only files that block rmtree."""
    exc = exc_info[1]
    if isinstance(exc, OSError) and exc.errno == errno.ENOTEMPTY:
        try:
            shutil.rmtree(path)
        except OSError:
            pass
        return
    os.chmod(path, 0o700)
    try:
        func(path)
    except OSError:
        pass


def prepare_output_dir(path: Path) -> None:
    """Recreate the Hive-style export directory to avoid stale shards."""
    if path.exists():
        shutil.rmtree(path, onerror=_handle_remove_error)
    path.mkdir(parents=True, exist_ok=True)


def write_partition(state: str, date: str, data: pd.DataFrame) -> None:
    partition_dir = OUTPUT_ROOT / f"State={state}" / f"Date={date}"
    partition_dir.mkdir(parents=True, exist_ok=True)
    file_path = partition_dir / "part-00000.csv"

    write_header = not file_path.exists()
    data.to_csv(file_path, mode="a", index=False, header=write_header)


def export_hive_partitioned_csv() -> None:
    prepare_output_dir(OUTPUT_ROOT)

    chunk_iter = pd.read_csv(CSV_SOURCE, chunksize=CHUNK_SIZE)
    for chunk_idx, chunk in enumerate(chunk_iter, start=1):
        grouped = chunk.groupby(["State", "Date"])
        for (state, date), df in grouped:
            write_partition(state, date, df)
        print(f"Processed chunk {chunk_idx}")

    sample = sorted(
        root.replace(str(OUTPUT_ROOT) + "/", "", 1)
        for root, _, files in os.walk(OUTPUT_ROOT)
        if files
    )[:5]
    print("Sample Hive partitions:", sample)
    print(f"Hive-partitioned CSV exported under {OUTPUT_ROOT}")


if __name__ == "__main__":
    export_hive_partitioned_csv()

