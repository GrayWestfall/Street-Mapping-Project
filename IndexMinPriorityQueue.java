/* Code & Commentary by Grayson Westfall */

public class IndexMinPriorityQueue<Key extends Comparable<Key>> {
		
	/* keys: keys[i] is the priority of item i
	 * pq: array of items in the queue
	 * qp: qp[i] is the index in pq where i is placed */
	protected Key[] keys;
	protected int pq[];
	protected int qp[];
	
	protected int maxN; // Maximum number of items in the queue
	protected int N = 0; // Number of items in the queue
		
	// Constructor that takes the array size as a parameter
	public IndexMinPriorityQueue(int maxN) {
		this.maxN = maxN;
		
		// Instantiates arrays to be used for queue
		keys = (Key[]) new Comparable[maxN + 1];
		pq = new int[maxN + 1];
		qp = new int[maxN + 1];
		
		/* Initializes array of indices to -1
		 * (-1 indicates the index number is
		 *  not in the queue) */
		for (int i = 0; i <= maxN; ++i) {
			qp[i] = -1;
		}
	}
		
	// Returns true if no items are in queue
	public boolean isEmpty() {
		return N == 0;
	}
		
	// Returns number of items in queue
	public int size() {
		return N;
	}
		
	/* Returns true if the index exists
	 * where that number is (is not -1) */
	public boolean contains(int i) {
		return qp[i] != -1;
	}

	/* Puts an item at the end of
	 * the queue then calls swim
	 * to move it up as necessary */
	public void insert(int i, Key key) {
		++N;
		qp[i] = N;
		pq[N] = i;
		keys[i] = key;
		swim(N);
	}
		
	/* Returns the item with lowest
	 * priority in the queue */
	public int minIndex() {
		return pq[1];
	}
	
	/* Returns the lowest priority
	 * that is in the current queue */
	public Key minKey() {
		return keys[pq[1]];
	}
	
	/* Deletes and returns the
	 * minimum value from the queue */
	public int delMin() {
		int min = pq[1];     // holds item with minimum priority
		exchange(1, N);      // moves the item to delete to the end of the array
		--N;                 // decrements number of items (removing what we just moved)
		sink(1);             // sinks the other item we moved to settle it back into place
		qp[min] = -1;        // changes index of item we deleted to -1 (doesn't exists anymore)
		keys[min] = null;    // changes priority of item we deleted to null
		pq[N+1] = -1;        // changes where the deleted item was to -1 to fully remove it
		return min;
	}
		
	/* Returns priority
	 * of item i */
	public Key keyOf(int i) {
		if (!contains(i)) {
			return keys[i];
		}
		else return null;
	}
	
	/* Change the priority of item i
	 * to new value (key) and call
	 * both sink and swim to move it
	 * into the appropriate position */
	public void changeKey(int i, Key key) {
		if (contains(i)) {
			keys[i] = key;
			swim(qp[i]);
			sink(qp[i]);
		}
	}
	
	/* Returns true if the priority of i in the
	 * queue is 'greater' than the priority of j */
	private boolean greater(int i, int j) {
		return keys[pq[i]].compareTo(keys[pq[j]]) > 0;
	}
		
	// Exchanges items at positions i and j
	private void exchange(int i, int j) {
		int temp = pq[i];
		pq[i] = pq[j];
		pq[j] = temp;
		qp[pq[i]] = i;
		qp[pq[j]] = j;
	}
		
	/* Repeatedly exchanges the item on
	 * which swim was called with its parent
	 * until it reaches the proper position */
	private void swim(int k) {
		while (k > 1 && greater(k/2, k)) {
			exchange(k/2, k);
			k = k/2;
		}
	}
		
	/* Repeatedly exchanges the item on which
	 * swim was called with its greater child
	 * until it reaches the proper position */
	private void sink(int k) {
		while (2*k <= N) {
				
			int j = 2*k;
			if (j < N && greater(j, j+1)) {
				++j;
			}
			if (!greater(k, j)) {
				break;
			}
			exchange(k, j);
			k = j;
		}
	}

}
