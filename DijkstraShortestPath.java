/* Code & Commentary by Grayson Westfall */

import java.awt.Color;

/* Class used to compute all shortest paths
 * from a given starting intersection */
public class DijkstraShortestPath {
	
	protected StreetEdge[] edgeTo;				// edgeTo[i] is the edge leading to intersection number i on the shortest path to it
	protected double[] distanceTo;				// distanceTo[i] is the distance of the shortest path from the start to intersection number i
	protected Intersection[] allIntersections;  // array of all intersections of the graph
	protected IndexMinPriorityQueue<Double> pq; // IndexMinPriorityQueue used in algorithm to calculate shortest paths
	
	// Constructor that calculates all shortest paths when called
	public DijkstraShortestPath(UndirectedGraph graph, String startInterTitle) {

		// Initialize fields to proper sizes
		edgeTo = new StreetEdge[graph.V()];
		distanceTo = new double[graph.V()];
		pq = new IndexMinPriorityQueue<Double>(graph.V());
		allIntersections = new Intersection[graph.V()];
		
		/* Iterates through all intersections in the graph
		 * argument and reassigns them to positions in 
		 * allIntersections[] that match the intersection number */
		for (LinkedList l : graph.intersections) {
			LinkedList<Intersection>.Node curNode = l.first;
			while (curNode != null) {
				int curInterNo = curNode.data.intersectionNo;
				allIntersections[curInterNo] = curNode.data;
				curNode = curNode.next;
			}
		}
		
		// Initialize all distances to infinity
		for (int v = 0; v < graph.V(); ++v) {
			distanceTo[v] = Double.POSITIVE_INFINITY;
		}

		/* Find node in the hash table that contains the proper
		 * starting intersection and take the information from it */
		LinkedList<Intersection>.Node curNode = graph.intersections[hashTitle(startInterTitle)].first;
		while (!curNode.data.title.equals(startInterTitle)) {
			curNode = curNode.next;
		}
		Intersection startInter = curNode.data;
		int intersectionNo = startInter.intersectionNo;
		
		distanceTo[intersectionNo] = 0.0; // Set distance to starting intersection to 0
		
		pq.insert(intersectionNo, 0.0); // Put the starting intersection into the queue
		
		/* Continue relaxing vertex with the lowest
		 * distanceTo[] value on the queue until
		 * no vertices are left to relax */
		while (!pq.isEmpty()) {
			relax(pq.delMin());
		}
	}
	
	// Returns the distance to a particular intersection from the start
	public double distanceTo(UndirectedGraph graph, String endInterTitle) {
		
		// Find corresponding node and the intersection in it
		LinkedList<Intersection>.Node curNode = graph.intersections[hashTitle(endInterTitle)].first;
		while (!curNode.data.title.equals(endInterTitle)) {
			curNode = curNode.next;
		}
		int v = curNode.data.intersectionNo;
		return distanceTo[v];
	}
	
	// Returns true if there is a path from the start to the end intersection title argument
	public boolean hasPathTo(UndirectedGraph graph, String endInterTitle) {
		LinkedList<Intersection>.Node curNode = graph.intersections[hashTitle(endInterTitle)].first;
		while (!curNode.data.title.equals(endInterTitle)) {
			curNode = curNode.next;
		}
		int v = curNode.data.intersectionNo;
		return distanceTo[v] < Double.POSITIVE_INFINITY;
	}
	
	// Method that returns the sequence of edges to the specified ending intersection
	public EdgeStack pathTo(UndirectedGraph graph, String endInterTitle) {
		
		/* Resets all edge colors to black in case of multiple method
		 * calls to avoid having leftover red edges from another path */
		for (StreetEdge e : graph.edges) {
			e.color = Color.BLACK;
		}
		
		// Find corresponding node and the intersection in it
		LinkedList<Intersection>.Node curNode = graph.intersections[hashTitle(endInterTitle)].first;
		while (!curNode.data.title.equals(endInterTitle)) {
			curNode = curNode.next;
		}
		int v = curNode.data.intersectionNo;
		
		// Return null if no such path exists
		if (!hasPathTo(v)) {
			return null;
		}
		
		// Create a new stack of edges to hold the path
		EdgeStack path = new EdgeStack();
		
		// Start with the edge to the end intersection
		StreetEdge e = edgeTo[v];
		while (e != null) {

			e.color = Color.RED; // Change its color to red for graphics portion
			path.push(e);        // Push it onto the stack
			
			Intersection edgeInter1 = e.either(); // Get one intersection of the edge
			
			/* If the intersection we just got is not the one we just
			 * dealt with, assign e with the edge to this new intersection,
			 * also reassigning v with the new intersection's number */
			if (edgeInter1.intersectionNo != v) {
				e = edgeTo[edgeInter1.intersectionNo];
				v = edgeInter1.intersectionNo;
			}
			
			/* If e.either() returned the edge we already addressed,
			 * then we need to get the other intersection and then 
			 * reassign e with the edge to that intersection, also
			 * reassigning v with the new intersection's number */
			else {
				Intersection edgeInter2 = e.other(edgeInter1);
				e = edgeTo[edgeInter2.intersectionNo];
				v = edgeInter2.intersectionNo;
			}
			
		}

		return path; // Return the stack of edges we created
	}
	
	/* The relaxation method that does the
	 * main portion of the algorithm */
	private void relax(int curInterNo) {
		
		// Create reference for corresponding intersection
		Intersection curIntersection = allIntersections[curInterNo];
		
		// Perform for each edge incident to the intersection
		for (StreetEdge e : curIntersection.adj) {
			
			// Get the intersection on the other end of the edge
			Intersection other = e.other(curIntersection);
			int otherInterNo = other.intersectionNo;
			
			/* If the current distance value to the other intersection
			 * is greater than it would be using the current edge,
			 * then reassign the distance value using this new edge */
			if (distanceTo[otherInterNo] > distanceTo[curInterNo] + e.weight()) {
				distanceTo[otherInterNo] = distanceTo[curInterNo] + e.weight();
				edgeTo[otherInterNo] = e;
				
				/* If the other intersection is already in the priority queue,
				 * then change its priority value to the newly assigned distance */
				if (pq.contains(otherInterNo)) {
					pq.changeKey(otherInterNo, distanceTo[otherInterNo]);
				}
				
				// Otherwise, add it in to the queue to be relaxed later
				else {
					pq.insert(otherInterNo, distanceTo[otherInterNo]);
				}
			}
			
		}
		
	}
	
	// Helper hasPathTo() method with different argument used in pathTo() method
	private boolean hasPathTo(int v) {
		return distanceTo[v] < Double.POSITIVE_INFINITY;
	}
	
	/* The same hashTitle() method from the graph class
	 * is used to help find the right intersections */
	private int hashTitle(String title) {
		int hash = 0;
		for (int i = 0; i < title.length(); ++i) {
			hash = (hash * 31 + title.charAt(i)) % 1009;
		}
		return hash;
	}
}
