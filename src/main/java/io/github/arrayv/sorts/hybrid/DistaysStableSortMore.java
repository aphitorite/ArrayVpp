package io.github.arrayv.sorts.hybrid;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;

final public class DistaysStableSortMore extends Sort {
    public DistaysStableSortMore(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        
        this.setSortListName("Distay's Stable (More)");
        this.setRunAllSortsName("Distay's Stable Sort (More)");
        this.setRunSortName("Distay's Stable Sort (More)");
        this.setCategory("Hybrid Sorts");
        this.setAuthors("Distray");
        this.setConstant("n log n");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
    }
    private boolean cInd(int[] array, int l, boolean aux) {
    	if(Reads.compareIndices(array, l, l+1, 0.5, true) > 0) {
    		onlySwap(array, l, aux);
    		return true;
    	}
    	return false;
    }
    private void onlySwap(int[] array, int l, boolean aux) {
		Writes.swap(array, l, l+1, 0.5, true, aux);
    }
    private void tailMerge(int[] array, int[] tmp, int o, int a, int m, int b, int routine, boolean aux) {
    	if(routine == 3) {
    		int l = m, M;
			while(l < b) {
				M = l + (b - l) / 2;
				if(Reads.compareIndices(array, M, m-1, 1, true) < 0) {
					l = M + 1;
				} else {
					b = M;
				}
			}
    	}
    	if(routine == 0 || routine == 3) {
    		Writes.arraycopy(array, m, tmp, o, b-m, 1, true, !aux);
    	}
    	int l = m-1, r = o+b-m-1;
    	while(l >= a && r >= o) {
    		Highlights.markArray(2, --b);
    		if(Reads.compareIndexValue(array, l, tmp[r], 1, true) > (routine == 1 ? -1 : 0))
    			Writes.write(array, b, array[l--], 0, true, aux);
    		else
    			Writes.write(array, b, tmp[r--], 0, true, aux);
    	}
    	while(r >= o)
			Writes.write(array, --b, tmp[r--], 1, true, aux);
    }
    private void merge(int[] array, int[] tmp, int a, int m, int b, int t, boolean aux) {
    	int l = a, r = m;
    	while(l < m && r < b) {
    		if(!aux)
    			Highlights.markArray(3, t);
    		if(Reads.compareIndices(array, l, r, 1, true) <= 0)
    			Writes.write(tmp, t++, array[l++], 0, true, aux);
    		else
    			Writes.write(tmp, t++, array[r++], 0, true, aux);
    	}
    	while(l < m)
			Writes.write(tmp, t++, array[l++], 1, true, aux);
    	while(r < b)
			Writes.write(tmp, t++, array[r++], 1, true, aux);
    }
    private void headMerge(int[] array, int[] tmp, int o, int a, int m, int b, boolean aux) {
    	int l=o, le=o+m-a, r = m;
		while(l < le && r < b) {
			Highlights.markArray(2, r);
			if(Reads.compareValueIndex(array, tmp[l], r, 1, true) <= 0) {
				Writes.write(array, a++, tmp[l++], 0, true, aux);
			} else {
				Writes.write(array, a++, array[r++], 0, true, aux);
			}
		}
		while(l < le)
			Writes.write(array, a++, tmp[l++], 1, true, aux);
    }
    private int quadRoutine(int[] array, int a, int b, int k, boolean aux) {
    	b = Math.min(a+4, b);
    	switch(k) {
    		case 0: // reverse
    			Writes.reversal(array, a, b-1, 0.25, true, aux);
    			return 1;
    		case 1: // first 2 ok
    			// i0 <= i1 > i2 > i3
    			if(a+1<b) Writes.swap(array, a+1, b-1, 0.25, true, aux);
    			while(a+1<b&&cInd(array, a++, aux));
    			break;
    		case 2: // middle ok
    			// i0 > i1 <= i2 > i3
    			if(a+3<b) onlySwap(array, a+2, aux);
    		case 4: // last 2 ok
    			// i0 > i1 > i2 <= i3
    			onlySwap(array, a, aux);
    		case 5: // pairs ok
    			// i0 <= i1 > 12 <= i3
    			if(a+2<b) onlySwap(array, a+1, aux);
    			          cInd(array, a, aux);
    			if(a+3<b) cInd(array, a+2, aux);
    			if(a+2<b) cInd(array, a+1, aux);
    			break;
    		case 3: // first 3 ok
    			// i0 <= i1 <= i2 > i3
    			if(a+3<b) onlySwap(array, a+2, aux);
    			if(a+2<b) {if(cInd(array, a+1, aux)) cInd(array, a, aux);}
    			else cInd(array, a, aux);
    			break;
    		case 6: // last 3 ok
    			// i0 > i1 <= i2 <= i3
    			onlySwap(array, a, aux);
    			while(++a+1<b&&cInd(array,a, aux));
    			break;
    		case 7:
    			return 1;
    	}
    	return 0;
    }
    private int mergeRoutine(int[] array, int[] tmp, int o, int a, int b, int g, boolean aux) {
    	int k = (           Reads.compareIndices(array, a+g-1,   a+g,   10, true) <= 0 ? 1 : 0) |
    			(a+2*g<b && Reads.compareIndices(array, a+2*g-1, a+2*g, 10, true) <= 0 ? 2 : 0) |
    			(a+3*g<b && Reads.compareIndices(array, a+3*g-1, a+3*g, 10, true) <= 0 ? 4 : 0);
    	if(b - a <= 4) {
    		return quadRoutine(array, a, b, k, aux);
    	}
    	switch(k) {
			case 2:
				if(a+3*g >= b) {
	    			Writes.arraycopy(array, a, tmp, 0, g, 1, true, !aux);
		    		headMerge(array, tmp, o, a, a+g, b, aux);
		    		break;
				}
    		case 0:
    	    	if(a+2*g < b) {
    	    		merge(array, tmp, a, a+g, Math.min(a+2*g, b), o, !aux);
    	    	} else if(a+g < b) {
    	    		tailMerge(array, tmp, o, a, a+g, b, 1, aux);
    	    		break;
    	    	}
    	    	if(a+3*g < b) {
    	    		merge(array, array, a+2*g, a+3*g, b, a, !aux);
    	    		tailMerge(array, tmp, o, a, a+(b-a-2*g), b, 1, aux);
    	    	} else {
    	    		headMerge(array, tmp, o, a, a+2*g, b, aux);
    	    	}
    	    	break;
    		case 1:
    			if(a+3*g < b) {
    	    		merge(array, tmp, a+2*g, a+3*g, b, o, aux);
    	    		tailMerge(array, tmp, o, a, a+2*g, b, 2, aux);
    			} else if(a+2*g < b) {
    	    		tailMerge(array, tmp, o, a, a+2*g, b, 0, aux);
    			}
    			break;
    		case 3:
    			if(a+3*g < b)
    				tailMerge(array, tmp, o, a, a+3*g, b, 3, aux);
	    		break;
    		case 4:
    	    	merge(array, tmp, a, a+g, Math.min(a+2*g, b), 0, !aux);
	    		headMerge(array, tmp, o, a, a+2*g, b, aux);
	    		break;
    		case 5:
    	    	tailMerge(array, tmp, o, a, a+2*g, b, 3, aux);
	    		break;
    		case 6:
    			int l = a, r = a + g, M;
        		while(l < r) {
        			M = l + (r - l) / 2;
        			if(Reads.compareIndices(array, a + g, M, 1, true) <= 0) {
        				r = M;
        			} else {
        				l = M + 1;
        			}
        		}
    			Writes.arraycopy(array, l, tmp, o, g-(l-a), 1, true, !aux);
	    		headMerge(array, tmp, o, l, a+g, b, aux);
	    		break;
    		case 7:
    			return 1;
    	}
    	return 0;
    }
    
    public void stableSort(int[] array, int[] t, int o, int a, int b, boolean aux) {
    	boolean shouldDelete = t == null;
    	if(t == null) {
    		t = Writes.createExternalArray((b-a)/2); o = 0;
    	}
    	int i=1, dfsc = 0;
    	for(; i<=(b-a)/4; i*=4) {
    		dfsc = 1;
    		for(int j=a; j+i<b; j+=4*i) {
    			dfsc &= mergeRoutine(array, t, o, j, Math.min(j+4*i, b), i, aux);
    		}
    		if(dfsc == 1) {
    			for(int j=a+4*i; j<b; j+=4*i) {
    				if(Reads.compareIndices(array, j-1, j, 5, true) > 0) {
    					dfsc = 0;
    					break;
    				}
    			}
    		}
			if(dfsc == 1) break;
    	}
		if(dfsc == 0) {
	    	while(i<b-a) {
	    		for(int j=a; j+i<b; j+=2*i)
	    			tailMerge(array, t, o, j, j+i, Math.min(j+2*i, b), 3, aux);
	    		i*=2;
	    	}
		}
    	if(shouldDelete)
    		Writes.deleteExternalArray(t);
    }
    
    @Override
    public void runSort(int[] array, int length, int bucketCount) {
        this.stableSort(array, null, 0, 0, length, false);
    }
}