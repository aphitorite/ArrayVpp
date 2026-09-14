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

public class GrayCode extends Shuffle {

        public String getName() {
            return "Gray Code Fractal";
        }
        @Override
        public void shuffleArray(int[] array) {
            int currentLen = arrayVisualizer.getCurrentLength();
            boolean delay = arrayVisualizer.shuffleEnabled();

            reversalRec(array, 0, currentLen, false, delay ? 1 : 0, Writes);
        }

        public void reversalRec(int[] array, int a, int b, boolean bw, double sleep, Writes Writes) {
            if (b-a < 3) return;

            int m = (a+b)/2;

            if (bw) Writes.reversal(array, a, m-1, sleep, true, false);
            else    Writes.reversal(array, m, b-1, sleep, true, false);

            this.reversalRec(array, a, m, false, sleep/2, Writes);
            this.reversalRec(array, m, b, true, sleep/2, Writes);
        }
    
}
