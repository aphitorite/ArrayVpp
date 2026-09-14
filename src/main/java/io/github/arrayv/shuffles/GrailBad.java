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

public class GrailBad extends Shuffle {

        public String getName() {
            return "Grailsort Adversary";
        }
        @Override
        public void shuffleArray(int[] array) {
            int currentLen = arrayVisualizer.getCurrentLength();
            boolean delay = arrayVisualizer.shuffleEnabled();
            makeRandom(arrayVisualizer);

            if (currentLen <= 16) Writes.reversal(array, 0, currentLen-1, delay ? 1 : 0, true, false);
            else {
                int blockLen = 1;
                while (blockLen * blockLen < currentLen) blockLen *= 2;

                int numKeys = (currentLen - 1) / blockLen + 1;
                int keys = blockLen + numKeys;

                shuffle(array, 0, currentLen, delay ? 0.25 : 0, Writes);
                sort(array, array, -1, 0, keys, delay ? 0.25 : 0, Writes);
                Writes.reversal(array, 0, keys-1, delay ? 0.25 : 0, true, false);
                Highlights.clearMark(2);
                sort(array, array, -1, keys, currentLen, delay ? 0.25 : 0, Writes);

                push(array, keys, currentLen, blockLen, delay ? 0.25 : 0, Writes);
            }
        }

        public void rotate(int[] array, int a, int m, int b, double sleep, Writes Writes) {
            Writes.reversal(array, a, m-1, sleep, true, false);
            Writes.reversal(array, m, b-1, sleep, true, false);
            Writes.reversal(array, a, b-1, sleep, true, false);
        }

        public void push(int[] array, int a, int b, int bLen, double sleep, Writes Writes) {
            int len = b-a,
                b1 = b - len%bLen, len1 = b1-a;
            if (len1 <= 2*bLen) return;

            int m = bLen;
            while (2*m < len) m *= 2;
            m += a;

            if (b1-m < bLen) push(array, a, m, bLen, sleep, Writes);
            else {
                m = a+b1-m;
                rotate(array, m-(bLen-2), b1-(bLen-1), b1, sleep, Writes);
                Writes.multiSwap(array, a, m, sleep/2, true, false);
                rotate(array, a, m, b1, sleep, Writes);
                m = a+b1-m;

                push(array, a, m, bLen, sleep/2, Writes);
                push(array, m, b, bLen, sleep/2, Writes);
            }
        }
    
}
