package dev.simmons.utilities.lists;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

public class LinkedList<T> implements List<T> {
    private Link<T> head;
    private int size;

    public LinkedList() {
        head = null;
        size = 0;
    }

    public LinkedList(T... items) {
        for(T item : items) {
            this.add(item);
        }
    }

    @Override
    public boolean isEmpty() {
        return head == null;
    }

    @Override
    public int length() {
        return this.size;
    }

    @Override
    public void add(T item) {
        if (item == null) {
            throw new IllegalArgumentException("Cannot add null items");
        }

        Link<T> newItem = new Link<T>();
        newItem.setData(item);

        if (this.head == null) {
            this.head = newItem;
            size = 1;
            return;
        }

        Link<T> curr = this.head;
        Link<T> next = curr.getNext();
        while (next != null) {
            next = next.getNext();
            curr = curr.getNext();
        }

        size++;

        newItem.setNext(next);
        curr.setNext(newItem);
    }

    @Override
    public T remove(int index) {
        if (index >= size || index < 0) {
            throw new ArrayIndexOutOfBoundsException("Given: " + index + " List size: " + size);
        }

        Link<T> curr = this.head;

        size--;

        if (index == 0) {
            this.head = curr.getNext();
            return curr.getData();
        }

        while (curr != null && --index > 0) {
            curr = curr.getNext();
        }

        Link<T> next = curr.getNext();
        curr.setNext(next.getNext());

        return next.getData();
    }

    @Override
    public boolean remove(T item) {
        if (item == null || size == 0) {
            return false;
        }

        boolean removed = false;
        Link<T> curr = this.head;

        if (this.head.data.equals(item)) {
            this.head = this.head.next;

            size--;
            return true;
        }

        while (curr.next != null) {
            if (curr.next.data.equals(item)) {
                removed = true;
                break;
            }

            curr = curr.next;
        }

        if (removed) {
            Link<T> next = curr.next;
            curr.next = next.next;
            next.next = null;
            size--;
        }

        return removed;
    }

    @Override
    public T get(int index) {
        if (index >= size || index < 0) {
            throw new ArrayIndexOutOfBoundsException("Given: " + index + " List size: " + size);
        }

        Link<T> curr = this.head;
        while (curr != null && index-- > 0) {
            curr = curr.getNext();
        }

        return curr.getData();
    }

    public int get(T item) {
        if (item == null) {
            return -1;
        }

        int i = 0;
        Link<T> curr = this.head;
        while (curr != null && curr.getData().equals(item)) {
            curr = curr.getNext();
            i++;
        }

        if (curr == null) {
            return -1;
        }

        return i;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Link<T> curr = LinkedList.this.head;

            @Override
            public boolean hasNext() {
                return curr != null;
            }

            @Override
            public T next() {
                T current = curr.data;
                curr = curr.next;
                return current;
            }

            @Override
            public void remove() {
                Iterator.super.remove();
            }

            @Override
            public void forEachRemaining(Consumer<? super T> action) {
                Iterator.super.forEachRemaining(action);
            }
        };
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        List.super.forEach(action);
    }

    @Override
    public Spliterator<T> spliterator() {
        return List.super.spliterator();
    }

    protected class Link<T> {
        private Link<T> next;
        private T data;

        Link() {
            next = null;
            data = null;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }

        public Link<T> getNext() {
            return next;
        }
        public void setNext(Link<T> next) {
            this.next = next;
        }
    }
}