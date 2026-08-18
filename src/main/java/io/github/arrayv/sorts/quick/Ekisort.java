package io.github.arrayv.sorts.quick;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.hybrid.NilSort;
import io.github.arrayv.sorts.hybrid.WhippingCreamSort;
import io.github.arrayv.sorts.insert.BinaryInsertionSort;
import io.github.arrayv.sorts.insert.BlockInsertionSortNeon;
import io.github.arrayv.sorts.templates.Sort;
import io.github.arrayv.utils.Rotations;

final public class Ekisort extends Sort {
	public Ekisort(ArrayVisualizer arrayVisualizer) {
		super(arrayVisualizer);
		this.setSortListName("Eki");
		this.setRunAllSortsName("Eki Sort");
		this.setRunSortName("Ekisort");
		this.setCategory("Quick Sorts");
        this.setAuthors("Distray");
		this.setBucketSort(false);
		this.setRadixSort(false);
		this.setUnreasonablySlow(false);
		this.setUnreasonableLimit(0);
		this.setBogoSort(false);
	}
	// Ekisort: In-place stable Kota partitioning algorithm in O(n^0.66) worst space
	private static int tolerance = 64, minblinsert = 8;
	private NilSort nilFuncs;
	private BlockInsertionSortNeon blinserter;
	private BinaryInsertionSort binserter;
	private WhippingCreamSort whipping;

	 /**
	  * Branchless comparator for 2 values.
	  * Returns {@code 1} if {@code array[pos0]} > {@code array[pos1]}, 0 otherwise.
	  * @param Array to work on
	  * @param Position of first number in array
	  * @param Position of second number in array
	  */
	
	private int cmp(int[] array, int pos0, int pos1) {
		int cmp = Reads.compareIndices(array, pos1, pos0, 0.125, true);
		return -(cmp >> 31);
	}

	 /**
	  * Calculates the closest power of 2 to {@code n}<sup>{@code 0.66}</sup>.
	  * @param Value which n equals
	  */
	
	private int fcrt(int v) {
		int l=0;
		while(1<<(++l+(l>>1))<v);
		return 1<<l;
	}

	 /**
	  * Calculates the closest power of 2 to {@code n}<sup>{@code 0.33}</sup>.
	  * @param Value which n equals
	  */
	
	private int cbrt(int v) {
		int l=0;
		while(1<<(3*++l)<v);
		return 1<<l;
	}

	 /**
	  * Subarray class, used for Cai merging.
	  */
	
	private class CaiBuf {
		public int pstart, start, mid, end;
		public CaiBuf(int start, int mid, int end) {
			this.pstart = this.start = start;
			this.mid = mid;
			this.end = end;
		}
		public int sortedLength() {
			if(end-mid <= 0) return -1;
			return end-mid;
		}
		public boolean oob() {
			return mid>=end || start>=end || start>mid;
		}
		public int bufferLength() {
			return mid-start;
		}
		public String toString() {
			return String.format("<%d, %d, %d>", start, mid, end);
		}
	}

	 /**
	  * Subarray class, used for Kota merging.
	  */
	
	private class Subarr implements Comparable<Subarr> {
		public int start, mid, end;
		public Subarr(int start, int mid, int end) {
			this.start = start;
			this.mid = mid;
			this.end = end;
		}
		public Subarr(int start, int end) {
			this.start = start;
			this.mid = this.end = end;
		}
		public int length() {
			return end - start;
		}
		public String toString() {
			return String.format("block <%d, %d, %d>", start, mid, end);
		}
		public int compareTo(Subarr other) {
			return (int) Math.signum(start - other.start);
		}
	}

	 /**
	  * Much faster unspread median of 3.
	  * @param The array to work on
	  * @param The location of the 3 medians, 1 apart
	  */
	
	private int medof3(int[] array, int loc) {
		int a = cmp(array, loc, loc+1),
			b = cmp(array, loc+(a^1), loc+2);
		b += (a^1)|b;
		int c = cmp(array, loc+b, loc+a);
		return loc + (((c - 1) & a) | (-c & b));
	}

	 /**
	  * Spread-out median of 3.
	  * @param The array to work on
	  * @param The first median location
	  * @param The second median location
	  * @param The third median location
	  */
	
	private int medof3(int[] array, int l, int l1, int l2) {
		int[] spread = new int[] {l, l1, l2};
		int a = cmp(array, l, l1),
			b = cmp(array, spread[a^1], l2);
		b += (a^1)|b;
		int c = cmp(array, spread[b], spread[a]);
		return spread[((c - 1) & a) | (-c & b)];
	}

	 /**
	  * Ninther, using a median of 3 medians of 3.
	  * @param The array to work on
	  * @param The start of the range
	  * @param The end of the range
	  */
	
	private int ninther(int[] array, int a, int b) {
		if(b-a < 4) {
			return a+(b-a)/2;
		}
		if(b-a < 8) {
			return medof3(array, a+(b-a-1)/2);
		}
		int d = (b-a+1)/8,
			m0 = medof3(array, a, a+d, a+2*d),
			m1 = medof3(array, a+3*d, a+4*d, a+5*d),
			m2 = medof3(array, a+6*d, a+7*d, b);
		return medof3(array, m0, m1, m2);
	}

	 /**
	  * Pseudomedian of 27, using a median of 3 ninthers.
	  * @param The array to work on
	  * @param The start of the range
	  * @param The end of the range
	  */
	
	private int pseudomo27(int[] array, int a, int b) {
		if(b-a < 3*27) {
			return ninther(array, a, b);
		}
		int d = (b-a+1)/8,
			m0 = ninther(array, a,a+2*d),
			m1 = ninther(array, a+3*d, a+5*d),
			m2 = ninther(array, a+6*d, b);
		return medof3(array, m0, m1, m2);
	}

	 /**
	  * Pseudomedian of 243, using a pseudomedian of 9 ninthers.
	  * @param The array to work on
	  * @param The start of the range
	  * @param The end of the range
	  */
	
	private int pseudomo81(int[] array, int a, int b) {
		if(b-a < 4*81) {
			return pseudomo27(array, a, b);
		}
		int d = (b-a+1)/24,
			m0 = ninther(array, a, a+2*d),
			m1 = ninther(array, a+3*d, a+5*d),
			m2 = ninther(array, a+6*d, a+8*d),
			m3 = ninther(array, a+9*d, a+11*d),
			m4 = ninther(array, a+12*d, a+14*d),
			m5 = ninther(array, a+15*d, a+17*d),
			m6 = ninther(array, a+18*d, a+20*d),
			m7 = ninther(array, a+19*d, a+21*d),
			m8 = ninther(array, a+22*d, b);
		return medof3(array,
			medof3(array, m0, m1, m2),
			medof3(array, m3, m4, m5),
			medof3(array, m6, m7, m8)
		);
	}

	 /**
	  * Pseudomedian of 243, using a pseudomedian of 9 psuedomedians of 27.
	  * @param The array to work on
	  * @param The start of the range
	  * @param The end of the range
	  */
	
	private int pseudomo243(int[] array, int a, int b) {
		if(b-a < 4*243) {
			return pseudomo81(array, a, b);
		}
		int d = (b-a+1)/24,
			m0 = pseudomo27(array, a, a+2*d),
			m1 = pseudomo27(array, a+3*d, a+5*d),
			m2 = pseudomo27(array, a+6*d, a+8*d),
			m3 = pseudomo27(array, a+9*d, a+11*d),
			m4 = pseudomo27(array, a+12*d, a+14*d),
			m5 = pseudomo27(array, a+15*d, a+17*d),
			m6 = pseudomo27(array, a+18*d, a+20*d),
			m7 = pseudomo27(array, a+19*d, a+21*d),
			m8 = pseudomo27(array, a+22*d, b);
		return medof3(array,
			medof3(array, m0, m1, m2),
			medof3(array, m3, m4, m5),
			medof3(array, m6, m7, m8)
		);
	}

	 /**
	  * Exclusive binary search, which returns {@code -1} when the key is a duplicate.
	  * @param The array to work on
	  * @param The start of the searching range
	  * @param The end of the searching range
	  * @param The key you want to search with
	  */
	
	private int binSearchE(int[] array, int l, int r, int k) {
		int a=0, b=r-l, m;
		while(a<b) {
			m=a+((b-a)>>1);
			switch(Reads.compareIndexValue(array, l+m, k, 1.25, true)) {
				case 1:
					b=m;
					break;
				case 0:
					return -1;
				case -1:
					a=m+1;
					break;
			}
		}
		return l+a;
	}

	 /**
	  * Optimized variant of {@code Writes.multiSwap}.
	  * @param The array to work on
	  * @param The location of the key
	  * @param Where the key should be pushed
	  * @param How long to sleep
	  * @param What alias you want to colorcode with
	  */
	
	private void shift(int[] array, int from, int to, double sleep, String use) {
		if(from == to)
			return;
		int k = array[from];
		for(int i=from; i<to; i++) {
			Highlights.colorCode(i, use);
		}
		for(int i=from-1; i>=to; i--) {
			Highlights.colorCode(i, use);
		}
		if(from < to) {
			Writes.arraycopy(array, from+1, array, from, to-from, sleep/2d, true, false);
		} else {
			Writes.reversearraycopy(array, to, array, to+1, from-to, sleep/2d, true, false);
		}
		Writes.write(array, to, k, sleep, true, false);
	}

	 
	 /**
	  * Radon/Tridge rotations, as found in Bad Tsrtsort. (WIP)
	  * @param The array to work on
	  * @param The start of the left side
	  * @param The length of the left side
	  * @param The length of the right side
	  * @param Whether to rotate the colorcodes as well
	  */
	
	private void rotate(int[] array, int pos, int leftLen, int rightLen, boolean CC) {
		int min = Math.min(leftLen, rightLen),
			max = Math.max(leftLen, rightLen),
			bridge = max-min;
		if(min <= 0)
			return;
		if(bridge <= min) {
			for(int i=min; i<max; i++) {
				if(CC) {
					int left = pos + i,
						right = pos + max - (i - min) - 1;
					if(left < right)
						Highlights.swapColors(left, right);
					else
						break;
				}
				else
					Highlights.colorCode("tridge-rev", pos+i);
			}
			Writes.reversal(array, pos+min, pos+max-1, 0.5, true, false);
		}
		for(int i=0; i<min; i++) {
			if(CC)
				Highlights.swapColors(pos+i, pos+max+i);
			else
				Highlights.colorCode("tridge-bridge", pos+i, pos+max+i);
			Writes.swap(array, pos+i, pos+max+i, 0.5, true, false);
		}
		if(bridge == 0)
			return;
		int a, b, c, swap;
		if(leftLen > rightLen) {
			if(bridge > min) {
				Rotations.cycleReverse(array, pos+min, bridge, min, 0.5, true, false);
				return;
			}
			a = pos + min;
			b = pos + max;
			c = b + min - 1;
			while(b < c) {
				swap = array[c];
				if(CC) {
					Highlights.swapColors(a, b);
					Highlights.swapColors(b, c);
				} else
					Highlights.colorCode("tridge-swap3", a, b, c);
				Writes.write(array, c--, array[a], 0.5, true, false);
				Writes.write(array, a++, array[b], 0.5, true, false);
				Writes.write(array, b++, swap, 0.5, true, false);
			}
		} else {
			if(bridge > min) {
				Rotations.cycleReverse(array, pos, min, bridge, 0.5, true, false);
				return;
			}
			a = pos; 
			b = a + min - 1; 
			c = a + max - 1;
			while(a < b) {
				swap = array[a];
				if(CC) {
					Highlights.swapColors(c, b);
					Highlights.swapColors(b, a);
				} else
					Highlights.colorCode("tridge-swap3", a, b, c);
				Writes.write(array, a++, array[c], 0.5, true, false);
				Writes.write(array, c--, array[b], 0.5, true, false);
				Writes.write(array, b--, swap, 0.5, true, false);
			}
		}
		for(int i=a; i<=c; i++) {
			if(CC) {
				int left = i,
					right = c - (i - a);
				if(left < right)
					Highlights.swapColors(left, right);
				else
					break;
			} else {
				Highlights.colorCode("tridge-rev", i);
			}
		}
		Writes.reversal(array, a, c, 0.5, true, false);
	}

	 
	 /**
	  * Get the minimum key, assuming there's still a sorted subarray in the exclusion.
	  * @param The array to work on
	  * @param A list of each of the subarrays, with their buffer
	  * @param Which subarray to exclude
	  */
	 protected int minKey0(int[] array, ArrayList<CaiBuf> buffers, int exclude) {
		 int min=exclude;
		 if(buffers.get(min).sortedLength() <= 0 || buffers.get(min).oob())
			 return -1;
		 boolean ltm = true;
		 for(int i=0; i<buffers.size(); i++) {
			 if(buffers.get(i).sortedLength() <= 0 || buffers.get(min).oob()) continue;
			 if(i >= min && ltm)
				 ltm = false;
			 if(i != exclude) {
				 int cmp = Reads.compareIndices(array, buffers.get(i).mid, buffers.get(min).mid, 0.1, true);
				 if(cmp < 0 || (cmp == 0 && ltm)) {
					 min = i;
					 if(ltm) {
						 ltm = false;
					 }
				 }
			 }
		 }
		 return min;
	 }
	 
	 /**
	  * Get the minimum key outside of the current subarray.
	  * @param The array to work on
	  * @param A list of each of the subarrays, with their buffer
	  * @param Which subarray to exclude
	  */
	 protected int minKey1(int[] array, ArrayList<CaiBuf> buffers, int exclude) {
		 int min=0;
		 while(min < buffers.size() && (min==exclude || buffers.get(min).sortedLength() <= 0))
			 min++;
		 if(min == buffers.size())
			 return -1;
		 for(int i=0; i<buffers.size(); i++) {
			 if(buffers.get(i).sortedLength() <= 0) continue;
			 if(i != min && Reads.compareIndices(array, buffers.get(i).mid, buffers.get(min).mid, 0.1, true) < 0)
				 min=i;
		 }
		 return min;
	 }
	 
	 /**
	  * Fallback stable K-way merge that works with very little buffer.
	  * *still in desperate need of cleanup*
	  * @param The array to work on
	  * @param The start and end of each subarray
	  */
	 private void stableCaiMerge(int[] array, int... ptrs) {
		 ArrayList<CaiBuf> buffers = new ArrayList<>();
		 for(int i=0; i<ptrs.length-1; i++) {
			 buffers.add(new CaiBuf(ptrs[i], ptrs[i], ptrs[i+1]));
		 }
		 buffers.get(0).start = buffers.get(0).pstart = keysLoc;
		 int to = keysLoc;
		 while(true) {
			 int maxBuffer = 0;
			 boolean oob = true;
			 for(int i=1; i<buffers.size(); i++) {
				 if(buffers.get(maxBuffer).bufferLength() < buffers.get(i).bufferLength()) {
					 maxBuffer = i;
				 }
				 oob = oob && buffers.get(i).oob();
			 }
			 CaiBuf now = buffers.get(maxBuffer);
			 if(oob) { // nope out (ensure you don't get stuck)
				 break;
			 }
			 while(now.bufferLength() > 0) { // merge the values while buffer remains
				 int j = minKey0(array, buffers, maxBuffer);
				 if(j == -1)
					 break;
				 Highlights.colorCode(buffers.get(j).mid, "buffer");
				 Highlights.colorCode(now.start, "cai-fallback");
				 Writes.swap(array, now.start++, buffers.get(j).mid++, 1, true, false);
			 }
			 if(now.bufferLength() > 0) { // still buffer remaining,
				 while(now.bufferLength() > 0) { // merge outside of the subarray
					 int j = minKey1(array, buffers, maxBuffer);
					 if(j == -1)
						 break;
					  Highlights.colorCode(buffers.get(j).mid, "buffer");
					  Highlights.colorCode(now.start, "cai-fallback");
					 Writes.swap(array, now.start++, buffers.get(j).mid++, 1, true, false);
				 }
			 }
			 if(maxBuffer > 0) { // push merged section back, if required
				 int e = now.pstart;
				 indexRotate(array, to, e, now.start, true);
				 for(int i=maxBuffer-1; i>=0; i--) { // adjust all the subarrays behind accordingly
					 buffers.get(i).end += now.start-e;
					 buffers.get(i).mid += now.start-e;
					 buffers.get(i).start += now.start-e;
					 buffers.get(i).pstart += now.start-e;
				 }
				 to += now.start - e;
				 now.pstart = now.start;
			 } else { // just change the variables, nothing else needs to be done here
				 to = now.pstart = now.start;
			 }
		 }
		 // push remaining buffers back (Caisort can't handle its buffer, apparently)
		 for(CaiBuf i : buffers) {
			 if(i.sortedLength() > 0) {
				 for(int j=i.mid; j<i.end; j++) {
					 Writes.swap(array, to++, j, 1, true, false);
				 }
			 }
		 }
		 keysLoc = ptrs[ptrs.length-1]-keysSize;
	 }

	/**
	 * Collection-based wrapper for {@code Ekisort.stableCaiMerge(int[], int...)}.
	 * @param Array to work on
	 * @param List describing the start and end of each subarray
	 */
	 
	 public void stableCaiMerge(int[] array, Collection<Integer> ptrs) {
		 Integer[] norm = ptrs.toArray(new Integer[0]);
		 int[] prim = new int[ptrs.size()];
		 for(int i=0; i<ptrs.size(); i++) {
			 prim[i] = norm[i];
		 }
		 stableCaiMerge(array, prim);
	 }

	/**
	 * Rotate a range, using [start...mid...end] instead of [start...start+len0...start+len1].
	 * @param Array to work on
	 * @param Start of left side
	 * @param End of left side, start of right side
	 * @param Whether to rotate the colorcodes as well
	 */
	
	private void indexRotate(int[] array, int start, int mid, int end, boolean CC) {
		rotate(array, start, mid-start, end-mid, CC);
	}

	/**
	 * Swap blocks of a given size.
	 * @param Array to work on
	 * @param Start of block A
	 * @param Start of block B
	 * @param Size of both blocks
	 */
	
	private void multiSwap(int[] array, int locA, int locB, int size) {
		for(int i=0; i<size; i++) {
			Highlights.swapColors(locA+i, locB+i);
			Writes.swap(array, locA+i, locB+i, 1, true, false);
		}
	}

	/**
	 * Get a set amount of unique keys, and push them back to the start of the array.
	 * @param Array to work on
	 * @param Start of the working range
	 * @param End of the working range
	 * @param Amount of keys wanted
	 * @param What alias you want to colorcode the keys with
	 */
	
	private int getKeys(int[] array, int start, int end, int keysNeeded, String use) {
		int keysNow = 1, keysAt = start, i = start + 1, uniquesPush = 0;
		while(i < end && keysNow < keysNeeded) {
			Highlights.markArray(3, i);
			int search = binSearchE(array, keysAt, keysAt + keysNow, array[i]);
			if(search == -1) {
				uniquesPush++;
			} else {
				if(uniquesPush > Math.min(tolerance, keysNow)) {
					rotate(array, keysAt, keysNow, uniquesPush, false);
					search += uniquesPush;
					keysAt += uniquesPush;
					uniquesPush = 0;
				}
				shift(array, i, search, 0.5, uniquesPush > 0 ? "lazykeyfind" : "keyfind");
				keysNow++;
			}
			i++;
		}
		Highlights.clearMark(3);
		rotate(array, start, keysAt-start, keysNow, true);
		for(int j=start; j<start+keysNow; j++) {
			Highlights.colorCode(j, use);
		}
		return keysNow;
	}

	/**
	 * Iterative ping-pong merge, used in {@code Ekisort.makePartition}.
	 * @param Array to work on
	 * @param Start of the working range
	 * @param End of the working range
	 * @param Buffer to merge to
	 * @param Whether you're merging with auxiliary or not
	 */
	
	private boolean pingPongMerge(int[] array, int start, int end, int buff) {
		for(int i = start; i < end; i += 32) {
			binserter.customBinaryInsert(array, i, Math.min(i+32, end), 0.25);
		}
		boolean aux = false;
		for(int j = 32; j < end - start; j *= 2) {
			int from = aux ? buff : start, to = aux ? start : buff;
			for(int i = 0; i < end - start; i += 2 * j) {
				int lo = from + i, le = from + i + j, en = from + Math.min(i + 2 * j, end - start);
				if(le > en) {
					le = en;
				}
				int hi = le;
				while(lo < le && hi < en) {
					Highlights.colorCode(to, "pingpong");
					if(Reads.compareValues(array[lo], array[hi]) <= 0) {
						Highlights.colorCode(lo, "buffer");
						Writes.swap(array, lo++, to++, 1, true, false);
					} else {
						Highlights.colorCode(hi, "buffer");
						Writes.swap(array, hi++, to++, 1, true, false);
					}
				}
				while(lo < le) {
					Highlights.colorCode(lo, "buffer");
					Highlights.colorCode(to, "pingpong");
					Writes.swap(array, lo++, to++, 1, true, false);
				}
				while(hi < en) {
					Highlights.colorCode(hi, "buffer");
					Highlights.colorCode(to, "pingpong");
					Writes.swap(array, hi++, to++, 1, true, false);
				}
			}
			aux = !aux;
		}
		return !aux;
	}

	/**
	 * Makes partial partitions, and returns a subarray.
	 * @param Array to work on
	 * @param Start of the working range
	 * @param Pivot to use
	 * @param End of the working range
	 */
	
	private Subarr makePartition(int[] array, int start, int pivot, int end) {
		int large = 0, small = 0, i = start;
		while(i < end && large < keysSize) {
			if(Reads.compareValues(array[i], pivot) >= 0) {
				Highlights.colorCode("swappart", large+keysLoc);
				Writes.swap(array, i, large + keysLoc, 1, true, false);
				large++;
			} else {
				Highlights.colorCode("mainpart", small+start);
				Writes.swap(array, i, small + start, 1, true, false);
				small++;
			}
			i++;
		}
		if(pingPongMerge(array, keysLoc, keysLoc + large, small + start))
			multiSwap(array, keysLoc, small + start, large);
		return new Subarr(start, small + start, i);
	}

	/**
	 * Copy of {@code Nilsort.pingpong}, to get around publicity.
	 * @param Array to work on
	 * @param Start of the working range
	 * @param End of the working range
	 */
	
	private void pingpong(int[] array, int start, int end) {
		if(end-start <= minblinsert) {
			blinserter.insertionSort(array, start, end);
			return;
		}
		for(int i=start; i<end; i+=minblinsert)
			blinserter.insertionSort(array, i, Math.min(i+minblinsert, end));
		for(int j=minblinsert; j<end-start; j*=2) {
			for(int i=start; i<end; i+=2*j) {
				if(i+j >= end) break;
				int k=Math.min(i+2*j, end);
				if(j < keysSize) {
					multiSwap(array, i, keysLoc, j);
					int l=keysLoc, r=i+j, t=i;
					while(l<keysLoc+j && r<k) {
						if(Reads.compareValues(array[l], array[r]) <= 0) {
							Writes.swap(array, l++, t++, 1, true, false);
						} else {
							Writes.swap(array, r++, t++, 1, true, false);
						}
					}
					while(l<keysLoc+j) {
						Writes.swap(array, l++, t++, 1, true, false);
					}
				} else {
					nilFuncs.grailMergeWithoutBuffer(array, i, j, k-i-j);
				}
			}
		}
	}

	/**
	 * Copy of {@code Nilsort.ppmergeruns}, to get around publicity.
	 * Head-merges at least 2x buffer-sized runs.
	 * @param Array to work on
	 * @param Start of the working range
	 * @param End of the working range
	 */
	
	private void ppmergeruns(int[] array, int start, int end) {
		int m=Math.max(minblinsert, keysSize);
		for(int i=start; i<end; i+=m) {
			pingpong(array, i, Math.min(i+m, end));
		}
	}

	/**
	 * Nilsort, without buffer redistribution
	 * @param Array to work on
	 * @param Start of the working range
	 * @param End of the working range
	 */
	
	private void nilSort_noBufferRedist(int[] array, int start, int end) {
		nilFuncs.bufsz = keysSize;
		if(keysSize < 4) {
			binserter.customBinaryInsert(array, start, end, 1);
			return;
		}
		ppmergeruns(array, start, end);
		for(int j=Math.max(keysSize, minblinsert); j<end-start; j*=2) {
			if(start < keysLoc)
				indexRotate(array, start - keysSize, keysLoc, keysLoc+keysSize, true);
			else
				indexRotate(array, keysLoc, keysLoc+keysSize, start, true);
			nilFuncs.buf = keysLoc = start - keysSize;
			for(int i=keysLoc + keysSize; i<end; i+=2*j) {
				if(i+j >= end)
					break;
				nilFuncs.zeromerge(array, i, i+j, Math.min(i+2*j, end));
			}
			keysLoc = nilFuncs.buf;
		}
	}

	/**
	 * Function identical to {@code Array.prototype.some} in Javascript
	 * @param Array to look at
	 * @param Function to check elements against
	 */
	
	private <T> boolean some(T[] array, Function<? super T, Boolean> func) {
		for(T i : array) {
			if(func.apply(i)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * (Kota) Find the minimum index
	 * @param Array to work on
	 * @param List of {@code Subarr}s to look through
	 */
	
	private Subarr minKota(int[] array, Subarr[] all) {
		Subarr n = all[0];
		for(Subarr i : all) {
			if(i != n && i.mid < i.end && (n.mid >= n.end || Reads.compareIndices(array, i.mid, n.mid, 0.1, true) < 0)) {
				n = i;
			}
		}
		if(n.mid >= n.end) {
			return null;
		}
		return n;
	}

	/**
	 * Get a bit in the bit list
	 * @param Bit list to fetch
	 * @param Index to look at
	 */
	
	private boolean bit(int[] bits, int index) {
		return (bits[index/8] & (1 << (index & 7))) != 0;
	}

	/**
	 * Flag a bit in the bit list
	 * @param Bit list to flag
	 * @param Index to flag at
	 */
	
	private void flag(int[] bits, int index) {
		Writes.changeAuxWrites(1);
		bits[index/8] |= 1 << (index & 7);
	}

	/**
	 * Get the destination of a block.
	 * Used by {@code Ekisort.cycleBlk}.
	 * @param Array to work on
	 * @param List of blocks to sort
	 * @param The bits to cycle with
	 * @param The start of the search
	 * @param The block you're looking at
	 * @param The end of the search
	 */

	private int destination(int[] array, ArrayList<Subarr> blocks, int[] bits, int a, int b1, int b) {
		int d = a, e = 0;
		for(int i = a+1; i < b; i++) {
			Highlights.markArray(2, i);
			int cmp = Reads.compareValues(array[blocks.get(i).start], array[blocks.get(a).start]);
			if(cmp < 0) d++;
			else if(i < b1 && !this.bit(bits, i) && cmp == 0) e++;
			Highlights.markArray(3, d);
			Delays.sleep(0.01);
		}
		while(this.bit(bits, d) || e-- > 0) {
			d++;
			Highlights.markArray(3, d);
			Delays.sleep(0.01);
		}
		return d;
	}

	/**
	 * Stable variant of Cyclesort, found by aphitorite, made into a block sorter.
	 * Requires O(n) bits to keep track of blocks.
	 * @param Array to work on
	 * @param List of blocks to sort
	 * @param The size across all the blocks
	 * @param The bits to cycle with
	 */
	
	private void cycleBlk(int[] array, ArrayList<Subarr> blocks, int block, int[] bits) {
		for(int i = 0; i < blocks.size()-1; i++) {
			if(!this.bit(bits, i)) {
				Highlights.markArray(1, i);
				int j = i;
				do {
					int k = this.destination(array, blocks, bits, i, j, blocks.size());
					multiSwap(array, blocks.get(i).start, blocks.get(k).start, block);
					this.flag(bits, k);
					j = k;
				}
				while(j != i);
			}
			Writes.changeAllocAmount(-1);
		}
	}
	
	/**
	 * K-way in-place merge algorithm, invented by aphitorite.
	 * Requires O(r / minBlk) auxiliary to track blocks, r being the range to sort.
	 * Re-sorts keybuffer after merging is finished.
	 * @param Array to work on
	 * @param Minimum block size
	 * @param The {@code Subarr}s to merge with, stored in an array.
	 */
	
	private void kotaWTF(int[] array, int minBlk, Subarr[] ptrs) {
		ptrs[0].start = keysLoc;
		int end = ptrs[ptrs.length-1].end;
		ArrayList<Subarr> blocks = new ArrayList<>();
		do {
			Subarr max = ptrs[0];
			for(Subarr i : ptrs) {
				if(i.mid-i.start >= max.mid-max.start) {
					max = i;
				}
			}
			int interval = minBlk, ogstart = max.start;
			Subarr access;
			do {
				access = minKota(array, ptrs);
				if(access != null) {
					Highlights.colorCode(max.start, "kota");
					Highlights.colorCode(access.mid, "buffer");
					Writes.swap(array, max.start++, access.mid++, 1, true, false);
				}
				interval--;
			} while(access != null && interval > 0);
			blocks.add(new Subarr(ogstart, max.start));
			Writes.changeAllocAmount(1);
			Writes.changeAuxWrites(1);
		} while(some(ptrs, a -> a.mid < a.end));
		Subarr fragment = null;
		int ble = blocks.size();
		if(blocks.get(ble-1).length() != minBlk) {
			Writes.changeAllocAmount(-1);
			fragment = blocks.remove(--ble);
		}
		Collections.sort(blocks);
		int[] bits = Writes.createExternalArray(((ble-1)/8)+1);
		cycleBlk(array, blocks, minBlk, bits);
		Writes.changeAllocAmount(-1);
		Writes.deleteExternalArray(bits);
		if(fragment != null) {
			indexRotate(array, fragment.start, fragment.end, end, true);
			for(int i=0; i<ptrs.length; i++) {
				if(ptrs[i].start > fragment.start) {
					ptrs[i].start -= fragment.length();
					ptrs[i].end -= fragment.length();
				}
			}
		}
		for(int i=0; i<ptrs.length; i++) {
			binserter.customBinaryInsert(array, ptrs[i].start, ptrs[i].end, 0.5);
		}
		for(int i=1; i<ptrs.length; i*=2) {
			for(int j=0; j<ptrs.length; j+=2*i) {
				if(j+i < ptrs.length) {
					indexRotate(array, ptrs[j].end, ptrs[j+i].start, ptrs[j+i].end, true);
					whipping.M8(array, ptrs[j].start, ptrs[j].end, ptrs[j].end + ptrs[j+i].length());
					ptrs[j].end += ptrs[j+i].length();
				}
			}
		}
		indexRotate(array, keysLoc, ptrs[0].start, ptrs[0].end, true);
	}
	
	/**
	 * Collection-based wrapper for {@code Ekisort.kotaWTF(int[], int, Subarr[])}.
	 * @param Array to work on
	 * @param Minimum block size
	 * @param The {@code Subarr}s to merge with, stored in a class that extends Collection.
	 */
	
	private void kotaWTF(int[] array, int minBlk, Collection<Subarr> ptrs) {
		kotaWTF(array, minBlk, ptrs.toArray(new Subarr[0]));
	}
	
	/**
	 * Merge function used in {@code Ekisort.mergeDownQuarter}.
	 * Head-merges [start...mid] with [mid...end] using the keybuffer as swap space.
	 * @param Array to work on
	 * @param The start of the lower half
	 * @param The end of the lower half and start of the upper half
	 * @param The end of the upper half
	 */
	
	private void mergeHalf(int[] array, int start, int mid, int end) {
		multiSwap(array, start, keysLoc, mid-start);
		int left = keysLoc, leftend = keysLoc + (mid - start), right = mid, to = start;
		while(left < leftend && right < end) {
			if(Reads.compareValues(array[left], array[right]) <= 0) {
				Writes.swap(array, to++, left++, 1, true, false);
			} else {
				Writes.swap(array, to++, right++, 1, true, false);
			}
		}
		while(left < leftend) {
			Writes.swap(array, to++, left++, 1, true, false);
		}
	}
	
	/**
	 * Merge three quarters of the keybuffer back into the rest of the array.
	 * @param Array to work on
	 * @param The end of the list (start defined by keysLoc)
	 */
	private void mergeDownQuarter(int[] array, int end) {
		int h = (keysSize + 1) / 2;
		mergeHalf(array, keysLoc + h, keysLoc + keysSize, end);
		int q = (h + 1) / 2;
		int temp = keysLoc;
		eki(array, keysLoc + q, keysLoc + h);
		keysLoc = temp;
		mergeHalf(array, temp + q, temp + h, end);
		eki(array, temp, temp + q);
		nilFuncs.grailMergeWithoutBuffer(array, temp, q, end - temp - q);
	}
	
	private int keysLoc, keysSize;
	
	public void eki(int[] array, int start, int end) {
		binserter = new BinaryInsertionSort(arrayVisualizer);
		blinserter = new BlockInsertionSortNeon(arrayVisualizer);
		nilFuncs = new NilSort(arrayVisualizer);
		whipping = new WhippingCreamSort(arrayVisualizer);
		if(end-start < 64) {
			nilFuncs.runZero(array, start, end);
			return;
		}
		Highlights.retainColorMarks(true);
		Highlights.defineColor("keyfind", new Color(0, 180, 255));
		Highlights.defineColor("pingpong", new Color(0, 255, 255));
		Highlights.defineColor("cai-fallback", new Color(0, 128, 128));
		Highlights.defineColor("kota", new Color(255, 0, 128));
		Highlights.defineColor("buffer", Color.DARK_GRAY);
		Highlights.defineColor("lazykeyfind", new Color(0, 0, 255));
		Highlights.defineColor("swappart", new Color(190, 80, 0));
		Highlights.defineColor("mainpart", new Color(160, 0, 0));
		Highlights.defineColor("tridge-rev", new Color(128, 0, 255));
		Highlights.defineColor("tridge-bridge", new Color(255, 0, 255));
		Highlights.defineColor("tridge-swap3", new Color(255, 128, 0));
		
		int size = fcrt(end-start);
		keysLoc = start;
		keysSize = getKeys(array, keysLoc, end, size, "buffer");
		
		int pivot = pseudomo243(array, keysLoc + keysSize, end),
			pi = array[pivot];
		
		ArrayList<Subarr> stack = new ArrayList<>();
		
		Subarr now = new Subarr(keysLoc + keysSize, keysLoc + keysSize);
		int minBlock = keysSize;
		do {
			now = makePartition(array, now.end, pi, end);
			stack.add(now);
			if(now.mid - now.start < minBlock) {
				 minBlock = now.mid - now.start;
			}
		} while(now.end < end);
		
		minBlock /= stack.size();
		
		for(Subarr s : stack) {
			nilSort_noBufferRedist(array, s.start, s.mid);
			s.mid = s.start;
		}
		rotate(array, start, keysLoc - start, keysSize, true);
		keysLoc = start;
		if(minBlock < cbrt(end-start)/2) {
			ArrayList<Integer> keys = new ArrayList<>();
			for(Subarr s : stack) {
				keys.add(s.start);
			}
			keys.add(end);
			stableCaiMerge(array, keys);
			indexRotate(array, start, keysLoc, keysLoc + keysSize, true);
			keysLoc = start;
			int h = (keysSize + 1) / 2;
			binserter.customBinaryInsert(array, keysLoc + h, keysLoc + keysSize, 0.5);
			mergeDownQuarter(array, end);
		} else {
			kotaWTF(array, minBlock, stack);
			mergeDownQuarter(array, end);
		}
	}

	@Override
	public void runSort(int[] array, int currentLength, int bucketCount) {
		eki(array, 0, currentLength);
	}
}