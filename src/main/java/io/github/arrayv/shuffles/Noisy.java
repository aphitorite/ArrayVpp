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

public class Noisy extends Shuffle {

        public String getName() {
            return "Noisy";
        }
        @Override
        public void shuffleArray(int[] array) {
            int currentLen = arrayVisualizer.getCurrentLength();
            boolean delay = arrayVisualizer.shuffleEnabled();
            makeRandom(arrayVisualizer);

            int i, size = Math.max(4, (int)(Math.sqrt(currentLen)/2));
            for (i = 0; i+size <= currentLen; i += random.nextInt(size-1)+1)
                shuffle(array, i, i+size, delay ? 0.5 : 0, Writes);
            shuffle(array, i, currentLen, delay ? 0.5 : 0, Writes);
        }
    
}
