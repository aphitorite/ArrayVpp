package io.github.arrayv.sorts.hybrid;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.insert.BinaryInsertionSort;
import io.github.arrayv.sorts.templates.Sort;
import io.github.arrayv.utils.ImplQueue;

final public class LogKitaSortLegacy extends Sort {
	public LogKitaSortLegacy(ArrayVisualizer arrayVisualizer) {
		super(arrayVisualizer);
		
		this.setSortListName("Log Kita (Legacy)");
		this.setRunAllSortsName("Legacy Log Kita Sort");
		this.setRunSortName("Legacy Log Kitasort");
		this.setCategory("Hybrid Sorts");
		this.setConstant("n log n");
		this.setBucketSort(false);
		this.setRadixSort(false);
		this.setUnreasonablySlow(false);
		this.setUnreasonableLimit(0);
		this.setBogoSort(false);
        this.setAuthors("Distray");
	}
	
	private int log(int v) {
		return 32-Integer.numberOfLeadingZeros(v-1);
	}
	
	// first power of two greater than or equal to W(n), because I want to play it safe
	private int[] productLog(int n) {
		int r = 1;
		while((r<<r)+r-1 < n) r++;
		int q = 0;
		while(1<<q < r) q++;
		return new int[] {r, 1<<q};
	}
	
	private void multiSwap(int[] array, int a, int b, int s) {
		while(s-- > 0) Writes.swap(array, a++, b++, 1, true, false);
	}
	
	private int medOf3(int[] array, int a, int b, int c) {
    	int d;
    	if(Reads.compareIndices(array, a, b, 0.5, true) > 0) {
    		d = b; b = a;
    	} else
    		d = a;
    	if(Reads.compareIndices(array, b, c, 0.5, true) > 0) {
    		if(Reads.compareIndices(array, d, c, 0.5, true) > 0) {
        		return d;
        	}
    		return c;
    	}
    	return b;
    }

    private int ninther(int[] array, int a, int b) {
    	if(b-a<=9)
    		return array[a+(b-a)/2];
    	int len = b - a, half = len / 2, quart = len / 4, eight = len / 8;
    	int c = medOf3(array, a, a+eight, a+quart);
    	int d = medOf3(array, a+quart+eight, a+half, a+half+eight);
    	int e = medOf3(array, b-quart, b-eight, b-1);
    	int f = medOf3(array, c, d, e);
    	return f;
    }

     // Median of 3 ninthers
	 private int pseudomo27(int[] array, int a, int b) {
			if (b - a < 64) {
				 return this.ninther(array, a, b);
			} else {
				 int d = (b - a + 1) / 8;
				 int m0 = this.ninther(array, a, a + 2 * d);
				 int m1 = this.ninther(array, a + 3 * d, a + 5 * d);
				 int m2 = this.ninther(array, a + 6 * d, b);
				 return this.medOf3(array, m0, m1, m2);
			}
	 }

	 // Ninther of 9 ninthers
	 private int pseudomo81(int[] array, int a, int b) {
			if (b - a < 256) {
				 return this.pseudomo27(array, a, b);
			} else {
				 int d = (b - a + 1) / 24;
				 int m0 = this.ninther(array, a, a + 2 * d);
				 int m1 = this.ninther(array, a + 3 * d, a + 5 * d);
				 int m2 = this.ninther(array, a + 6 * d, a + 8 * d);
				 int m3 = this.ninther(array, a + 9 * d, a + 11 * d);
				 int m4 = this.ninther(array, a + 12 * d, a + 14 * d);
				 int m5 = this.ninther(array, a + 15 * d, a + 17 * d);
				 int m6 = this.ninther(array, a + 18 * d, a + 20 * d);
				 int m7 = this.ninther(array, a + 19 * d, a + 21 * d);
				 int m8 = this.ninther(array, a + 22 * d, b);
				 return this.medOf3(array, this.medOf3(array, m0, m1, m2), this.medOf3(array, m3, m4, m5), this.medOf3(array, m6, m7, m8));
			}
	 }

	 // Ninther of 9 medians of 3 ninthers
	 private int pseudomo243(int[] array, int a, int b) {
			if (b - a < 16384) {
				 return this.pseudomo81(array, a, b);
			} else {
				 int d = (b - a + 1) / 24;
				 int m0 = this.pseudomo27(array, a, a + 2 * d);
				 int m1 = this.pseudomo27(array, a + 3 * d, a + 5 * d);
				 int m2 = this.pseudomo27(array, a + 6 * d, a + 8 * d);
				 int m3 = this.pseudomo27(array, a + 9 * d, a + 11 * d);
				 int m4 = this.pseudomo27(array, a + 12 * d, a + 14 * d);
				 int m5 = this.pseudomo27(array, a + 15 * d, a + 17 * d);
				 int m6 = this.pseudomo27(array, a + 18 * d, a + 20 * d);
				 int m7 = this.pseudomo27(array, a + 19 * d, a + 21 * d);
				 int m8 = this.pseudomo27(array, a + 22 * d, b);
				 return this.medOf3(array, this.medOf3(array, m0, m1, m2), this.medOf3(array, m3, m4, m5), this.medOf3(array, m6, m7, m8));
			}
	 }

	 // get rank of r between [a,a+g...b)
    private int gaprank(int[] array, int a, int b, int g, int r) {
    	int re = 0;
    	while(a < b) {
    		if(a != r) {
    			if(Reads.compareIndices(array, a, r, 0.25, true) < 0) re++;
    		}
    		a += g;
    	}
    	return re;
    }

    // hopefully better "rank of 243s" median selector
    private int rankof243s(int[] array, int a, int b) {
    	// 2^(log(b-a)/2)
    	int s = 1;
    	while(s*s<b-a) s*=2;

    	// low n: return ninther
    	if((s/=2) < 2) return ninther(array, a, b);
    	int mid = (b-a-1)/(2*s)+1, e = (b-a) / 8, cm = a+(b-a)/2, cr = 0;

    	// select pmo243 with gapped rank closest to middle
    	for(int i=0; i<e; i+=s) {
    		int p = pseudomo243(array, a+i, b-e+i), r = gaprank(array, a, b, s, p);
    		if(Math.abs(cr-mid)>Math.abs(r-mid)) {
    			cm = p;
    			cr = r;
    		}
    	}
    	return cm;
    }
    
    private void encode(int[] array, int a, int b, int v) {
    	while(v>0) {
    		if(v%2==1) Writes.swap(array, a, b, 1, true, false);
    		v/=2; a++; b++;
    	}
    }
	
	private void recode(int[] array, int a, int x, int w, int from, int f, int to, int t) {
		int F = x+from*w, T = x+to*w, tmp, i = 0;
		while(Math.min(f, t) > 0) {
			if((f & t) % 2 == 1) {
				tmp = array[F + i];
				Writes.write(array, F + i, array[a + i], 0.33, true, false);
				Writes.write(array, a + i, array[T + i], 0.33, true, false);
				Writes.write(array, T + i, tmp, 0.33, true, false);
			} else if(f % 2 == 1) {
				Writes.swap(array, a + i, F + i, 1, true, false);
			} else if(t % 2 == 1) {
				Writes.swap(array, a + i, T + i, 1, true, false);
			}
			f /= 2; t /= 2; i++;
		}
		while(f > 0) {
			if(f % 2 == 1)
				Writes.swap(array, a + i, F + i, 1, true, false);
			f /= 2; i++;
		}
		while(t > 0) {
			if(t % 2 == 1)
				Writes.swap(array, a + i, T + i, 1, true, false);
			t /= 2; i++;
		}
	}
    
    private int get(int[] array, int a, int p, int l, int c, boolean b) {
    	int v = 0, i = 0;
    	while(l-->0) {
    		v |= (Reads.compareIndexValue(array, a+i, p, 0.1, true) < c ^ b ? 1 << i : 0);
    		i++;
    	}
    	return v;
    }
	
	private int partitionEasy(int[] array, int[] tmp, int a, int b, int p, int c) {
		int j = 0;
		
		for(int i = a; i < b; i++) {
			Highlights.markArray(1, i);
			Delays.sleep(0.25);
			
			if(Reads.compareIndexValue(array, i, p, 0.5, true) < c)
				Writes.write(array, a++, array[i], 0.25, true, false);
			else
				Writes.write(tmp, j++, array[i], 0.25, false, true);
		}
		Writes.arraycopy(tmp, 0, array, a, j, 0.5, true, false);
		
		return a;
	}
	
	private void blockcycle(int[] array, int a, int m, int b, int l, int w, int p, int c, boolean i) {
		for(int k = 0; k < b - 1; k++) {
			int z = get(array, a+k*l, p, w, c, i);
			while(z != k) {
				multiSwap(array, a+k*l, a+z*l, l);
				z = get(array, a+k*l, p, w, c, i);
			}
			encode(array, a+k*l, m+k*l, k);
		}
		encode(array, a+(b-1)*l, m+(b-1)*l, b-1);
	}
	
	// log partition with +1 blocksize technique applied
    private int partition(int[] array, int[] tmp, int a, int b, int p, int c) {
    	final int blk = tmp.length + 1;
    	if(b-a < blk) return partitionEasy(array, tmp, a, b, p, c);
    	int l = 0, r = 0, t = a, lb = 0, rb = 0;
    	// type blocks
    	for(int i=a; i<b; i++) {
    		if(Reads.compareIndexValue(array, i, p, 0.5, true) < c) {
    			// build low block using swapspace in main list
    			Writes.write(array, t+l++, array[i], 0.25, true, false);
    			if(l == blk) {
    				l = 0;
    				t += blk;
    				lb++;
    			}
    		} else {
    			if(r == blk - 1) {
    				// shift incomplete low block over, copy over complete high block
    				int t2 = array[i];
    				Writes.arraycopy(array, t, array, t+blk, l, 0.25, true, false);
    				Writes.arraycopy(tmp, 0, array, t, r, 0.25, true, false);
    				Writes.write(array, t+r, t2, 0.25, true, false);
    				t += blk;
    				r = 0;
    				rb++;
    			} else {
    				// save element to build high block
    				Writes.write(tmp, r++, array[i], 0.25, true, true);
    			}
    		}
    	}
		// sort blocks
    	int min = Math.min(lb, rb);
    	if(min > 0) {
    		int M = log(min);
    		// tag blocks with indices
    		for(int i=0, j=0, k=0; i<min; i++) {
    			while(Reads.compareIndexValue(array, a+j*blk+M, p, 0.5, true) >= c) j++;
    			while(Reads.compareIndexValue(array, a+k*blk+M, p, 0.5, true) < c) k++;
    			encode(array, a+j++*blk, a+k++*blk, i);
    		}
    		if(lb < rb) {
    			for(int i=lb+rb-1, j=0; i>=0; i--) {
    				if(Reads.compareIndexValue(array, a+i*blk+M, p, 0.5, true) >= c)
    					multiSwap(array, a+i*blk, a+(i+j)*blk, blk);
    				else j++;
    			}
    			// indexsort blocks
    			blockcycle(array, a, a+lb*blk, lb, blk, M, p, c, lb<rb);
    		} else {
    			for(int i=0, j=0; i<lb+rb; i++) {
    				if(Reads.compareIndexValue(array, a+i*blk+M, p, 0.5, true) < c)
    					multiSwap(array, a+i*blk, a+j++*blk, blk);
    			}
    			// indexsort blocks
    			blockcycle(array, a+lb*blk, a, rb, blk, M, p, c, lb<rb);
    		}
    	}
    	// redistribute fragment
    	Writes.arraycopy(tmp, 0, array, b-r, r, 1, true, false);
    	if(l > 0) {
    		Writes.arraycopy(array, t, tmp, 0, l, 0.5, true, true);
    		Writes.arraycopy(array, a+lb*blk, array, a+lb*blk+l, rb*blk, 0.5, true, false);
    		Writes.arraycopy(tmp, 0, array, a+lb*blk, l, 0.5, true, false);
    	}
    	return a+l+lb*blk;
    }
    
    private int[] quickselect(int[] array, int[] tmp, int a, int b, int r) {
    	boolean bad = false;
    	while(b - a > 20) {
    		int m = array[bad ? rankof243s(array, a, b) : pseudomo243(array, a, b)];
    		int p = partition(array, tmp, a, b, m, 0);
    		if(p == a) p = partition(array, tmp, a, b, m, 1);
    		if(p == b) {
    			return new int[] {a, b};
    		}
    		bad = 6*(p-a)<b-a||6*(b-p)<b-a;
    		if(p <= r) a = p;
    		else b = p;
    	}
    	BinaryInsertionSort i = new BinaryInsertionSort(arrayVisualizer);
    	i.customBinaryInsert(array, a, b, 0.5);
    	int m1 = r, m2 = r;
    	do m1--; while(Reads.compareIndices(array, m1, r, 0.1, true) == 0);
    	   m1++;
    	do m2++; while(Reads.compareIndices(array, m2, r, 0.1, true) == 0);
    	return new int[] {m1, m2};
    }
    
    private void merge(int[] array, int[] tmp, int a, int m, int b, int t, boolean aux) {
    	int l = a, r = m;
    	while(l < m && r < b) {
    		if(Reads.compareIndices(array, l, r, 0.5, true) <= 0) {
    			Writes.write(tmp, t++, array[l++], 0.5, true, aux);
    		} else {
    			Writes.write(tmp, t++, array[r++], 0.5, true, aux);
    		}
    	}
    	while(l < m)
			Writes.write(tmp, t++, array[l++], 0.5, true, aux);
    	while(r < b)
			Writes.write(tmp, t++, array[r++], 0.5, true, aux);
    }
    
    private void tailmerge(int[] array, int[] tmp, int a, int m, int b) {
    	Writes.arraycopy(array, m, tmp, 0, b-m, 1, true, true);
    	int l = m-1, r = b-m-1;
    	while(l >= a && r >= 0) {
    		if(Reads.compareIndexValue(array, l, tmp[r], 0.5, true) > 0) {
    			Writes.write(array, --b, array[l--], 0.5, true, false);
    		} else {
    			Writes.write(array, --b, tmp[r--], 0.5, true, false);
    		}
    	}
    	while(r >= 0)
			Writes.write(array, --b, tmp[r--], 0.5, true, false);
    }
    
    private class KitaLinkedList {
    	public int k, j, i, blk, next, maxsize = 0;
    	private int x, p, c, w, w1, n, array[];
    	private ImplQueue<Integer> bf; // should stay a maximum of 3 in size, if not, it's failed
    	private boolean v;
    	public KitaLinkedList(int[] array, int offset, int next_offset, int blocks_remaining, int block_size, int log_size, int bitbuffer, int pivot, int bias, boolean invert) {
    		this.array = array;
    		this.i = this.j = offset;
    		this.blk = blocks_remaining;
    		this.next = this.n = next_offset;
    		this.w = block_size;
    		this.w1 = log_size;
    		this.x = bitbuffer;
    		this.p = pivot;
    		this.c = bias;
    		this.v = invert;
    		this.k = 0;
    		this.bf = new ImplQueue<>(3);
    		iterate();
    	}
    	public int iterate() {
    		this.bf.add(next);
    		maxsize = Math.max(maxsize, size());
			this.k = 0;
    		this.i = j + (next - n) * w;
    		if(blk == 1) {
    			this.next = -1;
    		} else {
	    		this.next = n + get(array, i, p, w1, c, v);
	    		encode(array, i, x + next * w, next - n);
    		}
    		return next;
    	}
    	public void incr() {
    		if(++k == w) {
    			if(--blk > 0) iterate();
    		}
    	}
    	public int shift() {
    		return bf.shift();
    	}
    	public int peek(int idx) {
    		return bf.peek(idx);
    	}
    	public int size() {
    		return bf.size();
    	}
    	public int val() {
    		if(blk == 0) return -1;
    		return i + k;
    	}
    	public int[] tag_last(int lbuf, int next_value) {
    		if(next_value < 2)
    			return new int[] {lbuf, next_value};
    		encode(array, lbuf, x + next_value * w, next_value);
    		return new int[0];
    	}
    	public void free() {
    		this.bf.clear();
    		this.array = null;
    		this.bf = null;
    	}
    }
    
    // indexsort with bitbuffer
    private void indexll(int[] array, int a, int b, int x, int w, int w1, int p, int c, boolean y) {
    	// traverse linkedlist, transcode to index order
    	for(int i = a, j = 0; j < (b-a)/w; j++) {
    		int k = get(array, i, p, w1, c, y);
    		recode(array, i, x, w, k, k, (i-a)/w, j>=2?j:0);
    		i = a + k * w;
    	}
    	int i = a+2*w, i1 = 2;
    	for(; i < b - w; i += w, i1++) {
    		int j = get(array, i, p, w1, c, y);
    		if(j == 0) continue; // pre-encoded check
    		while(j != i1) {
    			int k = get(array, a+j*w, p, w1, c, y);
    			// clear bitbuffer and swap block, encode new index into former block
    			encode(array, i, x+i-a, j);
    			recode(array, a+j*w, x, w, j, k, i1, k);
    			multiSwap(array, i, a+j*w, w);
    			j = k;
    		}
    		encode(array, i, x+i-a, i1);
    	}
    	for(int j = i, k = w1; k-->0; j++) {
    		if(Reads.compareIndexValue(array, j, p, 1, true) < 0 ^ y) {
    			encode(array, i, x+i-a, i1);
    			break;
    		}
    	}
    }
    
    // a kitamerge based off of the properties of a linked list
    private void kitamerge(int[] array, int[] tmp, int x, int i, int ib, int j, int jb, int p, int c, int w, int w1, boolean y) {
    	// cc: block counter, lc: relative left buffer size, rc: relative right buffer size, fbt: first buffer tag
    	// tc: tag count, bb: buffer location, bt: buffer tag, ls: last bb, ft: blocks connecting to tags 0 and 1
    	KitaLinkedList l = new KitaLinkedList(array, i, 0, ib, w, w1, x, p, c, y),
    				   r = new KitaLinkedList(array, j, ib, jb, w, w1, x, p, c, y);
    	int cc = 0, lc = 0, rc = 0, tc = 0, bb, bt, fbt = -1, ls = -1, ft[] = new int[2];
    	for(; cc<2*w; cc++) {
    		if(Reads.compareIndices(array, l.val(), r.val(), 0.5, true) <= 0) {
    			Writes.write(tmp, cc, array[l.val()], 0.5, true, true);
    			lc++;
    			l.incr();
    		} else {
    			Writes.write(tmp, cc, array[r.val()], 0.5, true, true);
    			rc++;
    			r.incr();
    		}
    	}
    	while(l.blk > 0 || r.blk > 0) {
    		while(lc >= rc && (l.blk > 0 || r.blk > 0)) {
    			bt = l.shift();
    			bb = i + bt * w;
    			for(cc=0; cc<w; cc++) {
    	    		if(r.blk == 0 || (l.blk > 0 && Reads.compareIndices(array, l.val(), r.val(), 0.5, true) <= 0)) {
    	    			Writes.write(array, bb+cc, array[l.val()], 0.5, true, true);
    	    			lc++;
    	    			l.incr();
    	    		} else {
    	    			Writes.write(array, bb+cc, array[r.val()], 0.5, true, true);
    	    			rc++;
    	    			r.incr();
    	    		}
    	    	}
    			lc -= w;
    			if(tc++>0) {
    				int[] t = l.tag_last(ls, bt);
    				if(t.length > 0) ft[t[1]] = t[0]; 
    			} else {
    				if(bt < 2) ft[bt] = -1;
    				fbt = bt;
    			}
    			ls = bb;
    		}
    		while(lc <= rc && (l.blk > 0 || r.blk > 0)) {
    			bt = r.shift();
    			bb = i + bt * w;
    			for(cc=0; cc<w; cc++) {
    	    		if(r.blk == 0 || (l.blk > 0 && Reads.compareIndices(array, l.val(), r.val(), 0.5, true) <= 0)) {
    	    			Writes.write(array, bb+cc, array[l.val()], 0.5, true, true);
    	    			lc++;
    	    			l.incr();
    	    		} else {
    	    			Writes.write(array, bb+cc, array[r.val()], 0.5, true, true);
    	    			rc++;
    	    			r.incr();
    	    		}
    	    	}
    			rc -= w;
    			if(tc++>0) {
    				int[] t = l.tag_last(ls, bt);
    				if(t.length > 0) ft[t[1]] = t[0];
    			} else {
    				if(bt < 2) ft[bt] = -1;
    				fbt = bt;
    			}
    			ls = bb;
    		}
    	}
    	int a1 = 0, a2 = 0;
    	while(a1 < l.size()) {
    		if(fbt == a2) fbt = l.peek(a1);
    		if(ft[a2] >= 0)
	    		encode(array, ft[a2], x+l.peek(a1)*w, l.peek(a1));
    		a1++; a2++;
    	}
    	a1 = 0;
    	while(a1 < r.size()) {
    		if(fbt == a2) fbt = r.peek(a1);
    		if(ft[a2] >= 0)
	    		encode(array, ft[a2], x+r.peek(a1)*w, r.peek(a1));
    		a1++; a2++;
    	}
    	a1 = 0;
    	while(l.size() > 0) {
    		int b = i + a1 * w, b1 = i + l.shift() * w;
    		Writes.arraycopy(array, b, array, b1, w, 1, true, false);
    		a1++;
    	}
    	while(r.size() > 0) {
    		int b = i + a1 * w, b1 = i + r.shift() * w;
    		Writes.arraycopy(array, b, array, b1, w, 1, true, false);
    		a1++;
    	}
    	Writes.arraycopy(tmp, 0, array, i, 2*w, 0.5, true, false);
    	encode(array, i, x+w, 1);
    	encode(array, i+w, x+fbt*w, fbt);
    	assert l.maxsize < 4 && r.maxsize < 4 : "The sort's probably not O(log n) space, contact Distray";
    	l.free(); r.free();
    }
    
    private void kita(int[] array, int[] tmp, int a, int b, int x, int w, int p, int c, boolean iv) {
    	int B = b, s = tmp.length / 2;
    	b -= (b - a) % s;
    	BinaryInsertionSort bi = new BinaryInsertionSort(arrayVisualizer);
    	for(int i = a; i < b; i += 16) {
    		// binary insert small n
        	bi.customBinaryInsert(array, i, Math.min(i+16, b), 0.5);
    	}
    	int j = 16;
    	for(; j <= tmp.length / 4; j *= 4) {
    		for(int i = a; i + j < b; i += 4 * j) {
    			// ping-pong merge groups of 4
    			merge(array, tmp, i, i+j, Math.min(i+2*j, b), 0, true);
    			merge(array, tmp, Math.min(i+2*j, b), Math.min(i+3*j, b), Math.min(i+4*j, b), 2*j, true);
    			merge(tmp, array, 0, Math.min(2*j, b-i), Math.min(4*j, b-i), i, false);
    		}
    	}
    	for(; j <= tmp.length; j *= 2) {
    		for(int i = a; i + j < b; i += 2 * j) {
    			// tailmerge pairs
    			tailmerge(array, tmp, i, i+j, Math.min(i+2*j, b));
    		}
    	}
    	// encode linkedlist indices
    	for(int i = a; i < b; i += j) {
    		if(i+s<b) encode(array, i, x+s+i-a, 1);
    		if(i+2*s<b) encode(array, i+s, x+2*s+i-a, 2);
    		if(i+3*s<b) encode(array, i+2*s, x+3*s+i-a, 3);
    	}
    	for(; j < b - a; j *= 2) {
    		for(int i = a; i + j < b; i += 2 * j) {
    			// kitamerge pairs
    			kitamerge(array, tmp, x+i-a, i, j/s, i+j, Math.min(j, b-i-j)/s, p, c, s, w, iv);
    		}
    	}
    	// sort blocks
    	indexll(array, a, b, x, s, w, p, c, iv);
    	if(b < B) {
    		// merge remaining fragment
    		bi.customBinaryInsert(array, b, B, 0.5);
    		tailmerge(array, tmp, a, b, B);
    	}
    }
    
    public void logkita(int[] array, int a, int b) {
    	int[] plgs = productLog(b-a);
    	int lg2 = plgs[1], lg = plgs[0];
    	int[] aux = Writes.createExternalArray(2*lg2);
    	int m = a+(b-a)/2;
    	// quickselect middle
    	int[] bnds = quickselect(array, aux, a, b, m);
    	// block merge both halves
    	kita(array, aux, a, bnds[0], bnds[0], lg-1, array[m], 0, true);
    	kita(array, aux, bnds[1], b, a, lg-1, array[m], 1, false);
    }
    
	@Override
	public void runSort(int[] array, int sortLength, int bucketCount) throws Exception {
		logkita(array, 0, sortLength);
	}
}