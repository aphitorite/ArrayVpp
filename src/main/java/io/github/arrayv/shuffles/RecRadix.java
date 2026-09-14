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

public class RecRadix extends Shuffle {

        public String getName() {
            return "Recursive Final Radix";
        }
        @Override
        public void shuffleArray(int[] array) {
            int currentLen = arrayVisualizer.getCurrentLength();
            boolean delay = arrayVisualizer.shuffleEnabled();

            weaveRec(array, 0, currentLen, 1, delay ? 0.5 : 0, Writes);
        }

        public void weaveRec(int[] array, int pos, int length, int gap, double delay, Writes Writes) {
            if (length < 2) return;

            int mod2 = length % 2;
            length -= mod2;
            int mid = length/2;
            int[] temp = new int[mid];

            for (int i = pos, j = 0; i < pos+gap*mid; i+=gap, j++)
                Writes.write(temp, j, array[i], 0, false, true);

            for (int i = pos+gap*mid, j = pos, k = 0; i < pos+gap*length; i+=gap, j+=2*gap, k++) {
                Writes.write(array, j, array[i], delay, true, false);
                Writes.write(array, j+gap, temp[k], delay, true, false);
            }

            weaveRec(array, pos, mid+mod2, 2*gap, delay/2, Writes);
            weaveRec(array, pos+gap, mid, 2*gap, delay/2, Writes);
        }
    
}
