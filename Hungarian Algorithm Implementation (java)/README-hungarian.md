# CS F222 (Discrete Structures in Computer Science) Course Project - Hungarian
## Introduction
* This version of the code uses the Hungarian algorithm to solve the problem. Bundled together in the Git repository is a Python version that uses Integer Linear Programming. Contact the authors if you do not find it!
* The Hungarian Algorithm as designed allows for only one-one matchings and no constraints. This programme uses the algorithm in succession to create one-many and many-one matchings while enforcing the constraints on every solution. See the javadoc comments in the source code for detailed explanations.

## Declaration: `HungarianAlgorithm.java`
The version of the Hungarian Algorithm used in this programme (`HungarianAlgorithm.java`) is a free-use source code supplied by Kevin L. Stern. Please find the full source code at Kevin L. Stern's [Git repository for the same](https://github.com/KevinStern/software-and-algorithms/blob/master/src/main/java/blogspot/software_and_algorithms/stern_library/optimization/HungarianAlgorithm.java) (retrieved 21 November 2023). It was selected because it claims to run in cubic time as opposed to many others that run in quartic time.

## What this programme DOES
* Read a `.csv` file containing lists of course preferences for different instructors for four different types of courses: First-Degree Compulsory Discipline Courses (CDCs), Higher-Degree Compulsory Discipline Courses, First-Degree Elective Courses and Higher-Degree Elective Courses.
* Read a `.csv` file containing a list of courses, their type and the number of sections in the course, from 1 to 16 (inclusive).
* Writes an output into a CSV file that contains multiple possible solutions, along with the corresponding values of a metric distinguishing two valid solutions, while satisfying the following **constraints**:
    * Every CDC is assigned to exactly the number of instructors required to take it.
    * Every elective course is assigned to either exactly the number of instructors required to take it, or to 0 instructors.
        * No course is only partially assigned.
    * Every instructor whose maximum load consists of half a course ("X1" in the problem statement) is assigned exactly half a course.
    * Every instructor whose maximum load consists of 1 course ("X2" in the problem statement) is assigned either half a course, one complete course, or half of one course and half of another.
    * Every instructor whose maximum load consists of 1.5 course ("X3" in the problem statement) is assigned either half a course, one complete course, half of one course and half of another, half of three courses, or half of one course and one complete other.
* See the report for details on how the metric printed under each solution is calculated.

## Compilation Instructions
* Please be sure to have a recent version of the [Java Runtime Environment](https://www.oracle.com/java/technologies/downloads/) installed.
* Verify that the following files are present in the working directory:
    * The following files are necessary for the execution of the programme.
        * Driver.java
        * Prof.java
        * Courses.java
        * CourseTypes.java
        * HungarianAlgorithm.java
        * /data/preferences/sample.csv
        * /data/courses/sample_courses.csv
    * The following files are not essential, but are test cases for the [examples](#examples) section and are used in the Testing section of the final report.
        * /data/preferences/oddsem.csv
        * /data/courses/oddsem_courses.csv
        * /data/preferences/toomanyprofs.csv
        * /data/preferences/toofewprofs.csv
        * /data/preferences/shortpreflists.csv
* In `/data/preferences`, maintain a new `.csv` file containing course preferences.
    * In the first line of the document, a header row IS EXPECTED. This can be anything you like (**not the case with the ILP implementation**).
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
    * A sample of an acceptable `.csv` file for course preferences is shown below for reference.

| Name                   | Category | FD CDC  | HD CDC  | FD Elec | HD Elec |
| ---------------------- | -------- | ------- | ------- | ------- | ------- |
| Dr A Baskar            | 2        | CS F111 | CS G513 | CS F429 | CS G568 |
| Dr A Baskar            | 2        | CS F211 | CS G524 | CS F430 | CS F612 |
| Dr A Baskar            | 2        | CS F212 |         |         |         |
| Dr Aditya Challa       | 2        | CS F211 | CS G513 | CS F431 | CS G551 |
| Dr Aditya Challa       | 2        | CS F212 | CS G524 | CS F432 | CS G553 |
| Dr Arnab Kumar Paul    | 2        | CS F111 | CS G513 | CS F432 | CS G541 |
| Dr Arnab Kumar Paul    | 2        | CS F211 | CS G524 | CS F433 | CS G551 |
| Dr Arnab Kumar Paul    | 2        | CS F303 |         |         |         |
| Dr Arnab Kumar Paul    | 2        | CS F363 |         |         |         |
| Prof Ashwin Srinivasan | 1        | CS F241 | CS G524 | CS F429 | CS G523 |
| Prof Ashwin Srinivasan | 1        | CS F303 | CS G513 | CS F446 | CS G516 |
| Prof Ashwin Srinivasan | 1        | CS F363 |         |         |         |
_An example of a preference input CSV_

* In `/data/courses`, maintain a new `.csv` file containing a list of courses.
    * In the first line of the document, a header row IS EXPECTED. This can be anything you like (**not the case with the ILP implementation**).
    * Each subsequent row contains the course code, course type and number of instructors required to take the course, separated by commas.
    * There should be no whitespaces immediately before or after commas.
    * Course codes should be unique for a given course. There is zero tolerance for error: even a difference of a whitespace or letter case will be interpreted as two separate courses.
    * The course type must be exactly one of the following (note punctuation): `FD_CDC`, `HD_CDC`, `FD_Elec` or `HD_Elec`.
    * The number of sections required to take the course must be a positive integer ranging from 1 to 16 (inclusive).
    * Each section can offer a total maximum course load of 1 (0.5+0.5) according to the convention followed in the report.
    * A sample of an acceptable `.csv` file for courses is shown below for reference.

| Course code | Type    | Sections |
| ----------- | ------- | -------- |
| CS F111     | FD_CDC  | 3        |
| CS F211     | FD_CDC  | 1        |
| CS G513     | HD_CDC  | 1        |
| CS F314     | FD_Elec | 1        |
| CS G516     | HD_Elec | 1        |
_An example of a course list CSV_

* Compile all java source files in the directory together using the following command at the terminal:
```sh
$ javac *.java
```
* Run the Driver class of the programme using the following command at the terminal. Substitute `$inputfilename` for the name of the CSV file you just created in `/data/preferences` containing the list of course preferences, and `$coursesfilename` for the name of the CSV file in `/data/courses` containing the list of courses
```sh
$ java Driver $inputfilename.csv $coursesfilename.csv
```
* If there are no exceptions, `Success!` is printed at the console along with the output filepath.
* FOR CS-IS DEPARTMENT ONLY: For your convenience, a CSV containing a list of courses offered by the CS-IS department in an EVEN semester has been made at `data/courses/sample_courses.csv`. To use this with your input file, run the following command at the terminal. Substitute `$inputfilename` for the name of the CSV file you just created in `/data/preferences/` containing the list of instructors with their course preferences.
```sh
$ java Driver $inputfilename.csv #if no second argument provided, courses taken from sample_courses.csv (even sem) by default
```
* FOR TESTING ONLY: Additionally to the previous point, a sample CSV (`data/preferences/sample.csv`) containing a list of CS-IS department instructors with sample preferences for courses offered in an even semester has been made. To use this TEST CASE, run the following command at the terminal.
```sh
$ java Driver #if no arguments provided, instructors taken from sample.csv and courses taken from sample_courses.csv (even sem) by default
```
* Example: an additional test case (provided) has `/data/preferences/oddsem.csv` and `/data/courses/oddsem_courses.csv` containing the list of CS-IS department preferences and sample instructor preferences respectively for an odd semester. To use this, run:
```sh
$ java Driver oddsem.csv oddsem_courses.csv
```
## Interpreting the Output
* Each column corresponds to an instructor, whose name is specified in the header.
* Every four-row sequence in the subsequent lines, beginning with the line immediately following the header, is a unique solution.
* Each appearance of a course under an instructor's name in a solution represents a load of 0.5 (according to the convention in the problem statement and report) coming to that instructor from the course.
* The fourth row of each aforementioned four-row sequence displays the following metric (see report for more detailed explanation):
    * ∀`instructor` ∑ ∀`course` assigned to `instructor` ∑`f(course)`, where  
        * `f(course)` is defined as (`n`-index of `course` in the preference list of its type for `instructor`), if it is found in the preference list, or -1 otherwise.
        * `n` is the maximum length of a preference list of any type of course for any instructor.
* Assignments are displayed in descending order of the metric, the highest metric indicating the optimal assignment.

## Examples
Let's take a look at some examples.
* `toofewprofs_input.csv` contains a subset of CS-IS department instructors whose total maximum load is not sufficient to satisfy that required by all even-semester CDCs listed in `sample_courses.csv`. Try running:
```sh
$ java Driver toofewprofs.csv #if no second argument provided, courses taken from sample_courses.csv (even sem) by default
```
java throws an ArrayIndexOutOfBoundsException as the programme runs out of free instructors before all CDCs can be assigned, and calls the Hungarian algorithm with a cost matrix with no rows.
* Try finding solutions for assigning even-semester courses to instructors who have provided preferences for an odd semester.
```sh
$ java Driver oddsem.csv #if no second argument provided, courses taken from sample_courses.csv (even sem) by default
```
The metric values written to the output file are very poor as most of the courses assigned to instructors are not on their preference lists.
* `/data/preferences/toomanyprofs.csv` contains a list of instructors that is so large that the total maximum load all instructors can take is more than the total load from ALL courses in `sample_courses.csv`. Try running:
```sh
$ java Driver toomanyprofs.csv #if no second argument provided, courses taken from sample_courses.csv (even sem) by default
```
No assignments are printed to the output file as every potential assignment would violate the constraint that no instructor be left without a course.
* `/data/preferences/emptypreflists.csv` contains a list of instructors whose preference lists are empty. Try running:
```sh
$ java Driver emptypreflists.csv #if no second argument provided, courses taken from sample_courses.csv (even sem) by default
```
The metric values written to the output file are highly negative as every course assigned to an instructor is not on their preference list.
* `/data/preferences/shortpreflists.csv` contains a list of instructors whose preference lists are of mixed length and shorter than usual. Try running:
```sh
$ java Driver shortpreflists.csv #if no second argument provided, courses taken from sample_courses.csv (even sem) by default
```
The metric values are lower than usual because fewer courses are on the instructors' preference lists.