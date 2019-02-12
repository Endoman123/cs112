/**
 * LinkedList implementation of a Stack
 */
public class ListStack<A> implements Stack<A>{
    Node top;
    
    public void push(A item) {
        top = new Node(item, top);
    }

    public A pop() {
        if (isEmpty())
            throw new IllegalStateException("Cannot pop an empty stack.");

        A ret = top.getValue();

        top = top.getNext();

        return ret;
    }

    public void dump() {
        top = null;
    }

    public boolean isEmpty() {
        return top == null;
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