README Street Mapping Project

Grayson Westfall
EMAIL: graywestfall@gmail.com

This project reads an input file formatted like the included text files (ur.txt, monroe.txt) to create an undirected graph.  The included files are formatted with each line as such:

if the line denotes an intersection (vertex of the graph):
i name_of_intersection latitude_val longitude_val

if the line denotes a road (edge of the graph):
r name_of_road name_of_intersection1 name_of_intersection2




The program works through command-line arguments:

The first argument should always be the name of the text file from which it is reading.  

The next argument can then be [-show] (brackets included), which will display a map of the graph that was constructed, or this argument can be omitted.  

Then it may be followed by the argument [-directions name_of_start_intersection name_of_end_intersection] (which will calculate the shortest path along the graph's edges from the start intersection to the end intersection using Dijkstra's algorithm if such a path exists).  It will print the edges it takes and their distances as well as the total distance of this path, and if the [-show] argument has been included, it will highlight this path on the map in red.

The above directions argument may be replaced by the [-meridianmap] argument, which will calculate the minimum spanning tree (MST) of the constructed graph, printing the edges included in the MST.  If the [-show] argument has been selected, the MST will be highlighted on the map in red.  
NOTE:  This argument will only work for ur.txt graph, as the monroe.txt graph is not completely connected and as such have no existing MST.
NOTE2: This argument and the directions argument may not be used in conjunction, as if the map is displayed, the highlighting of the map for one part may conflict with the highlighting for the other part.


The project contains the following classes that were used to help with the main algorithms
(each has commentary in the code that goes more in depth about their implementation):

LinkedList.java            : used in my implementation of the hash table; superclass of EdgeStack.java
EdgeStack.java             : stack of edges; used to hold edges from shortest path algorithm
Bag.java                   : bag class used in multiple classes to hold a set of edges
IndexMinPriorityQueue.java : index minimum priority queue; used in computation of shortest path / MST algorithm
Intersection.java          : class that represents the vertices of the graph
StreetEdge.java            : class that represents the edges of the graph

The following classes account for major portions of the project / algorithm work
(these also have commentary in the code for more in-depth detail):

UndirectedGraph.java
--------------------
The UndirectedGraph class has a constructor used to create the structure of the graph, using a hash table (comprised of an array of LinkedLists of size 1009 for separate chaining) for the vertices/intersections and a bag for the edges. Creates instances of all the following classes to perform the algorithms and display the graph

Helper methods:

calcDistanceMiles: calculates distance in miles between two latitude/longitude points
hashTitle        : calculates the hash value for the vertices using its String name


DijkstraShortestPath.java
-------------------------
This class computes the shortest paths from any intersection to all other intersections. The constructor computes all these values and stores them in a distance array. 

Helper methods:

relax    : used heavily in the computation of the algorithm; explained more in-detail in code commentary
hasPathTo: a private version of the other public method to avoid having to switch back and forth between String names and integers
hashTitle: the identical hash function from the UndirectedGraph class used to help find the right intersections


MapMST.java
-----------
This class computes the minimum spanning tree for a graph (given that the graph is completely connected). As with the other classes, the constructor computes the bulk of the algorithm.

The helper method, visit, similarly to in the shortest paths class, is used heavily in the computation of the algorithm.


MapFrame.java
-------------
This class creates the visual portion of the program and extends the JFrame class. This class mostly functions as a shell for the nested private class MapComponent that extends the JPanel class.

Helper methods (within MapComponent):

drawStreets     : called within the paintComponent method to draw all the edges of the graph (making them red if they are part of a shortest path / MST that has been computed)
addStreets      : adds latitude/longitude values from edges to a 2D array of coordinates to be used for the drawStreets method
longitudeConvert: converts degree longitude values to radians
latitudeConvert : converts degree latitude values to radians

As for the runtime of the program, the main portions to be covered will be the constructors in the UndirectedGraph, DijkstraShortestPath, and MapMST classes, as those do the significant portions of algorithm work that will account for the vast majority of the runtime.


UndirectedGraph constructor:

The constructor places all vertices into the hash table using a hash function that iterates through characters of each intersection's title, which should take, on average, time proportional to V, the number of vertices. Then for the edges, the program seeks out both vertices the edge is connected to in the hash table, which should take on average 2 * V/1009 for each edge (where 1009 is the number of buckets in my hash table), but may be essentially constant time where numbers of vertices are small and collisions in the hash table were infrequent/nonexistent.  Overall, this constructor should take time proportional to about V + (2EV)/1009.


DijkstraShortestPath constructor: 

The constructor creates a new array of all the vertices in the original graph for quicker access, which takes time proportional to V. As stated in the title of the class, it implements Dijkstra's shortest paths algorithm (the eager version), which takes time proportional to E log V in the worst case. So this constructor should take time proportional to roughly V + E log V.


MapMST constructor:

The constructor, like with the previous algorithm, replaces all vertices into a new array, taking time proportional to V. The class implements the eager version of Prim's MST algorithm, which takes time proportional to E log V.  So this constructor should also take time proportional to roughly V + E log V.