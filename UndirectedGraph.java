/* Code & Commentary by Grayson Westfall */

import java.io.*; 
import javax.swing.*;
import java.awt.*;
import java.util.Scanner;

public class UndirectedGraph {

	protected final int R = 6371; // approximation of Earth's radius in km (for distance calculation)
	
	protected int V = 0; 													   // number of intersections: vertices
	protected int E = 0; 													   // number of streets:       edges
	protected LinkedList<Intersection>[] intersections = new LinkedList[1009]; // Hash table of vertices
	protected Bag<StreetEdge> edges = new Bag<StreetEdge>();				   // Bag containing all edges
	protected MapFrame mapFrame;											   // Frame used to display map
	
	// Constructor that creates the graph structure
	public UndirectedGraph(BufferedReader in) throws IOException {
		
		// Initialize LinkedLists in hash table
		for (int i = 0; i < intersections.length; ++i) {
			intersections[i] = new LinkedList<>();
		}
		
		String nextLine; // Read next line of input file
		
		// Continue reading each line until end of file
		while ((nextLine = in.readLine()) != null) {

			Scanner lineScan = new Scanner(nextLine); // Prepare to scan elements of line
			String type = lineScan.next();            // type is either (i)ntersection or (r)oad
			
			// If line is an intersection
			if (type.equals("i")) {
				
				// Take values of intersection
				String title     = lineScan.next();
				double latitude  = Double.parseDouble(lineScan.next());
				double longitude = Double.parseDouble(lineScan.next());
				
				/* hashTitle() function computes intersection's index in the hash table
				 * then adds the new intersection to that index's LinkedList; current 
				 * value of V is used to assign intersection numbers */
				intersections[hashTitle(title)].addFront(new Intersection(title, latitude, longitude, V));
				
				++V; // increment number of vertices
			}
			
			// If line is a road
			else if (type.equals("r")) {
				
				// Read edge name from line
				String edgeName = lineScan.next();
				
				/* Starts at location in hash table where the associated intersection should
				 * be, then iterates through LinkedList until finding the correct node */
				String interName1 = lineScan.next();
				LinkedList<Intersection>.Node curNode = intersections[hashTitle(interName1)].first;
				while (! curNode.data.title.equals(interName1)) {
					try {
						curNode = curNode.next;
					}
					catch (NullPointerException e) {
						System.out.println("Problem occurred creating street edge.");
					}
				}
				Intersection inter1 = curNode.data; // hold reference for first intersection
				
				// Executes the same process for the second intersection
				String interName2 = lineScan.next();
				curNode = intersections[hashTitle(interName2)].first;
				
				while (! curNode.data.title.equals(interName2)) {
					try {
						curNode = curNode.next;
					}
					catch (NullPointerException e) {
						System.out.println("Problem occurred creating street edges.");
					}
				}
				Intersection inter2 = curNode.data; // hold reference for second intersection
				
				// Compute the edge's weight using the longitude and latitude values of the intersections
				double edgeWeight = calcDistanceMiles(inter1.latitude, inter1.longitude, inter2.latitude, inter2.longitude);
				
				StreetEdge newEdge = new StreetEdge(edgeName, inter1, inter2, edgeWeight); // construct the new edge

				/* Add the new edge to both of the 
				 * intersections' adjacency lists */
				inter1.adj.add(newEdge); 
				inter2.adj.add(newEdge);
				
				edges.add(newEdge); // add the edge to the graph's Bag of edges
				++E;                // increment number of edges
			}
		}
	}
	
	public int V() { return V; } // Method to return number of vertices
	public int E() { return E; } // Method to return number of edges
	
	// Helper method to calculate distance in miles using latitude and longitude values
	private double calcDistanceMiles(double lat1, double lon1, double lat2, double lon2) {
		double latDif = Math.toRadians(lat2 - lat1);
		double lonDif = Math.toRadians(lon2 - lon1);
		
		lat1 = Math.toRadians(lat1);
		lat2 = Math.toRadians(lat2);
		
		// First step in Haversine formula
		double a = Math.pow(Math.sin(latDif/2), 2) + Math.pow(Math.sin(lonDif/2), 2) * Math.cos(lat1) * Math.cos(lat2);
		
		// Next step in Haversine formula
		double c = 2 * Math.asin(Math.sqrt(a));
		
		// Return c times the Earth's radius
		double distKMS = R * c;
		
		// Convert to miles and return
		double distMiles = distKMS / 1.609;
		return distMiles;
	}
	
	/* Helper method to compute hash
	 * function on intersection names */
	private int hashTitle(String title) {
		int hash = 0;
		for (int i = 0; i < title.length(); ++i) {
			hash = (hash * 31 + title.charAt(i)) % 1009;
		}
		return hash;
	}
	
	public static void main(String[] args) throws IOException {
		
		File file = new File(args[0]);
		FileReader reader = new FileReader(file);
		BufferedReader streetMap = new BufferedReader(reader);
		
		UndirectedGraph graphMap = new UndirectedGraph(streetMap);
		
		/* If the map needs to be shown */
		if (args[1].equals("[-show]")) {
			
			// If only the map needs to be shown
			if (args.length == 2) {
				
				// Creates visual portion
				graphMap.mapFrame = new MapFrame(graphMap);
				graphMap.mapFrame.setVisible(true);
				
			}
			
			/* Determine whether command line asks
			 * for the shortest paths or the MST */
			else if (args[2].equals("[-directions")) {
				String startIntersectionTitle = args[3];
				String endIntersectionTitle = args[4].substring(0, args[4].indexOf("]")); // omit ending bracket
				
				// Calculates all shortest paths
				DijkstraShortestPath shortestPaths = new DijkstraShortestPath(graphMap, startIntersectionTitle);
				
				/* Attempts to find path from first intersection
				 * to the other and throws an exception if none exists */
				try {
					EdgeStack shortestPathTo = shortestPaths.pathTo(graphMap, endIntersectionTitle);

					// Prints out path from start intersection to end
					System.out.println("Shortest path to " + endIntersectionTitle + " from " + startIntersectionTitle + ":");
					while (!shortestPathTo.isEmpty()) {
						StreetEdge curEdge = shortestPathTo.pop();
						System.out.printf("\t" + curEdge.edgeName + ": %.4f miles\n", curEdge.weight);
					}
					System.out.printf("\tTotal distance: %.4f miles\n", shortestPaths.distanceTo(graphMap, endIntersectionTitle));
					
					// Creates visual portion
					graphMap.mapFrame = new MapFrame(graphMap);
					graphMap.mapFrame.setVisible(true);
					
				}
				catch(Exception excpt) {
					System.out.println("No path to " + endIntersectionTitle + " exists.");
				}
			}
			
			else if (args[2].equals("[-meridianmap]")) {
				
				/* Tries to calculate MST and throws an exception
				 * if none exists for the given graph */
				try {
					MapMST mst = new MapMST(graphMap);
				
					Iterable<StreetEdge> mstEdges = mst.edges();
				
					// Prints out edges in the graph's MST
					System.out.println("MINIMUM SPANNING TREE:"); 
					for (StreetEdge e : mstEdges) {
						System.out.printf("\t" + e.edgeName + ": %.4f miles\n", e.weight());
					}
				
					// Creates visual portion
					graphMap.mapFrame = new MapFrame(graphMap);
					graphMap.mapFrame.setVisible(true);
				}
				catch (Exception excpt) {
					System.out.println("No minimum spanning tree exists for this graph.");
				}
			}
		}
		
		/* If map does not need to be shown,
		 * performs the same functions as above
		 * but just without the visual portion  */
		else if (args[1].equals("[-directions")) {
			String startIntersectionTitle = args[2];
			String endIntersectionTitle = args[3].substring(0, args[3].indexOf("]")); // omit ending bracket
			
			DijkstraShortestPath shortestPaths = new DijkstraShortestPath(graphMap, startIntersectionTitle);
			
			try {
				EdgeStack shortestPathTo = shortestPaths.pathTo(graphMap, endIntersectionTitle);
			
				System.out.println("Shortest path to " + endIntersectionTitle + ":");
				while (!shortestPathTo.isEmpty()) {
					StreetEdge curEdge = shortestPathTo.pop();
					System.out.printf("\t" + curEdge.edgeName + ": %.4f miles\n", curEdge.weight);
				}
				System.out.printf("\tTotal distance: %.4f miles\n", shortestPaths.distanceTo(graphMap, endIntersectionTitle));
				
			}
			catch(Exception excpt) {
				System.out.println("No path to " + endIntersectionTitle + " exists.");
			}
		}
		else if (args[1].equals("[-meridianmap]")) {
			try {
				MapMST mst = new MapMST(graphMap);
			
				Iterable<StreetEdge> mstEdges = mst.edges();
			
				System.out.println("MINIMUM SPANNING TREE:"); 
				for (StreetEdge e : mstEdges) {
					System.out.printf("\t" + e.edgeName + ": %.4f miles\n", e.weight());
				}
			}
			catch (Exception excpt) {
				System.out.println("No minimum spanning tree exists for this graph.");
			}
		}
		
	}
}
