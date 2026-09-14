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

public class Almost extends Shuffle {

        public String getName() {
            return "Slight Shuffle";
        }
        @Override
        public void shuffleArray(int[] array) {
            int currentLen = arrayVisualizer.getCurrentLength();
            makeRandom(arrayVisualizer);

            for (int i = 0; i < Math.max(currentLen / 20, 1); i++){
                Writes.swap(array, random.nextInt(currentLen), random.nextInt(currentLen), 0, true, false);

                if (arrayVisualizer.shuffleEnabled()) Delays.sleep(10);
            }
        }
    
}
