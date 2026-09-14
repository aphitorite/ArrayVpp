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

public class DoubleLayered extends Shuffle {

        public String getName() {
            return "Double Layered";
        }
        @Override
        public void shuffleArray(int[] array) {
            int currentLen = arrayVisualizer.getCurrentLength();

            for (int i = 0; i < currentLen / 2; i += 2) {
                Writes.swap(array, i, currentLen - i - 1, 0, true, false);
                if (arrayVisualizer.shuffleEnabled()) Delays.sleep(1);
            }
        }
    
}
