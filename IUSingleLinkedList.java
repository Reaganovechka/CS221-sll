import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * Single-Linked node-based implementation of IndexedUnsortedList
 * 
 * @author Reagan Ovechka
 */
public class IUSingleLinkedList<T> implements IndexedUnsortedList<T> {
    private Node<T> head;
    private Node<T> tail;
    private int size;
    private int modCount;

    public IUSingleLinkedList() {
        head = tail = null;
        size = 0;
        modCount = 0;
    }

    @Override
    public void addToFront(T element) {
        Node<T> newNode = new Node<T>(element);
        newNode.setNextNode(head);
        head = newNode;
        if (tail == null) { // isEmpty is risky, if head==null was test for isEmpty, that would not work
            tail = newNode;
        }
        size++;
        modCount++;
    }

    @Override
    public void addToRear(T element) {
        Node<T> newNode = new Node<T>(element);
        if (tail != null) {
            tail.setNextNode(newNode);
        } else {
            head = newNode;
        }
        tail = newNode;
        size++;
        modCount++;
    }

    @Override
    public void add(T element) {
        addToRear(element);
    }

    @Override
    public void addAfter(T element, T target) {
        Node<T> newNode = new Node<T>(element);
        if (isEmpty() || !contains(target)) { // If the list is empty, or it does not contain the target element
            throw new NoSuchElementException();
        }
        int index = indexOf(target);
        Node<T> currentNode = head;
        for (int i = 0; i < index; i++) {
            currentNode = currentNode.getNextNode();
        }
        if (size > 1) {
            if (currentNode.getNextNode() == null) { // If it is being added to end of the list
                currentNode.setNextNode(newNode);
                tail = newNode;
            } else { // General case, added to the middle of a list
                Node<T> continueNode = currentNode.getNextNode(); // The new node will point to this node
                currentNode.setNextNode(newNode); // Add the new node to the existing set
                currentNode = currentNode.getNextNode(); // set current node to the new node
                currentNode.setNextNode(continueNode);// Point the new node to the existing nodes
            }
        } else { // If there is only 1 element in the list
            currentNode.setNextNode(newNode);
            tail = newNode;
        }
        size++;
        modCount++;
    }

    @Override
    public void add(int index, T element) {
        Node<T> newNode = new Node<T>(element);
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException();
        }
        if (isEmpty() && index == 0) { // If the list is empty and the index is 0
            head = newNode;
            tail = newNode;
        } else if (size == 1) {
            if (index == 0) {
                head = newNode;
                newNode.setNextNode(tail);
            } else {
                head.setNextNode(newNode);
                tail = newNode;
            }
        } else { // List larger than 1 element
            Node<T> currentNode = head;
            for (int i = 0; i < index - 1; i++) {
                currentNode = currentNode.getNextNode();
            }
            if (currentNode == head && index == 0) { // Add at index 0 in a longer list
                newNode.setNextNode(currentNode);
                head = newNode;
            } else if (currentNode == tail) { // Adding to end of the list
                currentNode.setNextNode(newNode);
                tail = newNode;
            } else { // General Case
                Node<T> continueNode = currentNode.getNextNode();
                // Link the nodes
                currentNode.setNextNode(newNode);
                currentNode = currentNode.getNextNode();
                currentNode.setNextNode(continueNode);
            }
        }

        size++;
        modCount++;
    }

    @Override
    public T removeFirst() { // O(1)
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        Node<T> firstNode = head;
        head = head.getNextNode();
        if (head == null) { // just removed the only node from a list
            tail = null; // Tail also needs to be null because the list is now empty
        }
        size--;
        modCount++;
        return firstNode.getElement();
    }

    @Override
    public T removeLast() { // Remove last is pretty bad for SLL because you have to go through every
                            // element before it, we dont know the previous element
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        T retVal = tail.getElement();
        if (size > 0) {
            Node<T> currentNode = head;
            for (int i = 0; i < size - 2; i++) {
                currentNode = currentNode.getNextNode();
            }
            tail = currentNode;
            currentNode.setNextNode(null);
        } else {
            head = tail = null;
        }
        size--;
        modCount++;
        return retVal;
    }

    @Override
    public T remove(T element) {
        // int index = indexOf(element); //This repeats loops, dont want to navigate
        // through the same nodes
        // if (index < 0 || index >= size) {
        // throw new IndexOutOfBoundsException();
        //
        // if (index == 0) {
        // removeFirst();
        // }
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        // must check the head node before general case search
        T retVal;
        // Beginning of a list
        if (head.getElement().equals(element)) {
            retVal = head.getElement();
            head = head.getNextNode();
            if (head == null) {
                tail = null;
            }
        } else {
            Node<T> currentNode = head;
            // Node<T> nodeAfterCurr = head;
            while (currentNode != tail && !currentNode.getNextNode().getElement().equals(element)) {
                currentNode = currentNode.getNextNode();
            }
            // if it is never found
            if (currentNode == tail) {
                throw new NoSuchElementException();
            }
            retVal = currentNode.getNextNode().getElement();
            if (currentNode == tail) {
                tail = currentNode;
            }
            // 'general case'- middle of long list
            currentNode.setNextNode(currentNode.getNextNode().getNextNode());
            // Was it the only element?
            if (head == tail) {
                currentNode = null;
            }
            // Was it the last node?
            if (currentNode.getNextNode() == null) {
                tail = currentNode;
            }

        }

        size--;
        modCount++;
        return retVal;
    }

    @Override
    public T remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }

        // General Case
        Node<T> currentNode = head;
        for (int i = 0; i < index - 1; i++) {
            currentNode = currentNode.getNextNode();
        }
        T retVal = null;
        // If removed only elem
        if (currentNode == head && head == tail) { // Removing the first and ONLY element
            retVal = currentNode.getElement();
            currentNode = null;
        } else if (currentNode == head && index == 0) {// If removed first elem
            retVal = head.getElement();
            head = currentNode.getNextNode();
        } else if (currentNode.getNextNode() == tail) {// If removed last elem
            retVal = tail.getElement();
            currentNode.setNextNode(null);
            tail = currentNode;
        } else { // General case
            retVal = currentNode.getNextNode().getElement();
            Node<T> continueNode = currentNode.getNextNode().getNextNode(); // Node after the one to be removed
            currentNode.setNextNode(continueNode);
        }

        size--;
        modCount++;
        return retVal;
    }

    @Override
    public void set(int index, T element) {
        // If the list is empty
        // if the index is out of bounds
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        Node<T> currentNode = head;
        for (int i = 0; i < index; i++) { // Iterate to the node to change
            currentNode = currentNode.getNextNode();
        }
        currentNode.setElement(element);
        modCount++;
    }

    @Override
    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        Node<T> currentNode = head;
        for (int i = 0; i < index; i++) {
            currentNode = currentNode.getNextNode();
        }
        return currentNode.getElement();
    }

    @Override
    public int indexOf(T element) {
        int index = 0;
        Node<T> currentNode = head; // Start at the beginning, head is the first node we ill look at
        while (currentNode != null && !element.equals(currentNode.getElement())) { // either I found it or i didnt
            currentNode = currentNode.getNextNode();
            index++;
        }
        if (currentNode == null) { // Did not find it, OR index >= size()
            index = -1;
        }
        return index;
    }

    @Override
    public T first() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        return head.getElement();
    }

    @Override
    public T last() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        return tail.getElement();
    }

    @Override
    public boolean contains(T target) {
        return indexOf(target) > -1;
    }

    @Override
    public boolean isEmpty() {
        return size == 0; // Could also do: head == null; OR tail == null; OR size() == 0;
    }

    @Override
    public int size() {
        return size;
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
        return new SLLIterator();
    }

    @Override
    public ListIterator<T> listIterator() {
        throw new UnsupportedOperationException("Unimplemented method 'listIterator'");
    }

    @Override
    public ListIterator<T> listIterator(int startingIndex) {
        throw new UnsupportedOperationException("Unimplemented method 'listIterator'");
    }

    /**
     * Basic Iterator for IUSingleLinkedList
     */
    private class SLLIterator implements Iterator<T> {
        private Node<T> nextNode;
        private int iterModCount;
        private boolean canRemove;

        /** Intitialize iterator before the first node */
        public SLLIterator() {
            nextNode = head;
            iterModCount = modCount;
            canRemove = false;
        }

        @Override
        public boolean hasNext() {
            if (iterModCount != modCount) {
                throw new ConcurrentModificationException();
            }
            if (isEmpty()) {
                return false;
            }
            return nextNode != null;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            if (isEmpty()) {
                throw new NoSuchElementException();
            }
            T retVal = nextNode.getElement();
            nextNode = nextNode.getNextNode();
            canRemove = true;
            return retVal;
        }

        @Override
        public void remove() {
            if (iterModCount != modCount) {
                throw new ConcurrentModificationException();
            }
            if (!canRemove) {
                throw new IllegalStateException();
            }
            canRemove = false;
            // If the node to be removed is the second elem
            if (head.getNextNode() == nextNode) { // Need to remove first element/head
                head = head.getNextNode();
                if (head == null) { // or size == 1;
                    tail = null;
                }
            } else {
                // general case - first 'previousPreviousNode'
                Node<T> prevPrevNode = head;
                while (prevPrevNode.getNextNode().getNextNode() != nextNode) { // Finds the prev prev node
                    prevPrevNode = prevPrevNode.getNextNode();
                }
                prevPrevNode.setNextNode(nextNode);
                if (prevPrevNode.getNextNode() == null) { // If the last element was removed
                    tail = prevPrevNode;
                }
            }
            size--;
            modCount++;
            iterModCount++;

        }

    }
}
