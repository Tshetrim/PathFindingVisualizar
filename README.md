## Pathfinding Visualizer

## Installation Instruction

Download and run .jar file executable

## Algo Notes

The current structure of the visualizer is such that graph is undirected. This means that every node next to a certain node is guaranteed to always be the same distance from that node.
The Breath-First Search works in this case because the graph is undirected. With the assumption that the next node is guaranteed to be that distance from the start, the BFS results are accurate.
In the case this was directed graph, ex. the start node's neighbours are not 1 unit away, but one is 4 units away, one is 1 unit away, and another is 20 units away, Djikstras Algorithm would be needed.

The other result that is quite crucial to see is that although the A* Pathfinding Algorithm can ususally get to the end node in significatly less space usage than the BFS, the A* Algo uses a PriorityQueue as opposed to a regular queue by the BFS. When adding a new node to a priority queue, it needs to be percolated up or down such that the head node is always the closest node to the end point. This
operation take log(n) time whereas the simple node enqueue in BFS is O(1) time.

The advantage of A* is that it works even with directed graphs. In that scenario, the A* almost always have a little O faster than Djikstras as they both utilize PriorityQueues but A\* has the
optimizing of going straight towards the target node.
