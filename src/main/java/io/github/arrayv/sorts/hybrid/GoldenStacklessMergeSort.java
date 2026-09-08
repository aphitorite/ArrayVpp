package io.github.arrayv.sorts.hybrid;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;

final public class GoldenStacklessMergeSort extends Sort {
    public GoldenStacklessMergeSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        
        this.setSortListName("Golden Stackless Merge");
        this.setRunAllSortsName("Golden Stackless Merge Sort");
        this.setRunSortName("Golden Stackless Mergesort");
        this.setCategory("Merge Sorts");
        this.setAuthors("Potassium");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
    }
    
  //BlockInsertionSort sort2 = new BlockInsertionSort(this.arrayVisualizer);
    public void binaryInsertSort(int[] array, int start, int end, double compSleep, double writeSleep) {
        for (int i = start; i < end; i++) {
            int num = array[i];
            int lo = start, hi = i;
            
            while (lo < hi) {
                int mid = lo + ((hi - lo) / 2); // avoid int overflow!
                Highlights.markArray(1, lo);
                Highlights.markArray(2, mid);
                Highlights.markArray(3, hi);
                
                Delays.sleep(compSleep);
                
                if (Reads.compareValues(num, array[mid]) < 0) { // do NOT move equal elements to right of inserted element; this maintains stability!
                    hi = mid;
                }
                else {
                    lo = mid + 1;
                }
            }

            Highlights.clearMark(3);
            
            // item has to go into position lo

            int j = i - 1;
            
            while (j >= lo)
            {
                Writes.write(array, j + 1, array[j], writeSleep, true, false);
                j--;
            }
            Writes.write(array, lo, num, writeSleep, true, false);
            
            Highlights.clearAllMarks();
        }
    }
    
    public void customBinaryInsert(int[] array, int start, int end, double sleep) {
        this.binaryInsertSort(array, start, end, sleep, sleep);
    }
    
    private boolean isRangeSorted(int[] array, int start, int end, boolean mark, boolean markLast) {
        for (int i = start; i < end - 1; ++i) {
            if (Reads.compareIndices(array, i, i + 1, 0.1, mark) > 0) {
            	//Highlights.incrementFancyFinishPosition();
                if (markLast) Highlights.markArray(3, i + 1);
                return false;
            }
        }
        return true;
    }
    
    public void mergeChunks(int[] array, int start, int mid, int end) {
        int f = start;
        int l = mid;
        
        int[] a2 = Writes.createExternalArray(mid - start + 1);
        
        for(int j = start; j <= mid; j++) {
        	Writes.write(a2, j - start, array[j], 0.05, true, true);
        }
    	
        for(int r = start; r < end; r++) {
        	if(f >= mid && l >= end) break;
    		Highlights.markArray(2, f);
    		Highlights.markArray(3, l);
        	if(f < mid && l >= end) {
        		Writes.write(array, r, a2[f - start], 1, true, false);
        		f++;
        	}
        	else if(f >= mid && l < end) {
        		Writes.write(array, r, array[l], 1, true, false);
        		l++;
        	}	
        	else if(Reads.compareValues(a2[f - start], array[l]) <= 0) {
        		Writes.write(array, r, a2[f - start], 1, true, false);
        		f++;
        	} else {
        		Writes.write(array, r, array[l], 1, true, false);
        		l++;
        	}
        }
        Highlights.clearMark(2);
        Writes.deleteExternalArray(a2);
    }
    
    // stolen from stackoverflow
    public int log2(int n){
        if(n <= 0) throw new IllegalArgumentException();
        return 31 - Integer.numberOfLeadingZeros(n);
    }
    
    double invPhi = 0.61803398875;
    
    public void stacklessMerge(int[] array, int length, int blockSize) {

    	if(length > blockSize) {
        	/*int start = 0;
        	while(start + blockSize < length && this.isRangeSorted(array, start, start + blockSize, true, false)) {
        		start += blockSize;
        	}// code for skipping over already sorted bits, i'll fix it later*/
    		int start = 0;
    		
    		customBinaryInsert(array, start, start + blockSize, 0.125);
    		int i = start + blockSize;
    		customBinaryInsert(array, i, i+blockSize, 0.125);
        	while(i+blockSize < length) {
        		customBinaryInsert(array, i, i+blockSize, 0.125);
    	        mergeChunks(array, 0, i, i+blockSize);
        		i += blockSize;
        	}
    		customBinaryInsert(array, i, length, 0.125);
            mergeChunks(array, 0, i, length);
    	} else customBinaryInsert(array, 0, length, 0.333);
    }
    
    @Override
    public void runSort(int[] array, int length, int bucketCount) {
    	stacklessMerge(array, length, (int)Math.pow((double)length, invPhi));
    }
}