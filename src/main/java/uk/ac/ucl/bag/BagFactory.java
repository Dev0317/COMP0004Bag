package uk.ac.ucl.bag;

import java.util.Comparator;

/**
 * A factory to create Bag objects. The class is implemented as a Singleton, such that only one factory
 * object can be created. The factory can be configured with the name of the bag class it creates instances
 * of.
 */
public class BagFactory<T> {
  private static BagFactory factory = null;

  /**
   * Return the single factory object, creating it if necessary.
   * @return a new instance of BagFactory
   */
  public static BagFactory getInstance() {
    if (factory == null) factory = new BagFactory();
    return factory;
  }

  // The name of the class that the factory will create objects of.
  private String bagClass;

  // The constructor is private to prevent code in any other class creating an instance.
  private BagFactory() {
  }

  /**
   * Set the class that the factory creates instances of.
   * @param aClass the name of the class.
   */

  public void setBagClass(String aClass)
  {
    bagClass = aClass;
  }

  /**
   * Create a bag that is an instance of the class the factory has been set to create.
   * @return The new bag.
   * @throws BagException If the class is not recognised as one from
   * which a bag object can be created.
   */
  public Bag<T> getBag() throws BagException {
    if (bagClass.equals("ArrayBag")) {
      return new ArrayBag<>();
    } else if (bagClass.equals("MapBag")) {
      return new MapBag<>();
    } else if (bagClass.equals("LinkedListBag")) {
      return new LinkedListBag<>();
    }
    throw new BagException
            ("Attempting to use BagFactory to create something that is not a Bag");
  }

  /**
   * Create a bag that is an instance of the class the factory has been set to create.
   * @param orderRelation allows the user to specify how comparison is done for the values
   * stored in a Bag
   * @return The new bag.
   * @throws BagException If the class is not recognised as one from
   * which a bag object can be created.
   */
  public Bag<T> getBag(Comparator<T> orderRelation) throws BagException {
    return getBag(Bag.MAX_SIZE, orderRelation);
  }

  /**
   * Create a bag that is an instance of the class the factory has been set to create, with the
   * given maximum bag size.
   * @param maxSize The maximum size of the new bag.
   * @param orderRelation allows the user to specify how comparison is done for the values
   * stored in a Bag
   * @return The new bag.
   * @throws BagException If the class is not recognised as one from
   * which a bag object can be created.
   */
  public Bag<T> getBag(int maxSize, Comparator<T> orderRelation) throws BagException {
    if (bagClass.equals("ArrayBag")) {
      return new ArrayBag<>(maxSize, orderRelation);
    } else if (bagClass.equals("MapBag")) {
      return new MapBag<>(maxSize, orderRelation);
    } else if (bagClass.equals("LinkedListBag")) {
      return new LinkedListBag<>(maxSize, orderRelation);
    }
    throw new BagException
      ("Attempting to use BagFactory to create something that is not a Bag");
  }
}
