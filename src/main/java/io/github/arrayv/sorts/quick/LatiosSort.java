package io.github.arrayv.sorts.quick;

import java.awt.Color;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.merge.InPlaceMergeSortIV;
import io.github.arrayv.sorts.templates.Sort;

final public class LatiosSort extends Sort {
    public LatiosSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        
        this.setSortListName("Latios");
        this.setRunAllSortsName("Latios Sort");
        this.setRunSortName("Latios Sort");
        this.setCategory("Quick Sorts");
        this.setAuthors("Distray");
        this.setConstant("n log n");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setQuestion("Set block size (default: 32, minimum: 32)", 32);
        this.setBogoSort(false);
    }
    private InPlaceMergeSortIV small;
	
	private int lg(int v, int b) {
		return (int) (Math.log(v) / Math.log(b));
	}
	
	private int cl2(int val) {
    	return 32 - Integer.numberOfLeadingZeros(val - 1);
    }
	
	// median of 3
	private int m3(int[] array, int l0, int l1, int l2, String use) {
		Highlights.colorCode(use, l0, l1, l2);
		int t;
		if(Reads.compareIndices(array, l0, l1, 5, true) > 0) {
			t = l0; l0 = l1; l1 = t;
		}
		if(Reads.compareIndices(array, l1, l2, 5, true) > 0) {
			t = l1; l1 = l2; l2 = t;
			if(Reads.compareIndices(array, l0, l1, 5, true) > 0) {
				return l0;
			}
		}
		return l1;
	}
	
	// median of medians with customizable depth
	private int mofm(int[] array, int start, int end, int depth) {
		if(end-start < 9 || depth <= 0) {
			return m3(array, start, start+(end-start)/2, end, "med3");
		}
		int e = (end - start) / 8;
		int m0 = mofm(array, start, start + 2 * e, --depth);
		int m1 = mofm(array, start + 3 * e, start + 5 * e, depth);
		int m2 = mofm(array, start + 6 * e, end, depth);
		return m3(array, m0, m1, m2, "medm");
	}
	
	// block-swap for optimized index blocksorter
	private void multiSwap(int[] array, int locA, int locB, int size) {
		for(int i=0; i<size; i++) {
			Highlights.swapColors(locA+i, locB+i);
			Writes.swap(array, locA+i, locB+i, 1, true, false);
		}
	}
	
	private void set(int[] array, int lo, int hi, int val, int log, boolean bit) {
		while(log-- > 0) {
			Highlights.colorCode("tag", lo+log, hi+log);
			if((val % 2 == 1) == bit) {
				Writes.swap(array, lo + log, hi + log, 1, true, false);
			}
			val /= 2;
		}
	}
	
	private int get(int[] array, int block, int piv, int log, int bias) {
		int v = 0, s = 1;
		while(log-- > 0) {
			v |= Reads.compareIndexValue(array, block+log, piv, 1, true) > -bias ? s : 0;
			s *= 2;
		}
		return v;
	}
	
	// visual fancy: arraycopy, but it clears the numbers it's copied from the source
	private void arraycopy_CSNC(int[] src, int offsetsrc, int[] dst, int offsetdst, int len, double sleep, boolean mark, boolean aux) {
		for(int i = 0; i < len; i++) {
			Writes.write(dst, offsetdst + i, src[offsetsrc + i], sleep, mark, aux);
			Writes.visualClear(src, offsetsrc + i);
		}
	}
	// ditto, but reversed
	private void reversearraycopy_ClearSrc(int[] src, int offsetsrc, int[] dst, int offsetdst, int len, double sleep, boolean mark, boolean aux, String use) {
		for(int i = len - 1; i >= 0; i--) {
			Highlights.colorCode(offsetdst + i, use);
			Writes.write(dst, offsetdst + i, src[offsetsrc + i], sleep, mark, aux);
			Writes.visualClear(src, offsetsrc + i);
		}
	}
	// ditto, but copy colorcodes instead of overwriting with the usecase
	private void reversearraycopy_CSCC(int[] src, int offsetsrc, int[] dst, int offsetdst, int len, double sleep, boolean mark, boolean aux) {
		for(int i = len - 1; i >= 0; i--) {
			Highlights.writeColor(src, offsetsrc + i, dst, offsetdst + i);
			Writes.write(dst, offsetdst + i, src[offsetsrc + i], sleep, mark, aux);
			Writes.visualClear(src, offsetsrc + i);
		}
	}
	
	// visual fancy: mark a range with one common alias
	private void batchColorCode_reverse(int start, int end, String alias) {
		for(int i = end - 1; i >= start; i--) {
			Highlights.colorCode(i, alias);
			Highlights.markArray(1, i);
			Delays.sleep(0.75);
		}
	}
	
	private void swapCopy(int[] src, int srcstart, int[] dst, int dststart, int srclen, int dstlen, double sleep, boolean mark, boolean toauxwrite, String use) {
		int i=0, tmp;
		for(; i<Math.min(srclen, dstlen); i++) {
			tmp = src[srcstart+i];
			Writes.write(src, srcstart+i, dst[dststart+i], sleep/2d, mark, toauxwrite);
			Highlights.colorCode(srcstart+i, use);
			Writes.write(dst, dststart+i, tmp, sleep/2d, mark, !toauxwrite);
		}
		for(; i<srclen; i++) {
			Writes.write(dst, dststart+i, src[srcstart+i], sleep, mark, !toauxwrite);
			Writes.visualClear(src, srcstart + i);
		}
		for(; i<dstlen; i++) {
			Writes.write(src, srcstart+i, dst[dststart+i], sleep, mark, toauxwrite);
			Highlights.colorCode(srcstart+i, use);
			Writes.visualClear(dst, dststart + i);
		}
	}
    
    private void sort(int[] array, int[] aux, int start, int end, int bias) {
    	final int blk = aux.length;
    	int p = -1;
    	boolean bf = false;
    	while(end-start > blk) {
    		if(!bf) {
    			int pI = mofm(array, start, end-1, lg(end-start, 9));
    	    	p = array[pI];
    		}
	    	int l0 = 0, l1 = 0, b = 0, r0 = 0, r1 = 0;
	    	boolean chkeq = false, invert = false;
	    	for(int i=start; i<end; i++) {
	    		int c=Reads.compareIndexValue(array, i, p, 1, true);
	    		if(c != 0)
	    			chkeq = true;
	    		boolean R = c > -bias;
	    		int t0 = R ? r0 : l0, t1 = R ? r1 : l1;
	    		if(c > -bias ^ invert) {
	    			Writes.write(aux, t0++, array[i], 1, true, true);
	    			if(t0 == blk) {
	    				swapCopy(array, start+b, aux, 0, R?l0:r0, blk, 1, true, false, R?"high":"low");
	    				t0 = 0;
	    				t1++;
	    				b+=blk;
	    				invert=!invert;
	    			} else {
	    				Writes.visualClear(array, i);
	    			}
	    		} else {
    				Highlights.colorCode(start+b+t0, R?"high":"low");
	    			if(start+b+t0 < i) {
		    			int z = array[i];
	    				Writes.visualClear(array, i);
	    				Writes.write(array, start+b+t0, z, 1, true, false);
	    			}
	    			if(++t0 == blk) {
	    				t0 = 0;
	    				t1++;
	    				b+=blk;
	    			}
	    		}
	    		if(R) {
	    			r0 = t0; r1 = t1;
	    		} else {
	    			l0 = t0; l1 = t1;
	    		}
	    	}
	    	int m = Math.min(l1, r1);
	    	if(m > 0) {
	    		int z = cl2(m), c = l1 + r1, t = start + c * blk;
				int j, k;
				j = k = start;
				for(int i = 0; i < m; i++) {
					while(Reads.compareValues(array[j+z], p) <= -bias) j += blk;
					while(Reads.compareValues(array[k+z], p) > -bias)  k += blk;
					set(array, j, k, i, z, l1 < r1);
					j += blk;
					k += blk;
				}
				if(l1 < r1) {
					for(j = t - blk, k = t; j >= start; j -= blk) {
						if(Reads.compareValues(array[j+z], p) > -bias) {
							multiSwap(array, j, k -= blk, blk);
						}
					}
					for(int i = start, h = 0; i < k; i += blk, h++) {
						int w = get(array, i, p, z, bias);
						while(h != w) { // index sort
							multiSwap(array, start+w*blk, i, blk);
							w = get(array, i, p, z, bias);
						}
						set(array, i, k+h*blk, h, z, l1 < r1); // compareless clear the block tag
					}
				} else {
					for(j = k = start; j < t; j += blk) {
						if(Reads.compareValues(array[j+z], p) <= -bias) {
							multiSwap(array, j, k, blk);
							k += blk;
						}
					}
					for(int h = 0; k < t; k += blk, h++) {
						int w = get(array, k, p, z, bias);
						while(h != w) {
							multiSwap(array, k+(w-h)*blk, k, blk);
							w = get(array, k, p, z, bias);
						}
						set(array, k, start+h*blk, h, z, l1 < r1); // compareless clear the block tag
					}
				}
	    	}
	    	if(invert) {
	    		if(l0 > 0) {
	    			reversearraycopy_CSCC(array, start+l1*blk, array, end-r0-r1*blk, r0+r1*blk, 1, true, false);
			    	reversearraycopy_ClearSrc(aux, 0, array, start+l1*blk, l0, 1, true, false, "lowfrag");
	    		}
	    	} else {
		    	reversearraycopy_ClearSrc(aux, 0, array, end-r0, r0, 1, true, false, "highfrag");
		    	if(l0 > 0) {
			    	arraycopy_CSNC(array, start+b, aux, 0, l0, 1, true, false);
		    		reversearraycopy_CSCC(array, start+l1*blk, array, end-r0-r1*blk, r1*blk, 1, true, false);
			    	reversearraycopy_ClearSrc(aux, 0, array, start+l1*blk, l0, 1, true, false, "lowfrag");
		    	}
	    	}
	    	if(chkeq) {
	    		bf = l0+l1==0||r0+r1==0;
	    		if(bf) {
	    			bias ^= 1;
	    			continue;
	    		}
				Writes.recursion();
				if(l1 > r1) {
					sort(array, aux, start + l0 + l1 * blk, end, bias); // sort right side with stack
					end = start + l0 + l1 * blk; // go onto left side without stack
				} else {
					sort(array, aux, start, start += l0 + l1 * blk, bias); // sort left side with stack, go on to right side
				}
	    	} else {
				batchColorCode_reverse(start, end, "sorted");
	    		return;
	    	}
    	}
    	small.IPM4(array, start, end);
		batchColorCode_reverse(start, end, "sorted");
    }

    @Override
    public void runSort(int[] array, int currentLength, int block) {
    	int[] a = Writes.createExternalArray(block);
		Highlights.retainColorMarks(true);
		Highlights.defineColor("med3", new Color(142, 225, 225));
		Highlights.defineColor("medm", new Color(142, 142, 255));
		Highlights.defineColor("low", new Color(255, 245, 142));
		Highlights.defineColor("high", new Color(142, 255, 152));
		Highlights.defineColor("tag", new Color(114, 204, 156));
		Highlights.defineColor("lowfrag", new Color(255, 142, 142));
		Highlights.defineColor("highfrag", new Color(255, 142, 255));
		Highlights.defineColor("sorted", new Color(0, 255, 0));
		small = new InPlaceMergeSortIV(arrayVisualizer);
    	sort(array, a, 0, currentLength, 0);
    	Writes.deleteExternalArray(a);
    }
}