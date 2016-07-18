# About
A solution to Vehicle Routing Problem with Due Dates (VRPD) using Branch and Bound

# Ideas
- Use Vehicle needed for remaining customers to peek lowest time needed to go from depot and comeback.
- Use vehicles (and their paths) to produce branch and bound nodes (for VRPTW)

# Todo
- Use Vehicle needed to peek lowest time needed to go from depot and comeback.

# Done
- if remaining vehicles is less than needed prune it
- Add a algorithm for minimum cars needed
- Add a simple lower bound to the branch and bound
- Add calculation time to the main function
- Add Last Depot Due Date
- Add a stub function for lower bound
- Remove early arriving penalty
- Fix vehicle start time, timeElapsed, arrival time, ...
- Improve printPath function show service times, and penalties on that vertex
- Improve the printPath function
- Move the redundant adding to priority queue code to a subroutine
- Add time to the cost.