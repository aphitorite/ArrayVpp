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

public class Organ extends Shuffle {

        public String getName() {
            return "Pipe Organ";
        }
        @Override
        public void shuffleArray(int[] array) {
            int currentLen = arrayVisualizer.getCurrentLength();
            boolean delay = arrayVisualizer.shuffleEnabled();
            int[] temp = new int[currentLen];

            for (int i = 0, j = 0; i < currentLen; i+=2){
                temp[j++] = array[i];
            }
            for (int i = 1, j = currentLen; i < currentLen; i+=2) {
                temp[--j] = array[i];
            }
            for (int i = 0; i < currentLen; i++){
                Writes.write(array, i, temp[i], delay ? 1 : 0, true, false);
            }
        }
    
}
