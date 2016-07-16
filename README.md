# About
A solution to Vehicle Routing Problem with Due Dates (VRPD) using Branch and Bound

# Todo
- Add time to the cost.
- Fix vehicle start time.
- Write a addedTime and addedPenalty function for BBNode (Use it for showing service time and penalty taken for each node)
- Improve printPath function show service times, and penalties on that vertex
- Add lower bound to the branch and bound


# Done
- Improve the printPath function
- Move the redundant adding to priority queue code to a subroutine

# Ideas
- Use knapsack for lower bound
- Use vehicles (and their paths) to produce branch and bound nodes