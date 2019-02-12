/**
 * Interface for Stack data structure
 * 
 * @param <A> the type to treat as an element
 */
public interface Stack<T> {
    public void push(T push);
    public T pop();
    public boolean isEmpty();
}