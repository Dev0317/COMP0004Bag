package uk.ac.ucl.bag;

/**
 * This class implements methods common to all concrete bag implementations
 * but does not represent a complete bag implementation.<br />
 *
 * New bag objects are created using a BagFactory, which can be configured in the application
 * setup to select which bag implementation is to be used.
 * is
 *
 * All concrete bag classes and their components have been written to conform to the JavaBeans
 * standard available from https://docs.oracle.com/javase/tutorial/javabeans/writing/index.html,
 * to make use of bean persistence. This allows bags of any type conforming to the JavaBeans
 * standard (All collections part of the java collections framework are written as JavaBeans
 * components) to be stored and retrieved from storage.
 *
 * In order to function as a JavaBeans class, a class must obey certain conventions. It must
 * be serializable, have public access, provide a default no argument constructor and
 * bean properties must be accessible via getter and setter methods. Evidently, this undermines
 * restrictions imposed by encapsulation. However, this problem can be solved by convention,
 * just as convention stipulates Bag objects only be instantiated from a proper BagFactory. The
 * Bag interface does not provide documentation for methods part of the JavaBeans API.
 * Therefore access to the API is restricted to automated tools by convention.
 */
import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.Iterator;

public abstract class AbstractBag<T> implements Bag<T> {

  // The order relation is a binary relation that ranks values stored in a Bag against each other.

  Comparator<T> orderRelation;

  public Bag<T> createMergedAllOccurrences(Bag<T> b) throws BagException {
    Bag<T> result = BagFactory.getInstance().getBag(Comparator.naturalOrder());
    for (T value : this)
    {
      result.addWithOccurrences(value, this.countOf(value));
    }
    for (T value : b)
    {
      result.addWithOccurrences(value, b.countOf(value));
    }
    return result;
  }

  public Bag<T> createMergedAllUnique(Bag<T> b) throws BagException {
    Bag<T> result = BagFactory.getInstance().getBag(Comparator.naturalOrder());
    for (T value : this)
    {
      if (!result.contains(value)) result.add(value);
    }
    for (T value : b)
    {
      if (!result.contains(value)) result.add(value);
    }
    return result;
  }

  @Override
  public String toString() {
    // The StringBuilder class is used for efficient append operations in a loop
    StringBuilder sb = new StringBuilder();
    Iterator<T> iterator = this.iterator();

    sb.append("[");
    if (this.size() > 0) {
      T value = iterator.next();
      sb.append(value.toString()).append(": ").append(this.countOf(value));

      while (iterator.hasNext()) {
        value = iterator.next();
        sb.append(", ").append(value.toString()).append(": ").append(this.countOf(value));
      }
    }
    sb.append("]");

    return sb.toString();
  }

  public void removeAllCopies() {
    for (T value : this) {
      while (this.countOf(value) > 1) {
        this.remove(value);
      }
    }
  }

  public Bag<T> subtract(Bag<T> bag) throws BagException{
    Bag<T> result = BagFactory.getInstance().getBag(this.orderRelation);

    for (T value : this) {
      result.addWithOccurrences(value, this.countOf(value));
    }

    for (T value: bag) {
      int i = 0;
      while (i < bag.countOf(value) && result.contains(value)) {
        result.remove(value);
        i++;
      }
    }

    return result;
  }

  public void persist(String path) throws IOException {
    XMLEncoder xmlEncoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(path)));
    xmlEncoder.writeObject(this);
    xmlEncoder.close();
  }


}
