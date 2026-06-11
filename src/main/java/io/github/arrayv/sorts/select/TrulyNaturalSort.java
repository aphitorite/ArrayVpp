package io.github.arrayv.sorts.select;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;

final public class TrulyNaturalSort extends Sort {  
    public TrulyNaturalSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        
        this.setSortListName("Truly Natural");
        this.setRunAllSortsName("Truly Natural Sort");
        this.setRunSortName("TNSort");
        this.setCategory("Selection Sorts");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
        this.setAuthors("Potassium");
    }
    
    public void selectionSort(int[] array, int length, int[] tmp) {
        for(int i = 0; i < length; i++) {
        	Writes.write(tmp, i, array[i], 0.125, true, false);
        }
        
        for (int i = length - 1; i >= 0; i--) {
            int lowestindex = length - 1;
            
            for (int j = 0; j < length; j++) {
                Highlights.markArray(2, j);
                Delays.sleep(0.125);
                
                if (Reads.compareValues(tmp[j], tmp[lowestindex]) > -1 && tmp[j] > -32767){
                    lowestindex = j;
                    Highlights.markArray(1, lowestindex);
                    Delays.sleep(0.125);
                }
            }
            Writes.write(array, i, tmp[lowestindex], 0.25, true, true);
            Writes.write(tmp, lowestindex, -32767, 0.02, true, false); // yes i do have negative distributions 
            														   // but this is so low it wont be a problem
            														   // probably...
        }
    }
    
    @Override
    public void runSort(int[] array, int length, int bucketCount) {
    	int[] tmp = Writes.createExternalArray(length);
    	selectionSort(array, length, tmp);
    	Writes.deleteExternalArray(tmp);
    }
}