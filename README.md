# Course Project CS F222 Fall 2023 - An Application of Graph Optimization
## Table of Contents

- [Table of Contents](#table-of-contents)
- [Authors](#authors)
- [Problem Statement](#problem-statement)
- [Features](#features)
- [Filesystem](#filesystem)
- [General Usage](#general-usage)
- [Output](#output)

## Authors
* Aadi Nesarkar (2021B3A70967G)
    * f20210967@goa.bits-pilani.ac.in
* Vrishti Godhwani (2022A7PS0260G)
    * f20220260@goa.bits-pilani.ac.in
* Ronit Kunkolienker (2022A7PS0091G)
    * f20220091@goa.bits-pilani.ac.in

## Video
* A demonstration video is available on YouTube. [Click here](https://youtu.be/BTjz53S0O6I) to watch it.

## Problem Statement

The research problem at hand involves optimization of a University Course Assignment System. Within a department, there are "n" faculty members categorised into three distinct groups: "x1," "x2," and "x3." Faculty in each category are assigned different course loads, with "x1" handling 0.5 courses per semester, "x2" taking 1 course per semester, and "x3" managing 1.5 courses per semester.

In this system, faculty members have the flexibility to take multiple courses in a given semester, and conversely, a single course can be assigned to multiple faculty members. When a course is shared between two professors, each professor's load is considered to be 0.5 courses. Moreover, each faculty member maintains a preference list of courses, ordered by their personal preferences, with the most preferred courses appearing at the top. Importantly, there is no prioritisation among faculty members within the same category.

The primary objective of this research problem is to develop an assignment scheme that maximises the number of courses assigned to faculty while aligning with their preferences and the category-based constraints ("x1," "x2," "x3").

This problem is unique due to the flexibility it offers regarding the number of courses faculty members can take, distinct from typical assignment problems.

## Features
- We have approached the problem statement using 2 methods: Integer Linear Programming (implemented in Python) and the Hungarian Algorithm (implemented in java)
- Flexible assignment system allowing each instructor to take multiple courses and each course to be assigned to multiple instructors
- Categorization of faculty into categories "x1," "x2," and "x3" based on their maximum course load
- Preference-based course assignment considering individual faculty preferences
- Optimization to maximize the number of courses assigned within category-based constraints

## Filesystem
* The repository contains two directories, each containing source code for the java Hungarian Algorithm implementation or the python Integer Linear Programming (ILP) implementation
* Each directory further has subdirectories for input and output data
* Each directory has its own README explaining the required format of input data and compilation instructions

## General Usage
* The source code and individual README for each method is in its respective directory
* Both implementations require two CSV files each to first be populated, one containing a list of instructor preferences, and the other containing the list of courses
* **The file format for each implementation is explained in its own README**
* For both implementations, some example CSV files containing various test cases explained in the report have been provided

## Output
* Both implementations generate multiple solutions to the problem
* Each solution is a list of every instructor and the courses they are assigned to
* Every solution satisfies the constraints in the problem statement, as interpreted in the project report
* Solutions are ranked in ascending order of _total cost_ and descending order of _metric_ as explained in the project report
