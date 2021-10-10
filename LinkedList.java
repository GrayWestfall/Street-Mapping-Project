/* Code & Commentary by Grayson Westfall */

/* LinkedList class that gets extended to create the stack of StreetEdges 
 * and used in the implementation of the graph's hash table of vertices */
public class LinkedList<T> {
	
	protected class Node {
		protected T data;
		protected Node next;
	}
	
	protected Node first;
	protected int N;
	
	public boolean isEmpty() {
		if (first == null) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public int size() {
		return N;
	}
	
	public void addFront(T data) {
		Node oldFirst = first;
		first = new Node();
		first.data = data;
		first.next = oldFirst;
		++N;
	}
	
	public T removeFront() {
		T data = first.data;
		first = first.next;
		--N;
		return data;
	}
}
