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

public class MovedElement extends Shuffle {

        public String getName() {
            return "Shifted Element";
        }
        @Override
        public void shuffleArray(int[] array) {
            int currentLen = arrayVisualizer.getCurrentLength();
            boolean delay = arrayVisualizer.shuffleEnabled();
            makeRandom(arrayVisualizer);

            int start = random.nextInt(currentLen);
            int dest = random.nextInt(currentLen);
            if (dest < start) {
                IndexedRotations.holyGriesMills(array, dest, start, start + 1, delay ? 1 : 0, true, false);
            } else {
                IndexedRotations.holyGriesMills(array, start, start + 1, dest, delay ? 1 : 0, true, false);
            }
        }
    
}
