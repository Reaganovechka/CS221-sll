/**
 * Linkable Node for linear data structures.
 * 
 * @author Reagan Ovechka
 */
public class Node<T> {
    private Node<T> nextNode;
    private T element;

    /**
     * Intitialize a new Node with the given element.
     * 
     * @param element
     */
    public Node(T element) {
        this.element = element;
        nextNode = null;
    }

    /**
     * Intitalize a Node with the given element and next Node.
     * 
     * @param element
     * @param nextNode
     */
    public Node(T element, Node<T> nextNode) {
        this.element = element;
        this.nextNode = nextNode;
    }

    public Node<T> getNextNode() {
        return nextNode;
    }

    public void setNextNode(Node<T> nextNode) {
        this.nextNode = nextNode;
    }

    public T getElement() {
        return element;
    }

    public void setElement(T element) {
        this.element = element;
    }

}
