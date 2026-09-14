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

public class Sawtooth extends Shuffle {

        public String getName() {
            return "Sawtooth";
        }
        @Override
        public void shuffleArray(int[] array) {
            int currentLen = arrayVisualizer.getCurrentLength();
            boolean delay = arrayVisualizer.shuffleEnabled();
            int count = 4;

            int k = 0;
            int[] temp = new int[currentLen];

            for (int j = 0; j < count; j++)
                for (int i = j; i < currentLen; i+=count)
                    Writes.write(temp, k++, array[i], 0, false, true);

            for (int i = 0; i < currentLen; i++)
                Writes.write(array, i, temp[i], delay ? 1 : 0, true, false);
        }
    
}
