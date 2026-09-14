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

public class ShellWorstBase extends Shuffle {

    	public String getName() {
    		return "Shell Killer";
    	}
    	public void shuffleArray(int[] array) {
            int n = arrayVisualizer.getCurrentLength();
    		int[] gaps = arrayVisualizer.getSelectedGapSequence().getGaps(n);
    		int[] cnts = Writes.createExternalArray(n);
    		int[] tmp = Writes.createExternalArray(n);
    		int x = gaps.length;
    		cnts[0] = tmp[0] = x + 1;
    		for(int j : gaps) {
    			x--;
    			for(int i = j; i < n; i++) {
    				int v = i - j;
    				if(cnts[v] > 0 && cnts[i] == 0) {
    					cnts[i] = x;
    				}
    			}
    		}
    		int min = cnts[0], max = cnts[0];
    		for(int i = 1; i < n; i++) {
    			min = Math.min(cnts[i], min);
    			max = Math.max(cnts[i], max);
    		}
    		for(int i = 0; i < n; i++) {
    			Writes.write(cnts, i, cnts[i] - min, 0, false, true);
    		}
    		int[] cnt2 = Writes.createExternalArray(max-min+2);
    		for(int i = 0; i < n; i++) {
    			Writes.write(cnt2, cnts[i], cnt2[cnts[i]] + 1, 0, false, true);
    		}
    		for(int i = 1; i < max - min + 1; i++) {
    			Writes.write(cnt2, i, cnt2[i] + cnt2[i-1], 0, false, true);
    		}
    		for(int i = 0; i < n; i++) {
    			Writes.write(tmp, i, array[--cnt2[cnts[i]]], 1, true, true);
    		}
    		Writes.deleteExternalArrays(cnts, cnt2);
    		Writes.arraycopy(tmp, 0, array, 0, n, 1, true, false);
    		Writes.deleteExternalArrays(tmp);
    	}
    
}
