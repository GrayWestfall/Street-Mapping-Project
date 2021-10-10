/* Code & Commentary by Grayson Westfall */

/* Intersection class used as
 * vertices in UndirectedGraph */
public class Intersection {
	
	protected String title; 								// title of intersection
	protected double latitude;								// latitude value of intersection
	protected double longitude;								// longitude value of intersection
	protected int intersectionNo;							// the integer number associated with the intersection
	protected Bag<StreetEdge> adj = new Bag<StreetEdge>();  // Bag of edges incident to the intersection

	// Constructor that initializes title, latitude, longitude, & intersectionNo
	public Intersection(String title, double latitude, double longitude, int intersectionNo) {
		this.title = title;
		this.latitude = latitude;
		this.longitude = longitude;
		this.intersectionNo = intersectionNo;
	}
}
