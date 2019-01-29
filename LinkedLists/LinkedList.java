public class LinkedList<A> {
    private Node front;
    
    // HHHHHHHHHHHhhh
    public LinkedList() {
        // whatever
    }

    public static void main(String[] args) {
        LinkedList<Integer> list = new LinkedList<>();

        // Initialize list
        list.add(3);
        list.add(2);
        list.add(1);

        // Display
        // Should be [1 2 3]
        System.out.println(list);

        // Remove second node
        list.remove(1);

        // Display
        // Should be [1 3]
        System.out.println(list);

        // Add more values
        list.add(7);
        list.add(5);
        list.add(4);

        // Insert 6 into the right place
        list.insert(6, 2);

        // Display
        // Should be [4 5 6 7 2 3]
        System.out.println(list);

        // Remove the 3 in the list
        list.removeValue(3);
        
        // Remove the 2 in the list
        list.remove(4);

        // Display
        // Should be [4 5 6 7]
        System.out.println(list);

        // Remove the first value in the list
        list.remove(0);

        // Remove the 5 in the list
        list.removeValue(5);

        // Display
        // Should be [6 7]
        System.out.println(list);
    }

    public A get(int index) {
        return getNode(index).getValue();
    }

    /**
     * Appends a Node to the front of the LinkedList 
     * 
     * @param val the value of the new node
     */
    public void add(A val) {
        insert(val, 0);
    }

    /**
     * Insersts a Node into the Linked List
     * 
     * @param val   the value to insert
     * @param index the position to insert the node
     */
    public void insert(A val, int index) {
        if (index == 0) {
            front = new Node(val, front);
        } else {
            Node curNode = getNode(index - 1);

            if (curNode != null) 
                curNode.setNext(new Node(val, curNode.getNext()));
        }
    }

    /**
     * Removes the Node at the specified index
     * 
     * @param index the index of the item to remove from the LinkedList
     * @return the item that was removed, or null if unsuccessful.
     */
    public A remove(int index) {
        Node toRemove = null;

        if (index == 0) {
            toRemove = front;
            front = front.getNext();
        } else {
            Node temp = getNode(index - 1);
            toRemove = temp.getNext();

            temp.setNext(toRemove.getNext());
        }
        
        return toRemove != null ? toRemove.getValue() : null;
    }

    /**
     * Remove first instance of value
     * 
     * @param val the value to search for
     * @return the value that was removed, or null if unsuccessful.
     */
    public A removeValue(A val) {
        Node cur = front;
        A removedVal = null;

        if (front.getValue().equals(val)) {
            removedVal = front.getValue();
            front = front.getNext();
        }

        while (cur != null && cur.getNext() != null) {
            Node next = cur.getNext();
            if (next.getValue().equals(val)) {
                removedVal = next.getValue();
                cur.setNext(next.getNext());
                break;
            }

            cur = next;
        }

        return removedVal;
    }

    /**
     * Gets the String representation of this LinkedList
     */
    public String toString() {
        String ret = "[";

        if (front != null) {
            Node curNode = front;
            while (curNode != null) {
                ret += "" + curNode.getValue() + " ";
                curNode = curNode.getNext();
            }

            ret = ret.substring(0, ret.length() - 1);
        }

        ret += "]";

        return ret;
    }

    /**
     * Searches for the first instance of the target value
     * 
     * @param val the value to search for
     * @return the first Node containing the value being searched, or null if not found
     */
    private Node search(A val) {
        Node cur = front;

        while (cur != null) {
            if (cur.getValue().equals(val))
                break;

            cur = cur.getNext();
        }

        return cur;
    }

    /**
     * Helper method to traverse LinkedList
     * 
     * @param index the index to traverse to
     * @return the Node at the specified index, or null if out of bounds
     */
    private Node getNode(int index) {
        Node cur = front;
        
        for (int i = 0; cur != null && i < index; i++) {
            cur = cur.getNext();
        }

        return cur;
    }

    /**
     * A class that encapsulates one element in a LinkedList.`
     */
    private class Node {
        private A value;
        private Node next;

        /**
         * Constructor
         * @param val the value of the node
         * @param n   the next node
         */
        public Node(A val, Node n) {
            value = val;
            next = n;
        }

        public A getValue() {
            return value;
        }

        public Node getNext() {
            return next;
        }

        public void setValue(A v) {
            value = v;
        }

        public void setNext(Node n) {
            next = n;
        }
    }
}