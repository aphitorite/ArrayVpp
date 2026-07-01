package io.github.arrayv.sorts.hybrid;


import io.github.arrayv.sorts.merge.InPlaceMergeSortIV;
import io.github.arrayv.sorts.select.MaxHeapSort;
import io.github.arrayv.sorts.templates.Sort;
import io.github.arrayv.main.ArrayVisualizer;

final public class QuickGridSort extends Sort {
    public QuickGridSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        
        this.setSortListName("Quick Grid");
        this.setRunAllSortsName("Quick Grid Sort");
        this.setRunSortName("Quick Gridsort");
        this.setCategory("Hybrid Sorts");
        this.setConstant("n log n");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
        this.setAuthors("Distray");
    }
    // I Can't Believe It's Not Izasort!
	
	private class BitArray {
		private final int[] array;
		private final int pa, pb, w;
		
		public final int size, length;
		
		public BitArray(int[] array, int pa, int pb, int size, int w) {
			this.array = array;
			this.pa = pa;
			this.pb = pb;
			this.size = size;
			this.w  = w;
			this.length = size*w;
		}
		
		private void flipBit(int a, int b) {
			Writes.swap(array, a, b, 0.25, true, false);
		}
		private boolean getBit(int a, int b) {
			return Reads.compareIndices(array, a, b, 0, false) > 0;
		}
		private void setBit(int a, int b, boolean bit) {
			if(this.getBit(a, b) ^ bit)
				this.flipBit(a, b);
		}
		
		public void free() {
			int i1 = pa+length;
			for(int i = pa, j = pb; i < i1; i++, j++)
				this.setBit(i, j, false);
		}
		
		public void insert(int from, int idx, int uInt) {
			assert (idx >= 0 && idx < size) : "BitArray index out of bounds";
			int i1 = pa+idx*w, s = from*w;
			for(int i=pa+s-w, j=pb+s-w; i > i1;) {
				flipBit(--i, i + w);
				flipBit(--j, j + w);
			}
			this.set(idx, uInt);
		}
		
		private String debug() {
			String z="[";
			for(int i=0; i<size; i++) {
				int v=0, s=i*w;
				for(int j=0; j<w; j++) {
					if(array[pa+s+j] > array[pb+s+j]) {
						v += 1 << j;
					}
				}
				z += String.format("%d%s", v, i==size-1?"]":", ");
			}
			return z;
		}
		
	    public void set(int idx, int uInt) {
			assert (idx >= 0 && idx < size) : "BitArray index out of bounds";
			
			int s = idx*w, i1 = pa+s+w;
			for(int i = pa+s, j = pb+s; i < i1; i++, j++, uInt >>= 1)
				this.setBit(i, j, (uInt & 1) == 1);
			
			if(uInt > 0) System.out.printf("Warning: Word too large at index %d\n", idx);
		}
		public int get(int idx) {
			assert (idx >= 0 && idx < size) : "BitArray index out of bounds";
			
			int r = 0, s = idx*w;
			for(int k = 0, i = pa+s, j = pb+s; k < w; k++, i++, j++)
				r |= (this.getBit(i, j) ? 1 : 0) << k;
			return r;
		}
		
		public int incr(int idx) { // constant time "++bitarray[index]" return
			assert (idx >= 0 && idx < size) : "BitArray index out of bounds";
			
			int s = idx*w, i1 = pa+s+w;
			int k = 0;
			boolean f = false;
			for(int i = pa+s, j = pb+s, S = 0; i < i1; i++, j++) {
				if(!f) this.flipBit(i, j);
				boolean b = this.getBit(i, j);
				k |= (b ? 1 : 0) << S++;
				f |= b;
			}
			if(!f)
				System.out.printf("Warning: Integer overflow at index %d\n", idx);
			return k;
		}
		
		public void incrStop(int idx) {
			assert (idx >= 0 && idx < size) : "BitArray index out of bounds";
			
			int s = idx*w, i1 = pa+s+w;
			for(int i = pa+s, j = pb+s; i < i1; i++, j++) {
				this.flipBit(i, j);
				if(this.getBit(i, j)) return;
			}
		}
		
		public int decr(int idx) { // constant time "--bitarray[index]" return
			assert (idx >= 0 && idx < size) : "BitArray index out of bounds";
			
			int s = idx*w, i1 = pa+s+w;
			int k = 0;
			boolean f = false;
			for(int i = pa+s, j = pb+s, S = 0; i < i1; i++, j++) {
				if(!f) this.flipBit(i, j);
				boolean b = this.getBit(i, j);
				k |= (b ? 1 : 0) << S++;
				f |= !b;
			}
			if(!f)
				System.out.printf("Warning: Integer overflow at index %d\n", idx);
			return k; 
		}
		
		public void decrStop(int idx) {
			assert (idx >= 0 && idx < size) : "BitArray index out of bounds";
			
			int s = idx*w, i1 = pa+s+w;
			for(int i = pa+s, j = pb+s; i < i1; i++, j++) {
				this.flipBit(i, j);
				if(!this.getBit(i, j)) return;
			}
			System.out.printf("Warning: Integer underflow at index %d\n", idx);
		}
	}
	private InPlaceMergeSortIV ipm4;
	private MaxHeapSort heap;
    private static final int MIN_INSERT = 16;
	
	private void insertRun(int[] array, int start, int end, boolean d) {
		boolean invert = d;
		int l, r, m, j, t;
		for(int i=start+1; i<end; i++) {
			if(invert ^ Reads.compareIndices(array, i-1, i, 0.01, true) <= 0) {
				continue;
			}
			if(invert ^ Reads.compareIndices(array, start, i, 0.01, true) > 0) {
				Writes.reversal(array, start, i-1, 0.5, true, false);
				invert = !invert;
				continue;
			}
			l = start + 1;
			r = i - 1;
			while(l < r) {
				m = l + (r - l) / 2;
				if(invert ^ Reads.compareIndices(array, m, i, 0.0625, true) > 0) {
					r = m;
				} else {
					l = m + 1;
				}
			}
			t = array[i];
			j = i - 1;
			while(j >= l) {
				Writes.write(array, j+1, array[j--], 0.5, true, false);
			}
			Writes.write(array, l, t, 0.5, true, false);
		}
		if(invert ^ d)
			Writes.reversal(array, start, end-1, 1, true, false);
	}
	

	private int partition(int[] array, int a, int b, boolean invert) {
		int m = a + (b - a) / 2, d = invert ? -1 : 1, e = a+1, f = b;
		Writes.swap(array, a, m, 1, true, false);
		int i = a, j = b, c = -2;
		Highlights.markArray(3, a);
		
		do {
			do {
				if(c == 0) Writes.swap(array, i, e++, 1, true, false);
				i++;
				Highlights.markArray(1, i);
				Delays.sleep(0.5);
			}
			while(i < j && Reads.compareIndices(array, i, a, 0, false) != d);
			
			do {
				if(c == 0) Writes.swap(array, j, f--, 1, true, false);
				j--;
				Highlights.markArray(2, j);
				Delays.sleep(0.5);
			}
			while(j >= i && Reads.compareIndices(array, j, a, 0, false) != -d);
				
			if(i < j) Writes.swap(array, i, j, 1, true, false);
			
			else {
				Writes.swap(array, a, j, 1, true, false);
				Highlights.clearMark(3);
				return j;
			}
		}
		while(true);
	}
	private void quickSelect(int[] array, int a, int b, int r, boolean invert) {
		while(b-a > MIN_INSERT) {
			int m = this.partition(array, a, b, invert);
			if(m > r) b = m;
			else if(m < r) a = m + 1;
			else return;
		}
		if(b-a <= MIN_INSERT) 
			insertRun(array, a, b, invert);
	}
	private void dualQuickSelect(int[] array, int a, int b, int r1, int r2) {
		int a1 = a, b1 = b;
		
		while(b-a > MIN_INSERT) {
			int m = this.partition(array, a, b, false);
			
			if(m > r2 && m < b1)        b1 = m;
			else if(m < r2 && m+1 > a1) a1 = m+1;
			else if(m == r2)            a1 = b1;
			
			if(m > r1)      b = m;
			else if(m < r1) a = m+1;
			else            break;
		}
		if(b-a <= MIN_INSERT) 
			insertRun(array, a, b, false);
		
		while(b1-a1 > MIN_INSERT) {
			int m = this.partition(array, a1, b1, false);
			
			if(m == r2) return;
			
			else if(m > r2) b1 = m;
			else if(m < r2) a1 = m+1;
			else            break;
		}
		if(b1-a1 <= MIN_INSERT) 
			insertRun(array, a1, b1, false);
	}
	private boolean mergePP(int[] array, int a, int b, int p) {
		WhippingCreamSort wc = new WhippingCreamSort(arrayVisualizer);
        boolean A = false;
        for(int i = a; i < b; i += 16) {
        	wc.whipping_cream(array, i, Math.min(i+16, b));
        }
        for(int j = 16; j < b - a; j *= 2) {
        	int f = A ? p : a, t = A ? a : p;
        	for(int i = 0; i < b - a; i += 2 * j) {
        		int T = t + i, L = f + i, M = f + i + j, R = M, E = Math.min(f + i + 2 * j, f + b - a);
        		if(E < M) M = E;
        		while(L < M && R < E) {
        			if(Reads.compareValues(array[L], array[R]) <= 0) 
                        Writes.swap(array, T++, L++, 1, true, false);
                    else
                        Writes.swap(array, T++, R++, 1, true, false);
        		}
        		while(L < M)
                    Writes.swap(array, T++, L++, 1, true, false);
        		while(R < E)
                    Writes.swap(array, T++, R++, 1, true, false);
        	}
        	A=!A;
        }
        return A;
	}
	
	private void multiSwap(int[] array, int a, int b, int s) {
		for(int i=0; i<s; i++) {
			Writes.swap(array,a+i,b+i,1,true,false);
		}
	}
	
	private int binSearchL(int[] array, BitArray indices, int start, int left, int right, int key) {
		while(left < right) {
			int m = right - (right - left) / 2;
			if(Reads.compareIndices(array, start+m, key, 0.25, true) < 0) {
				left = m;
			} else {
				right = m - 1;
			}
		}
		return left;
	}
	
	private void tailMerge(int[] array, int a, int m, int b, int t, int u, boolean almv) {
		if(!almv) {
			multiSwap(array, m, u, b - m);
		}
		int l=m-1, r=u+(b-m)-1, T=t+(b-m);
		while(l>=a && r>=u) {
			T--;
			if(Reads.compareIndices(array, l, r, 0.5, true) > 0) {
				Writes.swap(array, T, l--, 1, true, false);
			} else {
				Writes.swap(array, T, r--, 1, true, false);
			}
			if(T == t)
				T = m;
		}
		while(r >= u) {
			Writes.swap(array, --T, r--, 1, true, false);
			if(T == t)
				T = m;
		}
	}
	
	private void merge(int[] array, int a, int m, int b, int t) {
		int l = a, r = m;
		while(l < m && r < b) {
			if(Reads.compareIndices(array, l, r, 0.25, true) <= 0) {
				Writes.swap(array, l++, t++, 1, true, false);
			} else {
				Writes.swap(array, r++, t++, 1, true, false);
			}
		}
		while(l < m)
			Writes.swap(array, l++, t++, 1, true, false);
		while(r < b)
			Writes.swap(array, r++, t++, 1, true, false);
	}
	
	private void gridB(int[] array, BitArray sizes, BitArray indices, int a, int m, int b, int sqrt, int bsz) {
		int z = Math.min(m + bsz, b);
		boolean whichSwap = mergePP(array, m, z, a);
		if(b == z && whichSwap) {
			multiSwap(array, a, m, b - m);
		} else if(z < b) {
			if(whichSwap) {
				this.multiSwap(array, a+sqrt+1, a+bsz+1, sqrt-1);
				Writes.swap(array, m, a, 1, true, false);
				Writes.swap(array, m+1, a+sqrt, 1, true, false);
			} else {
				this.multiSwap(array, m+1, a+1, sqrt-1);
				this.multiSwap(array, m+sqrt+1, a+bsz+1, sqrt-1);
				Writes.swap(array, m+sqrt, m+1, 1, true, false);
			}
			sizes.set(0, sqrt);
			sizes.set(1, sqrt);
			indices.set(1, 1);
			int k = 2;
			for(int i=m+bsz; i<b; i++) {
				int p = binSearchL(array, indices, m, 0, k-1, i),
					n = indices.get(p),
					s = sizes.incr(n) - 1;
				Writes.swap(array, a+s+n*bsz, i, 2.5, true, false);
				if(s == bsz - 1) {
					Writes.swap(array, a+n*bsz, m+p, 1, true, false);
					boolean invert = this.mergePP(array, a+sqrt+n*bsz, a+(n+1)*bsz, m+k);
					this.tailMerge(array, a+n*bsz, a+sqrt+n*bsz, a+(n+1)*bsz, a+k*bsz, m+k, invert);
					sizes.set(n, sqrt);
					sizes.set(k, sqrt);
					Writes.swap(array, a+n*bsz, m+p, 1, true, false);
					Writes.multiSwap(array, m+k, m+p+1, 1, true, false);
					Writes.swap(array, a+k*bsz, m+p+1, 1, true, false);
					indices.insert(k+1, p+1, k++);
				}
			}
			for(int i=0; i<k; i++) {
				int j=indices.get(i);
				Writes.swap(array, a+j*bsz, m+i, 1, true, false);
			}
			for(int J=0; J<k; J++) {
				int j=indices.get(J),
					i=sizes.get(j);
				if(i > sqrt) {
					heap.customHeapSort(array, a+sqrt+j*bsz, a+i+j*bsz, 0.5);
					this.merge(array, a+j*bsz, a+sqrt+j*bsz, a+i+j*bsz, m);
				} else {
					this.multiSwap(array, a+j*bsz, m, i);
				}
				m+=i;
			}
			indices.free();
			sizes.free();
		}
	}
	
	private void grid(int[] array, BitArray sizes, BitArray indices, int a, int b, int sqrt) {
		if(b-a <= sizes.length) {
			if(mergePP(array, a, b, sizes.pa))
				multiSwap(array, sizes.pa, a, b-a);
			return;
		}
		int m = a + (b - a) / 2, M = m + (b - m) / 2;
		this.quickSelect(array, a, b, m, true);
		int bsz = 2*sqrt;
		this.gridB(array, sizes, indices, a, m, M, sqrt, bsz);
		this.gridB(array, sizes, indices, a, M, b, sqrt, bsz);
		this.merge(array, m, M, b, a);
		this.grid(array, sizes, indices, a+b-m, b, sqrt);
	}
	
	public void sort(int[] array, int a, int b) {
		heap = new MaxHeapSort(arrayVisualizer);
		ipm4 = new InPlaceMergeSortIV(arrayVisualizer);
		int log = 32 - Integer.numberOfLeadingZeros(b-a-1), slog = (log + 1) >> 1, sqrt = 1 << slog;
		int size = sqrt*(slog+1);
		int a1 = a + 2 * size, b1 = b - 2 * size;
		if(a1 >= b1 - sqrt) {
			ipm4.IPM4(array, a, b);
			return;
		}
		this.dualQuickSelect(array, a, b, a1, b1);
		
		BitArray sizes = new BitArray(array, a, b1, sqrt, slog + 2);
		BitArray indices = new BitArray(array, a+size+sqrt, b1+size+sqrt, sqrt, slog);
		
		this.grid(array, sizes, indices, a1, b1, sqrt);
		indices.free();
		sizes.free();
		if(this.mergePP(array, a, a1, b1))
			this.multiSwap(array, a, b1, a1 - a);
		heap.customHeapSort(array, b1, b, 1);
	}
    @Override
    public void runSort(int[] array, int length, int bucketCount) {
    	sort(array, 0, length);
    }
}