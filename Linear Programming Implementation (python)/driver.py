# Usage
# One argument (uses default courses file path)
# python driver.py path/to/your/file.csv

# Two arguments (specifies both file paths)
# python driver.py path/to/your/file.csv path/to/your/courses.csv


import sys
import csv_matrix
import ILP_func

if __name__ == '__main__':
    # Set default file paths
    default_file_path = r"data\Preferences\Odd_Sem.csv"
    default_courses_file_path = r"data\Courses\Odd_sem_courses.csv"

    # Check the number of command line arguments
    num_args = len(sys.argv)

    # Set file paths based on the number of arguments
    if num_args == 2:
        file_path = sys.argv[1]  # Use the provided file path
        courses_file_path = default_courses_file_path  # Use default courses file path
    elif num_args == 3:
        file_path = sys.argv[1]  # Use the first argument as file path
        # Use the second argument as courses file path
        courses_file_path = sys.argv[2]
    else:
        file_path = default_file_path  # Use default file path
        courses_file_path = default_courses_file_path  # Use default courses file path

    # Load and process the CSV files
    result_merged_df, filtered_courses_df = csv_matrix.csvToMatrix(
        file_path, courses_file_path)  # Unpack the tuple
    result_names, result_courses_vector, result_costs, result_category_vector, result_sections = csv_matrix.processMergedDF(
        result_merged_df, filtered_courses_df)

    # Solve ILP problem
    ILP_func.ILP_solve(result_costs, result_names,
                       result_category_vector, result_courses_vector, result_sections)
