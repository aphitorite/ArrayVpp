package io.github.arrayv.sorts.quick;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.QuadSorting;

final public class FladSort extends QuadSorting {
    public FladSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        this.setSortListName("Flad");
        this.setRunAllSortsName("Flad Sort");
        this.setRunSortName("Fladsort");
        this.setCategory("Quick Sorts");
  	    this.setAuthors("Distray");
  	    this.setConstant("n log n");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
        this.setAuthors("Scandum");
    }
    private int cmp(int v0, int v1) {
    	return -(Reads.compareValues(v1, v0) >> 31);
    }
    private void fladPart(int[] array, int[] swap, int bucket, int offs0, int offs1, int nmemb) {
    	if(nmemb < 2)
    		return;
    	if(nmemb < 32) {
    		if(bucket == 1)
    			fladCopy(array, swap, offs0, offs1, nmemb);
    		quadSort(array, offs0, nmemb);
    		return;
    	}
    	int ptx=bucket==1?offs1:offs0;
    	int[] part=bucket==1?swap:array;
    	boolean swapAux=swap!=array;
    	int loop=nmemb>>3, C, lS = 0, rS = 0, piv=part[ptx+nmemb/2];
    	while(loop > 0) {
    		--loop;
    		Highlights.markArray(2, ptx);
    		C=cmp(part[ptx],piv);Writes.write(array,lS+offs0,part[ptx],0.5,true,false);
    		Writes.write(swap,rS+offs1,part[ptx++],0.5,true,swapAux);lS+=C^1;rS+=C;
    		Highlights.markArray(2, ptx);
    		C=cmp(part[ptx],piv);Writes.write(array,lS+offs0,part[ptx],0.5,true,false);
    		Writes.write(swap,rS+offs1,part[ptx++],0.5,true,swapAux);lS+=C^1;rS+=C;
    		Highlights.markArray(2, ptx);
    		C=cmp(part[ptx],piv);Writes.write(array,lS+offs0,part[ptx],0.5,true,false);
    		Writes.write(swap,rS+offs1,part[ptx++],0.5,true,swapAux);lS+=C^1;rS+=C;
    		Highlights.markArray(2, ptx);
    		C=cmp(part[ptx],piv);Writes.write(array,lS+offs0,part[ptx],0.5,true,false);
    		Writes.write(swap,rS+offs1,part[ptx++],0.5,true,swapAux);lS+=C^1;rS+=C;
    		Highlights.markArray(2, ptx);
    		C=cmp(part[ptx],piv);Writes.write(array,lS+offs0,part[ptx],0.5,true,false);
    		Writes.write(swap,rS+offs1,part[ptx++],0.5,true,swapAux);lS+=C^1;rS+=C;
    		Highlights.markArray(2, ptx);
    		C=cmp(part[ptx],piv);Writes.write(array,lS+offs0,part[ptx],0.5,true,false);
    		Writes.write(swap,rS+offs1,part[ptx++],0.5,true,swapAux);lS+=C^1;rS+=C;
    		Highlights.markArray(2, ptx);
    		C=cmp(part[ptx],piv);Writes.write(array,lS+offs0,part[ptx],0.5,true,false);
    		Writes.write(swap,rS+offs1,part[ptx++],0.5,true,swapAux);lS+=C^1;rS+=C;
    		Highlights.markArray(2, ptx);
    		C=cmp(part[ptx],piv);Writes.write(array,lS+offs0,part[ptx],0.5,true,false);
    		Writes.write(swap,rS+offs1,part[ptx++],0.5,true,swapAux);lS+=C^1;rS+=C;
    	}
    	switch(nmemb&7) {
    	case 7: Highlights.markArray(2, ptx); C=cmp(part[ptx],piv);Writes.write(array,lS+offs0,part[ptx],0.5,true,false); Writes.write(swap,rS+offs1,part[ptx++],0.5,true,swapAux);lS+=C^1;rS+=C;
    	case 6: Highlights.markArray(2, ptx); C=cmp(part[ptx],piv);Writes.write(array,lS+offs0,part[ptx],0.5,true,false); Writes.write(swap,rS+offs1,part[ptx++],0.5,true,swapAux);lS+=C^1;rS+=C;
    	case 5: Highlights.markArray(2, ptx); C=cmp(part[ptx],piv);Writes.write(array,lS+offs0,part[ptx],0.5,true,false); Writes.write(swap,rS+offs1,part[ptx++],0.5,true,swapAux);lS+=C^1;rS+=C;
    	case 4: Highlights.markArray(2, ptx); C=cmp(part[ptx],piv);Writes.write(array,lS+offs0,part[ptx],0.5,true,false); Writes.write(swap,rS+offs1,part[ptx++],0.5,true,swapAux);lS+=C^1;rS+=C;
    	case 3: Highlights.markArray(2, ptx); C=cmp(part[ptx],piv);Writes.write(array,lS+offs0,part[ptx],0.5,true,false); Writes.write(swap,rS+offs1,part[ptx++],0.5,true,swapAux);lS+=C^1;rS+=C;
    	case 2: Highlights.markArray(2, ptx); C=cmp(part[ptx],piv);Writes.write(array,lS+offs0,part[ptx],0.5,true,false); Writes.write(swap,rS+offs1,part[ptx++],0.5,true,swapAux);lS+=C^1;rS+=C;
    	case 1: Highlights.markArray(2, ptx); C=cmp(part[ptx],piv);Writes.write(array,lS+offs0,part[ptx],0.5,true,false); Writes.write(swap,rS+offs1,part[ptx++],0.5,true,swapAux);lS+=C^1;rS+=C;
    	}
    	if(rS >= nmemb) {
			fladCopy(array, swap, offs0+lS, offs1, nmemb);
    		quadSort(array, offs0+lS, nmemb);
    	} else
    		fladPart(array, swap, 1, offs0+lS, offs1, rS);
    	if(lS >= nmemb) {
    		quadSort(array, offs0, nmemb);
    		return;
    	}
		fladPart(array, swap, 0, offs0, offs1, lS);
    }
    private void tmpMerge(int[] array, int[] tmp, int start, int mid, int end) {
    	int[][] table = new int[][] {array, tmp};
    	int[] ptrs = new int[] {mid-1, end-mid-1};
    	int t=end;
    	while(ptrs[0] >= start && ptrs[1] >= 0) {
    		int C = cmp(tmp[ptrs[1]], array[ptrs[0]]);
    		Writes.write(array, --t, table[C][ptrs[C]--], 1, true, false);
    	}
    	while(ptrs[1] >= 0)
    		Writes.write(array, --t, tmp[ptrs[1]--], 1, true, false);
    }
    
    private void fladCopy(int[] dst, int[] src, int ptrOff1, int ptrOff0, int len) {
    	Writes.arraycopy(src, ptrOff0, dst, ptrOff1, len, 1, true, true);
    }
    public void fladSort(int[] array, int start, int nmemb) {
    	if(nmemb < 16) {
    		tailSwap(array, start, nmemb);
    	} else {
    		int[] quad = Writes.createExternalArray((nmemb+1)/2);
    		fladCopy(quad, array, 0, start+nmemb/2, quad.length);
    		fladPart(array, array, 0, start, start+nmemb/2, nmemb/2);
    		fladPart(quad, array, 0, 0, start+nmemb/2, quad.length);
    		tmpMerge(array, quad, start, start+nmemb/2, start+nmemb);
    		Writes.deleteExternalArray(quad);
    	}
    }

    @Override
    public void runSort(int[] array, int currentLength, int bucketCount) {
    	fladSort(array, 0, currentLength);
    }
}