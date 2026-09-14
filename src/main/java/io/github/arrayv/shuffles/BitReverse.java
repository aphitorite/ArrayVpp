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

public class BitReverse extends Shuffle {

        @Override
        public String getName() {
            return "Bit Reversal";
        }
        @Override
        public void shuffleArray(int[] array) {
            int currentLen = arrayVisualizer.getCurrentLength();
            int len = 1 << (int)(Math.log(arrayVisualizer.getCurrentLength())/Math.log(2));
            boolean delay = arrayVisualizer.shuffleEnabled();
            boolean pow2 = len == currentLen;

            int[] temp = Arrays.copyOf(array, currentLen);
            for (int i = 0; i < len; i++) array[i] = i;

            int m = 0;
            int d1 = len>>1, d2 = d1+(d1>>1);

            for (int i = 1; i < len-1; i++) {
                int j = d1;

                for (
                    int k = i, n = d2;
                    (k&1) == 0;
                    j -= n, k >>= 1, n >>= 1
                );
                m += j;
                if (m > i) Writes.swap(array, i, m, delay ? 1 : 0, true, false);
            }
            Highlights.clearMark(2);

            if (!pow2) {
                for (int i = len; i < currentLen; i++)
                    Writes.write(array, i, array[i-len], 0.5, true, false);

                int[] cnt = new int[len];

                for (int i = 0; i < currentLen; i++)
                    cnt[array[i]]++;

                for (int i = 1; i < cnt.length; i++)
                    cnt[i] += cnt[i-1];

                for (int i = currentLen-1; i >= 0; i--)
                    Writes.write(array, i, --cnt[array[i]], 0.5, true, false);
            }
            int[] bits = Arrays.copyOf(array, currentLen);

            for (int i = 0; i < currentLen; i++)
                Writes.write(array, i, temp[bits[i]], 0, true, false);
        }
    
}
