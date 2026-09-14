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

public class Pairwise extends Shuffle {

        public String getName() {
            return "Final Pairwise Pass";
        }
        @Override
        public void shuffleArray(int[] array) {
            int currentLen = arrayVisualizer.getCurrentLength();
            boolean delay = arrayVisualizer.shuffleEnabled();
            Reads Reads = arrayVisualizer.getReads();
            makeRandom(arrayVisualizer);

            shuffle(array, 0, currentLen, delay ? 0.5 : 0, Writes);

            //create pairs
            for (int i = 1; i < currentLen; i+=2)
                if (Reads.compareIndices(array, i - 1, i, delay ? 0.5 : 0, true) > 0)
                    Writes.swap(array, i-1, i, delay ? 0.5 : 0, true, false);

            Highlights.clearMark(2);

            int[] temp = new int[currentLen];

            //sort the smaller and larger of the pairs separately with pigeonhole sort
            for (int m = 0; m < 2; m++) {
                for (int k = m; k < currentLen; k+=2)
                    Writes.write(temp, array[k], temp[array[k]] + 1, 0, false, true);

                int i = 0, j = m;
                while (true) {
                    while (i < currentLen && temp[i] == 0) i++;
                    if (i >= currentLen) break;

                    Writes.write(array, j, i, delay ? 0.5 : 0, true, false);

                    j+=2;
                    Writes.write(temp, i, temp[i] - 1, 0, false, true);
                }
            }
        }
    
}
