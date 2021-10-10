/* Code & Commentary by Grayson Westfall */

import java.util.Iterator;

public class Bag<Item> implements Iterable<Item> {

	private Node first; // Pointer to 'first' item in Bag
	
	// Node class to hold Bag items
	private class Node {
		Item item;
		Node next;
	}
	
	// ListIterator class allows for iteration through the Bag
	private class ListIterator implements Iterator<Item> {
		
		private Node current = first;
		
		public boolean hasNext() { return current != null; }
		
		public Item next() {
			Item item = current.item;
			current = current.next;
			return item;
		}
	}
	
	// Adds an item to the bag
	public void add(Item item) {
		Node oldFirst = first;
		first = new Node();
		first.item = item;
		first.next = oldFirst;
	}
	
	// The iterator method uses the custom ListIterator
	public Iterator<Item> iterator() { return new ListIterator(); }
}
