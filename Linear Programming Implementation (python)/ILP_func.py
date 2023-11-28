from ortools.sat.python import cp_model
import csv_saver


class SolutionPrinter(cp_model.CpSolverSolutionCallback):
    def __init__(self, variables, num_professors, num_courses, costs, names, courses):
        cp_model.CpSolverSolutionCallback.__init__(self)
        self.__variables = variables
        self.__num_professors = num_professors
        self.__num_courses = num_courses
        self.__costs = costs
        self.__solution_count = 0
        self.__names = names
        self.__courses = courses
        self.__total_cost = 0

    def OnSolutionCallback(self):
        self.__solution_count += 1
        print(f'Solution {self.__solution_count}:')
        for i in range(self.__num_professors):
            for j in range(self.__num_courses):
                if self.Value(self.__variables[i][j]) == 1:
                    print(
                        f'{self.__names[i]} is assigned to {self.__courses[j]}. Cost = {self.__costs[i][j]}')
                    self.__total_cost += self.__costs[i][j]
                    csv_saver.dict_append(
                        str(self.__names[i]), str(self.__courses[j]))
        print(self.__total_cost)
        csv_saver.save(self.__total_cost)
        self.__total_cost = 0
        print()

    def solution_count(self):
        return self.__solution_count


def ILP_solve(costs, names, categories, courses, sections):

    costs = costs.tolist()
    # Some courses like CS F111 have more than one section so more than two professors can be assigned.
    for i in range(len(sections)):
        while (sections[i] != 1):
            courses.append(courses[i])
            for j in range(len(costs)):
                # np.append(costs[j], costs[j][i])
                costs[j].append(costs[j][i])
            sections[i] -= 1

    # Doubling the number of courses since each course can be taken by two professors max.
    for i in range(len(costs)):
        costs[i] = costs[i] + costs[i]
    courses.extend(courses)
    print(courses)

    num_professors = len(costs)
    num_courses = len(costs[0])
    print(num_courses, num_professors)

    # Model
    # Create the cp_model.
    model = cp_model.CpModel()

    # Variables
    # x[i][j] is an array of 0-1 variables, which will be 1
    # if professor i is assigned to task j.
    x = []
    for i in range(num_professors):
        x.append([])
        for j in range(num_courses):
            x[i].append(model.NewBoolVar(f'x[{i},{j}]'))

    # Constraints
    # Each prof is assigned to at most number of courses in categories.
    for i in range(num_professors):
        model.Add(sum(x[i][j] for j in range(num_courses)) <= categories[i])

    # (Optional Constraint) Each prof is assigned minimum 1 course
    for i in range(num_professors):
        model.Add(sum(x[i][j] for j in range(num_courses)) >= 1)

    # Each half course is assigned to exactly one professor.
    for j in range(num_courses):
        model.Add(sum(x[i][j] for i in range(num_professors)) == 1)

    # Maximizes the preference of assignment
    model.Maximize(sum(x[i][j] * costs[i][j]
                   for i in range(num_professors) for j in range(num_courses)))

    # Solve
    solver = cp_model.CpSolver()
    solver.parameters.enumerate_all_solutions = True

    solution_printer = SolutionPrinter(
        x, num_professors, num_courses, costs, names, courses)
    status = solver.Solve(model, solution_printer)
