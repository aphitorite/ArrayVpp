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

public class RandomShuffle extends Shuffle {

        // If you want to learn why the random shuffle was changed,
        // I highly encourage you read this. It's quite fascinating:
        // http://datagenetics.com/blog/november42014/index.html

        public String getName() {
            return "Randomly";
        }
        @Override
        public void shuffleArray(int[] array) {
            int currentLen = arrayVisualizer.getCurrentLength();
            boolean delay = arrayVisualizer.shuffleEnabled();
            makeRandom(arrayVisualizer);
            shuffle(array, 0, currentLen, delay ? 1 : 0, Writes);
        }
    
}
