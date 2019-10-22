package uk.ac.ucl.bag;

import java.util.Comparator;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Objects;

/*
   This class implements Bags using an ArrayList as the internal data structure.
   It is implemented as a JavaBeans component @see AbstractBag.java
*/
public class ArrayBag<T> extends AbstractBag<T> {
  /*
     Objects of class Element store a value and its occurrence count. It is implemented as a JavaBeans component
     @see AbstractBag.java. Note that the class is static (nested top level class) and does not have access to the scope
     of class ArrayBag even though it is nested inside the class. This means that the type variable
     T is not in scope, so class Element has to be declared using a different type variable E.
  */
  public static class Element<E> {
    public int count;
    public E value;
    public Element(int count, E value) {
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

    public E getValue() {
      return value;
    }

    public void setValue(E value) {
      this.value = value;
    }
  }

  private int maxSize;
  private ArrayList<Element<T>> contents;

  public ArrayBag() throws BagException {
    this(MAX_SIZE, Comparator.comparing(Objects::hashCode));
  }

  public ArrayBag(int maxSize, Comparator<T> orderRelation) throws BagException {
    if (maxSize > MAX_SIZE) {
      throw new BagException("Attempting to create a Bag with size greater than maximum");
    }
    if (maxSize < 1) {
      throw new BagException("Attempting to create a Bag with size less than 1");
    }
    this.maxSize = maxSize;
    contents = new ArrayList<>();
    this.orderRelation = orderRelation;
  }

  public int getMaxSize() {
    return maxSize;
  }

  public void setMaxSize(int maxSize) {
    this.maxSize = maxSize;
  }

  public ArrayList<Element<T>> getContents() {
    return contents;
  }

  public void setContents(ArrayList<Element<T>> contents) {
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
      contents.add(new Element<>(1,value));
    } else {
      throw new BagException("Bag is full");
    }
  }

  public void addWithOccurrences(T value, int occurrences) throws BagException {
    for (int i = 0 ; i < occurrences ; i++) {
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
    for (int i = 0 ; i < contents.size() ; i++) {
      Element<T> element = contents.get(i);
      if (orderRelation.compare(element.value, value) == 0) {
        element.count--;
        if (element.count == 0) {
          contents.remove(element);
          return;
        }
      }
    }
  }

  public boolean isEmpty()
  {
    return contents.size() == 0;
  }

  public int size()
  {
    return contents.size();
  }

  /* This class implements the iterator interface to allow the unique values in ArrayBag objects to be iterated through.
   * The iterator returns each unique value without any copies (i.e., one value for each element in the
   * ArrayList data structure). Notice that this class is not declared static and is a nested inner class, which
   * does have access to the scope of the ArrayBag class, allowing it to access the ArrayList data structure
   * directly. The use of the static keyword when declaring nested classes makes an important difference.
   * The class is still private, though, and cannot be accessed outside the scope of the ArrayBag class.
   * However, a reference to an object of the class can be returned as a reference of type Iterator.
   */
  private class ArrayBagUniqueIterator implements Iterator<T> {
    private int index = 0;

    public boolean hasNext() {
      if (index < contents.size()) return true;
      return false;
    }

    public T next()
    {
      return contents.get(index++).value;
    }
  }

  /*
    Return an iterator object. Code calling this method will get an object that behaves as an iterator but does not
    need to know the actual class of the object.
   */
  public Iterator<T> iterator()
  {
    return new ArrayBagUniqueIterator();
  }

  /*
    This class implements an additional iterator that returns all values in a bag including a value for each copy.
    It is also a nested inner class.
   */
  private class ArrayBagIterator implements Iterator<T> {
    private int index = 0;
    private int count = 0;
    
    public boolean hasNext() {
      if (index < contents.size()) {
        if (count < contents.get(index).count) return true;
        if ((count == contents.get(index).count) && ((index + 1) < contents.size())) return true;
      }
      return false;
    }

    public T next() {
      if (count < contents.get(index).count) {
        T value = contents.get(index).value;
        count++;
        return value;
      }
      count = 1;
      index++;
      return contents.get(index).value;
    }
  }

  public Iterator<T> allOccurrencesIterator()
  {
    return new ArrayBagIterator();
  }
}
