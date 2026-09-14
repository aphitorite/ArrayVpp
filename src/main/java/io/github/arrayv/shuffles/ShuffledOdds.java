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

public class ShuffledOdds extends Shuffle {

        public String getName() {
            return "Scrambled Odds";
        }
        @Override
        public void shuffleArray(int[] array) {
            int currentLen = arrayVisualizer.getCurrentLength();
            makeRandom(arrayVisualizer);

            for (int i = 1; i < currentLen; i += 2){
                int randomIndex = (((random.nextInt(currentLen - i) / 2)) * 2) + i;
                Writes.swap(array, i, randomIndex, 0, true, false);

                if (arrayVisualizer.shuffleEnabled()) Delays.sleep(2);
            }
        }
    
}
