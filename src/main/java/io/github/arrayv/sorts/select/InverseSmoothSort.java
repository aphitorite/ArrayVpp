package io.github.arrayv.sorts.select;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;

final public class InverseSmoothSort extends Sort {
    public InverseSmoothSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        
        this.setSortListName("Inverse Smooth");
        this.setRunAllSortsName("Inverse Smooth Sort");
        this.setRunSortName("Inverse Smoothsort");
        this.setCategory("Selection Sorts");
  	    this.setAuthors("Distray");
        this.setConstant("n log n");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
    }
    
    private static int[] LP = {1, 1, 3, 5, 9, 15, 25, 41, 67, 109, 177, 287, 465, 753, 1219, 1973,
    						   3193, 5167, 8361, 13529, 21891, 35421, 57313, 92735, 150049, 242785,
    						   392835, 635621, 1028457, 1664079, 2692537, 4356617, 7049155, 11405773,
    						   18454929, 29860703, 48315633, 78176337, 126491971, 204668309, 331160281,
    						   535828591, 866988873, 1402817465}; // recursive's too slow :(
    
    private int icd(int v, int x) {
    	if(v <= 0) return x;
    	int n = 0;
    	do {
    		int mc = LP[x]; // size of first child
    		if(n <= v && v < n + x) { // v found in first trail
    			return x - (v - n);
    		}
    		if(++n + mc <= v && v < n + mc + x - 2) { // v found in second trail
    			return x - (v - (n + mc)) - 2;
    		}
    		int d = ((v - n) / mc);
    		n += mc * d; // close in on v
    		
    		x -= d + 1;
    	} while(x > 0);
    	return 0; // within lowest node, 0
    }
    
    private void siftDown(int[] array, int a, int b, int hl, int tmp, boolean step) {
    	while(hl > 0) {
	    	int l = a + 1, ll = l, hc = LP[hl], d = 1;
	    	if(l + hc < b && Reads.compareIndices(array, ll, l + hc, 0.25, true) < 0) {
	    		ll = l + hc;
	    		d++;
	    	}
	    	if(ll < b && Reads.compareValueIndex(array, tmp, ll, 0.5, true) < 0) {
	    		Writes.write(array, a, array[ll], 0.618d, true, false);
	    		a = ll; step = true; hl -= d;
	    	} else {
	    		break;
	    	}
    	}
		if(step) Writes.write(array, a, tmp, 1, true, false);
    }
    

    @Override
    public void runSort(int[] array, int length, int bucketCount) {
    	int mh = 0;
    	while(LP[++mh+1] < length);
		for(int i = length - 1; i >= 0; i--) {
			int d = icd(i, mh);
			if(d > 0)
				siftDown(array, i, length, d, array[i], false);
		}
    	for(int i = length - 1; i > mh; i--) {
    		int t = array[i];
    		Writes.write(array, i, array[0], 2.5, true, false);
    		siftDown(array, 0, i, mh, t, true);
    	}
    	Writes.reversal(array, 0, mh, 0.5, true, false);
    }
}