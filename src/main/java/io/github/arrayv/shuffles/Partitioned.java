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

public class Partitioned extends Shuffle {

        public String getName() {
            return "Partitioned";
        }
        @Override
        public void shuffleArray(int[] array) {
            int currentLen = arrayVisualizer.getCurrentLength();
            boolean delay = arrayVisualizer.shuffleEnabled();
            makeRandom(arrayVisualizer);

            this.sort(array, array, -1, 0, currentLen, delay ? 0.5 : 0, Writes);
            Highlights.clearMark(2);
            this.shuffle(array, 0, currentLen/2, delay ? 0.5 : 0, Writes);
            this.shuffle(array, currentLen/2, currentLen, delay ? 0.5 : 0, Writes);
        }
    
}
