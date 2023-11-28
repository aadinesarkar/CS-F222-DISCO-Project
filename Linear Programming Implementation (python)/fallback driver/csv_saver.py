import pandas as pd
from collections import defaultdict
import os
name_courses_dict = defaultdict(list)


def dict_append(name, course):

    name_courses_dict[name].append(course)


def save(total_cost: int):
    name_courses_dict["Total cost"] = [total_cost]
    for key, value in name_courses_dict.items():
        while len(value) < 3:
            value.append('')
    if (os.path.exists('output.csv')):
        df1 = pd.read_csv('output.csv')
        df2 = pd.DataFrame.from_dict(
            name_courses_dict)
        empty_df = pd.DataFrame([[None]*len(df1.columns)], columns=df1.columns)
        df3 = pd.concat([df1, empty_df, df2])
        df3.to_csv('output.csv', index=False)
    else:
        df = pd.DataFrame.from_dict(
            name_courses_dict)
        col = df.pop('Total cost')
        df.insert(0, col.name, col)
        df.to_csv('output.csv', index=False)
    name_courses_dict.clear()
