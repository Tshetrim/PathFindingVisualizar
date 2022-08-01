## Pathfinding Visualizer

## Installation Instruction

Download and run .jar file executable 

##Program usage notes

##Future ideas to possibly implement
- have a portal node that can allow the searching node to teleport to another portal node in the graph
- figure out a way to implement weights into the grid, possibly with a type of node that if held over for a period of time causes the distance to be higher by making the cell a darker color. 
- multi end node targeting
- improved cell coloring. 
- search direction chaning (drag and move order of direction searched)

## Algo Notes

The current structure of the visualizer is such that graph is unweighted. This means that every node next to a certain node is guaranteed to always be the same distance from that node.
The Breath-First Search works in this case because the graph is unweighted. With the assumption that the next node is guaranteed to be that distance from the start, the BFS results are accurate.
In the case this was weighted graph, ex. the start node's neighbours are not 1 unit away, but one is 4 units away, one is 1 unit away, and another is 20 units away, Djikstras Algorithm would be needed.

The other result that is quite crucial to see is that although the A* Pathfinding Algorithm can ususally get to the end node in significatly less space usage than the BFS, the A* Algo uses a PriorityQueue as opposed to a regular queue by the BFS. When adding a new node to a priority queue, it needs to be percolated up or down such that the head node is always the closest node to the end point. This
operation take log(n) time whereas the simple node enqueue in BFS is O(1) time. The other disadvantage of A* against BFS is that the shortest root is not always found. 
See: http://theory.stanford.edu/~amitp/GameProgramming/Heuristics.html

The advantage of A* is that it works even with weighted graphs. In that scenario, the A* almost always has a little O faster than Djikstras as they both utilize PriorityQueues but A* has the
optimizing heuristic of going straight towards the target node. Although this is just a heuristic, and therefore there are scenarios in which Dijkstras with its comprehensive search provide a shorter root. 

Also, the heuristic used in the program is the air-line distance (euclidean distance) heuristic which calcualtes a g,h, and f cost between the star, end, and current node. 
See: https://www.wiwi.uni-kl.de/bisor-orwiki/Heuristics:_A*-algorithm_1#:~:text=The%20A*%20(or%20A%20star,to%20find%20the%20optimal%20solution.


