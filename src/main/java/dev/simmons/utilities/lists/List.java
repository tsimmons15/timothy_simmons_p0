package dev.simmons.utilities.lists;

/**
 * A List generic that implements the Iterable interface.
 * @param <T> The data to be stored in the list.
 */
public interface List<T> extends Iterable<T> {
    /**
     * Return whether the given list is empty.
     * @return True if the list is empty, false otherwise.
     */
    boolean isEmpty();

    /**
     * The length representing how many elements are currently stored in our list.
     * @return The integer length.
     */
    int length();

    /**
     * Add the given T object to our list.
     * @param item The new T object.
     */
    void add(T item);

    /**
     * Remove the element currently located at index.
     * @param index The index to remove.
     * @return The object removed or null if the index is out of bounds.
     */
    T remove(int index);

    /**
     * Remove the first occurrence of the given item.
     * @param item The item to be removed.
     * @return True if the item was found and removed, false otherwise.
     */
    boolean remove(T item);

    /**
     * Get the element at the given index.
     * @param index The index of the item to retrieve.
     * @return The retrieved item or null if the index was out of bounds.
     */
    T get(int index);

    /**
     * Get the index of the given item object.
     * @param item The object to find the index.
     * @return The index the item was found, or -1 if the item was not found.
     */
    int get(T item);
}

