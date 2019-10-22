package uk.ac.ucl.bag;

import org.junit.Before;
import org.junit.Test;
import java.io.IOException;
import java.util.*;
import static org.junit.Assert.*;

public class MapBagTest {

    private Bag<ArrayList<String>> bag;
    private BagFactory<ArrayList<String>> bagFactory;

    @Before
    public void setUp() throws Exception {
        bagFactory = BagFactory.getInstance();
        bagFactory.setBagClass("MapBag");
        bag = bagFactory.getBag(2, Comparator.comparing(ArrayList::toString));

        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("foo");
        arrayList.add("bar");
        bag.add(arrayList);

        ArrayList<String> arrayList2 = new ArrayList<>();
        arrayList2.add("mar");
        arrayList2.add("roo");
        arrayList2.add("oon");
        bag.add(arrayList2);

        ArrayList<String> arrayList3 = new ArrayList<>();
        arrayList3.add("mar");
        arrayList3.add("roo");
        arrayList3.add("oon");
        bag.add(arrayList3);
    }

    @Test
    public void testToString() {
        assertTrue(bag.toString().equals("[[mar, roo, oon]: 2, [foo, bar]: 1]"));
    }

    @Test (expected = BagException.class)
    public void testAddToFullBag() throws BagException{
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("John");
        arrayList.add("Doe");
        arrayList.add("Smith");
        bag.add(arrayList);
    }

    @Test
    public void testContains() {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("mar");
        arrayList.add("roo");
        arrayList.add("oon");
        assertTrue(bag.contains(arrayList) && !bag.contains(new ArrayList<>()));
    }

    @Test
    public void testCountOf() throws BagException {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("foo");
        arrayList.add("bar");

        ArrayList<String> arrayList2 = new ArrayList<>();
        arrayList2.add("John");
        arrayList2.add("Doe");
        arrayList2.add("Smith");

        assertTrue(bag.countOf(arrayList) == 1 && bag.countOf(arrayList2) == 0);
    }

    @Test
    public void testRemove() {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("foo");
        arrayList.add("bar");
        for(int i = 0; i < 2; i++) {
            bag.remove(arrayList);
        }
        assertTrue(bag.toString().equals("[[mar, roo, oon]: 2]"));
    }

    @Test
    public void testIsEmpty() throws BagException {
        assertTrue(!bag.isEmpty() && bagFactory.getBag(Comparator.comparing(ArrayList::toString)).isEmpty());
    }

    @Test
    public void testSize() {
        assertEquals(2, bag.size());
    }

    @Test
    public void testIterator() {
        Iterator<ArrayList<String>> iterator = bag.iterator();
        ArrayList<ArrayList<String>> actualValues = new ArrayList<>();
        ArrayList<ArrayList<String>> expectedValues = new ArrayList<>();

        ArrayList<String> arrayList2 = new ArrayList<>();
        arrayList2.add("mar");
        arrayList2.add("roo");
        arrayList2.add("oon");
        expectedValues.add(arrayList2);

        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("foo");
        arrayList.add("bar");
        expectedValues.add(arrayList);

        while (iterator.hasNext()) {
            actualValues.add(iterator.next());
        }

        assertTrue(expectedValues.equals(actualValues));
    }


    @Test
    public void testAllOccurrencesIterator() {
        Iterator<ArrayList<String>> iterator = bag.allOccurrencesIterator();
        ArrayList<ArrayList<String>> actualValues = new ArrayList<>();
        ArrayList<ArrayList<String>> expectedValues = new ArrayList<>();

        ArrayList<String> arrayList2 = new ArrayList<>();
        arrayList2.add("mar");
        arrayList2.add("roo");
        arrayList2.add("oon");
        expectedValues.add(arrayList2);
        expectedValues.add(arrayList2);

        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("foo");
        arrayList.add("bar");
        expectedValues.add(arrayList);

        while (iterator.hasNext()) {
            actualValues.add(iterator.next());
        }

        assertTrue(expectedValues.equals(actualValues));
    }

    @Test
    public void testRemoveAllCopies() {
        bag.removeAllCopies();
        assertTrue(bag.toString().equals("[[mar, roo, oon]: 1, [foo, bar]: 1]"));
    }

    @Test
    public void testSubtract() throws BagException {
        Bag<ArrayList<String>> bag2 = bagFactory.getBag(Comparator.comparing(ArrayList::toString));

        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("foo");
        arrayList.add("bar");
        bag2.addWithOccurrences(arrayList, 3);

        ArrayList<String> arrayList2 = new ArrayList<>();
        arrayList2.add("mar");
        arrayList2.add("roo");
        arrayList2.add("oon");
        bag2.add(arrayList2);

        ArrayList<String> arrayList3 = new ArrayList<>();
        arrayList3.add("John");
        arrayList3.add("Doe");
        arrayList3.add("Smith");
        bag2.add(arrayList3);

        assertTrue(bag2.subtract(bag).toString().equals("[[John, Doe, Smith]: 1, [foo, bar]: 2]"));
    }

    @Test
    public void testPersist() throws IOException, BagException {
        BagFactory<ArrayList<Map<Character, String>>> bagFactory = BagFactory.getInstance();
        bagFactory.setBagClass("MapBag");
        Bag<ArrayList<Map<Character, String>>> bag = bagFactory.getBag(Comparator.comparing(ArrayList::toString));

        ArrayList<Map<Character, String>> arrayList = new ArrayList<>();

        Map<Character, String> map = new HashMap<>();
        map.put('A', "Ant");
        map.put('B', "Beetle");
        map.put('C', "Car");

        Map<Character, String> map1 = new HashMap<>();
        map1.put('D', "Drum");
        map1.put('E', "Elephant");

        Map<Character, String> map2 = new HashMap<>();
        map2.put('F', "Fun");

        arrayList.add(map);
        arrayList.add(map1);
        arrayList.add(map2);

        bag.add(arrayList);

        ArrayList<Map<Character, String>> arrayList2 = new ArrayList<>();

        Map<Character, String> map3 = new HashMap<>();
        map3.put('G', "Germ");
        map3.put('H', "Hobbit");
        map3.put('I', "Indigo");

        arrayList2.add(map3);

        bag.addWithOccurrences(arrayList2, 2);

        bag.persist("tmp.xml");
        assertTrue(bag.toString().equals(Bag.loadFrom("tmp.xml").toString()));
    }
}