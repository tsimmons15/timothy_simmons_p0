package dev.simmons.utilities.sets;

public interface Set<T extends Comparable<T>> {
    boolean add(T item);
    boolean remove(T item);
}
