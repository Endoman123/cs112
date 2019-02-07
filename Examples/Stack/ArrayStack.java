import java.util.ArrayList;

/**
 * ArrayList implementation of a Stack
 */
public class ArrayStack<T> implements Stack<T> {
    public ArrayList<T> data;

    public void push(T item) {
        data.add(item);
    }

    public T pop() {
        if (isEmpty())
            throw new IllegalStateException("oh no please");

        return data.remove(data.size() - 1);
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }
}