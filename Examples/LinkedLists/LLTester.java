public class LLTester {
    public static void main(String[] args) {
        testInteger();
    }

    public static void testInteger() {
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

        // Insert 8 after 7
        list.insertAfter(8, 7);

        // Display
        // Should be [6 7 8]
        System.out.println(list);

        // Get the size
        // Should be 3
        System.out.println(list.getSize());
    }

    public static void testString() {
        LinkedList<String> list = new LinkedList<>(
            "Orange",
            "Pear",
            "Apple"
        );

        // Display
        // Should be [Orange Pear Apple]
        System.out.println(list);
    }
}