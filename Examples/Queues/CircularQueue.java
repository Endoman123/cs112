public class CircularQueue<T> extends Queue<T> {
    private Node tail;
    private int size = 0;

    public void enqueue(T item) {
        
    }

    /**
     * A class that encapsulates one element in a LinkedList.`
     */
    private class Node {
        private T value;
        private Node next;

        /**
         * Constructor
         * @param val the value of the node
         * @param n   the next node
         */
        public Node(T val, Node n) {
            value = val;
            next = n;
        }

        public T getValue() {
            return value;
        }

        public Node getNext() {
            return next;
        }

        public void setValue(T v) {
            value = v;
        }

        public void setNext(Node n) {
            next = n;
        }
    }
}