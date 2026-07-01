package io.github.arrayv.sorts.merge;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;
import io.github.arrayv.sorts.templates.Parallelize;

// Like Even More Parallel Merge Sort, but smaller and slightly more readable

final public class MiniMergeSortExtraParallel extends Sort implements Parallelize {
    public MiniMergeSortExtraParallel(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        
        this.setSortListName("Merge (Even More Parallel)"); // Mini Merge (Extra-Parallel)
        this.setRunAllSortsName("Mini Merge Sort (Extra-Parallel)");
        this.setRunSortName("Mini Mergesort (Extra-Parallel)");
        this.setCategory("Merge Sorts");
        this.setComparisonBased(true);
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
        this.setAuthors("Distray");
    }
    
    // write on a separate thread
    protected Void parallelWrite(Object... data) {
    	assert data.length == 4;
    	Writes.write((int[])data[0], (int)data[1], (int)data[2], 0.5, true, (boolean)data[3]);
    	return null;
    }
    
    // arraycopy with n threads
    protected Void parallelCopy(Object... data) {
    	assert data.length == 6;
    	int[] src = (int[]) data[0];
    	int[] dst = (int[]) data[2];
    	int off0 = (int) data[1];
    	int off1 = (int) data[3];
    	int len = (int) data[4];
    	boolean aux = (boolean) data[5];
    	Func[] vals = new Func[len];
    	for(int i=0; i<len; i++) {
    		vals[i] = new Func(dst, off1+i, src[off0+i], aux).setConsumer(this::parallelWrite);
    	}
    	for(int i=0; i<len; i++) {
    		vals[i].start();
    	}
    	for(int i=0; i<len; i++) {
    		try {
				vals[i].join();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
    	}
    	return null;
    }
    
    // mergesort routine
    public void mergeRoutine(int[] array, int[] aux, int a, int b, int t, boolean raux) {
    	int m = (b - a) / 2;
    	if(m > 0) {
    		// make functions to merge both halves
    		Func left = new Func(aux, array, t, t + m, a, !raux).setConsumer(this::mergeWrapper);
    		Func right = new Func(aux, array, t + m, t + b - a, a + m, !raux).setConsumer(this::mergeWrapper);
    		
    		// start both threads, wait for both to finish
    		left.start();
    		right.start();
    		
    		try {
				left.join();
				right.join();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
    		
    		// parallel merge
    		runMerge(aux, array, t, t + m, t + m, t + b - a, a, raux);
    	}
    }
    
    // binary search function
    private int binSearch(int[] array, int a, int b, int v, int right) {
    	int c = b;
    	while(a<b) {
    		int m=a+(b-a)/2;
    		if(Reads.compareIndices(array, m, v, 0.5, true) < right) {
    			a = m + 1;
    		} else {
    			b = m;
    		}
    	}
    	return Math.min(a, c); // sanity check
    }
    
    // extra-parallel merge function
    public void runMerge(int[] from, int[] to, int a, int b, int c, int d, int t, boolean aux) {
    	Func left, right;
    	if(a < b && c < d && Reads.compareIndices(from, b-1, c, 0.1, true) > 0) {
			// binary search in the smaller subsection
    		int m1, m2;
    		if(b - a < c - d) {
    			m2 = c + (d - c) / 2;
    			m1 = binSearch(from, a, b, m2, 1);
    		} else {
    			m1 = a + (b - a) / 2;
    			m2 = binSearch(from, c, d, m1, 0);
    		}
    		
    		// merge on both halves
    		left = new Func(from, to, a, m1, c, m2, t, aux).setConsumer(this::routineWrapper);
    		right = new Func(from, to, m1, b, m2, d, t+(m1-a)+(m2-c), aux).setConsumer(this::routineWrapper);
    	} else {
    		// copy both parts if no need to merge
    		left = new Func(from, a, to, t, b - a, aux).setConsumer(this::parallelCopy);
    		right = new Func(from, c, to, t + b - a, d - c, aux).setConsumer(this::parallelCopy);
    	}

    	// start both threads, wait for both to finish
		left.start();
		right.start();
		
		try {
			left.join();
			right.join();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
    }
    
    public void copyNecessary(int[] array, int[] aux, int a, int b, int t, int alt) {
    	int m = (b - a) / 2;
    	if(m > 0) {
    		// make functions to merge both halves
    		Func left = new Func(array, aux, a, a+m, t, alt^1).setConsumer(this::copyWrapper);
    		Func right = new Func(array, aux, a+m, b, t+m, alt^1).setConsumer(this::copyWrapper);
    		
    		// start both threads, wait for both to finish
    		left.start();
    		right.start();
    		
    		try {
				left.join();
				right.join();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
    	} else if(alt == 1) {
        	parallelCopy(array, a, aux, t, b-a, true);
    	}
    }
    
    protected Void routineWrapper(Object... data) {
    	assert data.length == 8;
    	run("runMerge", data);
    	return null;
    }
    
    protected Void mergeWrapper(Object... data) {
    	assert data.length == 6;
    	run("mergeRoutine", data);
    	return null;
    }
    
    protected Void copyWrapper(Object... data) {
    	assert data.length == 5;
    	run("copyNecessary", data);
    	return null;
    }
    
    public void merge(int[] array, int a, int b) {
    	int[] aux = Writes.createExternalArray(b-a);
    	copyNecessary(array, aux, a, b, 0, 0);
    	mergeRoutine(array, aux, a, b, 0, false);
    	Writes.deleteExternalArray(aux);
    }
    
    @Override
    public void runSort(int[] array, int length, int bucketCount) {
    	this.merge(array, 0, length);
    }
}