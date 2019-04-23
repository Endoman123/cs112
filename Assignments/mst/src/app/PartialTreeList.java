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
			} while (neighbor != null);

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

			// Report PTX and PQX
			// System.out.println(ptx);

			// Get highest priority arc that doesn't have a vector in ptx
			while (isVertexInTree(alpha.getv2(), ptx) && !ptx.getArcs().isEmpty()) {
				System.out.println("" + alpha + " is in " + ptx);
				alpha = ptx.getArcs().deleteMin();
			}

			// Alpha is the new arc
			//System.out.println("" + alpha + " is a component of the MST");
			ret.add(alpha);

			pty = ptlist.removeTreeContaining(alpha.getv2());

			// Report PTY and PQY
			//System.out.println(pty);

			// Merge PTY to PTX
			ptx.merge(pty);

			ptlist.append(ptx);

			System.out.println("------------------");
			for (PartialTree p : ptlist)
				System.out.println(p);
		}

		return ret;
	}

	/**
	 * Check if a vertex is in a partial tree
	 *
	 * @param vertex the vertex to check against the partial tree
	 * @param pt     the tree whose root to check against
	 * @return whether or not the vertex belongs to the given tree
	 * @throws NoSuchElementException If there is no matching tree
	 */
	private static boolean isVertexInTree(Vertex vertex, PartialTree pt) {
		Vertex p = vertex;

		while (p != p.parent)
			p = p.parent;

		return p.name.equals(pt.getRoot().name);
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
    	Node toRemove = null;

    	while (p != p.parent)
    		p = p.parent;

		// Step 1: get the node to remove and remove it
		if (rear.tree.getRoot().name.equals(p.name)) { // The rear node is the one to remove
			toRemove = rear;

			// If only one node, set rear to null, otherwise just set to next node
			rear = rear == rear.next ? null : rear.next;
		} else { // Gotta search
			for (Node n = rear; n.next != n.next.next; n = n.next) {
				if (n.next.tree.getRoot().name.equals(p.name)) { // If the NEXT node is the node to remove
					toRemove = n.next;

					// If next node is the last node (i.e.: the node after the next node is itself)
					// point the next node to this node
					// otherwise set the next node to the node after the next
					n.next = n.next == n.next.next ? n : n.next.next;
					break;
				}
			}
		}

		// Step 2: return or throw an error
		if (toRemove == null)
			throw new NoSuchElementException("No matching partial tree! " + vertex.name);
		else {
			size--;
			return toRemove.tree;
		}
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


