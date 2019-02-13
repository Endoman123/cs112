public class Problem6 {
    // creates a new linked list consisting of the items common to the input lists
    // returns the front of this new linked list, null if there are no common items
    public IntNode commonElements(IntNode frontL1, IntNode frontL2) {
        IntNode l1 = frontL1, l2 = frontL2, front = null, back = new IntNode(0, null);

        // Merge time
        while (l1 != null && l2 != null) {
            switch (Math.signum(l2.data - l1.data)) { // Fun and fast way to compare
                case 1:
                    l1 = l1.next;
                    break;
                case -1:
                    l2 = l2.next;
                    break;
                default:
                    IntNode temp = new IntNode(l1.data, null);

                    if (front == null) {
                        front = temp;
                    } else if (back == null) {
                        back = temp;
                        front.next = back;
                    } else {
                        back.next = temp;
                    }
            }    
        }

        return ret;
    }

    public class IntNode {
        public int data;
        public IntNode next;
        
        public IntNode(int data, IntNode next) {
            this.data = data; this.next = next;
        }

        public String toString() {
            return data + "";
        }
    }
}