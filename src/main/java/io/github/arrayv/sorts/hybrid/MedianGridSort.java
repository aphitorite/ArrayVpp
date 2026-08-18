package io.github.arrayv.sorts.hybrid;

import io.github.arrayv.sorts.merge.InPlaceMergeSortIV;
import io.github.arrayv.sorts.templates.Sort;
import io.github.arrayv.main.ArrayVisualizer;

final public class MedianGridSort extends Sort {
    public MedianGridSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        
        this.setSortListName("Median Grid");
        this.setRunAllSortsName("Median Grid Sort");
        this.setRunSortName("Median Gridsort");
        this.setCategory("Hybrid Sorts");
        this.setAuthors("Distray");
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
	    
	    public void xor(int idx, int uInt) {
			assert (idx >= 0 && idx < size) : "BitArray index out of bounds";
			
			int s = idx*w, i1 = pa+s+w;
			for(int i = pa+s, j = pb+s; i < i1; i++, j++, uInt >>= 1)
				if((uInt & 1) == 1)
					flipBit(i, j);
			
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
    
    private int cmpOne(int[] array, int pos0, int pos1) {
    	int cmp = Reads.compareIndices(array, pos1, pos0, 0.125, true);
    	return -(cmp >> 31);
    }
    
    private int medof3(int[] array, int loc) {
    	int a=loc, b=a+1, c=b+1, t;
		if(cmpOne(array, a, b) == 1) {
			t=a;a=b;b=t;
		}
		if(cmpOne(array, b, c) == 1) {
			t=b;b=c;c=t;
    		if(cmpOne(array, a, b) == 1) {
        		return a;
    		}
		}
		return b;
    }
    
    private int medof3(int[] array, int l, int l1, int l2) {
    	int t;
    	if(cmpOne(array, l, l1) == 1) {
			t=l;l=l1;l1=t;
		}
		if(cmpOne(array, l1, l2) == 1) {
			t=l1;l1=l2;l2=t;
    		if(cmpOne(array, l, l1) == 1) {
        		return l;
    		}
		}
		return l1;
	}
    
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
    
    private int pseudomo27(int[] array, int a, int b) {
    	if(b-a < 256) {
    		return ninther(array, a, b);
    	}
    	int d = (b-a+1)/8,
    		m0 = ninther(array, a,a+2*d),
    		m1 = ninther(array, a+3*d, a+5*d),
    		m2 = ninther(array, a+6*d, b);
    	return medof3(array, m0, m1, m2);
    }
    
    private int pseudomo81(int[] array, int a, int b) {
    	if(b-a < 1024) {
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
    
    private int pseudomo243(int[] array, int a, int b) {
    	if(b-a < 65536) {
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
	
    private void sift(int[] array, int start, int root, int len, int tmp) {
    	int j = root;
    	while(2*j+1 < len) {
    		j=2*j+1;
    		if(j+1 < len && Reads.compareValues(array[start+j], array[start+j+1]) < 0) {
    			j++;
    		}
    	}
    	while(Reads.compareValueIndex(array, tmp, start+j, 0.25, true) > 0) {
    		j=(j-1)/2;
    	}
    	for(int t2; j>root; j=(j-1)/2) {
    		t2=array[start+j];
    		Writes.write(array, start+j, tmp, 0.5, true, false);
    		tmp=t2;
    	}
		Writes.write(array, start+root, tmp, 0.5, true, false);
    }
    
    private void heap(int[] array, int start, int end) {
    	int p=end-start;
    	for(int j=(p-1)/2; j>=0; j--) {
    		this.sift(array, start, j, p, array[start+j]);
    	}
    	for(int j=p-1; j>0; j--) {
    		int t=array[start+j];
    		Writes.write(array, start+j, array[start], 1, true, false);
    		this.sift(array, start, 0, j, t);
    	}
    }

	private int partition(int[] array, int a, int b, boolean invert) {
		int m = pseudomo243(array, a, b-1), d = invert ? -1 : 1;
		Writes.swap(array, a, m, 1, true, false);
		int i = a, j = b;
		Highlights.markArray(3, a);
		
		do {
			do {
				i++;
				Highlights.markArray(1, i);
				Delays.sleep(0.5);
			}
			while(i < j && Reads.compareIndices(array, i, a, 0, false) == -d);
			
			do {
				j--;
				Highlights.markArray(2, j);
				Delays.sleep(0.5);
			}
			while(j >= i && Reads.compareIndices(array, j, a, 0, false) == d);
				
			if(i < j) Writes.swap(array, i, j, 1, true, false);
			
			else {
				Writes.swap(array, a, j, 1, true, false);
				Highlights.clearMark(3);
				return j;
			}
		}
		while(true);
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
			sizes.xor(0, sqrt);
			sizes.xor(1, sqrt);
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
					sizes.xor(n, (s + 1) ^ sqrt);
					sizes.xor(k, sqrt);
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
					this.heap(array, a+sqrt+j*bsz, a+i+j*bsz);
					this.merge(array, a+j*bsz, a+sqrt+j*bsz, a+i+j*bsz, m);
				} else {
					this.multiSwap(array, a+j*bsz, m, i);
				}
				m+=i;
				sizes.xor(j, i);
				indices.xor(J, j);
			}
		}
	}
	
	private void grid(int[] array, BitArray sizes, BitArray indices, int a, int b, int sqrt) {
		int bsz = 2*sqrt, m;
		while(b-a > sizes.length) {
			m = this.partition(array, a, b, true);
			if(m-a < b-m) {
				int z = (b-m+1)/2;
				for(int i=a; i<m; i+=z) {
					int j=Math.min(i+z, m);
					this.gridB(array, sizes, indices, m, i, j, sqrt, bsz);
				}
				int j=Math.min(a+z, m);
				this.merge(array, a, j, m, b-=m-a);
			} else {
				int z = (m-a+1)/2;
				for(int i=m; i<b; i+=z) {
					int j=Math.min(i+z, b);
					this.gridB(array, sizes, indices, a, i, j, sqrt, bsz);
				}
				int j=Math.min(m+z, b);
				this.merge(array, m, j, b, a);
				a += b-m;
			}
		}
		if(mergePP(array, a, b, sizes.pa))
			multiSwap(array, sizes.pa, a, b-a);
	}
	
	public void sort(int[] array, int a, int b) {
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
		this.heap(array, b1, b);
	}
    @Override
    public void runSort(int[] array, int length, int bucketCount) {
    	sort(array, 0, length);
    }
}