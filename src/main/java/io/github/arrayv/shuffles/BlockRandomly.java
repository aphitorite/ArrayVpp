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

public class BlockRandomly extends Shuffle {

        @Override
        public String getName() {
            return "Randomly w/ Blocks";
        }
        @Override
        public void shuffleArray(int[] array) {
            int currentLen = arrayVisualizer.getCurrentLength();
            int blockSize = pow2lte((int)Math.sqrt(currentLen));
            currentLen -= currentLen%blockSize;
            boolean delay = arrayVisualizer.shuffleEnabled();
            double sleep = delay ? 1 : 0;

            makeRandom(arrayVisualizer);
            for (int i = 0; i < currentLen; i += blockSize) {
                int randomIndex = random.nextInt((currentLen - i) / blockSize) * blockSize + i;
                blockSwap(array, i, randomIndex, blockSize, Writes, sleep);
            }
        }

        private void blockSwap(int[] array, int a, int b, int len, Writes Writes, double sleep) {
            for (int i = 0; i < len; i++) {
                Writes.swap(array, a + i, b + i, sleep, true, false);
            }
        }

        private int pow2lte(int value) {
            int val;
            for (val = 1; val <= value; val <<= 1);
            return val >> 1;
        }
    
}
