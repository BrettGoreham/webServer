package model.vinmonopolet;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Comparator;

public class SortedMaxLengthListTest {

    @Test
    public void testMaxLengthFeature() {
        int[] intsToAdd = new int[] {0,1,2,3,4,5};

        SortedMaxLengthList<Integer> sortedMaxLengthList = new SortedMaxLengthList<Integer>(5, Integer::compareTo);

        for (int j : intsToAdd) {
            sortedMaxLengthList.add(j);
        }

        Assertions.assertEquals(5, sortedMaxLengthList.size());
    }

    @Test
    public void testOrderFeature(){
        int maxLength = 5;
        int[] intsToAdd = new int [] {5,2,4,3,1,0};
        int[] expectedAscendingOrder = new int[] {0,1,2,3,4};
        int[] expectedDescendingOrder = new int[] {5,4,3,2,1};

        SortedMaxLengthList<Integer> ascendingSortedList = new SortedMaxLengthList<>(maxLength, Integer::compareTo);

        Comparator<Integer> descendingComparator = (o1, o2) -> -1 * Integer.compare(o1, o2);
        SortedMaxLengthList<Integer> descendingSortedList = new SortedMaxLengthList<>(maxLength, descendingComparator);

        for (int j : intsToAdd) {
            ascendingSortedList.add(j);
            descendingSortedList.add(j);
        }

        for(int i = 0; i < maxLength; i++) {
            Assertions.assertEquals(expectedAscendingOrder[i], ascendingSortedList.get(i));
            Assertions.assertEquals(expectedDescendingOrder[i], descendingSortedList.get(i));
        }
    }
}
