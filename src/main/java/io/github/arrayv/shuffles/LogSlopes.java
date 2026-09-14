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

public class LogSlopes extends Shuffle {

        public String getName() {
            return "Logarithmic Slopes";
        }
        @Override
        public void shuffleArray(int[] array) {
            int currentLen = arrayVisualizer.getCurrentLength();
            boolean delay = arrayVisualizer.shuffleEnabled();

            int[] temp = new int[currentLen];
            for (int i = 0; i < currentLen; i++)
                Writes.write(temp, i, array[i], 0, false, true);

            Writes.write(array, 0, 0, delay ? 1 : 0, true, false);
            for (int i = 1; i < currentLen; i++) {
                int log = (int) (Math.log(i) / Math.log(2));
                int power = (int) Math.pow(2, log);
                int value = temp[2 * (i - power) + 1];
                Writes.write(array, i, value, delay ? 1 : 0, true, false);
            }
        }
    
}
