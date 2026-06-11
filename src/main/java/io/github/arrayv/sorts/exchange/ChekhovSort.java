package io.github.arrayv.sorts.exchange;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.BogoSorting;

/**
 * Markov Sort is like Gnome Sort, but the next element to be inserted
 * randomly walks within the sorted section until it is in the correct position.
 * 
 * @author invented by Blasterfreund
 * @author implemented in Java by Sam Walko (Anonymous0726)
 * @author refactored by EmeraldBlock
 */
final public class ChekhovSort extends BogoSorting {
    public ChekhovSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);

        this.setSortListName("Chekhov");
        this.setRunAllSortsName("Chekhov Sort (Optimized Markov Sort)"); // Optimized Markov Sort
        this.setRunSortName("Chekhov Sort");
        this.setCategory("Insertion Sorts");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(true);
        this.setUnreasonableLimit(1024);
        this.setBogoSort(true);
        this.setAuthors("Blasterfreund, Sam Walko (Anonymous0726), EmeraldBlock");
    }

    @Override
    public void runSort(int[] array, int length, int bucketCount) {
    	for(int i=1; i<length; i++) {
    		int rand = randInt(0, i);
    		boolean right = true;
    		if(Reads.compareValues(array[rand], array[i]) == 1) {
    			right = false;
    		}
    		if(right) {
    			while(rand < i) {
    				if(Reads.compareValues(array[rand], array[i]) == 1)
    					break;
    				rand++;
    				Highlights.markArray(1, rand);
    				Delays.sleep(0.1);
    			}
    			for(int j=i-1;j>=rand;j--) {
    				Writes.swap(array, j, j+1, 0.33, true, false);
    			}
    		} else {
    			while(rand >= 0) {
    				if(Reads.compareValues(array[rand], array[i]) == -1)
    					break;
    				Highlights.markArray(1, rand--);
    				Delays.sleep(0.1);
    			}
    			for(int j=i-1;j>rand;j--) {
    				Writes.swap(array, j, j+1, 0.33, true, false);
    			}
    		}
    	}
    }
}
