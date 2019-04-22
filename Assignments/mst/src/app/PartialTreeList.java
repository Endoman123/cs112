package app;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

import structures.*;

/**
 * Stores partial trees in a circular linked list
 * 
 */
public class PartialTreeList implements Iterable<PartialTree> {
    
	/**
	 * Inner class - to build the partial tree circular linked list 
	 * 
	 */
	public static class Node {
		/**
		 * Partial tree
		 */
		public PartialTree tree;
		
		/**
		 * Next node in linked list
		 */
		public Node next;
		
		/**
		 * Initializes this node by setting the tree part to the given tree,
		 * and setting next part to null
		 * 
		 * @param tree Partial tree
		 */
		public Node(PartialTree tree) {
			this.tree = tree;
			next = null;
		}
	}

	/**
	 * Pointer to last node of the circular linked list
	 */
	private Node rear;
	
	/**
	 * Number of nodes in the CLL
	 */
	private int size;
	
	/**
	 * Initializes this list to empty
	 */
    public PartialTreeList() {
    	rear = null;
    	size = 0;
    }

    /**
     * Adds a new tree to the end of the list
     * 
     * @param tree Tree to be added to the end of the list
     */
    public void append(PartialTree tree) {
    	Node ptr = new Node(tree);
    	if (rear == null) {
    		ptr.next = ptr;
    	} else {
    		ptr.next = rear.next;
    		rear.next = ptr;
    	}
    	rear = ptr;
    	size++;
    }

    /**
	 * Initializes the algorithm by building single-vertex partial trees
	 * 
	 * @param graph Graph for which the MST is to be found
	 * @return The initial partial tree list
	 */
	public static PartialTreeList initialize(Graph graph) {
		PartialTreeList ret = new PartialTreeList();

		for (Vertex v : graph.vertices) {
			PartialTree t = new PartialTree(v);
			Vertex.Neighbor neighbor = v.neighbors;

			do {
				t.getArcs().insert(new Arc(v, neighbor.vertex, neighbor.weight));
				neighbor = neighbor.next;
			} while (neighbor.next != null);

			ret.append(t);
		}
		
		return ret;
	}
	
	/**
	 * Executes the algorithm on a graph, starting with the initial partial tree list
	 * for that graph
	 * 
	 * @param ptlist Initial partial tree list
	 * @return Array list of all arcs that are in the MST - sequence of arcs is irrelevant
	 */
	public static ArrayList<Arc> execute(PartialTreeList ptlist) {
		ArrayList<Arc> ret = new ArrayList<>();

		while (ptlist.size() > 1) {
			PartialTree ptx = ptlist.remove(), pty;
			Arc alpha = ptx.getArcs().deleteMin();
			String v2 = getVertexFromArc(alpha, 2);

			// Get highest priority arc that doesn't have a vector in ptx
			while (isVertexInTree(v2, ptx) && !ptx.getArcs().isEmpty()) {
				alpha = ptx.getArcs().deleteMin();
				v2 = getVertexFromArc(alpha, 2);
			}

			// Alpha is the new arc
			ret.add(alpha);

			pty = ptlist.removeTreeWithRoot(v2);

			ptx.merge(pty);

			ptlist.append(ptx);
		}

		return ret;
	}

	/**
	 * Helper method: Check if vertex is in partial tree
	 *
	 * @param v   the vertex to find in pt
	 * @param pt  the tree to check against the arc
	 * @return whether or not v is in pt
	 */
	private static boolean isVertexInTree(String v, PartialTree pt) {
		for (Arc a : pt.getArcs()) {
			if (getVertexFromArc(a, 1).equals(v))
				return true;
		}

		return false;
	}

	/**
	 * Gets the specified vertex name from the arc
	 *
	 * @param arc the arc to fetch vertices from
	 * @param ind the vertex (1 or 2) to get
	 * @return the vertex name, or null if error
	 */
	private static String getVertexFromArc(Arc arc, int ind) {
		String name = arc.toString();
		int from;

		if (ind == 1) {
			from = 1;
		} else if (ind == 2) {
			from = name.indexOf(" ") + 1;
		} else
			return null;

		return name.substring(from, name.indexOf(" ", from));
	}
	
    /**
     * Removes the tree that is at the front of the list.
     * 
     * @return The tree that is removed from the front
     * @throws NoSuchElementException If the list is empty
     */
    public PartialTree remove() 
    throws NoSuchElementException {
    			
    	if (rear == null) {
    		throw new NoSuchElementException("list is empty");
    	}
    	PartialTree ret = rear.next.tree;
    	if (rear.next == rear) {
    		rear = null;
    	} else {
    		rear.next = rear.next.next;
    	}
    	size--;
    	return ret;
    		
    }

    /**
     * Removes the tree in this list that contains a given vertex.
     * 
     * @param vertex Vertex whose tree is to be removed
     * @return The tree that is removed
     * @throws NoSuchElementException If there is no matching tree
     */
    public PartialTree removeTreeContaining(Vertex vertex)
    throws NoSuchElementException {
    	Vertex p = vertex;

    	while (p.parent != null)
    		p = p.parent;

		return removeTreeWithRoot(p.name);
    }

	/**
	 * Removes the tree with the given root vertex name
	 *
	 * @param name name of the vertex to remove
	 * @return the partial tree containing the vertex with the specified name
	 * @throws NoSuchElementException if no tree was found
	 */
    private PartialTree removeTreeWithRoot(String name) throws NoSuchElementException {
		for (PartialTree p : this) {
			if (name.equals(p.getRoot().name))
				return p;
		}

		throw new NoSuchElementException("No matching partial tree! " + name);
    }
    
    /**
     * Gives the number of trees in this list
     * 
     * @return Number of trees
     */
    public int size() {
    	return size;
    }
    
    /**
     * Returns an Iterator that can be used to step through the trees in this list.
     * The iterator does NOT support remove.
     * 
     * @return Iterator for this list
     */
    public Iterator<PartialTree> iterator() {
    	return new PartialTreeListIterator(this);
    }
    
    private class PartialTreeListIterator implements Iterator<PartialTree> {
    	
    	private PartialTreeList.Node ptr;
    	private int rest;
    	
    	public PartialTreeListIterator(PartialTreeList target) {
    		rest = target.size;
    		ptr = rest > 0 ? target.rear.next : null;
    	}
    	
    	public PartialTree next() 
    	throws NoSuchElementException {
    		if (rest <= 0) {
    			throw new NoSuchElementException();
    		}
    		PartialTree ret = ptr.tree;
    		ptr = ptr.next;
    		rest--;
    		return ret;
    	}
    	
    	public boolean hasNext() {
    		return rest != 0;
    	}
    	
    	public void remove() 
    	throws UnsupportedOperationException {
    		throw new UnsupportedOperationException();
    	}
    	
    }
}


