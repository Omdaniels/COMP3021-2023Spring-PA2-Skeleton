package hk.ust.comp3021.utils;

public class CustomArrayList<E> {
    private Object[] elements;
    private int size;
    private int capacity;

    /**
     * CustomArrayList constructor with default capacity (5)
     * `size` is set to the initial value 0
     */
    public CustomArrayList() {
        capacity = 5;
        elements = new Object[capacity];
    }

    /**
     * CustomArrayList constructor with given capacity
     * `size` is set to the initial value 0
     * @param initialCapacity the initial capacity of the list
     */
    public CustomArrayList(int initialCapacity) {
        capacity = initialCapacity;
        elements = new Object[capacity];
    }

    /**
     * Adds a new element into `elements`. Once `size` is equal to `capacity`,
     * we need to resize `elements` to twice its original size.
     * @param element to be added into `elements`
     */
    public void add(E element) {
        if (size == capacity) {
            resize(capacity * 2);
        }
        elements[size++] = element;
    }

    /**
     * Modifies the size of `elements`
     * @param newCapacity to indicate the new capacity of `elements`
     */
    private void resize(int newCapacity) {
        Object[] newElements = new Object[newCapacity];
        for (int i = 0; i < size; i++) {
            newElements[i] = elements[i];
        }
        elements = newElements;
        capacity = newCapacity;
    }

    /**
     * Obtains target element based on the given index. Once the index is not within [0, size),
     * we need to return null.
     * @param index to indicate the element position
     * @return element whose index is `index`, or null if index is out of range
     */
    public E get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        return (E) elements[index];
    }

    /**
     * Obtains the size of `elements`
     * @return `size`
     */
    public int size() {
        return size;
    }

    /**
     * Determines whether the list is empty
     * @return boolean variable that indicates the list status
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Determines whether the input is in `elements`
     * @param obj to be determined
     * @return boolean variable that indicates the existence of `obj`
     */
    public boolean contains(E obj) {
        for (int i = 0; i < size; i++) {
            if (elements[i].equals(obj)) {
                return true;
            }
        }
        return false;
    }
}
