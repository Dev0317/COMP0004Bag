package uk.ac.ucl.bag;

import java.util.*;

/*
   This class implements Bags using a HashMap as the internal data structure.
   It is implemented as a JavaBeans component @see AbstractBag.java
 */

public class MapBag <T> extends AbstractBag<T> {
    /*
     Objects of class MutableInt store the occurrence count of a value.
     It is implemented as a JavaBeans component @see AbstractBag.java
     */

    public static class MutableInt {
        int count;

        public MutableInt(int count) {this.count = count;}

        public MutableInt() {}

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }

    private int maxSize;
    private HashMap<T, MutableInt> contents;

    public MapBag() throws BagException {
        this(MAX_SIZE, Comparator.comparing(Objects::hashCode));
    }

    public MapBag(int maxSize, Comparator<T> orderRelation) throws BagException {
        if (maxSize > MAX_SIZE) {
            throw new BagException("Attempting to create a Bag with size greater than maximum");
        }
        if (maxSize < 1) {
            throw new BagException("Attempting to create a Bag with size less than 1");
        }
        this.maxSize = maxSize;
        contents = new HashMap<>();
        this.orderRelation = orderRelation;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public HashMap<T, MutableInt> getContents() {
        return contents;
    }

    public void setContents(HashMap<T, MutableInt> contents) {
        this.contents = contents;
    }

    public void add(T value) throws BagException {
        addWithOccurrences(value, 1);
    }

    public void addWithOccurrences(T value, int occurrences) throws BagException {
        for (T key : contents.keySet()) {
            if (orderRelation.compare(key, value) == 0) {
                contents.get(key).count += occurrences;
                return;
            }
        }
        if (contents.size() < maxSize) {
            contents.put(value, new MutableInt(occurrences));
        } else {
            throw new BagException("Bag is full");
        }
    }

    public boolean contains(T value) {
        for (T key : contents.keySet()) {
            if (orderRelation.compare(key, value) == 0) {
                return true;
            }
        }
        return false;
    }

    public int countOf(T value) {
        for (T key : contents.keySet()) {
            if (orderRelation.compare(key, value) == 0) {
                return contents.get(key).count;
            }
        }
        return 0;
    }

    public void remove(T value) {
        for (T key : contents.keySet()) {
            if (orderRelation.compare(key, value) == 0) {
                MutableInt occurrences = contents.get(key);
                occurrences.count--;
                if (occurrences.count == 0) {
                    contents.remove(key);
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

    /*
      Return an iterator object. Code calling this method will get an object that behaves as an iterator but does not
      need to know the actual class of the object.
     */
    public Iterator<T> iterator()
    {
        return contents.keySet().iterator();
    }

    /*
      This class implements an additional iterator that returns all values in a bag including a value for each copy.
      It is also a nested inner class.
     */
    private class MapBagIterator implements Iterator<T> {
        Iterator<Map.Entry<T, MutableInt>> iterator = contents.entrySet().iterator();
        Map.Entry<T, MutableInt> currentEntry = iterator.next();
        int count = 0;

        public boolean hasNext()
        {
            return count < currentEntry.getValue().count || iterator.hasNext();
        }

        public T next() {
            if (count < currentEntry.getValue().count) {
                count++;
                return currentEntry.getKey();
            }
            count = 1;
            currentEntry = iterator.next();
            return currentEntry.getKey();
        }
    }

    public Iterator<T> allOccurrencesIterator()
    {
        return new MapBagIterator();
    }
}
