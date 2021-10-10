/* Code & Commentary by Grayson Westfall */

import java.awt.Color;

/* Class used to find the minimum spanning
 * tree of a given graph, if it exists */
public class MapMST {
	
	protected StreetEdge[] edgeTo;				// edgeTo[i] is the edge that connects intersection number i to the MST
	protected double[] distanceTo;				// distanceTo[i] is the distance of the edge that connects it to the MST
	protected boolean[] marked;					// marked[i] is set to true if intersection number i has been visited by the algorithm already
	protected Intersection[] allIntersections;  // array of all intersections of the graph
	protected IndexMinPriorityQueue<Double> pq; // IndexMinPriorityQueue used in algorithm to find the MST
	
	// Constructor that finds the MST when called
	public MapMST(UndirectedGraph graph) {
		
		// Initialize fields to proper values
		edgeTo = new StreetEdge[graph.V()];
		distanceTo = new double[graph.V()];
		marked = new boolean[graph.V()];
		pq = new IndexMinPriorityQueue<Double>(graph.V());
		allIntersections = new Intersection[graph.V()];
		
		/* Iterates through all intersections in the graph
		 * argument and reassigns them to positions in 
		 * allIntersections[] that match the intersection number */
		for (LinkedList l : graph.intersections) {
			LinkedList<Intersection>.Node curNode = l.first;
			while (curNode != null) {
				int intersectionNo = curNode.data.intersectionNo;
				allIntersections[intersectionNo] = curNode.data;
				curNode = curNode.next;
			}
		}
		
		// Initialize all distances to infinity
		for (int i = 0; i < graph.V(); ++i) {
			distanceTo[i] = Double.POSITIVE_INFINITY;
		}
		
		distanceTo[0] = 0.0; // Set distance to starting intersection to 0
		pq.insert(0, 0.0);   // Put the starting intersection into the queue
		
		/* Continue visiting the closest intersection
		 * to the MST on the priority queue until
		 * there are no more vertices to visit */
		while (!pq.isEmpty()) {
			visit(graph, pq.delMin());
		}
	}
	
	// Visitation method that performs the majority of the MST algorithm
	private void visit(UndirectedGraph graph, int curInterNo) {
		
		marked[curInterNo] = true; // Mark the current intersection number as visited
		
		Intersection curInter = allIntersections[curInterNo]; // Get reference to intersection
		
		// For each edge incident to the current intersection
		for (StreetEdge e : curInter.adj) {
			
			// Get the other intersection and its number
			Intersection otherInter = e.other(curInter);
			int otherInterNo = otherInter.intersectionNo;
			
			/* If the other intersection is already
			 * marked then we don't need the edge to
			 * connect it to the tree again */
			if (marked[otherInterNo]) {
				continue;
			}
			
			/* Otherwise, if the current edge is the 
			 * shortest connection to this new intersection */
			if (e.weight() < distanceTo[otherInterNo]) {
				
				edgeTo[otherInterNo] = e; // This edge is the new edge to that intersection
				
				distanceTo[otherInterNo] = e.weight();
				
				/* If this intersection is in the queue already, change its 
				 * priority according to the new edge, and if not, then add it */
				if (pq.contains(otherInterNo)) {
					pq.changeKey(otherInterNo, distanceTo[otherInterNo]);
				}
				else {
					pq.insert(otherInterNo, distanceTo[otherInterNo]);
				}
			}
		}
		
	}
	
	// Returns all edges in the MST
	public Iterable<StreetEdge> edges() {
		
		/* Create a bag to hold all the edges
		 * and add all edges in the edgeTo[] array */
		Bag<StreetEdge> MST = new Bag<StreetEdge>();
		for (int v = 1; v < edgeTo.length; ++v) {
			edgeTo[v].color = Color.RED;
			MST.add(edgeTo[v]);
		}
		return MST;
	}
}
