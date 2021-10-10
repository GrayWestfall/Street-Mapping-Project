/* Code & Commentary by Grayson Westfall */

import java.awt.Color;

/* StreetEdge class used as edges in the UndirectedGraph class */
public class StreetEdge implements Comparable<StreetEdge> {
	
	protected String edgeName;				// edge's name
	protected Intersection v;				// 1st intersection incident to the edge
	protected Intersection w;				// 2nd intersection incident to the edge
	protected double weight;				// the edge's weight (distance between the vertices)
	protected Color color = Color.BLACK;    // the edge's color (used when displaying map)
	
	// Constructor to initialize edgeName, v, w, and weight
	public StreetEdge(String edgeName, Intersection v, Intersection w, double weight) {
		this.edgeName = edgeName;
		this.v = v;
		this.w = w;
		this.weight = weight;
	}
	
	public double weight() { return weight; }   // method to return weight 
	public Intersection either() { return v; }  // method to return 1 vertex
	
	// Returns opposite vertex of argument provided
	public Intersection other(Intersection vertex) {
		if 		(vertex.equals(v)) return w;
		else if (vertex.equals(w)) return v;
		else throw new RuntimeException("Invalid argument for other().");
	}
	
	/* Comparison method used to determine whether the
	 * edge is longer than the argument edge or not */
	public int compareTo(StreetEdge otherEdge) {
		if      (this.weight() < otherEdge.weight()) return -1;
		else if (this.weight > otherEdge.weight())   return  1;
		else 										 return  0;
	}

}
