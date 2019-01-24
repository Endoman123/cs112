public class LinkedList<A> {
    private Node<A> first;

    

    private class Node<A> {
        private A value;
        public Node<A> next;

        public Node(A val, Node<A> n) {
            value = val;
            next = n;
        }

        public A getValue() {
            return value;
        }
    }
}