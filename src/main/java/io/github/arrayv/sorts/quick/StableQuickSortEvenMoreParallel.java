package io.github.arrayv.sorts.quick;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;
import io.github.arrayv.sorts.templates.Parallelize;

final public class StableQuickSortEvenMoreParallel extends Sort implements Parallelize {
    public StableQuickSortEvenMoreParallel(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        
        this.setSortListName("Stable Quick (Even More Parallel)");
        this.setRunAllSortsName("Even More Parahell Stable Quicksort");
        this.setRunSortName("Even More Parallel Stable Quicksort");
        this.setCategory("Quick Sorts");
        this.setAuthors("Distray");
        this.setConstant("n log n");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
    }
    
    // write on a separate thread
    protected Void parallelWrite(Object... data) {
    	assert data.length == 4;
    	Writes.write((int[])data[0], (int)data[1], (int)data[2], 0.5, true, (boolean)data[3]);
    	return null;
    }
    
    protected Void incrementRange(Object... data) {
    	assert data.length == 4;
    	int[] target = (int[]) data[0];
    	int a = (int) data[1];
    	int b = (int) data[2];
    	int[] v = (int[]) data[3];
    	Func[] vals = new Func[b-a];
    	for(int i=0; i<b-a; i++) {
    		vals[i] = new Func(target, a+i, target[a+i]+v[target[a+i]>>>31], true).setConsumer(this::parallelWrite);
    	}
    	for(int i=0; i<b-a; i++) {
    		vals[i].start();
    	}
    	for(int i=0; i<b-a; i++) {
    		try {
				vals[i].join();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
    	}
    	return null;
    }
    
    protected Void fillRange(Object... data) {
    	assert data.length == 4;
    	int[] target = (int[]) data[0];
    	int a = (int) data[1];
    	int b = (int) data[2];
    	int v = (int) data[3];
    	Func[] vals = new Func[b-a];
    	for(int i=a; i<b; i++) {
    		vals[i-a] = new Func(target, i, v, true).setConsumer(this::parallelWrite);
    	}
    	for(int i=0; i<b-a; i++) {
    		vals[i].start();
    	}
    	for(int i=0; i<b-a; i++) {
    		try {
				vals[i].join();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
    	}
    	return null;
    }
    
    // O(log n) "lower than pivot" counter
    public int[] countLo(int[] array, int[] cnts, int a, int b, int t, int p, int c) {
    	int m = a + (b - a) / 2;
    	if(a >= m) {
    		int[] v = Reads.compareIndexValue(array, a, p, 0.5, true) < c ? new int[] {1, 0} : new int[] {0, -1};
    		Writes.write(cnts, t, cnts[t] + v[0], 1, true, true);
    		return v;
    	}
    	
    	Func l = new Func(array, cnts, a, m, t, p, c).setConsumer(this::clw),
    	     r = new Func(array, cnts, m, b, t+m-a, p, c).setConsumer(this::clw);
    	
    	// count both halves
    	l.start();
    	r.start();
    	
    	try {
			l.join();
			r.join();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
    	
		// not sure if this changes anything, but it looked a lot slower doing it using a standard function call, even though it worked
		Func d = new Func(cnts, t+m-a, t+b-a, l.returnVal).setConsumer(this::incrementRange);
		d.start();
    	try {
			d.join();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		
    	// return sum of high and low
		int[] s = new int[] {((int[])l.returnVal)[0]+((int[])r.returnVal)[0], ((int[])l.returnVal)[1]+((int[])r.returnVal)[1]};
		return s;
    }
    
    private int[] clw(Object... data) {
    	assert data.length == 7;
    	return (int[]) run("countLo", data);
    }
    
    public Void threadPartition(Object... data) {
    	assert data.length == 7;
    	int[] array = (int[]) data[0];
    	int[] cnts = (int[]) data[1];
    	int a = (int) data[2];
    	int a1 = (int) data[3];
    	int a2 = (int) data[4];
    	int m = (int) data[5];
    	int v = (int) data[6];
    	
    	int[] p = new int[] {a1+cnts[a2+a-a1], a1+m+~cnts[a2+a-a1]};
    	
    	Writes.write(array, p[cnts[a2+a-a1]>>>31], v, 1, true, false);
    	return null;
    }
    
    // should be O(log n) as well (max: 2 partitions, threaded counting partition should be O(1) due to being branchless)
    public int partition(int[] array, int[] cnts, int a, int b, int t) {
    	if(a >= b - 1) return -1;
    	fillRange(cnts, t, t+b-a, -1);
    	int[] c = countLo(array, cnts, a, b, t, array[a+(b-a)/2], 0);
    	if(c[0] == 0) {
        	fillRange(cnts, t, t+b-a, -1);
        	c = countLo(array, cnts, a, b, t, array[a+(b-a)/2], 1);
        	if(c[1] == 0) return -1;
    	}
    	Func[] v = new Func[b-a];
    	for(int i=a; i<b; i++) {
    		v[i-a] = new Func(array, cnts, i, a, t, c[0], array[i]).setConsumer(this::threadPartition);
    	}
    	for(int i=0; i<b-a; i++) {
    		v[i].start();
    	}
    	for(int i=0; i<b-a; i++) {
    		try {
    			v[i].join();
    		} catch(InterruptedException e) {
    			Thread.currentThread().interrupt();
    		}
    	}
    	return c[0];
    }
    
    public Void innerSort(Object... data) {
    	assert data.length == 5;
    	int[] array = (int[]) data[0];
    	int[] cnts = (int[]) data[1];
    	int a = (int) data[2], b = (int) data[3], t = (int) data[4];
    	int p = partition(array, cnts, a, b, t);
    	if(p >= 0) {
    		Func l = new Func(array, cnts, a, a+p, t).setConsumer(this::innerSort),
    			 r = new Func(array, cnts, a+p, b, t+p).setConsumer(this::innerSort);
    		l.start();
    		r.start();
    		try {
    			l.join();
    			r.join();
    		} catch(InterruptedException e) {
    			Thread.currentThread().interrupt();
    		}
    	}
    	return null;
    }
    
    public void sort(int[] array, int a, int b) {
    	int[] cnts = Writes.createExternalArray(b-a);
    	innerSort(array, cnts, a, b, 0);
    	Writes.deleteExternalArray(cnts);
    }

    @Override
    public void runSort(int[] array, int currentLength, int bucketCount) {
    	sort(array, 0, currentLength);
    }
}