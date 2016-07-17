# About
A solution to Vehicle Routing Problem with Due Dates (VRPD) using Branch and Bound

# Ideas
- Use knapsack for lower bound of vehicle need for remaining customers
- Use Vehicle needed for remaining customers to peek lowest time needed to go from depot and comeback.
- Use vehicles (and their paths) to produce branch and bound nodes (for VRPTW)


# Todo
- Add Last Depot Due Date
- Add calculation time to the main function
- Add a simple lower bound to the branch and bound
- Implement Knapsack for lower bound.
- if remaining vehicles is less than needed prune it
- Use Vehicle needed to peek lowest time needed to go from depot and comeback.

# Done
- Remove early arriving penalty
- Fix vehicle start time, timeElapsed, arrival time, ...
- Improve printPath function show service times, and penalties on that vertex
- Improve the printPath function
- Move the redundant adding to priority queue code to a subroutine
- Add time to the cost.