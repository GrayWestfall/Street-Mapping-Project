/* Code & Commentary by Grayson Westfall */

/* A stack of edges that extends LinkedList
 * used to trace the shortest path */
public class EdgeStack extends LinkedList<StreetEdge> {
	
	// Pushes an item onto the stack
	public void push(StreetEdge e) {
		addFront(e);
	}

	// Pops the top StreetEdge from the stack
	public StreetEdge pop() {
		try {
			StreetEdge e = removeFront();
			return e;
		}
		catch (Exception excpt) {
			System.out.println("Error in EdgeStack, not enough edges.");
			return null;
		}
		
	}
}

