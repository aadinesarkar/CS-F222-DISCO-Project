# CS F222 (Discrete Structures in Computer Science) Course Project - Integer Linear Programming
## Introduction
* This version of the code uses Integer Linear Programming to solve the problem. Bundled together in the Git repository is a Java version that uses the Hungarian Algorithm. Contact the authors if you do not find it!
* This program uses the CP-SAT solver to find all the satisfiable solutions to the problem. A version of ILP_func that used the SCIP solver is also provided for the sake of completeness. The code using SCIP solver is not used because all the solutions given during the branch-cut-and-price process of SCIP are not accessible through python or-tools wrapper.

## Declaration: `Google OR-Tools`
The Google OR-Tools software suite is used in this program (`ILP_func.py`). Google Optimization Tools (a.k.a., OR-Tools) is an open-source, fast and portable software suite for solving combinatorial optimization problems. Please find the full source code at Google's [Github repository for the same](https://github.com/google/or-tools) (retrieved 28 November 2023). It was selected because it provides software libraries and APIs for constraint optimization and linear optimization. OR-Tools is written in C++, but provide wrappers in Python, C# and Java.

## What this programme DOES
* Read a `.csv` file containing lists of course preferences for different instructors for four different types of courses: First-Degree Compulsory Discipline Courses (CDCs), Higher-Degree Compulsory Discipline Courses, First-Degree Elective Courses and Higher-Degree Elective Courses.
* Read a `.csv` file containing a list of courses, their type and the number of instructors required to take the course from 1 to 32 (inclusive).
* Writes an output into `output.csv` that contains multiple possible solutions, along with the total cost distinguishing two valid solutions, while satisfying the following **constraints**:
    * Every CDC is assigned to exactly the number of instructors required to take it.
    * Every elective course is assigned to either exactly the number of instructors required to take it, or to 0 instructors.
    * No course is only partially assigned.
    * Every instructor whose maximum load consists of half a course ("X1" in the problem statement) is assigned exactly half a course.
    * Every instructor whose maximum load consists of 1 course ("X2" in the problem statement) is assigned either half a course, one complete course, or half of one course and half of another.
    * Every instructor whose maximum load consists of 1.5 course ("X3" in the problem statement) is assigned either half a course, one complete course, half of one course and half of another, half of three courses, or half of one course and one complete other.
* The total cost for every assignment is provided along with each solution.

## Installation
We used python 3.11.0 with the following package versions: ortools=9.8.3296, pandas=2.1.3 and numpy=1.26.2  
A requirements.txt is provided for easy setup. It is recommended to create a virtual environment to install packages.
```bash
pip install ortools
```
pandas and numpy are installed as dependencies of ortools.

## Usage

### Input File Format

The code requires two files to be provided or it will fallback to the in-built defaults.

1. Courses.csv
2. Preferences.csv

#### Format of Courses.csv

| Course Code | Type    | Sections |
| ----------- | ------- | -------- |
| CS F111     | FD_CDC  | 3        |
| CS G524     | HD_CDC  | 1        |
| CS F402     | FD_Elec | 1        |
| CS G519     | HD_Elec | 1        |

_Table 1: Format of Courses.csv_

#### Format of Preferences.csv

| Name          | Category | FD CDC  | HD CDC  | FD Elec | HD Elec |
| ------------- | -------- | ------- | ------- | ------- | ------- |
| A Baskar      | 2        | CS F111 | CS G513 | CS F429 | CS G568 |
| A Baskar      | 2        | CS F211 | CS G524 | CS F430 | CS F612 |
| A Baskar      | 2        | CS F212 |         |         |         |
| A Baskar      | 2        | CS F241 |         |         |         |
| Aditya Challa | 2        | CS F211 | CS G513 | CS F431 | CS G551 |
| Aditya Challa | 2        | CS F212 | CS G524 | CS F432 | CS G553 |

_Table 2: Format of Preferences.csv_

Preferences.csv and Courses.csv can be named something else but should be given as terminal arguments in the correct order.

### Execution Instructions
* Make sure external packages are installed as given in [Installation](#installation)
* Verify that the following files are present in the working directory:
    * The following files are necessary for the execution of the programme.
        * csv_matrix.py
        * csv_saver.py
        * ILP_func.py
        * driver.py
        * HungarianAlgorithm.java
        * data\Preferences\Odd_Sem.csv
        * data\Courses\Odd_sem_courses.csv
    * Additional test cases are given in data and ronit_data folders. The ronit_data folder contains the test cases used for the Hungarian Algorithm approach.
* Make sure `output.csv` **does not exist** otherwise output will be appended at the bottom.
* Make a new `.csv` file containing course preferences anywhere you like. Preferably in a new folder.
    * In the first line of the document, a header row IS EXPECTED. Maintain format as given in [Format of Preferences.csv](#format-of-preferencescsv)
    * Each subsequent row contains -- separated by commas in the source file -- the name of an instructor, the maximum course load for an instructor, and the course codes IN ORDER for their preferred FD CDC, HD CDC, FD Elective and HD Elective.
    * There should be no whitespaces immediately before or after commas.
    * The course load must be a positive integer ≤3, where the load coming from half a course is considered to be '1' **as opposed to the problem statement and report where it is considered to be 0.5**.
        * For an instructor of category "X1" as specified in the problem statement, the course load should be 1.
        * For an instructor of category "X2" as specified in the problem statement, the course load should be 2.
        * For an instructor of category "X3" as specified in the problem statement, the course load should be 3.
    * Courses with a higher preference must be on a line above those with a lower preference.
    * No two lines of preferences for the same instructor may be separated by a line containing preferences for another instructor. Failure to satisfy this could cause the programme to crash.
    * Course codes should be unique for a given course. There is zero tolerance for error: even a difference of a whitespace or letter case will be interpreted as two separate courses.
    * Where there is no course to be entered, do not place any text (not even a whitespace) between two commas.
    * A sample of an acceptable `.csv` file for course preferences is shown in [Format of Preferences.csv](#format-of-preferencescsv).

* Create a new `.csv` file containing a list of courses anywhere you like. Preferably in the new folder you created for Preferences.csv
    * In the first line of the document, a header row IS EXPECTED. Follow format given in [Format of Courses.csv](#format-of-coursescsv)
    * Each subsequent row contains the course code, course type and number of instructors required to take the course, separated by commas.
    * There should be no whitespaces immediately before or after commas.
    * Course codes should be unique for a given course. There is zero tolerance for error: even a difference of a whitespace or letter case will be interpreted as two separate courses.
    * The course type must be exactly one of the following (note punctuation): `FD_CDC`, `HD_CDC`, `FD_Elec` or `HD_Elec`.
    * The number of sections required to take the course must be a positive integer ranging from 1 to 16 (inclusive).
    * Each section can offer a total maximum course load of 1 (0.5+0.5) according to the convention followed in the report.
    * A sample of an acceptable `.csv` file for courses is shown below for reference.

* Run driver.py as show below with paths to the created files.
```sh
#One argument (uses default courses file path)
python driver.py path/to/your/file.csv

#Two arguments (specifies both file paths)
python driver.py path/to/your/file.csv path/to/your/courses.csv
```

## Interpreting the Output
* Each column corresponds to an instructor, whose name is specified in the header.
* Every four-row sequence in the subsequent lines, beginning with the line immediately following the header, is a unique solution.
* Each appearance of a course under an instructor's name in a solution represents a load of 0.5 (according to the convention in the problem statement and report) coming to that instructor from the course.
* The fourth row of each aforementioned four-row sequence displays is a blank row to seperate the solutions.
* The first column contains the total cost of the assignment. While we use the word "cost", a higher cost indicates a higher preference for a course.
* Assignments are displayed in descending order of the total cost, the highest total cost indicating the optimal assignment.

## Testing
`sample_output.csv` contains the output for
```sh
python driver.py data\toomanyprofs_input.csv data\Odd_sem_courses.csv
```
Refer to pdf document for more testing results.
The current implementation of the program may not work for some edge cases. A fallback program is also given in the `fallback driver` folder. A limitation of the fallback program is that it does not assign CDCs not in the preference list. For e.g. it does not give any solution for emptypreflists_input.csv. You may also have to comment out the optional constraint to get a solution if no solution is given. The SCIP solver implementation is given in `SCIP solver (redundant)`.