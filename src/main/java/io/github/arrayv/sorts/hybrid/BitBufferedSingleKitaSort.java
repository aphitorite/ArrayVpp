package io.github.arrayv.sorts.hybrid;


import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;
import io.github.arrayv.utils.IndexedRotations;
import io.github.arrayv.utils.Rotations;

final public class BitBufferedSingleKitaSort extends Sort {
	public BitBufferedSingleKitaSort(ArrayVisualizer arrayVisualizer) {
		super(arrayVisualizer);
		
		this.setSortListName("Bit-Buffered Single Kita");
		this.setRunAllSortsName("Bit-Buffered Single Kita Sort");
		this.setRunSortName("Bit-Buffered Single Kitasort");
		this.setCategory("Hybrid Sorts");
        this.setAuthors("Distray");
		this.setConstant("n log n");
		this.setBucketSort(false);
		this.setRadixSort(false);
		this.setUnreasonablySlow(false);
		this.setUnreasonableLimit(0);
		this.setBogoSort(false);
	}
	
	private static final int tolerance = 64, LG = 32;
	
	private int key, keys;
	
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
		
		public void insert(int from, int idx, int uInt, boolean stable) {
			assert (idx >= 0 && idx < size) : "BitArray index out of bounds";
			if(stable) {
				for(int i=from, j, k = 0; i>=idx; i--) {
					j = get(i);
					xor(i+1, j ^ k);
					k = j;
				}
			} else {
				int i1 = pa+idx*w, s = from*w;
				for(int i=pa+s-w, j=pb+s-w; i > i1;) {
					flipBit(--i, i + w);
					flipBit(--j, j + w);
				}
			}
			this.set(idx, uInt);
		}
		
		public String debug() {
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
				System.out.printf("Warning: Integer underflow at index %d\n", idx);
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
	
	private void multiSwap(int[] array, int a, int b, int s) {
		for(int i=0; i<s; i++) {
			Writes.swap(array,a+i,b+i,1,true,false);
		}
	}
	
	private void pushbw(int[] array, int a, int b, int s) {
		while(s > 0) {
			Writes.swap(array,a+--s,--b,1,true,false);
		}
	}
	
	private int log2(int val) {
		return 31 - Integer.numberOfLeadingZeros(val);
	}
	
	private int log(int val, int base) {
		return (int) (Math.log(val) / Math.log(base));
	}
	
	private int sqrt(int val) {
		int l=0;
		while(1<<(++l<<1)<val);
		return 1<<l;
	}
	
	// code from MBHG
	private int binSearchE(int[] array, int l, int r, int k) {
		int a=0, b=r-l, m;
		while(a<b) {
			m=a+((b-a)>>1);
			switch(Reads.compareIndexValue(array, l+m, k, 0.25, true)) {
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
	
	private void shift(int[] array, int from, int to, double sleep) {
		if(from == to)
			return;
		int k = array[from];
		if(from < to) {
			Writes.arraycopy(array, from+1, array, from, to-from, sleep/2d, true, false);
		} else {
			Writes.reversearraycopy(array, to, array, to+1, from-to, sleep/2d, true, false);
		}
		Writes.write(array, to, k, sleep, true, false);
	}
	
	private int getKeys(int[] array, int start, int end, int keysNeeded) {
		int keysNow = 1, keysAt = start, i = start + 1, uniquesPush = 0;
		while(i < end && keysNow < keysNeeded) {
			Highlights.markArray(3, i);
			int search = binSearchE(array, keysAt, keysAt + keysNow, array[i]);
			if(search == -1) {
				uniquesPush++;
			} else {
				if(uniquesPush > Math.min(tolerance, keysNow / 2)) {
					Rotations.centered(array, keysAt, keysNow, uniquesPush, 1, true, false);
					search += uniquesPush;
					keysAt += uniquesPush;
					uniquesPush = 0;
				}
				shift(array, i, search, 0.5);
				keysNow++;
			}
			i++;
		}
		Highlights.clearMark(3);
		Rotations.centered(array, start, keysAt-start, keysNow, 1, true, false);
		return keysNow;
	}
	
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
	
	// median of 3
	private int medOf3(int[] array, int l0, int l1, int l2) {
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
	private int medOfMed(int[] array, int start, int end, int depth) {
		if(end-start < 9 || depth <= 0) {
			return medOf3(array, start, start+(end-start)/2, end);
		}
		int e = (end - start) / 8;
		int m0 = medOfMed(array, start, start + 2 * e, --depth);
		int m1 = medOfMed(array, start + 3 * e, start + 5 * e, depth);
		int m2 = medOfMed(array, start + 6 * e, end, depth);
		return medOf3(array, m0, m1, m2);
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
	private boolean pingPongNS(int[] array, int a, int b, int p) {
		boolean A = false;
		for(int i = a; i < b; i += 16) {
			insertRun(array, i, Math.min(i+16, b), false);
		}
		for(int j = 16; j < b - a; j *= 2) {
			int f = A ? p : a, t = A ? a : p;
			for(int i = 0; i < b - a; i += 2 * j) {
				int T = t + i, L = f + i, M = f + i + j, R = M,
				    E = Math.min(f + i + 2 * j, f + b - a);
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
	private void pingPong(int[] array, int a, int b, int p) {
		if(pingPongNS(array, a, b, p))
			multiSwap(array, a, p, b - a);
	}
	private void tailMerge(int[] array, int a, int m, int b, int t, int u, boolean almv) {
		if(t<0) t=m;
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
	
	private void inc(int[] val, int[] offs, int[] tag, int[] bnd, BitArray t, int idx) {
		final int blk = keys / 2;
		if(((val[idx] - offs[idx]) % blk) > ((++val[idx] - offs[idx]) % blk)) {
			if(++tag[idx] < bnd[idx])
				val[idx] = offs[idx] + t.get(tag[idx]) * blk;
		}
	}
	
	private void blockMerge(int[] array, int o, int a, int m, int b, BitArray t0, BitArray t1) {
		final int blk = keys / 2;
		int[] cnt = new int[2], offs = new int[] {a, m}, bnd = new int[] {(m-o)/blk, (b-o)/blk},
		      tag0 = new int[] {(a-o)/blk, (m-o)/blk}, tag1 = new int[] {tag0[0], tag0[1]}, 
		      mrg = new int[] {a+t0.get(tag0[0])*blk, m+t0.get(tag0[1])*blk},
		      buf = new int[] {mrg[0], mrg[1]};
		int tag = 0, A = (a-o)/blk, A2 = A, M = (m-o)/blk;
		for(int i=0; i<2*blk; i++) {
			if(Reads.compareIndices(array, mrg[0], mrg[1], 1, true) <= 0) {
				Writes.swap(array, key+i, mrg[0], 1, true, false);
				inc(mrg, offs, tag0, bnd, t0, 0);
				cnt[0]++;
			} else {
				Writes.swap(array, key+i, mrg[1], 1, true, false);
				inc(mrg, offs, tag0, bnd, t0, 1);
				cnt[1]++;
			}
		}
		while(tag0[0] < bnd[0] || tag0[1] < bnd[1]) {
			int l = cnt[0] < cnt[1] ? 1 : 0;
			for(int c=0; c<blk; c++) {
				if(tag0[0] < bnd[0] && (tag0[1] == bnd[1] || Reads.compareIndices(array, mrg[0], mrg[1], 1, true) <= 0)) {
					Writes.swap(array, buf[l], mrg[0], 1, true, false);
					inc(mrg, offs, tag0, bnd, t0, 0);
					cnt[0]++;
				} else {
					Writes.swap(array, buf[l], mrg[1], 1, true, false);
					inc(mrg, offs, tag0, bnd, t0, 1);
					cnt[1]++;
				}
				inc(buf, offs, tag1, bnd, t0, l);
			}
			cnt[l] -= blk;
			t1.xor(tag++, t0.get(tag1[l]-1)+l*(M-A));
		}
		int w = 0, x;
		while(cnt[0] > 0) {
			multiSwap(array, key+w, a+(x=t0.get(tag1[0]++))*blk, blk);
			t0.set(A2++, x);
			w += blk; cnt[0] -= blk;
		}
		while(cnt[1] > 0) {
			multiSwap(array, key+w, m+(x=t0.get(tag1[1]++))*blk, blk);
			t0.set(A2++, x+M-A);
			w += blk; cnt[1] -= blk;
		}
		for(int i=0; i<tag; i++) {
			int p = t1.get(i);
			t0.set(A2++, p);
			t1.xor(i, p);
		}
	}
	
	private void kita(int[] array, int a, int c, BitArray tag0, BitArray tag1) {
		if(c - a <= keys) {
			pingPong(array, a, c, key);
			return;
		}
		int k = keys & ~1, K = keys / 2;
		int b = c - (c - a) % k;
		for(int i=a, z=0; i<b; i+=k) {
			if(z++%2==0)
				pingPong(array, i, Math.min(i+k, b), key);
			else
				tailMerge(array, i-k, i, Math.min(i+k, b), -1, key, pingPongNS(array, i, Math.min(i+k, b), key));
		}
		for(int i=0; i<(c-a)/K; i++) {
			tag0.xor(i, i&3);
		}
		for(int j=2*k; j<b-a; j*=2) {
			for(int i=a; i+j<b; i+=2*j) {
				blockMerge(array, a, i, i+j, Math.min(i+2*j, b), tag0, tag1);
			}
		}
		for(int i=a, j=0, l; i<b; i+=K, j++) {
			if((l = tag0.get(j)) != j) {
				int m = j;
				do {
					multiSwap(array, a+m*K, a+l*K, K);
					tag0.xor(m, l ^ m);
					m = l;
					l = tag0.get(l);
				} while(l != j);
				tag0.xor(m, l ^ m);
			}
		}
		tag0.free();
		if(b < c) {
			kita(array, b, c, tag0, tag1);
			tailMerge(array, a, b, c, -1, key, false);
		}
	}
	
	// stolen from IPSC
	private int binSearch(int[] array, int a, int b, int val, int bias) {
		while(a < b) {
			int m = a+(b-a)/2;
			
			if(Reads.compareValues(val, array[m]) < bias) 
				b = m;
			else	 
				a = m+1;
		}
		return a;
	}
	private void merge(int[] array, int[] cnt, int a, int m, int b, int piv, int bias) {
		int m1 = this.binSearch(array, m, b, piv, bias);
		
		int aCnt = m1-m, mCnt = b-m1;
		
		IndexedRotations.cycleReverse(array, a+cnt[0], m, m1, 1, true, false);
		cnt[0] += aCnt;
		cnt[1] += mCnt;
	}
	private int lpart(int[] array, int start, int end, int bias) {
		int blk = keys;
		int pi = medOfMed(array, start, end-1, log(end-start, 9)), p = array[pi];
		int l = 0, r = 0, lb = 0, rb = 0, t = start;
		boolean chkeq = false;
		for(int i = start; i < end; i++) {
			int cmp = Reads.compareIndexValue(array, i, p, 1, true);
			if(cmp != 0) chkeq = true;
			if(cmp > -bias) {
				Writes.swap(array, key+r++, i, 0.5, true, false);
				if(r == blk) {
					multiSwap(array, t, i-l+1, l);
					multiSwap(array, key, t, blk);
					t += blk; r = 0; rb++;
				}
			} else {
				Writes.swap(array, t+l++, i, 0.5, true, false);
				if(l == blk) {
					t += blk; l = 0; lb++;
				}
			}
		}
		int m = lb < rb ? lb : rb, T = t, mid = start + lb * blk, tF = mid;
		int j, k, M = log2(m-1)+1;
		boolean mR = false;
		if(m > 0) {
			if(blk <= M) {
				// continually rotate blocks into bigger chunks (aphitorite's IPSC, modified for binary partition merging)
				int nxt = t, i, ll = 0, rr = 0;
				int[] cnt = new int[2];
				tF = t;
				while(blk <= M) {
					cnt[0] = cnt[1] = 0;
					blk *= 2;
					for(nxt = i = start; i < t; i += blk) {
						if(i + blk / 2 < t &&
							Reads.compareValues(array[i], p) > -bias &&
							Reads.compareValues(array[i+blk/2], p) <= -bias) {
							Rotations.cycleReverse(array, i, blk/2, Math.min(blk/2, t - i - blk/2), 1, true, false);
						}
						merge(array, cnt, nxt, i, Math.min(i+blk, t), p, bias);
						
						ll += cnt[0] / blk;
						
						nxt += cnt[0] - (cnt[0] %= blk);
						
						Rotations.cycleReverse(array, nxt, cnt[0], cnt[1] - (cnt[1] % blk), 1, true, false);
						rr += cnt[1] / blk;
						nxt += cnt[1] - (cnt[1] %= blk);
					}
					lb = ll;
					rb = rr;
					m = Math.min(ll, rr);
					M = log2(m-1)+1;
					t = nxt;
					IndexedRotations.cycleReverse(array, t + cnt[0], t + cnt[0] + cnt[1], tF, 1, true, false);
					tF -= cnt[1];
					if(cnt[0] + cnt[1] > 0)
						mR = true;
				}
			}
			j = k = start;
			for(int i = 0; i < m; i++) {
				while(Reads.compareValues(array[j+M], p) <= -bias) j += blk;
				while(Reads.compareValues(array[k+M], p) > -bias) k += blk;
				set(array, j, k, i, M, lb < rb);
				j += blk;
				k += blk;
			}
			if(lb < rb) {
				for(j = t - blk, k = t; j >= start; j -= blk) {
					if(Reads.compareValues(array[j+M], p) > -bias) {
						multiSwap(array, j, k -= blk, blk);
					}
				}
				for(int i = start, h = 0; i < k; i += blk, h++) {
					int w = get(array, i, p, M, bias);
					while(h != w) { // index sort
						multiSwap(array, start+w*blk, i, blk);
						w = get(array, i, p, M, bias);
					}
					set(array, i, k+h*blk, h, M, lb < rb); // compareless clear the block tag
				}
			} else {
				for(j = start, k = start; j < t; j += blk) {
					if(Reads.compareValues(array[j+M], p) <= -bias) {
						multiSwap(array, j, k, blk);
						k += blk;
					}
				}
				for(int h = 0; k < t; k += blk, h++) {
					int w = get(array, k, p, M, bias);
					while(h != w) {
						multiSwap(array, k+(w-h)*blk, k, blk);
						w = get(array, k, p, M, bias);
					}
					set(array, k, start+h*blk, h, M, lb < rb); // compareless clear the block tag
				}
			}
		}
		multiSwap(array, T+l, key, r);
		multiSwap(array, T, key, l);
		if(l != 0)
			pushbw(array, tF, T+l, tF == mid ? rb*blk : T - tF);
		multiSwap(array, tF, key, l);
		if(mR) {
			IndexedRotations.cycleReverse(array, start+lb*blk, t, tF + l, 1, true, false);
			l += tF - t;
		}
		if(chkeq)
			return start + l + lb * blk;
		return -1;
	}
	private void logsort(int[] array, int a, int b) {
		if(a >= b - 16) {
			insertRun(array, a, b, false);
			return;
		}
		int m = lpart(array, a, b, 0);
		if(m < 0) return;
		if(m == a || m == b) m = lpart(array, a, b, 1);
		logsort(array, a, m); logsort(array, m, b);
	}
	public void logSort(int[] array, int a, int b, int startingKeys) {
		key = a;
		if(startingKeys <= 0) {
			int s = sqrt(b - a), target = Math.max(s, LG);
			keys = getKeys(array, key, b, target);
		} else {
			keys = startingKeys;
		}
		a = key+keys;
		logsort(array, a, b);
		redistribute(array, b);
	}
	private void logselect(int[] array, int a, int b, int r0, int r1) {
		int a1 = a, b1 = b;
		
		while(b-a > 16) {
			int m = lpart(array, a, b, 0);
			if(m < 0 && a == a1 && b == b1) return;
			else if(m < 0) break;
			if(m == a || m == b) m = lpart(array, a, b, 1);
			
			if(m > r1 && m < b1)	  b1 = m;
			else if(m < r1 && m > a1) a1 = m;
			else if(m == r1)		  a1 = b1;
			
			if(m > r0)	  b = m;
			else if(m < r0) a = m;
			else			break;
		}
		if(b-a <= 16)
			insertRun(array, a, b, false);
		
		while(b1-a1 > 16) {
			int m = lpart(array, a1, b1, 0);
			if(m < 0) return;
			if(m == a1 || m == b1) m = lpart(array, a1, b1, 1);
			
			if(m == r1) return;
			
			else if(m > r1) b1 = m;
			else if(m < r1) a1 = m;
			else			break;
		}
		if(b1-a1 <= 16) 
			insertRun(array, a1, b1, false);
	}
	
	private void redistribute(int[] array, int end) {
		int a = key, carry = 0;
		while(keys > 0) {
			if(carry == 1) {
				Writes.swap(array, a, a + keys++, 1, true, false);
				heap(array, a+1, a+keys);
			} else {
				heap(array, a, a+keys);
			}
			int Z = binSearch(array, a + keys, end, array[a], 1);
			IndexedRotations.cycleReverse(array, a, a + keys, Z, 1, true, false);
			a += Z - (a + keys);
			int l = a, r = a + keys, le = a + keys / 2, t = a + (keys + 1) / 2;
			while(l < le && r < end) {
				if(Reads.compareValues(array[l], array[r]) <= 0) {
					Writes.swap(array, t++, l++, 1, true, false);
				} else {
					Writes.swap(array, t++, r++, 1, true, false);
				}
			}
			carry = keys % 2;
			keys /= 2;
		}
	}
	
	public void singleKitaSort(int[] array, int start, int end) {
		int s = sqrt(end - start), target = 2 * Math.max(s, LG);
		key = start;
		keys = getKeys(array, key, end, target);
		start = key+keys;
		int j = (end-start)/(keys/2), p = log2(j-1)+1, val = 2 * j * p;
		if(val >= (end - start) / 3) {
			logSort(array, key, end, keys);
			return;
		} else {
			logselect(array, start, end, start + val, end - val);
			BitArray t0 = new BitArray(array, start, end - val, j, p);
			BitArray t1 = new BitArray(array, start + val / 2, end - val + val / 2, j, p);
			kita(array, start + val, end - val, t0, t1);
			logsort(array, start, start + val);
			logsort(array, end - val, end);
		}
		redistribute(array, end);
	}
	@Override
	public void runSort(int[] array, int sortLength, int bucketCount) throws Exception {
		singleKitaSort(array, 0, sortLength);
	}
}