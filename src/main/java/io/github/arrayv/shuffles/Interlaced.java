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

public class Interlaced extends Shuffle {

        public String getName() {
            return "Interlaced";
        }
        @Override
        public void shuffleArray(int[] array) {
            int currentLen = arrayVisualizer.getCurrentLength();
            boolean delay = arrayVisualizer.shuffleEnabled();

            int[] referenceArray = new int[currentLen];
            for (int i = 0; i < currentLen; i++) {
                referenceArray[i] = array[i];
            }

            int leftIndex = 1;
            int rightIndex = currentLen - 1;

            for (int i = 1; i < currentLen; i++) {
                if (i % 2 == 0) {
                    Writes.write(array, i, referenceArray[leftIndex++], delay ? 1 : 0, true, false);
                } else {
                    Writes.write(array, i, referenceArray[rightIndex--], delay ? 1 : 0, true, false);
                }
            }
        }
    
}
