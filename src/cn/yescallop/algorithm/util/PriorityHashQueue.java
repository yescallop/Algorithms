package cn.yescallop.algorithm.util;

import java.util.*;

/**
 * A modified PriorityQueue with array index
 * @author Scallop Ye
 */
public class PriorityHashQueue<E> extends AbstractQueue<E> {

    private static final int DEFAULT_INITIAL_CAPACITY = 11;

    Object[] queue; // non-private to simplify nested class access

    private Map<Object, Integer> elementIndex;

    private int size = 0;

    private final Comparator<? super E> comparator;

    public PriorityHashQueue() {
        this(DEFAULT_INITIAL_CAPACITY, null);
    }

    public PriorityHashQueue(int initialCapacity) {
        this(initialCapacity, null);
    }

    public PriorityHashQueue(Comparator<? super E> comparator) {
        this(DEFAULT_INITIAL_CAPACITY, comparator);
    }

    public PriorityHashQueue(int initialCapacity,
                         Comparator<? super E> comparator) {
        // Note: This restriction of at least one is not actually needed,
        // but continues for 1.5 compatibility
        if (initialCapacity < 1)
            throw new IllegalArgumentException();
        this.queue = new Object[initialCapacity];
        this.elementIndex = new HashMap<>(initialCapacity);
        this.comparator = comparator;
    }

    @SuppressWarnings("unchecked")
    public PriorityHashQueue(Collection<? extends E> c) {
        if (c instanceof SortedSet<?>) {
            SortedSet<? extends E> ss = (SortedSet<? extends E>) c;
            this.comparator = (Comparator<? super E>) ss.comparator();
            initElementsFromCollection(ss);
        }
        else if (c instanceof PriorityHashQueue<?>) {
            PriorityHashQueue<? extends E> pq = (PriorityHashQueue<? extends E>) c;
            this.comparator = (Comparator<? super E>) pq.comparator();
            initFromPriorityHashQueue(pq);
        }
        else {
            this.comparator = null;
            initFromCollection(c);
        }
    }

    @SuppressWarnings("unchecked")
    public PriorityHashQueue(PriorityHashQueue<? extends E> c) {
        this.comparator = (Comparator<? super E>) c.comparator();
        initFromPriorityHashQueue(c);
    }

    @SuppressWarnings("unchecked")
    public PriorityHashQueue(PriorityQueue<? extends E> c) {
        this.comparator = (Comparator<? super E>) c.comparator();
        initFromPriorityQueue(c);
    }

    @SuppressWarnings("unchecked")
    public PriorityHashQueue(SortedSet<? extends E> c) {
        this.comparator = (Comparator<? super E>) c.comparator();
        initElementsFromCollection(c);
    }

    private void initFromPriorityQueue(PriorityQueue<? extends E> c) {
        if (c.getClass() == PriorityQueue.class) {
            this.queue = c.toArray();
            initElementIndex();
            this.size = c.size();
        } else {
            initFromCollection(c);
        }
    }

    private void initFromPriorityHashQueue(PriorityHashQueue<? extends E> c) {
        if (c.getClass() == PriorityHashQueue.class) {
            this.queue = c.toArray();
            this.elementIndex = new HashMap<>(c.elementIndex);
            this.size = c.size();
        } else {
            initFromCollection(c);
        }
    }

    private void initElementsFromCollection(Collection<? extends E> c) {
        Object[] a = c.toArray();
        // If c.toArray incorrectly doesn't return Object[], copy it.
        if (a.getClass() != Object[].class)
            a = Arrays.copyOf(a, a.length, Object[].class);
        int len = a.length;
        if (len == 1 || this.comparator != null)
            for (int i = 0; i < len; i++)
                if (a[i] == null)
                    throw new NullPointerException();
        this.queue = a;
        this.size = a.length;
    }

    private void initFromCollection(Collection<? extends E> c) {
        initElementsFromCollection(c);
        heapify();
        initElementIndex();
    }

    private void initElementIndex() {
        this.elementIndex = new HashMap<>(size);
        for (int i = 0; i < size; i++) {
            elementIndex.put(queue[i], i);
        }
    }

    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    private void grow(int minCapacity) {
        int oldCapacity = queue.length;
        // Double size if small; else grow by 50%
        int newCapacity = oldCapacity + ((oldCapacity < 64) ?
                (oldCapacity + 2) :
                (oldCapacity >> 1));
        // overflow-conscious code
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = hugeCapacity(minCapacity);
        queue = Arrays.copyOf(queue, newCapacity);
    }

    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) // overflow
            throw new OutOfMemoryError();
        return (minCapacity > MAX_ARRAY_SIZE) ?
                Integer.MAX_VALUE :
                MAX_ARRAY_SIZE;
    }

    public boolean add(E e) {
        return offer(e);
    }

    public boolean offer(E e) {
        if (e == null)
            throw new NullPointerException();
        int i = size;
        if (i >= queue.length)
            grow(i + 1);
        size = i + 1;
        if (i == 0) {
            queue[0] = e;
            elementIndex.put(e, 0);
        } else
            siftUp(i, e);
        return true;
    }

    @SuppressWarnings("unchecked")
    public E peek() {
        return (size == 0) ? null : (E) queue[0];
    }

    public int indexOf(Object o) {
        if (o != null) {
            Integer index = elementIndex.get(o);
            return index == null ? -1 : index;
        }
        return -1;
    }

    public boolean remove(Object o) {
        int i = indexOf(o);
        if (i == -1)
            return false;
        else {
            removeAt(i);
            return true;
        }
    }

    @SuppressWarnings("unchecked")
    public E get(int index) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException();
        return (E) queue[index];
    }

    @SuppressWarnings("unchecked")
    public void adjust(int index) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException();
        E e = (E) queue[index];
        siftDown(index, e);
        if (queue[index] == e)
            siftUp(index, e);
    }

    public boolean contains(Object o) {
        return indexOf(o) != -1;
    }

    public Object[] toArray() {
        return Arrays.copyOf(queue, size);
    }

    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        final int size = this.size;
        if (a.length < size)
            // Make a new array of a's runtime type, but my contents:
            return (T[]) Arrays.copyOf(queue, size, a.getClass());
        System.arraycopy(queue, 0, a, 0, size);
        if (a.length > size)
            a[size] = null;
        return a;
    }

    @SuppressWarnings("ConstantConditions")
    public Iterator<E> iterator() {
        return null;
    }

    public int size() {
        return size;
    }

    public void clear() {
        for (int i = 0; i < size; i++)
            queue[i] = null;
        elementIndex.clear();
        size = 0;
    }

    @SuppressWarnings("unchecked")
    public E poll() {
        if (size == 0)
            return null;
        int s = --size;
        E result = (E) queue[0];
        E x = (E) queue[s];
        queue[s] = null;
        if (s != 0)
            siftDown(0, x);
        elementIndex.remove(result);
        return result;
    }

    @SuppressWarnings("unchecked")
    private void removeAt(int i) {
        // assert i >= 0 && i < size;
        int s = --size;
        elementIndex.remove(queue[i]);
        if (s == i) { // removed last element
            queue[i] = null;
        } else {
            E moved = (E) queue[s];
            queue[s] = null;
            siftDown(i, moved);
            if (queue[i] == moved)
                siftUp(i, moved);
        }
    }

    private void siftUp(int k, E x) {
        if (comparator != null)
            siftUpUsingComparator(k, x);
        else
            siftUpComparable(k, x);
    }

    @SuppressWarnings("unchecked")
    private void siftUpComparable(int k, E x) {
        Comparable<? super E> key = (Comparable<? super E>) x;
        while (k > 0) {
            int parent = (k - 1) >>> 1;
            Object e = queue[parent];
            if (key.compareTo((E) e) >= 0)
                break;
            queue[k] = e;
            elementIndex.put(e, k);
            k = parent;
        }
        queue[k] = key;
        elementIndex.put(key, k);
    }

    @SuppressWarnings("unchecked")
    private void siftUpUsingComparator(int k, E x) {
        while (k > 0) {
            int parent = (k - 1) >>> 1;
            Object e = queue[parent];
            if (comparator.compare(x, (E) e) >= 0)
                break;
            queue[k] = e;
            elementIndex.put(e, k);
            k = parent;
        }
        queue[k] = x;
        elementIndex.put(x, k);
    }

    private void siftDown(int k, E x) {
        if (comparator != null)
            siftDownUsingComparator(k, x);
        else
            siftDownComparable(k, x);
    }

    @SuppressWarnings("unchecked")
    private void siftDownComparable(int k, E x) {
        Comparable<? super E> key = (Comparable<? super E>)x;
        int half = size >>> 1;        // loop while a non-leaf
        while (k < half) {
            int child = (k << 1) + 1; // assume left child is least
            Object c = queue[child];
            int right = child + 1;
            if (right < size &&
                    ((Comparable<? super E>) c).compareTo((E) queue[right]) > 0)
                c = queue[child = right];
            if (key.compareTo((E) c) <= 0)
                break;
            queue[k] = c;
            elementIndex.put(c, k);
            k = child;
        }
        queue[k] = key;
        elementIndex.put(key, k);
    }

    @SuppressWarnings("unchecked")
    private void siftDownUsingComparator(int k, E x) {
        int half = size >>> 1;
        while (k < half) {
            int child = (k << 1) + 1;
            Object c = queue[child];
            int right = child + 1;
            if (right < size &&
                    comparator.compare((E) c, (E) queue[right]) > 0)
                c = queue[child = right];
            if (comparator.compare(x, (E) c) <= 0)
                break;
            queue[k] = c;
            elementIndex.put(c, k);
            k = child;
        }
        queue[k] = x;
        elementIndex.put(x, k);
    }

    @SuppressWarnings("unchecked")
    private void heapify() {
        for (int i = (size >>> 1) - 1; i >= 0; i--)
            siftDown(i, (E) queue[i]);
    }

    public Comparator<? super E> comparator() {
        return comparator;
    }
}
