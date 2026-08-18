package io.github.arrayv.sorts.distribute;

import io.github.arrayv.sorts.templates.Sort;
import io.github.arrayv.sorts.quick.FluxSort;

import io.github.arrayv.main.ArrayVisualizer;

/*
Copyright (C) 2014-2021 Igor van den Hoven ivdhoven@gmail.com
*/

/*
Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
"Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:
The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/


// *Ported by Distray, version in wolfsort repo as of 2022/02/18*

final public class WolfSort extends Sort {
    public WolfSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        
        this.setSortListName("Wolf");
        this.setRunAllSortsName("Wolf Sort");
        this.setRunSortName("Wolfsort");
        this.setCategory("Distribution Sorts");
        this.setAuthors("Scandum");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
    }
    private FluxSort flux;
    private boolean c(int[] array, int o, int a, int b) {
    	return Reads.compareIndices(array, a+o, b+o, 0.125, true) > 0;
    }
    private boolean c2(int v, int w) {
    	return Reads.compareValues(v, w) > 0;
    }
    private void tailswap2(int[] dest, int[] src, int offset1, int offset0) {
    	if(c(src, offset0, 0, 1)) {
    		Writes.write(dest, offset1, src[offset0+1], 1, true, false);
    		Writes.write(dest, offset1+1, src[offset0], 1, true, false);
    	} else {
    		Writes.write(dest, offset1, src[offset0], 1, true, false);
    		Writes.write(dest, offset1+1, src[offset0+1], 1, true, false);
    	}
    }
    private void tailswap3(int[] dest, int[] src, int offset1, int offset0) {
    	if(c(src, offset0, 0, 1)) {
    		if(!c(src, offset0, 0, 2)) {
        		Writes.write(dest, offset1, src[offset0+1], 1, true, false);
        		Writes.write(dest, offset1+1, src[offset0], 1, true, false);
        		Writes.write(dest, offset1+2, src[offset0+2], 1, true, false);
    		} else if(c(src, offset0, 1, 2)) {
        		Writes.write(dest, offset1, src[offset0+2], 1, true, false);
        		Writes.write(dest, offset1+1, src[offset0+1], 1, true, false);
        		Writes.write(dest, offset1+2, src[offset0], 1, true, false);
    		} else {
        		Writes.write(dest, offset1, src[offset0+1], 1, true, false);
        		Writes.write(dest, offset1+1, src[offset0+2], 1, true, false);
        		Writes.write(dest, offset1+2, src[offset0], 1, true, false);
    		}
    	} else if(c(src, offset0, 1, 2)){
    		if(c(src, offset0, 0, 2)) {
        		Writes.write(dest, offset1, src[offset0+2], 1, true, false);
        		Writes.write(dest, offset1+1, src[offset0], 1, true, false);
        		Writes.write(dest, offset1+2, src[offset0+1], 1, true, false);
    		} else {
        		Writes.write(dest, offset1, src[offset0], 1, true, false);
        		Writes.write(dest, offset1+1, src[offset0+2], 1, true, false);
        		Writes.write(dest, offset1+2, src[offset0+1], 1, true, false);
    		}
    	} else {
    		Writes.write(dest, offset1, src[offset0+1], 1, true, false);
    		Writes.write(dest, offset1+1, src[offset0+1], 1, true, false);
    		Writes.write(dest, offset1+2, src[offset0+2], 1, true, false);
    	}
    }
    private void tailswap4(int[] dest, int[] src, int offset1, int offset0) {
    	tailswap3(dest, src, offset1, offset0);
    	if(c2(dest[offset1+1], src[offset0+3])) {
        	if(c2(dest[offset1], src[offset0+3])) {
        		Writes.write(dest, offset1+3, dest[offset1+2], 1, true, false);
        		Writes.write(dest, offset1+2, dest[offset1+1], 1, true, false);
        		Writes.write(dest, offset1+1, dest[offset1], 1, true, false);
        		Writes.write(dest, offset1, src[offset0+3], 1, true, false);
        	} else {
        		Writes.write(dest, offset1+3, dest[offset1+2], 1, true, false);
        		Writes.write(dest, offset1+2, dest[offset1+1], 1, true, false);
        		Writes.write(dest, offset1+1, src[offset0+3], 1, true, false);
        	}
    	} else if(c2(dest[offset1+2], src[offset0+3])) {
    		Writes.write(dest, offset1+3, dest[offset1+2], 1, true, false);
    		Writes.write(dest, offset1+2, src[offset0+3], 1, true, false);
    	} else {
    		Writes.write(dest, offset1+3, src[offset0+3], 1, true, false);
    	}
    }
    private void ginsert(int[] dest, int key, int start, int end) {
    	while(start <= --end) {
    		if(Reads.compareValues(dest[end], key) <= 0)
    			break;
    		Writes.write(dest, end+1, dest[end], 1, true, false);
    	}
    	Writes.write(dest, end+1, key, 1, true, false);
    }
    private void tailswap(int[] dest, int[] src, int offset1, int offset0, int nmemb) {
    	switch(nmemb) {
    	case 1:
    		Writes.write(dest, offset1, src[offset0], 1, true, false);
    	case 0:
    		return;
    	case 2:
    		tailswap2(dest, src, offset1, offset0);
    		return;
    	case 3:
    		tailswap3(dest, src, offset1, offset0);
    		return;
    	default:
    		tailswap4(dest, src, offset1, offset0);
    	}
    	if(nmemb==4)
    		return;
    	for(int i=4; i<nmemb; i++) {
    		ginsert(dest, src[i+offset0], offset1, offset1+i);
    	}
    	
    }
    public void wolf(int[] array, int start, int end) {
		flux = new FluxSort(arrayVisualizer);
		int nmemb = end-start;
		if(nmemb < 1024) {
			flux.fluxSort(array, start, end);
			return;
		}
		int[] swap = Writes.createExternalArray(nmemb);
		int buckets=256, moduler=16777216;
		while(moduler>8096&&nmemb/buckets>4) {
			buckets<<=1;
			moduler>>=1;
		}
		int bsize = nmemb/(buckets/16), index, cnt, pta = start;
		int[] count = new int[buckets];
		int[] stack = new int[buckets];
		
		for(cnt = nmemb; cnt > 0; --cnt) {
			index = array[pta++] / moduler;
			if(++count[index] == bsize) {
				flux.fluxSort_swapDef(array, swap, start, end);
				Writes.deleteExternalArray(swap);
				return;
			}
		}
		cnt = 0;
		for(index=0; index<buckets; index++) {
			stack[index]=cnt;
			cnt+=count[index];
		}
		pta=start;
		for(cnt=nmemb; cnt>0; --cnt) {
			index = array[pta] / moduler;
			swap[stack[index]++]=array[pta++];
		}
		pta=start;
		cnt=0;
		for(index=0; index<buckets; index++) {
			bsize=count[index];
			if(bsize!=0) {
				if(bsize<=32) {
					tailswap(array, swap, pta, cnt, bsize);
				} else {
					Writes.arraycopy(swap, cnt, array, pta, bsize, 1, true, false);
					flux.fluxSort_swapDef(array, swap, pta, pta+bsize);
				}
				pta+=bsize;
				cnt+=bsize;
			}
		}
		Writes.deleteExternalArray(swap);
    }
    @Override
    public void runSort(int[] array, int length, int bucketCount) {
    	wolf(array, 0, length);
    }
}