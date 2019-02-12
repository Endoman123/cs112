/**
 * Interface for Queue data structure
 * @param <T> type to store
 */
public interface Queue<T> {
    public void enqueue(T item);
    public T dequeue();
}