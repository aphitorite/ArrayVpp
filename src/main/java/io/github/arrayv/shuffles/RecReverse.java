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

public class RecReverse extends Shuffle {

        public String getName() {
            return "Recursive Reversal";
        }
        @Override
        public void shuffleArray(int[] array) {
            int currentLen = arrayVisualizer.getCurrentLength();
            boolean delay = arrayVisualizer.shuffleEnabled();

            reversalRec(array, 0, currentLen, delay ? 1 : 0, Writes);
        }

        public void reversalRec(int[] array, int a, int b, double sleep, Writes Writes) {
            if (b-a < 2) return;

            Writes.reversal(array, a, b-1, sleep, true, false);

            int m = (a+b)/2;
            this.reversalRec(array, a, m, sleep/2, Writes);
            this.reversalRec(array, m, b, sleep/2, Writes);
        }
    
}
