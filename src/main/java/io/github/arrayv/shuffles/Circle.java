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

public class Circle extends Shuffle {

        public String getName() {
            return "First Circle Pass";
        }
        @Override
        public void shuffleArray(int[] array) {
            int currentLen = arrayVisualizer.getCurrentLength();
            boolean delay = arrayVisualizer.shuffleEnabled();
            Reads Reads = arrayVisualizer.getReads();
            makeRandom(arrayVisualizer);

            shuffle(array, 0, currentLen, delay ? 0.5 : 0, Writes);

            int n = 1;
            for (; n < currentLen; n*=2);

            circleSortRoutine(array, 0, n-1, currentLen, delay ? 0.5 : 0, Reads, Writes);
        }

        public void circleSortRoutine(int[] array, int lo, int hi, int end, double sleep, Reads Reads, Writes Writes) {
            if (lo == hi) return;

            int high = hi;
            int low = lo;
            int mid = (hi - lo) / 2;

            while (lo < hi) {
                if (hi < end && Reads.compareIndices(array, lo, hi, sleep / 2, true) > 0)
                    Writes.swap(array, lo, hi, sleep, true, false);

                lo++;
                hi--;
            }

            circleSortRoutine(array, low, low + mid, end, sleep/2, Reads, Writes);
            if (low + mid + 1 < end) circleSortRoutine(array, low + mid + 1, high, end, sleep/2, Reads, Writes);
        }
    
}
