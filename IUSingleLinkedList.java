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

    public IUSingleLinkedList() {
        head = tail = null;
        size = 0;
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
    }

    @Override
    public void add(T element) {
        addToRear(element);
    }

    @Override
    public void addAfter(T element, T target) {
        Node<T> newNode = new Node<T>(element);
        if (isEmpty() || !contains(target)) { //If the list is empty, or it does not contain the target element
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
            } else { //General case, added to the middle of a list
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
    }

    @Override
    public void add(int index, T element) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'add'");
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

            // Was it the last node?
            if (currentNode.getNextNode() == null) {
                tail = currentNode;
            }
        }

        // End of a list

        // only element

        size--;
        return retVal;
    }

    @Override
    public T remove(int index) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'remove'");
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
        for (Node<T> node : this) {
            str.append(node.toString());
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'iterator'");
    }

    @Override
    public ListIterator<T> listIterator() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'listIterator'");
    }

    @Override
    public ListIterator<T> listIterator(int startingIndex) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'listIterator'");
    }

}
