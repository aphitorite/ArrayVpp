package io.github.arrayv.sorts.quick;

import java.awt.Color;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;

final public class SimplifiedAeosQSort extends Sort {
	public SimplifiedAeosQSort(ArrayVisualizer arrayVisualizer) {
		super(arrayVisualizer);
		
		this.setSortListName("Simplified Aeos Quick");
		this.setRunAllSortsName("Simplified Aeos Quicksort");
		this.setRunSortName("Simplified Aeos Quicksort");
		this.setCategory("Quick Sorts");
  	    this.setAuthors("Anonymous0726, Distray");
  	    this.setConstant("n log n");
		this.setBucketSort(false);
		this.setRadixSort(false);
		this.setUnreasonablySlow(false);
		this.setUnreasonableLimit(0);
		this.setBogoSort(false);
	}
	
	private int lg(int v, int b) {
		return (int) (Math.log(v) / Math.log(b));
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
	
	// visual fancy: arraycopy, but it clears the numbers it's copied from the source
	private void arraycopy_ClearSrc(int[] src, int offsetsrc, int[] dst, int offsetdst, int len, double sleep, boolean mark, boolean aux, String use) {
		for(int i = 0; i < len; i++) {
			Highlights.colorCode(offsetdst + i, use);
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
	
	// planned custom index aux size: rotate partition when you run out indices
	// planned: faster stable partition when smaller than element buffer
	// block partition for small buffer
	public void sort(int[] array, int[] laux, int[] iaux, int start, int end, int bias, int depth, boolean heading) {
		Writes.recordDepth(depth++);
		final int blen = laux.length / 2; // block length (should not change)
		
		// WTF: p has to be initialized, otherwise it errors
		// (it gets initialized by a variable that already evaluates to true at the start)
		int p = -1; 
		
		boolean biasflip = false;
		while(end-start >= 9) { // repeat until you hit fallback
			if(heading)
				arrayVisualizer.setExtraHeading(String.format(", Greater%s Bias%s", bias == 1 ? "/Equal" : "", biasflip ? " (bias flipped)" : ""));
			if(!biasflip) {
				int pI = mofm(array, start, end-1, lg(end-start, 9)); // get median of medians with depth log_9(n)
				p = array[pI];
			}
			int s0 = 0, s1 = 0, t = start, blkslo = 0, blks = 0; // other info (mainly blocks info)
			
			boolean chkeq = false; // equal check, skips further partitioning on no unique
			
			for(int i = start; i < end; i++) {
				Highlights.markArray(1, i);
				Delays.sleep(0.125);
				int cmp = Reads.compareValues(array[i], p);
				if(cmp != 0) // set the flag if you find elements distinct from pivot
					chkeq = true;
				if(cmp > -bias) {
					Writes.write(laux, blen + s1++, array[i], 1, true, true); // put the number into the high bucket

					if(s1 >= blen) { // then, unload a high block back into the main array if you're out of space
						arraycopy_ClearSrc(laux, blen, array, t, blen, 1, true, false, "high");
						t += blen; s1 = 0;
						Writes.write(iaux, blks++, -1, 1, true, true);
					}
				} else {
					Writes.write(laux, s0++, array[i], 1, true, true); // put the number into the low bucket

					if(s0 >= blen) { // then, unload a low block back into the main array if you're out of space
						arraycopy_ClearSrc(laux, 0, array, t, blen, 1, true, false, "low");
						t += blen; s0 = 0;
						Writes.write(iaux, blks++, blkslo, 1, true, true);
						blkslo += blen;
					}
				}
				if(t < i && blks > 0)
					Writes.visualClear(array, i);
			}
			
			// assuming the equal check fails (distinct from pivot),
			if(chkeq) {
				// fill unresolved blocks, now that we know how many low blocks there are
				for(int i = 0, blkshi = blkslo; i < blks; i++) {
					if(iaux[i] == -1) {
						Highlights.markArray(1, start + i * blen);
						Writes.write(iaux, i, blkshi, 5, true, true);
						blkshi += blen;
					}
				}
				
				// do an *optimized* index sort on the blocks (aids auxwrites very slightly)
				for(int i = 0; i < blks; i++) {
					int now = iaux[i];
					while(Reads.compareOriginalValues(now/blen, i) != 0) {
						int fr = start + now,
						    to = start + i * blen;
						multiSwap(array, fr, to, blen);
						int tmp = iaux[now/blen];
						Writes.write(iaux, now/blen, now, 0.25, true, true);
						now = tmp;
					}
					Writes.write(iaux, i, now, 0.25, true, true);
				}
			}
			int blkshi = (blks * blen) - blkslo;
			
			reversearraycopy_ClearSrc(laux, blen, array, end - s1, s1, 1, true, false, "highfrag"); // unload the high fragment back into main
			if(s0 != 0) { // and if there is a low fragment,
				reversearraycopy_CSCC(array, start + blkslo, array, end - (s1 + blkshi), blkshi, 1, true, false); // push the high blocks back,
				reversearraycopy_ClearSrc(laux, 0, array, start + blkslo, s0, 1, true, false, "lowfrag"); // and concatenate it with the low blocks
			}
			
			if(chkeq) { // if the equal check fails, recurse
				biasflip = s1 + blkshi == 0 || s0 + blkslo == 0;
				if(biasflip) { // flip the bias if there are no blocks on one side
					bias ^= 1;
					continue;
				}
				Writes.recursion();
				if(blkslo > blkshi) {
					sort(array, laux, iaux, start + s0 + blkslo, end, bias, depth, heading); // sort right side with stack
					end = start + s0 + blkslo; // go onto left side without stack
				} else {
					sort(array, laux, iaux, start, start += s0 + blkslo, bias, depth, heading); // sort left side with stack, go on to right side
				}
			} else {
				batchColorCode_reverse(start, end, "sorted"); // color the equal subsection with the "sorted" alias
				return;
			}
		}
		TernaryRotateQuickSort t = new TernaryRotateQuickSort(arrayVisualizer); // do a ternary rotate partition on small n
		t.partition(array, start, end);
		batchColorCode_reverse(start, end, "sorted"); // color the subsection with the "sorted" alias
	}
	
	@Override
	public void runSort(int[] array, int currentLength, int bucketCount) {
		int l = 0, s = 1;
		while((1<<(l+=2)) < currentLength) {
			s *= 2;
		}
		int[] LAUX = Writes.createExternalArray(2 * s);
		int[] IAUX = Writes.createExternalArray(currentLength / s);
		Highlights.retainColorMarks(true);
		Highlights.defineColor("med3", new Color(142, 225, 225));
		Highlights.defineColor("medm", new Color(142, 142, 255));
		Highlights.defineColor("low", new Color(255, 245, 142));
		Highlights.defineColor("high", new Color(142, 255, 152));
		Highlights.defineColor("lowfrag", new Color(255, 142, 142));
		Highlights.defineColor("highfrag", new Color(255, 142, 255));
		Highlights.defineColor("sorted", new Color(0, 255, 0));
		sort(array, LAUX, IAUX, 0, currentLength, 0, 0, true);
		arrayVisualizer.setExtraHeading("");
		Writes.deleteExternalArrays(LAUX, IAUX);
	}
}