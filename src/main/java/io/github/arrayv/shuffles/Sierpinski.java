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

public class Sierpinski extends Shuffle {

        public String getName() {
            return "Sierpinski Triangle";
        }
        @Override
        public void shuffleArray(int[] array) {
            int currentLen = arrayVisualizer.getCurrentLength();
            int[] triangle = new int[currentLen];
            triangleRec(triangle, 0, currentLen);

            int[] temp = Arrays.copyOf(array, currentLen);
            for (int i = 0; i < currentLen; i++)
                Writes.write(array, i, temp[triangle[i]], 1, true, false);
        }

        public void triangleRec(int[] array, int a, int b) {
            if (b-a < 2) return;
            if (b-a == 2) {
                array[a+1]++;
                return;
            }

            int h = (b-a)/3, t1 = (a+a+b)/3, t2 = (a+b+b+2)/3;
            for (int i = a;  i < t1; i++) array[i] += h;
            for (int i = t1; i < t2; i++) array[i] += 2*h;

            triangleRec(array, a, t1);
            triangleRec(array, t1, t2);
            triangleRec(array, t2, b);
        }
    
}
