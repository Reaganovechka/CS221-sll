import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public class IUArrayList<T> implements IndexedUnsortedList<T> {
    private T[] array;
    private int rear;
    private int modCount;

    public final static int DEFAULT_CAPACITY = 10;

    /**
     * Initialize list with default capacity.
     */
    public IUArrayList() {
        // array = (T[])(new Object[DEFAULT_CAPACITY]);
        // rear = 0;
        this(DEFAULT_CAPACITY);
    }

    /**
     * Initialize list with given capacity.
     * 
     * @param intitalCapacity
     */
    @SuppressWarnings(value = "unchecked")
    public IUArrayList(int intitalCapacity) {
        array = (T[]) (new Object[intitalCapacity]);
        rear = 0;
        modCount = 0;
    }

    /**
     * Doubles the array capacity if there is no more room to add elements.
     */
    private void expandIfNecessary() {
        if (rear >= array.length) {
            array = Arrays.copyOf(array, array.length * 2);
        }
    }

    @Override
    public void addToFront(T element) {
        expandIfNecessary();
        for (int i = rear; i > 0; i--) { // "shift loop"
            array[i] = array[i - 1];
        }
        array[0] = element;
        rear++;
        modCount++;
    }

    @Override
    public void addToRear(T element) {
        expandIfNecessary();
        array[rear] = element;
        rear++;
        modCount++;
    }

    @Override
    public void add(T element) {
        addToRear(element);
    }

    @Override
    public void addAfter(T element, T target) {
        // check if it contatins, expand, Get the index of the target val, incriment to
        // the right of that val, add new value, increment rear
        if (!contains(target)) {
            throw new NoSuchElementException();
        }
        expandIfNecessary();
        int targetIndex = indexOf(target);
        for (int i = rear; i > targetIndex + 1; i--) {
            array[i] = array[i - 1];
        }
        array[targetIndex + 1] = element;
        rear++;
        modCount++;
    }

    @Override
    public void add(int index, T element) {
        // increment everything from index to the right over 1, use set to 'add' the
        // element at given index
        if (index < 0 || index > rear) {
            throw new IndexOutOfBoundsException();
        }
        expandIfNecessary();
        for (int i = rear; i > index; i--) {
            array[i] = array[i - 1];
        }
        array[index] = element;
        rear++;
        modCount++;
    }

    @Override
    public T removeFirst() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        T retVal = array[0];
        for (int i = 0; i < rear - 1; i++) {
            array[i] = array[i + 1];
        }
        rear--;
        array[rear] = null;
        modCount++;
        return retVal;
    }

    @Override
    public T removeLast() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        T retVal = array[rear - 1]; // store return value
        array[rear - 1] = null; // remove the last value
        rear--; // decriment rear
        modCount++;
        return retVal;
    }

    @Override
    public T remove(T element) {
        if (!contains(element)) {
            throw new NoSuchElementException();
        }
        int elemIndex = indexOf(element);
        remove(elemIndex);
        modCount++;
        return element;
    }

    @Override
    public T remove(int index) {
        if (index < 0 || index >= rear) {
            throw new IndexOutOfBoundsException();
        }
        T retVal = array[index]; // for safe keeping
        // now we shift
        for (int i = index; i < rear - 1; i++) {
            array[i] = array[i + 1];
        }
        rear--;
        array[rear] = null;
        modCount++;
        return retVal;
    }

    @Override
    public void set(int index, T element) {
        // only allow replacing an existing element (0 <= index < rear)
        if (index < 0 || index >= rear) {
            throw new IndexOutOfBoundsException();
        }
        expandIfNecessary();
        array[index] = element;
        modCount++;
    }

    @Override
    public T get(int index) {
        if (index < 0 || index >= rear) {
            throw new IndexOutOfBoundsException();
        }
        return array[index];
    }

    @Override
    public int indexOf(T element) {
        int index = -1;
        for (int i = 0; i < rear; i++) {
            if (element.equals(array[i])) {
                index = i;
                break;
            }
        }
        return index;
    }

    @Override
    public T first() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        return array[0];
    }

    @Override
    public T last() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        return array[rear - 1];
    }

    @Override
    public boolean contains(T target) {
        return indexOf(target) > -1;
    }

    @Override
    public boolean isEmpty() {
        return rear == 0;
    }

    @Override
    public int size() {
        return rear;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("[");
        // for (int i = 0; i < rear; i++) {
        // str.append(array[i].toString());
        // str.append(", ");
        // }
        for (T element : this) {
            str.append(element.toString());
            str.append(", ");
        }
        if (!isEmpty()) {
            str.delete(str.length() - 2, str.length());
        }
        str.append("]");
        return str.toString();

    }

    @Override
    public Iterator<T> iterator() {
        return new ALIterator();
    }

    @Override
    public ListIterator<T> listIterator() {
        throw new UnsupportedOperationException("Unimplemented method 'listIterator'");
    }

    @Override
    public ListIterator<T> listIterator(int startingIndex) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'listIterator'");
    }

    /** Basic iterator class for IUArrayList */
    private class ALIterator implements Iterator<T> {
        private int nextIndex;
        private boolean canRemove;
        private int iterModCount;

        /** Initialize iterator before first element. */
        public ALIterator() {
            nextIndex = 0;
            canRemove = false;
            iterModCount = modCount;
        }

        @Override
        public boolean hasNext() {
            if (iterModCount != modCount) { // Something happened
                throw new ConcurrentModificationException();
            }
            return nextIndex < rear;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            canRemove = true;
            // T retVal = array[nextIndex];
            nextIndex++;
            // return retVal;
            return array[nextIndex - 1];
        }

        @Override
        public void remove() {
            if (iterModCount != modCount) { // Something happened
                throw new ConcurrentModificationException();
            }
            if (!canRemove) {
                throw new IllegalStateException();
            }
            canRemove = false; // Will not be allowed to remove last element twice
            for (int i = nextIndex - 1; i < rear - 1; i++) {
                array[i] = array[i + 1];
            }
            array[rear - 1] = null;
            rear--;
            nextIndex--;
            modCount++; // The list got changed
            iterModCount++; // ONLY THIS iterator knows

        }

    }

} // End of IUArrayList
