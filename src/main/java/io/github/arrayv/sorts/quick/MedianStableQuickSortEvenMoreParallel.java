package io.github.arrayv.sorts.quick;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;
import io.github.arrayv.sorts.templates.Parallelize;

final public class MedianStableQuickSortEvenMoreParallel extends Sort implements Parallelize {
    public MedianStableQuickSortEvenMoreParallel(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        
        this.setSortListName("Median Stable Quick (Even More Parallel)");
        this.setRunAllSortsName("Even More Parallel Median Stable Quicksort");
        this.setRunSortName("Median Stable Quicksort (Extra-Parallel)");
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
    		Highlights.markArray(2, a+i);
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
    
    protected Void zeroRange(Object... data) {
    	assert data.length == 3;
    	int[] target = (int[]) data[0];
    	int a = (int) data[1];
    	int b = (int) data[2];
    	Func[] vals = new Func[b-a];
    	for(int i=a; i<b; i++) {
    		vals[i-a] = new Func(target, i, -1, true).setConsumer(this::parallelWrite);
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
    
    private int medOf3(int[] array, int a, int b, int c) {
    	int d;
    	if(Reads.compareIndices(array, a, b, 0.5, true) > 0) {
    		d = b; b = a;
    	} else
    		d = a;
    	if(Reads.compareIndices(array, b, c, 0.5, true) > 0) {
    		if(Reads.compareIndices(array, d, c, 0.5, true) > 0) {
        		return d;
        	}
    		return c;
    	}
    	return b;
    }
    
    public int medP3(int[] array, int a, int b, int d) {
    	if(b-a==3||(b-a>3&&d==0)) {
    		return medOf3(array, a, a+(b-a)/2, b-1);
    	} else if(b-a<3) {
    		return a+(b-a)/2;
    	}
    	int t=(b-a)/3;
    	Func l = new Func(array, a, a+t, --d).setConsumer(this::medWrap),
    		 c = new Func(array, a+t, b-t, d).setConsumer(this::medWrap),
    		 r = new Func(array, b-t, b,   d).setConsumer(this::medWrap);
    	
    	// get median of thirds
    	l.start();
    	c.start();
    	r.start();
    	
    	try {
    		l.join();
    		c.join();
    		r.join();
    	} catch(InterruptedException e) {
    		Thread.currentThread().interrupt();
    	}
    	
    	// median
    	return medOf3(array, (int)l.returnVal, (int)c.returnVal, (int)r.returnVal);
    }
    
    protected Integer medWrap(Object... data) {
    	assert data.length == 3;
    	return (Integer) run("medP3", data);
    }
    
    public int medOfMed(int[] array, int a, int b) {
    	if(b-a <= 6) {
    		return a+(b-a)/2;
    	}
    	int p = 1;
    	while(6*p < b-a) p*=3;
    	
    	Func l = new Func(array, a, a+p, -1).setConsumer(this::medWrap),
    		 c = new Func(array, a+p, b-p).setConsumer(this::momWrap),
    		 r = new Func(array, b-p, b, -1).setConsumer(this::medWrap);
    	
    	// get median of thirds
    	l.start();
    	c.start();
    	r.start();
    	
    	try {
    		l.join();
    		c.join();
    		r.join();
    	} catch(InterruptedException e) {
    		Thread.currentThread().interrupt();
    	}
    	
    	// median
    	return medOf3(array, (int)l.returnVal, (int)c.returnVal, (int)r.returnVal);
    }
    
    protected Integer momWrap(Object... data) {
    	assert data.length == 3;
    	return (Integer) run("medOfMed", data);
    }
    
    // O(log n) "lower than pivot" counter
    public int[] countLo(int[] array, int[] cnts, int a, int b, int t, int p, int c) {
    	int m = a + (b - a) / 2;
    	if(a >= m) {
    		int[] v = Reads.compareIndexValue(array, a, p, 0.5, true) < c ? new int[] {1, 0} : new int[] {0, -1};
    		Writes.write(cnts, t, cnts[t] + v[0], 1, true, true);
    		Highlights.markArray(1, t);
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
    	
    	// increment the range
		incrementRange(cnts, t+m-a, t+b-a, l.returnVal);
		Highlights.clearMark(2);
		
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
    public int partition(int[] array, int[] cnts, int a, int b, int t, boolean bad) {
    	if(a >= b - 1) return -1;
    	zeroRange(cnts, t, t+b-a);
    	int p = array[bad?medOfMed(array, a, b):medP3(array, a, b, 2)];
    	int[] c = countLo(array, cnts, a, b, t, p, 0);
    	if(c[0] == 0) {
        	zeroRange(cnts, t, t+b-a);
        	c = countLo(array, cnts, a, b, t, p, 1);
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
    	boolean bad = (boolean) data[5];
    	int p = partition(array, cnts, a, b, t, bad);
    	if(p >= 0) {
    		bad = p < (b - a) / 6 || p >= 5 * (b - a) / 6;
    		Func l = new Func(array, cnts, a, a+p, t, bad).setConsumer(this::innerSort),
    			 r = new Func(array, cnts, a+p, b, t+p, bad).setConsumer(this::innerSort);
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
    	innerSort(array, cnts, a, b, 0, false);
    	Writes.deleteExternalArray(cnts);
    }

    @Override
    public void runSort(int[] array, int currentLength, int bucketCount) {
    	sort(array, 0, currentLength);
    }
}