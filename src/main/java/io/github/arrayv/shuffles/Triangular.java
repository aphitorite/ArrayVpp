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

public class Triangular extends Shuffle {

        public String getName() {
            return "Triangular";
        }
        @Override
        public void shuffleArray(int[] array) {
            int currentLen = arrayVisualizer.getCurrentLength();
            boolean delay = arrayVisualizer.shuffleEnabled();
            int[] triangle = new int[currentLen];

            int j = 0, k = 2;
            int max = 0;

            for (int i = 1; i < currentLen; i++, j++) {
                if (i == k) {
                    j = 0;
                    k *= 2;
                }
                triangle[i] = triangle[j]+1;
                if (triangle[i] > max) max = triangle[i];
            }
            int[] cnt = new int[max+1];

            for (int i = 0; i < currentLen; i++)
                cnt[triangle[i]]++;

            for (int i = 1; i < cnt.length; i++)
                cnt[i] += cnt[i-1];

            for (int i = currentLen-1; i >= 0; i--)
                triangle[i] = --cnt[triangle[i]];

            int[] temp = Arrays.copyOf(array, currentLen);
            for (int i = 0; i < currentLen; i++)
                Writes.write(array, i, temp[triangle[i]], delay ? 1 : 0, true, false);
        }
    
}
