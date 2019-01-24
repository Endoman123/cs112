public class LinkedList<A> {
    private Node front;
    
    // HHHHHHHHHHHhhh
    public LinkedList() {
        // whatever
    }

    public static void main(String[] args) {
        LinkedList<Integer> list = new LinkedList<>();

        list.addToFront(3);
        list.addToFront(2);
        list.addToFront(1);

        list.remove(1);

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
    public void addToFront(A val) {
        front = new Node(val, front);
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
            front = toRemove.getNext();
        } else {
            Node temp = getNode(index - 1);

            toRemove = temp.getNext();
            temp.setNext(toRemove.getNext());
        }

        return toRemove.getValue();
    }

    /**
     * Prints the String representation of this LinkedList
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
     * Helper method to traverse LinkedList
     * 
     * @param index the index to traverse to
     * @return the Node at the specified index, or null if out of bounds
     */
    private Node getNode(int index) {
        Node cur = front;
        
        for (int i = 0; i < index; i++) {
            cur = cur.getNext();

            if (cur == null)
                return null;
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