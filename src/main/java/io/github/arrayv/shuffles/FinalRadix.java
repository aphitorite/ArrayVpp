package io.github.arrayv.shuffles;

import java.util.*;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.shuffles.templates.Shuffle;
import io.github.arrayv.sorts.select.MaxHeapSort;
import io.github.arrayv.sorts.select.PoplarHeapSort;
import io.github.arrayv.sorts.select.SmoothSort;
import io.github.arrayv.sorts.select.TriangularHeapSort;
import io.github.arrayv.sorts.templates.PDQSorting;
import io.github.arrayv.utils.*;

public class FinalRadix extends Shuffle {

        public String getName() {
            return "Final Radix";
        }
        @Override
        public void shuffleArray(int[] array) {
            int currentLen = arrayVisualizer.getCurrentLength();
            boolean delay = arrayVisualizer.shuffleEnabled();

            currentLen -= currentLen % 2;
            int mid = currentLen/2;
            int[] temp = new int[mid];

            for (int i = 0; i < mid; i++)
                Writes.write(temp, i, array[i], 0, false, true);

            for (int i = mid, j = 0; i < currentLen; i++, j+=2) {
                Writes.write(array, j, array[i], delay ? 1 : 0, true, false);
                Writes.write(array, j+1, temp[i-mid], delay ? 1 : 0, true, false);
            }
        }
    
}
