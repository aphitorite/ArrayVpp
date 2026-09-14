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

public class HalfRotation extends Shuffle {

        public String getName() {
            return "Half Rotation";
        }
        @Override
        public void shuffleArray(int[] array) {
            int currentLen = arrayVisualizer.getCurrentLength();
            boolean delay = arrayVisualizer.shuffleEnabled();

            int a = 0, m = (currentLen + 1) / 2;

            if (currentLen % 2 == 0)
                while (m < currentLen) Writes.swap(array, a++, m++, delay ? 1 : 0, true, false);
            else {
                Highlights.clearMark(2);
                int temp = array[a];
                while (m < currentLen) {
                    Writes.write(array, a++, array[m], delay ? 0.5 : 0, true, false);
                    Writes.write(array, m++, array[a], delay ? 0.5 : 0, true, false);
                }
                Writes.write(array, a, temp, delay ? 0.5 : 0, true, false);
            }
        }
    
}
