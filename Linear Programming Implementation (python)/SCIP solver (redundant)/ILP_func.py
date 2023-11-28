from ortools.linear_solver import pywraplp
import csv_saver


def ILP_solve(costs, names, categories, courses):

    for i in range(len(costs)):
        costs[i] = costs[i] + costs[i]
    courses.extend(courses)
    # print(costs)
    num_professors = len(costs)
    num_courses = len(costs[0])
    print(num_courses, num_professors)

    # Solver
    # Create the mip solver with the SCIP backend.
    solver = pywraplp.Solver.CreateSolver("SCIP")

    if not solver:
        return

    # Variables
    # x[i, j] is an array of 0-1 variables, which will be 1
    # if professor i is assigned to task j.
    x = {}
    for i in range(num_professors):
        for j in range(num_courses):
            x[i, j] = solver.IntVar(0, 1, "")

    # Constraints
    # categories = [2, 2, 2, 1, 2, 2, 3, 2, 3, 2, 2, 2, 3,
    #               2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2]
    print(len(categories))
    category_count = 0
    # Each prof in x1 is assigned to at most category task.
    for i in range(num_professors):
        solver.Add(solver.Sum([x[i, j] for j in range(
            num_courses)]) <= categories[category_count])
        category_count += 1

    # Each half course is assigned to exactly one professor.
    for j in range(num_courses):
        solver.Add(solver.Sum([x[i, j] for i in range(num_professors)]) == 1)

    # Objective
    objective_terms = []
    for i in range(num_professors):
        for j in range(num_courses):
            objective_terms.append(costs[i][j] * x[i, j])
    solver.Maximize(solver.Sum(objective_terms))

    # Solve
    print(f"Solving with {solver.SolverVersion()}")
    status = solver.Solve()

    # Print solution.
    if status == pywraplp.Solver.OPTIMAL or status == pywraplp.Solver.FEASIBLE:
        print(f"Total cost = {solver.Objective().Value()}\n")
        for i in range(num_professors):
            for j in range(num_courses):
                # Test if x[i,j] is 1 (with tolerance for floating point arithmetic).
                if x[i, j].solution_value() > 0.5:
                    print(f"{names[i]} assigned to {courses[j]}." +
                          f" Cost: {costs[i][j]}")
                    csv_saver.dict_append(str(names[i]), str(courses[j]))
        csv_saver.save()

        while solver.NextSolution():
            print(f"Total cost = {solver.Objective().Value()}\n")
            for i in range(num_professors):
                for j in range(num_courses):
                    # Test if x[i,j] is 1 (with tolerance for floating point arithmetic).
                    if x[i, j].solution_value() > 0.5:
                        print(f"{names[i]} assigned to {courses[j]}." +
                              f" Cost: {costs[i][j]}")
                        csv_saver.dict_append(str(names[i]), str(courses[j]))
            csv_saver.save()

    else:
        print("No solution found.")


if __name__ == "__main__":
    # Data
    costs = [[0,   0,   0,   0, 120,  90,  60,  30,   0,   0,   0,   0,   0,
              0, 120,  90,  60,   0, 120,  90,   0,   0,   0,   0,   0,   0,
              0,   0,   0,   0,   0],
             [0,   0,   0,   0,   0, 120,  90,  60,   0,  30,   0,   0,   0,
             0,  60,  90, 120,   0,   0,   0,   0, 120,  90,   0,   0,   0,
             0,   0,   0,   0,   0],
             [0,   0,   0,  30,   0,   0,   0, 120,   0,  90,  60,   0,   0,
             0,  90, 120,  60,   0,   0,   0,  90, 120,   0,   0,   0,   0,
             0,   0,   0,   0,   0],
             [0,   0,   0,   0,  60, 120,  90,  30,   0,   0,   0,   0,   0,
             0, 120,  90,  60,   0,   0,   0,   0,   0,   0,   0,   0,   0,
             0, 120,  90,   0,   0],
             [0,   0,   0,  60,   0,   0,   0,   0,   0, 120,  90,   0,  30,
             0,  90,  60, 120,   0,   0,   0,   0,   0,   0,   0,   0, 120,
             90,   0,   0,   0,   0],
             [0,   0,   0,   0,  90,   0,  30,   0,   0, 120,   0,  60,   0,
             0,  90, 120,  60,   0,   0,   0,   0,   0, 120,  90,   0,   0,
             0,   0,   0,   0,   0],
             [0,   0,   0, 120,   0,  90,   0,  60,   0,  30,   0,   0,   0,
             0, 120,  90,  60,   0,   0,   0,   0,   0,   0,   0,   0,   0,
             0, 120,  90,   0,   0],
             [0,   0,   0,   0, 120,  90,  60,  30,   0,   0,   0,   0,   0,
             0,  60, 120,  90,   0,   0,   0,   0,   0,   0, 120,  90,   0,
             0,   0,   0,   0,   0],
             [0,   0,   0,  30,   0,  60,   0, 120,   0,   0,  90,   0,   0,
             0,  60,  90, 120,   0,   0,   0,   0,   0,   0,   0,   0,   0,
             0,   0, 120,  90,   0],
             [0,   0, 120,   0,  30,  90,   0,  60,   0,   0,   0,   0,   0,
             0,  90, 120,  60,   0,   0,   0,   0,   0, 120,  90,   0,   0,
             0,   0,   0,   0,   0],
             [0,   0,   0,   0,  60,   0,  30,   0,   0,  90,   0,   0, 120,
             0,  90,  60, 120,   0, 120,  90,   0,   0,   0,   0,   0,   0,
             0,   0,   0,   0,   0],
             [0,   0,   0,   0,   0,   0, 120,  90,   0,  60,  30,   0,   0,
             0, 120,  90,  60,   0,   0,   0,   0,   0,   0,  90,   0,   0,
             0,   0,   0, 120,   0],
             [0,   0,   0,  90,  30,   0,   0,   0,   0,   0, 120,   0,  60,
             0,  90, 120,  60,   0,   0,   0,   0,   0,   0,   0,   0,   0,
             0, 120,  90,   0,   0],
             [0,   0,   0,   0,  90,   0,  30,   0,   0, 120,   0,   0,  60,
             0,  90,  60, 120,   0,   0,   0,   0,   0,   0,   0,  90,   0,
             0,   0, 120,   0,   0],
             [0,   0,   0,   0,  60,  90,   0,  30,   0,   0, 120,   0,   0,
             0,  90, 120,  60,   0,   0,   0,   0,   0,   0, 120,   0,   0,
             0,   0,  90,   0,   0],
             [0,   0,   0,  30,  60,   0,   0, 120,   0,   0,  90,   0,   0,
             0,  90,  60, 120,   0, 120,  90,   0,   0,   0,   0,   0,   0,
             0,   0,   0,   0,   0],
             [0,   0,   0, 120,   0,   0,   0,  90,   0,  30,  60,   0,   0,
             0,  90,  60, 120,   0,   0,   0,   0,   0,   0,   0,   0,   0,
             0,   0,   0, 120,  90],
             [0,   0,   0,   0,  60,  90,   0,  30,   0,   0, 120,   0,   0,
             0,  90,  60, 120,   0, 120,   0,   0,   0,   0,   0,   0,   0,
             0,   0,   0,   0,  90],
             [0,   0,   0,  30,  60,  90,   0, 120,   0,   0,   0,   0,   0,
             0,  90, 120,  60,   0,   0,   0,   0,   0,   0,   0,  90, 120,
             0,   0,   0,   0,   0],
             [0,   0,   0, 120,   0,  90,   0,  60,   0,  30,   0,   0,   0,
             0, 120,  90,  60,   0,   0,   0,   0,   0,   0,  90,   0,   0,
             0,   0,   0, 120,   0],
             [0,   0,   0,   0,  60,  90, 120,  30,   0,   0,   0,   0,   0,
             0,  90,  60, 120,   0,   0,   0,   0,   0,   0, 120,   0,   0,
             0,   0,   0,   0,  90],
             [0,   0,   0,   0,  60,   0,  90,  30,   0,   0, 120,   0,   0,
             0, 120,  90,  60,   0,   0,   0,   0,   0,   0,   0,   0,   0,
             0,   0,   0, 120,  90],
             [30,   0,   0,   0,  60,   0,   0,  90,   0,   0,   0, 120,   0,
             90,   0, 120,   0,  60,   0,   0,   0,   0,   0,   0,   0, 120,
             0,   0,   0,   0,  90],
             [0,   0,   0,   0,  60,  90,   0,  30,   0,   0, 120,   0,   0,
             0, 120,  90,  60,   0, 120,   0,   0,   0,   0,   0,   0,   0,
             0,   0,   0,   0,  90],
             [0,   0,   0, 120,   0,  90,   0,  60,   0,  30,   0,   0,   0,
             0,  90,  60, 120,   0,   0,   0,   0,   0,   0, 120,   0,   0,
             0,   0,   0,  90,   0],
             [0,   0,   0,   0,  60,   0,   0,  30,   0,   0, 120,  90,   0,
             0, 120,  60,  90,   0,   0,   0,   0,   0,   0, 120,   0,   0,
             0,   0,  90,   0,   0],
             [0, 120,   0,   0,   0,  90,   0,  60,   0,  30,   0,   0,   0,
             0, 120,  90,  60,   0, 120,   0,   0,   0,   0,  90,   0,   0,
             0,   0,   0,   0,   0],
             [0,   0,   0,   0,  90,   0,  30,   0,   0,   0,   0,  60, 120,
             0,  90,  60, 120,   0,  90,   0,   0,   0,   0,   0, 120,   0,
             0,   0,   0,   0,   0],
             [0,   0,   0,   0,   0,   0,   0,  30,  60,   0,  90,   0, 120,
             0, 120,  90,  60,   0,   0,   0,   0,   0,   0,   0,   0,   0,
             0,   0,  90,   0, 120],
             [0,   0,   0,  30,   0,  60,   0, 120,   0,   0,  90,   0,   0,
             0,  90,  60, 120,   0,   0,   0,   0,   0,   0,   0,  90,   0,
             0,   0,   0,   0, 120]]
    names = ['A Baskar', 'Aditya Challa', 'Arnab Kumar Paul',
             'Ashwin Srinivasan', 'Basabdatta Bhattacharya',
             'Bharat  Deshpande', 'Biju Nair', 'Devashish Gosain',
             'Diptendu Chatterjee', 'Gargi Prabhu', 'Harikrishan N.B.',
             'Hemant Rathore', 'Kanchan Manna', 'Kunal Korgaonkar',
             'Neena Govias', 'Rajesh Kumar', 'Ramprasad Joshi', 'Sanjay Sahay',
             'Santonu Sarkar', 'Shubhangi Gawali', 'Siddharth Gupta',
             'Snehanshu Saha', 'Sougata Sen', 'Sravan Danda', 'Sujith Thomas',
             'Surjya Ghosh', 'Swaroop Joshi', 'Swati Agarwal',
             'Tanmay Verlekar', 'Vinayak Naik ']
    categories = [2, 2, 2, 1, 2, 2, 3, 2, 3, 2, 2, 2, 3,
                  2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2]
    courses = ['FD CDC_CS F214', 'FD CDC_CS F215', 'FD CDC_CS F301', 'FD CDC_CS F351', 'FD CDC_CS F213', 'FD CDC_CS F214', 'FD CDC_CS F215', 'FD CDC_CS F222', 'FD CDC_CS F251', 'FD CDC_CS F301', 'FD CDC_CS F342', 'FD CDC_CS F351', 'FD CDC_CS F372', 'HD CDC_CS G523', 'HD CDC_CS G525',
               'HD CDC_CS G526', 'HD CDC_CS G623', 'HD CDC_CS G625', 'FD Elec_CS F314', 'FD Elec_CS F315', 'FD Elec_CS F316', 'FD Elec_CS F317', 'FD Elec_CS F320', 'FD Elec_CS F401', 'FD Elec_CS F402', 'FD Elec_CS F407', 'FD Elec_CS F413', 'FD Elec_CS F415', 'FD Elec_CS F422', 'FD Elec_CS F424', 'FD Elec_CS F425']
    ILP_solve(costs, names, categories, courses)
