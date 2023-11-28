import pandas as pd
import numpy as np
from itertools import permutations
from itertools import product


def csvToMatrix(file_path, max_attempts=100):
    # Read CSV file into a DataFrame
    df = pd.read_csv(file_path)
    courses = pd.read_csv("data\Courses.csv")

    # Check if all professors hava atleast one preference
    # Display a warning and delete rows where all values are NaN except 'Name' and 'Category'
    for name in df['Name'].unique():
        rows_for_name = df[df['Name'] == name]

        # Check if all values (excluding 'Name' and 'Category') are NaN for a particular name
        if all(rows_for_name.drop(['Name', 'Category'], axis=1).isna().values.flatten()):
            print(
                f"Warning: {name} has not given preference for any course ")

            # Delete rows where all values are NaN except 'Name' and 'Category'
            # df = df[df['Name'] != name]
    ####

    # Check if all courses actually exist
    df['FD CDC'] = df['FD CDC'].where(
        df['FD CDC'].isin(courses['Courses']), pd.NA)
    df['HD CDC'] = df['HD CDC'].where(
        df['HD CDC'].isin(courses['Courses']), pd.NA)
    df['FD Elec'] = df['FD Elec'].where(
        df['FD Elec'].isin(courses['Courses']), pd.NA)
    df['HD Elec'] = df['HD Elec'].where(
        df['HD Elec'].isin(courses['Courses']), pd.NA)
    ####

    # Calculate the total number of unique courses
    total_courses = (df['HD Elec'].nunique(
    ) + df['FD Elec'].nunique() + df['HD CDC'].nunique() + df['FD CDC'].nunique())
    # Calculate the sum of "Category" for unique names
    sum_category_unique_names = df.drop_duplicates(
        subset='Name', keep='first')['Category'].sum()

    # Create a dataframe of courses and sections
    # Extract unique values from each relevant column
    unique_fd_cdc = df['FD CDC'].dropna().unique()
    unique_hd_cdc = df['HD CDC'].dropna().unique()
    unique_fd_elec = df['FD Elec'].dropna().unique()
    unique_hd_elec = df['HD Elec'].dropna().unique()
    # Combine all unique course values
    unique_courses = set(unique_fd_cdc) | set(
        unique_fd_elec) | set(unique_hd_cdc) | set(unique_hd_elec)
    # Create a new DataFrame containing only the relevant courses and their sections
    filtered_courses_df = courses[courses['Courses'].isin(unique_courses)]
    ####

    # Remove extra courses
    total_sections_filtered = filtered_courses_df['Sections'].sum()
    # Drop duplicates based on the "Name" column, keeping only the first entry
    unique_names_df = df.drop_duplicates(subset='Name', keep='first')

    # Calculate the sum of elements in the "Category" column for unique names
    sum_category_unique_names = unique_names_df['Category'].sum()

    while True:
        # Extract unique values from each relevant column
        unique_fd_cdc = df['FD CDC'].dropna().unique()
        unique_hd_cdc = df['HD CDC'].dropna().unique()
        unique_fd_elec = df['FD Elec'].dropna().unique()
        unique_hd_elec = df['HD Elec'].dropna().unique()

        # Calculate the sum of "Category" for unique names
        sum_category_unique_names = df.drop_duplicates(
            subset='Name', keep='first')['Category'].sum()
        # Combine all unique course values
        unique_courses = set(unique_fd_cdc) | set(
            unique_fd_elec) | set(unique_hd_cdc) | set(unique_hd_elec)
        # Create a new DataFrame containing only the relevant courses and their sections
        filtered_courses_df = courses[courses['Courses'].isin(unique_courses)]
        total_sections_filtered = filtered_courses_df['Sections'].sum()

        if (2 * total_sections_filtered) <= sum_category_unique_names:
            break  # Break the loop if the condition is met

        num_unique_HD_Elec = len(unique_hd_elec)
        num_unique_FD_Elec = len(unique_fd_elec)

        if num_unique_HD_Elec > 0 and df['HD Elec'].notna().any():
            # Choose a random index where 'HD Elec' is not already NaN
            random_index = np.random.choice(df.index[df['HD Elec'].notna()])
            # Set the chosen value to NaN
            df.at[random_index, 'HD Elec'] = np.NaN
        elif num_unique_FD_Elec > 0 and df['FD Elec'].notna().any():
            # Choose a random index where 'FD Elec' is not already NaN
            random_index = np.random.choice(df.index[df['FD Elec'].notna()])
            # Set the chosen value to NaN
            df.at[random_index, 'FD Elec'] = np.NaN
        else:
            # Break the loop if there are no non-NaN values in 'HD Elec' or 'FD Elec'
            print("Assignment not possible , less number of professors")
            exit()
    ####

    # Merged_df
    # Group by 'Name' and calculate the size of each group
    max_rows_with_same_name = df.groupby('Name').size().max()

    # One-hot encode each specified column
    columns_to_encode = ['FD CDC', 'HD CDC', 'FD Elec', 'HD Elec']
    for column in columns_to_encode:
        one_hot_encoded = pd.get_dummies(df[column], prefix=column)
        df = pd.concat([df, one_hot_encoded], axis=1)

    # Drop the original columns after encoding
    df = df.drop(columns=columns_to_encode)

    # Declare the weight variable
    weight = 30
    # Iterate over unique names and access each row
    for name in df['Name'].unique():
        rows_for_name = df[df['Name'] == name]

        # Iterate through each row for the same name
        for i, (_, row) in enumerate(rows_for_name.iterrows()):
            # Replace occurrences of 1 in columns (excluding 'Category') with calculated value
            for col in df.columns.difference(['Category', 'Name']):
                # Explicitly convert the column to numeric before multiplication
                df.at[row.name, col] = pd.to_numeric(
                    row[col]) * ((max_rows_with_same_name - i) * weight)

    # Custom aggregation function
    def custom_agg(series):
        if series.name == 'Category':
            return series.iloc[0]
        else:
            return series.sum()

    # Group by 'Name' and aggregate using the custom function
    merged_df = df.groupby('Name').agg(custom_agg).reset_index()
    ####

    return merged_df, filtered_courses_df


###################################################################################################################################


def processMergedDF(merged_df, filtered_courses_df):
    # Extract the 'Category' column as a NumPy array
    category_vector = merged_df['Category'].values

    # Exclude 'Name' and 'Category' columns and create a 2D array
    costs = merged_df.drop(['Name', 'Category'], axis=1).values

    # Get all column headers except 'Name' and 'Category'
    headers_vector = [
        col for col in merged_df.columns if col not in ['Name', 'Category']]

    unique_names = merged_df['Name'].unique().tolist()

    # Extract course names from headers_vector and look up sections in filtered_courses_df
    sections_vector = []
    for header in headers_vector:
        course_name = header.split('_')[1]
        sections = filtered_courses_df.loc[filtered_courses_df['Courses']
                                           == course_name, 'Sections'].values
        sections_vector.extend(sections)

    return unique_names, headers_vector, costs, category_vector, sections_vector

###########################################################################################################################


# # Example usage:
# file_path = "data\Even_sem.csv"
# result_merged_df, filtered_courses_df = csvToMatrix(
#     file_path)  # Unpack the tuple
# result_names, result_headers_vector, result_costs, result_category_vector = processMergedDF(
#     result_merged_df)

# print(result_merged_df)

# # Print or use the results as needed
# print(result_costs)
# print(result_category_vector)
# print(result_headers_vector)
