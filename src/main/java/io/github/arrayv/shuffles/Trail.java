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

public class Trail extends Shuffle {

        public String getName() {
            return "Trail";
        }
        @Override
        public void shuffleArray(int[] array) {
            int currentLen = arrayVisualizer.getCurrentLength();
            Reads reads = arrayVisualizer.getReads();
            int min = currentLen/2, max=min, now=min;
            int[] trail = new int[currentLen];
            for(int i=0; i<currentLen-1; i++) {
            	trail[i]=now;
            	now-=reads.compareValues(array[i], array[i+1]);
            	if(now<min) min=now; if(now>max) max=now;
            }
            trail[currentLen-1] = now;
            for(int i=0; i<currentLen; i++) {
            	double v = (trail[i]-min)/((max-min)/(double)currentLen);
            	Writes.write(array, i, (int) v, 1, true, false);
            }
        }
    
}
