package io.github.arrayv.sorts.hybrid;

import io.github.arrayv.main.ArrayVisualizer;

import io.github.arrayv.sorts.insert.InsertionSort;
import io.github.arrayv.sorts.templates.Sort;

import java.util.function.Function;

final public class DayoSort extends Sort {
    public DayoSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        
        this.setSortListName("Dayo");
        this.setRunAllSortsName("Dayo Sort");
        this.setRunSortName("Dayosort");
        this.setCategory("Hybrid Sorts");
        this.setAuthors("Distray");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
    }
    
    private int log(int v) {
    	return 31 - Integer.numberOfLeadingZeros(v);
    }
    
    class Sublist {
    	private int array[];
    	private boolean disqualified = false;
    	public int mrg, buf, end;
    	public Sublist left, right, winner, parent;
    	public Sublist(int[] array, int a, int b) {
    		this.array = array;
    		mrg = buf = a;
    		end = b;
    	}
    	public Sublist(int[] array) {
    		this(array, 0, 0);
    	}
    	public Sublist() {
    		this(null);
    	}
    	public boolean oob() {
    		return disqualified || mrg >= end;
    	}
    	public void build() {
    		if(left == null || left.oob()) {
    			if(right == null || right.oob()) {
    				this.disqualified = true;
    				return;
    			} else {
    				winner = right;
    			}
    		} else if(right == null || right.oob()) {
    			winner = left;
    		} else if(this.compare(left, right) <= 0) {
    			winner = left;
    		} else {
				winner = right;
    		}
			copyWinner();
    	}
    	private void copyWinner() {
    		this.array = winner.array;
    		this.mrg = winner.mrg;
    		this.end = winner.end;
    		this.disqualified = winner.disqualified;
    	}
    	public void rebuild() {
    		Sublist now = this;
    		while(true) {
    			now = now.parent;
    			if(now == null) break;
    			now.build();
    		}
    	}
    	public Sublist winner() {
    		if(winner == null)
    			return this;
    		if(oob()) {
    			Sublist p = this.parent;
    			while(p.oob()) {
    				p = p.parent;
    			}
    			return p;
    		}
    		return winner.winner();
    	}
    	public int bufferSize() {
    		return mrg - buf;
    	}
    	private int compare(Sublist a, Sublist b) {
    		return Reads.compareIndices(a.array, a.mrg, b.mrg, 0.25, true);
    	}
    }
    
    private InsertionSort is;
	
	//median selection and sort helper code for aeosqsort by Anonymous0726
	
	private int medianOf3(int[] array, int[] indices) {
		// small length cases
		
		// maybe an error would be better but w/e
		if(indices.length == 0) return -1;
		
		// median of 1 or 2 elements can just be the first
		if(indices.length < 3) return indices[0];
		
		// 3 element case (common)
		// only first 3 elements are considered if given an array of 4+ indices
		if(Reads.compareIndices(array, indices[0], indices[1], 0.5, true) <= 0) {
			if(Reads.compareIndices(array, indices[1], indices[2], 0.5, true) <= 0)
				return indices[1];
			if(Reads.compareIndices(array, indices[0], indices[2], 0.5, true) < 0)
				return indices[2];
			return indices[0];
		}
		if(Reads.compareIndices(array, indices[1], indices[2], 0.5, true) >= 0) {
			return indices[1];
		}
		if(Reads.compareIndices(array, indices[0], indices[2], 0.5, true) <= 0) {
			return indices[0];
		}
		return indices[2];
	}

	private int mOMHelper(int[] array, int start, int length) {
		if(length == 1) return start;
		
		int[] meds = new int[3];
		int third = length / 3;
		meds[0] = mOMHelper(array, start, third);
		meds[1] = mOMHelper(array, start + third, third);
		meds[2] = mOMHelper(array, start + 2 * third, third);
				
		return medianOf3(array, meds);
	}

	private int medianOfMedians(int[] array, int start, int length) {
		if(length == 1) return start;
		
		int[] meds = new int[3];
		
		int nearPower = (int) Math.pow(3, Math.round(Math.log(length)/Math.log(3)));
		if(nearPower == length)
			return mOMHelper(array, start, length);
		
		nearPower /= 3;
		// uncommon but can happen with numbers slightly smaller than 2*3^k
		// (e.g., 17 < 18 or 47 < 54)
		if(2*nearPower >= length) nearPower /= 3;
		
		meds[0] = mOMHelper(array, start, nearPower);
		meds[2] = mOMHelper(array, start + length - nearPower, nearPower);
		meds[1] = medianOfMedians(array, start + nearPower, length - 2 * nearPower);
		
		return medianOf3(array, meds);
	}
	
	private void multiSwap(int[] array, int a, int b, int s) {
		for(int i=0; i<s; i++) {
			Writes.swap(array,a+i,b+i,1,true,false);
		}
	}
	
	private void set(int[] array, int lo, int hi, int val, int log, boolean bit) {
		while(log-- > 0) {
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
	
    private void merge(int[] array, int[] buf, int a, int m, int b, int t, boolean aux) {
    	int l = a, r = m;
    	while(l < m && r < b) {
    		if(Reads.compareIndices(array, l, r, 1, true) <= 0) {
    			Writes.write(buf, t++, array[l++], 1, true, aux);
    		} else {
    			Writes.write(buf, t++, array[r++], 1, true, aux);
    		}
    	}
    	while(l < m && l < b)
			Writes.write(buf, t++, array[l++], 1, true, aux);
    	while(r < b)
			Writes.write(buf, t++, array[r++], 1, true, aux);
    }
    
    private void merge(int[] array, int[] tmp, int a, int b, int o) {
    	for(int i=o, j=0; j<b-a; i+=8, j+=8) {
    		is.customInsertSort(tmp, i, Math.min(i+8, o+b-a), 0.5, true);
    	}
    	boolean A = false;
    	for(int j=8; j<b-a; j*=2) {
    		int t=A?o:a, f=A?a:o;
    		for(int i=0; i<b-a; i+=2*j) {
    			merge(A?array:tmp, A?tmp:array, f+i, f+i+j, Math.min(f+i+2*j, f+b-a), t+i, A);
    		}
    		A=!A;
    	}
    	if(!A) Writes.arraycopy(tmp, o, array, a, b-a, 1, true, false);
    }
	
    private int[] partition(int[] array, int[] tmp, int a, int b, int p, final int blk, int B) {
    	int lb = 0, rb = 0, l = 0, r = 0, t = a;
    	for(int i=a; i<b; i++) {
    		int cmp = Reads.compareIndexValue(array, i, p, 1, true);
    		if(cmp > -B) {
    			Writes.write(tmp, blk+r++, array[i], 0.25, true, true);
    			if(r == blk) {
    				merge(array, tmp, t, t+blk, blk);
    				// Writes.arraycopy(tmp, blk, array, t, blk, 1, true, false);
    				rb++;
    				r = 0;
    				t += blk;
    			}
    		} else {
    			Writes.write(tmp, l++, array[i], 0.25, true, true);
    			if(l == blk) {
    				merge(array, tmp, t, t+blk, 0);
    				// Writes.arraycopy(tmp, 0, array, t, blk, 1, true, false);
    				lb++;
    				l = 0;
    				t += blk;
    			}
    		}
    	}
    	int m = Math.min(lb, rb);
    	if(m > 0) {
    		int j, k, M = log(m-1)+1;
    		j = k = a;
			for(int i = 0; i < m; i++) {
				while(Reads.compareValues(array[j+M], p) <= -B) j += blk;
				while(Reads.compareValues(array[k+M], p) > -B) k += blk;
				set(array, j, k, i, M, lb < rb);
				j += blk;
				k += blk;
			}
			if(lb < rb) {
				for(j = t - blk, k = t; j >= a; j -= blk) {
					if(Reads.compareValues(array[j+M], p) > -B) {
						multiSwap(array, j, k -= blk, blk);
					}
				}
				for(int i = a, h = 0; i < k; i += blk, h++) {
					int w = get(array, i, p, M, B);
					while(h != w) { // index sort
						multiSwap(array, a+w*blk, i, blk);
						w = get(array, i, p, M, B);
					}
					set(array, i, k+h*blk, h, M, lb < rb); // compareless clear the block tag
				}
			} else {
				for(j = k = a; j < t; j += blk) {
					if(Reads.compareValues(array[j+M], p) <= -B) {
						multiSwap(array, j, k, blk);
						k += blk;
					}
				}
				for(int h = 0; k < t; k += blk, h++) {
					int w = get(array, k, p, M, B);
					while(h != w) {
						multiSwap(array, k+(w-h)*blk, k, blk);
						w = get(array, k, p, M, B);
					}
					set(array, k, a+h*blk, h, M, lb < rb); // compareless clear the block tag
				}
			}
    	}
    	merge(array, tmp, b-r, b, blk);
    	merge(array, tmp, b-r-l, b-r, 0);
    	return new int[] {a+lb*blk, b-r-l};
    }
	
	private <T> boolean some(T[] array, Function<? super T, Boolean> func) {
		for(T i : array) {
			if(func.apply(i)) {
				return true;
			}
		}
		return false;
	}
	
	private Sublist build(Sublist[] buffers, int start, int end) {
		if(start >= end)
			return buffers[start];
		int mid = start + (end - start) / 2;
		Sublist left = build(buffers, start, mid);
		Sublist right = build(buffers, mid+1, end);
		Sublist root = new Sublist();
		root.left = left;
		root.right = right;
		left.parent = root;
		right.parent = root;
		root.build();
		return root;
	}
    private void blockmerge(int[] array, int[] hold, int bit, int a, int b, int gap, int block, int pivot, boolean hi) {
    	Sublist[] s = new Sublist[(b-a-1)/gap+1];
    	for(int i=0; i<s.length; i++) {
    		s[i] = new Sublist(array, a+i*gap, Math.min(a+(i+1)*gap, b));
    	}
    	int l = log((b-a-1)/block)+1, j = bit, w = 0;
    	Sublist root = build(s, 0, s.length-1);
    	for(int i=0, c=0; i<hold.length; i++) {
    		Sublist p = root.winner();
    		Writes.write(hold, i, array[p.mrg++], 1, true, false);
    		p.rebuild();
    		if(++c==block) {
    			c = 0;
    			w++;
    			j += l;
    		}
    	}
    	while(some(s, buf -> !buf.oob())) {
    		Sublist maxBuf = s[0];
    		for(int i = 1; i < s.length; i++) {
    			if(s[i].bufferSize() > maxBuf.bufferSize())
    				maxBuf = s[i];
    		}
    		int z = maxBuf.buf;
    		for(int c = 0; c < block; c++) {
        		Sublist winner = root.winner();
        		Writes.write(array, maxBuf.buf++, array[winner.mrg++], 1, true, false);
        		winner.rebuild(); 
    		}
    		set(array, j, z, w++, l, !hi);
    		j += l;
    	}
    	j = bit;
    	for(int i=w=0, k=0; i<s.length; i++) {
    		Sublist now = s[i];
    		while(now.bufferSize() > 0) {
        		int z = now.buf;
	    		for(int c = 0; c < block; c++) {
	    			Writes.write(array, now.buf++, hold[k++], 1, true, false);
	    		}
	    		set(array, j, z, w++, l, !hi);
	    		j += l;
    		}
    	}
		for(int i = a, h = 0; i < b; i += block, h++) {
			int x = get(array, i, pivot, l, 0);
			while(h != x) { // index sort
				// System.out.printf("%d %d %d %d %d\n", x, l, block, i, h);
				multiSwap(array, a+x*block, i, block);
				x = get(array, i, pivot, l, 0);
			}
			set(array, i, bit+h*l, h, l, !hi); // compareless clear the block tag
		}
    }
    private void tailMerge(int[] array, int[] buf, int start, int mid, int end) {
    	Writes.arraycopy(array, mid, buf, 0, end-mid, 1, true, true);
    	int l=mid-1, r=end-mid-1, t=end;
    	while(l >= start && r >= 0) {
    		t--;
    		if(Reads.compareValues(array[l], buf[r]) > 0)
    			Writes.write(array, t, array[l--], 1, true, false);
    		else
    			Writes.write(array, t, buf[r--], 1, true, false);
    	}
    	while(r >= 0)
			Writes.write(array, --t, buf[r--], 1, true, false);
    }
    private boolean ratioBad(int a, int b, int l, int r, int lg) {
    	return ((r-l)/lg) * (log((r-l-1)/lg)+1) > b-a;
    }
    private void dayo(int[] array, int[] buffer, int sqr, int lg, int a, int b) {
    	int m = medianOfMedians(array, a, (b-a)-(~(b-a)&1));
    	int p = array[m];
    	int[] bounds = partition(array, buffer, a, b, p, sqr, 0);
    	int l = lg*((b-a)/(2*sqr));
    	if(buffer.length < l) {
    		Writes.deleteExternalArray(buffer);
        	buffer = Writes.createExternalArray(l);
    	}
    	int c = bounds[0], d = bounds[1];
    	if(d-c > c-a) {
    		blockmerge(array, buffer, c, a, c, sqr, lg, p, false);
    		if(ratioBad(a, c, c, d, lg)) {
    			dayo(array, buffer, sqr, lg, c, d);
    		} else {
        		blockmerge(array, buffer, a, c, d, sqr, lg, p, true);
    		}
    	} else {
    		blockmerge(array, buffer, a, c, d, sqr, lg, p, true);
    		if(ratioBad(c, d, a, c, lg)) {
    			dayo(array, buffer, sqr, lg, a, c);
    		} else {
        		blockmerge(array, buffer, c, a, c, sqr, lg, p, false);
    		}
    	}
    	tailMerge(array, buffer, a, d, b);
    }
    public void dayo(int[] array, int a, int b) {
    	is = new InsertionSort(arrayVisualizer);
    	int lg = 32 - Integer.numberOfLeadingZeros(b-a), sqr = lg * lg;
    	int[] buffer = Writes.createExternalArray(2*sqr);
    	dayo(array, buffer, sqr, lg, a, b);
    }
    @Override
    public void runSort(int[] array, int nmemb, int bucketCount) {
    	dayo(array, 0, nmemb);
    }
}