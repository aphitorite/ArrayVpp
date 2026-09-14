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

public class RandomRuns extends Shuffle {

        public String getName() {
            return "Random Runs";
        }
        @Override
        public void shuffleArray(int[] array) {
            int n = arrayVisualizer.getCurrentLength();
            boolean d = arrayVisualizer.shuffleEnabled();
            makeRandom(arrayVisualizer);
            shuffle(array, 0, n, d?1:0, Writes);
            for(int i = 0; i < n;) {
            	int r = random.nextInt(n / 24);
            	sort(array, array, -1, i, Math.min(i + r, n), d?1:0, Writes);
            	i += r;
            }
        }
    
}
