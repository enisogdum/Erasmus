import csv
import random
from datetime import datetime, timedelta

# Configuration
num_rows = 10_000_000  # Adjust to reach ~1GB+
num_columns = 100
output_file = "../2/mydata.csv"

# Sample data for text fields
first_names = ['John', 'Jane', 'Alice', 'Bob', 'Mike', 'Sara', 'Tom', 'Emma']
last_names = ['Smith', 'Johnson', 'Brown', 'Taylor', 'Anderson', 'Thomas']
cities = ['New York', 'Los Angeles', 'Chicago', 'Houston', 'Phoenix']
states = ['NY', 'CA', 'IL', 'TX', 'AZ']
genders = ['M', 'F', 'Other']
words = ['fever', 'cough', 'headache', 'pain', 'dizziness', 'treatment', 'medication', 'therapy', 'report', 'test']

# Generate random date
def random_date(start_year=2018, end_year=2025):
    start = datetime(start_year, 1, 1)
    end = datetime(end_year, 12, 31)
    delta = end - start
    random_days = random.randint(0, delta.days)
    return (start + timedelta(days=random_days)).strftime("%Y-%m-%d")

# Generate random sentence
def random_sentence(min_words=3, max_words=10):
    return ' '.join(random.choices(words, k=random.randint(min_words, max_words)))

# Generate one row
def generate_row():
    row = []
    for i in range(num_columns):
        if i == 0:
            row.append(random_date())  # Date
        elif i == 1:
            row.append(random.choice(first_names) + ' ' + random.choice(last_names))  # Name
        elif i == 2:
            row.append(str(random.randint(0, 100)))  # Age
        elif i == 3:
            row.append(random.choice(genders))  # Gender
        elif i == 4:
            row.append(random.choice(cities))  # City
        elif i == 5:
            row.append(random.choice(states))  # State
        elif 6 <= i < 30:
            row.append(random_sentence(3, 10))  # Symptoms
        elif 30 <= i < 60:
            row.append(random_sentence(5, 12))  # Treatment
        elif 60 <= i < 80:
            row.append(random_sentence(8, 15))  # Test reports
        else:
            row.append(random.choice(words))  # Misc
    return row

# Column names
columns = ['Date', 'Patient_Name', 'Age', 'Gender', 'City', 'State'] + \
          [f'Symptom_{i}' for i in range(6, 30)] + \
          [f'Treatment_{i}' for i in range(30, 60)] + \
          [f'Test_Report_{i}' for i in range(60, 80)] + \
          [f'Misc_{i}' for i in range(80, 100)]

# Write CSV in chunks
chunk_size = 100_000
with open(output_file, mode='w', newline='', encoding='utf-8') as f:
    writer = csv.writer(f)
    writer.writerow(columns)
    for start in range(0, num_rows, chunk_size):
        chunk = [generate_row() for _ in range(min(chunk_size, num_rows - start))]
        writer.writerows(chunk)
        print(f"Written rows {start} to {start + len(chunk)}")

print(f"CSV file '{output_file}' generated successfully!")
