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

public class ShuffledHead extends Shuffle {

        public String getName() {
            return "Scrambled Head";
        }
        @Override
        public void shuffleArray(int[] array) {
            int currentLen = arrayVisualizer.getCurrentLength();
            boolean delay = arrayVisualizer.shuffleEnabled();
            makeRandom(arrayVisualizer);

            int[] aux = new int[currentLen];
            int i = currentLen - 1, j = currentLen - 1, k = 0;
            while (i >= 0) {
                Highlights.markArray(2, i);
                if (random.nextDouble() < 1/7d)
                    Writes.write(aux, k++, array[i--], delay ? 1 : 0, false, true);
                else
                    Writes.write(array, j--, array[i--], delay ? 1 : 0, true, false);
            }
            Writes.arraycopy(aux, 0, array, 0, k, delay ? 1 : 0, true, false);
            shuffle(array, 0, j, delay ? 2 : 0, Writes);
        }
    
}
