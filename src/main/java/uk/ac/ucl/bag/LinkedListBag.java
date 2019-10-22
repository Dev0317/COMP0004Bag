package uk.ac.ucl.bag;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;

/*
   This class implements Bags using a LinkedList as the internal data structure.
   It is implemented as a JavaBeans component @see AbstractBag.java
 */
public class LinkedListBag<T> extends AbstractBag<T> {
    /*
       Objects of class LinkedList store a linked list of Node objects that store a value and its
       occurrences. It is implemented as a JavaBeans component @see AbstractBag.java
       Note that the class is static (nested top level class) and does not have access to the scope
       of class ArrayBag even though it is nested inside the class. This means that the type variable
       T is not in scope, so class Element has to be declared using a different type variable U.
     */

    public static class LinkedList<U> implements Iterable<U> {

        public static class Node<V> {
            public V value;
            public Node<V> next;

            public Node(V value) {
                this.value = value;
                this.next = null;
            }

            public Node() {}

            public V getValue() {
                return value;
            }

            public void setValue(V value) {
                this.value = value;
            }

            public Node<V> getNext() {
                return next;
            }

            public void setNext(Node<V> next) {
                this.next = next;
            }
        }

        private Node<U> head;
        private int size;

        public LinkedList() {
            head = null;
            size = 0;
        }

        public Node<U> getHead() {
            return head;
        }

        public void setHead(Node<U> head) {
            this.head = head;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public void prepend(U e) {
            Node<U> newNode = new Node<>(e);
            newNode.next = head;
            head = newNode;
            size++;
        }

        public int size() {
            return this.size;
        }

        private class LinkedListIterator implements Iterator<U> {
            private LinkedList.Node<U> nextNode = head;
            private LinkedList.Node<U> curNode = null;
            private LinkedList.Node<U> prevNode = null;
            private boolean removedCurNode = false;

            public boolean hasNext() {
                return nextNode != null;
            }

            public U next() {
                prevNode = (removedCurNode)? prevNode : curNode;
                curNode = nextNode;
                removedCurNode = false;
                nextNode = nextNode.next;
                return curNode.value;
            }

            public void remove() {
                if (curNode != null) {
                    if (prevNode == null) {
                        head = curNode.next;
                        curNode.next = null;
                    } else {
                        prevNode.next = curNode.next;
                        curNode.next = null;
                    }
                    removedCurNode = true;
                    size--;
                }
            }
        }

        /*
          Return an iterator object. Code calling this method will get an object that behaves as an iterator but does not
          need to know the actual class of the object.
         */
        public Iterator<U> iterator() {
            return new LinkedListIterator();
        }

    }

    /*
      Objects of class Element store a value and its occurrence count. It is implemented as a JavaBeans component
      @see AbstractBag.java. Note that the class is static (nested top level class) and does not have access to the scope        of class ArrayBag even though it is nested inside the class. This means that the type variable
      T is not in scope, so class Element has to be declared using a different type variable E.
    */

    public static class Element<W> {
        public int count;
        public W value;

        public Element(int count, W value) {
            this.count = count;
            this.value = value;
        }

        public Element() {}

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public W getValue() {
            return value;
        }

        public void setValue(W value) {
            this.value = value;
        }
    }

    private int maxSize;
    private LinkedList<Element<T>> contents;

    public LinkedListBag() throws BagException {
        this(MAX_SIZE, Comparator.comparing(Objects::hashCode));
    }

    public LinkedListBag(int maxSize, Comparator<T> orderRelation) throws BagException {
        if (maxSize > MAX_SIZE) {
            throw new BagException("Attempting to create a Bag with size greater than maximum");
        }
        if (maxSize < 1) {
            throw new BagException("Attempting to create a Bag with size less than 1");
        }
        this.maxSize = maxSize;
        contents = new LinkedList<>();
        this.orderRelation = orderRelation;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public LinkedList<Element<T>> getContents() {
        return contents;
    }

    public void setContents(LinkedList<Element<T>> contents) {
        this.contents = contents;
    }

    public void add(T value) throws BagException {
        for (Element<T> element : contents) {
            if (orderRelation.compare(element.value, value) == 0) {
                element.count++;
                return;
            }
        }
        if (contents.size() < maxSize) {
            contents.prepend(new Element<>(1, value));
        } else {
            throw new BagException("Bag is full");
        }
    }

    public void addWithOccurrences(T value, int occurrences) throws BagException {
        for (int i = 0; i < occurrences; i++) {
            add(value);
        }
    }

    public boolean contains(T value) {
        for (Element<T> element : contents) {
            if (orderRelation.compare(element.value, value) == 0) {
                return true;
            }
        }
        return false;
    }

    public int countOf(T value) {
        for (Element<T> element : contents) {
            if (orderRelation.compare(element.value, value) == 0) {
                return element.count;
            }
        }
        return 0;
    }

    public void remove(T value) {
        Iterator<Element<T>> iterator = contents.iterator();

        while (iterator.hasNext()) {
            Element<T> element = iterator.next();
            if (orderRelation.compare(element.value, value) == 0) {
                element.count--;
                if (element.count == 0) {
                    iterator.remove();
                    return;
                }
            }
        }
    }

    public boolean isEmpty() {
        return contents.size() == 0;
    }

    public int size() {
        return contents.size();
    }

    /* This class implements the iterator interface to allow the unique values in LinkedList objects to be iterated through.
     * The iterator returns each unique value without any copies (i.e., one value for each element in the
     * LinkedList data structure). Notice that this class is not declared static and is a nested inner class, which
     * does have access to the scope of the LinkedListBag class, allowing it to access the LinkedList data structure
     * directly. The use of the static keyword when declaring nested classes makes an important difference.
     * The class is still private, though, and cannot be accessed outside the scope of the LinkedListBag class.
     * However, a reference to an object of the class can be returned as a reference of type Iterator.
     */
    private class LinkedListBagUniqueIterator implements Iterator<T> {
        Iterator<Element<T>> iterator = contents.iterator();

        public boolean hasNext() {
            return iterator.hasNext();
        }

        public T next() {
            return iterator.next().value;
        }
    }

    /*
      Return an iterator object. Code calling this method will get an object that behaves as an iterator but does not
      need to know the actual class of the object, which is private anyway.
     */
    public Iterator<T> iterator() {
        return new LinkedListBagUniqueIterator();
    }

    /*
      This class implements an additional iterator that returns all values in a bag including a value for each copy.
      It is also a nested inner class.
     */
    private class LinkedListBagIterator implements Iterator<T> {
        Iterator<Element<T>> iterator = contents.iterator();
        Element<T> currentElement = iterator.next();
        int count = 0;

        public boolean hasNext()
        {
            return count < currentElement.count || iterator.hasNext();
        }

        public T next() {
            if (count < currentElement.count) {
                count++;
                return currentElement.value;
            }
            count = 1;
            currentElement = iterator.next();
            return currentElement.value;
        }
    }

    public Iterator<T> allOccurrencesIterator() {
        return new LinkedListBagIterator();
    }
}

