import pandas as pd

input_file = "/Users/enisogdum/Desktop/Erasmus+/Erasmus Courses/Big Data Analysis/2/hospital_fake_data.csv"
output_folder = "/Users/enisogdum/Desktop/Erasmus+/Erasmus Courses/Big Data Analysis/2/split_by_state/"

# Make sure folder exists
import os
os.makedirs(output_folder, exist_ok=True)

# Read CSV in chunks to avoid memory issues
chunk_size = 100_000  # Adjust if needed
for chunk in pd.read_csv(input_file, chunksize=chunk_size):
    for state, state_df in chunk.groupby('State'):
        file_path = os.path.join(output_folder, f"hospital_State_{state}.csv")
        if os.path.exists(file_path):
            state_df.to_csv(file_path, mode='a', index=False, header=False)
        else:
            state_df.to_csv(file_path, mode='w', index=False, header=True)
    print(f"Processed chunk with rows {chunk.index.start} to {chunk.index.stop}")