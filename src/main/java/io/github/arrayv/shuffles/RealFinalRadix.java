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

public class RealFinalRadix extends Shuffle {

        public String getName() {
            return "Real Final Radix";
        }
        @Override
        public void shuffleArray(int[] array) {
            int currentLen = arrayVisualizer.getCurrentLength();

            int mask = 0;
            for (int i = 0; i < currentLen; i++)
                while (mask < array[i]) mask = (mask << 1) + 1;
            mask >>= 1;

            int[] counts = new int[mask+2];
            int[] tmp    = new int[currentLen];

            System.arraycopy(array, 0, tmp, 0, currentLen);

            for (int i = 0; i < currentLen; i++)
                counts[(array[i]&mask)+1]++;

            for (int i = 1; i < counts.length; i++)
                counts[i] += counts[i-1];

            for (int i = 0; i < currentLen; i++)
                Writes.write(array, counts[tmp[i]&mask]++, tmp[i], 1, true, false);
        }
    
}
