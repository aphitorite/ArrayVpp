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

public class TriHeap extends Shuffle {

        public String getName() {
            return "Triangular Heapified";
        }
        @Override
        public void shuffleArray(int[] array) {
            int currentLen = arrayVisualizer.getCurrentLength();
            boolean delay = arrayVisualizer.shuffleEnabled();

            if (delay) Delays.setSleepRatio(Delays.getSleepRatio()*10);
            else       Delays.changeSkipped(true);

            TriangularHeapSort triangularHeapSort = new TriangularHeapSort(arrayVisualizer);
            triangularHeapSort.triangularHeapify(array, currentLen);

            if (delay) Delays.setSleepRatio(Delays.getSleepRatio()/10);
            else       Delays.changeSkipped(false);
        }
    
}
