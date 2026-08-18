package io.github.arrayv.sorts.hybrid;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.insert.BinaryInsertionSort;
import io.github.arrayv.sorts.templates.Sort;

final public class LoggierKitaSort extends Sort {
	public LoggierKitaSort(ArrayVisualizer arrayVisualizer) {
		super(arrayVisualizer);
		
		this.setSortListName("Loggier Kita");
		this.setRunAllSortsName("Loggier Kita Sort");
		this.setRunSortName("Loggier Kitasort");
		this.setCategory("Hybrid Sorts");
        this.setAuthors("Distray");
		this.setConstant("n log n");
		this.setBucketSort(false);
		this.setRadixSort(false);
		this.setUnreasonablySlow(false);
		this.setUnreasonableLimit(0);
		this.setBogoSort(false);
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
    
    // logselect function
    private int[] quickselect(int[] array, int[] tmp, int a, int b, int r) {
    	boolean bad = false;
    	while(b - a > 20) {
    		// select good enough median
    		int m = array[bad ? rankof243s(array, a, b) : pseudomo243(array, a, b)];
    		// partition using either bias, whichever one yields results
    		int p = partition(array, tmp, a, b, m, 0);
    		if(p == a) p = partition(array, tmp, a, b, m, 1);
    		if(p == b) {
    			// return boundary if no uniques
    			return new int[] {a, b};
    		}
    		// bad ratio is 6:1 instead of 8:1
    		bad = 6*(p-a)<b-a||6*(b-p)<b-a;
    		if(p <= r) a = p;
    		else b = p;
    	}
    	// binary insert and find boundaries on small n
    	BinaryInsertionSort i = new BinaryInsertionSort(arrayVisualizer);
    	i.customBinaryInsert(array, a, b, 0.5);
    	int m1 = a, m2 = b-1;
    	do m1--; while(Reads.compareIndices(array, m1, r, 0.1, true) == 0);
    	do m2++; while(Reads.compareIndices(array, m2, r, 0.1, true) == 0);
    	return new int[] {m1+1, m2};
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
    
    // indexsort with bitbuffer
    private void indexll(int[] array, int a, int b, int x, int w, int w1, int p, int c, boolean y) {
    	// traverse linkedlist, transcode to index order
    	for(int i = a, j = 0; j < (b-a)/w; j++) {
    		int k = get(array, i, p, w1, c, y);
    		recode(array, i, x, w, k, k, (i-a)/w, j);
    		i = a + k * w;
    	}
    	int i = a+w, i1 = 1;
    	for(; i < b - w; i += w, i1++) {
    		int j = get(array, i, p, w1, c, y);
    		if(j == 0) continue; // pre-encoded check
    		int m = i-a;
    		while(j != i1) {
    			int k = get(array, a+j*w, p, w1, c, y);
    			// clear bitbuffer using last index and swap block
    			encode(array, i, x+m, j);
    			multiSwap(array, i, a+(m=j*w), w);
    			j = k;
    		}
    		encode(array, i, x+m, i1);
    	}
    	for(int j = i, k = w1; k-->0; j++) {
    		if(Reads.compareIndexValue(array, j, p, 1, true) < 0 ^ y) {
    			encode(array, i, x+i-a, i1);
    			break;
    		}
    	}
    }
    
    private int inc(int[] array, int[] vals, int[] offsets, int[] prefetch, int indice, int p, int w1, int c, boolean v, final int w) {
    	if(++vals[indice] % w == 0) {
    		advance(array, vals, offsets, prefetch, indice, p, w1, c, v, w);
    		return 1;
    	}
    	return 0;
    }
    
    private void advance(int[] array, int[] vals, int[] offsets, int[] prefetch, int indice, int p, int w1, int c, boolean v, final int w) {
    	if(prefetch[indice] == 0) {
			vals[indice] = -1; // ran out of linked list
		} else {
			vals[indice] = prefetch[indice] * w;
			prefetch[indice] = get(array, offsets[indice] + vals[indice], p, w1, c, v);
		}
    }
    
    private int posq(int[] vals, int[] offsets, int[] prefetch, int x, int indice, int b, final int w) {
    	if(vals[indice] == -1) return -1;
    	return (prefetch[indice] & (1 << b)) == 0 ?
    			offsets[indice] + (vals[indice] - vals[indice] % w) + b :
    			x + prefetch[indice] * w + (offsets[indice] - offsets[0]) + b;
    }
    
    private int pos(int[] vals, int[] offsets, int[] prefetch, int x, int indice, final int w) {
    	return posq(vals, offsets, prefetch, x, indice, vals[indice] % w, w);
    }
    // a kitamerge based off of the properties of a linked list (! big rewrite !)
    private void kitamerge(int[] array, int[] tmp, int x, int i, int ib, int j, int jb, int p, int c, int w1, boolean v, final int w) {
    	// no longer uses lt/rt. although they save moves, they are otherwise completely useless, *and* take up aux space.
    	// all the big stuff is paired up now, allowing for the merge code to be compacted a lot with macro functions.
    	
    	int[] ptrs = {0, 0}, buffers = {0, 0}, offsets = {i, j};
    	
    	// used as prefetch
    	int[] pfP = {get(array, i, p, w1, c, v), get(array, j, p, w1, c, v)},
    		  pfB = {pfP[0],			   		 pfP[1]};
    	
    	int lf = -1, ls = -1, ft = -1;
    	
    	for (int ic = 0; ic < w; ic++) {
    		// get positions...
    		int l = pos(ptrs, offsets, pfP, x, 0, w), r = pos(ptrs, offsets, pfP, x, 1, w);
    		if (r == -1 || Reads.compareIndices(array, l, r, 1, true) <= 0) {
    			Writes.write(tmp, ic, array[l], 1, true, true);
    			inc(array, ptrs, offsets, pfP, 0, p, w1, c, v, w);
    		} else {
    			Writes.write(tmp, ic, array[r], 1, true, true);
    			inc(array, ptrs, offsets, pfP, 1, p, w1, c, v, w);
    		}
    	}
    	
    	while (ptrs[0] != -1 || ptrs[1] != -1) {
    		// get the index for the more fillable buffer.
            int lv = ptrs[0] != buffers[0] && // pointer may be on a block behind the buffer.
            	(ptrs[0] == -1 || ptrs[1] == -1 || (ptrs[0] % w == buffers[0] % w) ||
            	Reads.compareIndices(array, 
            		posq(buffers, offsets, pfB, x, 0, w - 1, w),
            		posq(buffers, offsets, pfB, x, 1, w - 1, w),
            	1, true) <= 0) ? 0 : 1;
            int bi = buffers[lv] / w + ib * lv, bp = pfB[lv] + ib * lv;
            
            for (int ic = 0; ic < w; ic++) {
        		// get positions...
        		int l = pos(ptrs, offsets, pfP, x, 0, w), r = pos(ptrs, offsets, pfP, x, 1, w);
        		int t = posq(buffers, offsets, pfB, x, lv, ic, w);
        		if (l != -1 && (r == -1 || Reads.compareIndices(array, l, r, 1, true) <= 0)) {
        			Writes.write(array, t, array[l], 1, true, false);
        			inc(array, ptrs, offsets, pfP, 0, p, w1, c, v, w);
        		} else {
        			Writes.write(array, t, array[r], 1, true, false);
        			inc(array, ptrs, offsets, pfP, 1, p, w1, c, v, w);
        		}
            }
            
            // out of necessity.
            encode(array, i + bi * w, x + bp * w, bp % ib);
            // advance the buffer up one block...
            advance(array, buffers, offsets, pfB, lv, p, w1, c, v, w);
            
			if (ft < 0) { // store first block tagged if possible,
				ft = bi;
			} else if (bi == 0) { // store block 0's link if possible,
				lf = ls;
			} else { // or just encode the next value in the chain.
				encode(array, i + ls * w, x + bi * w, bi);
			}
			ls = bi;
    	}
    	
    	int lv = buffers[0] != -1 ? 0 : 1;
    	int free = buffers[lv] + offsets[lv], fi = buffers[lv] / w + ib * lv;
    	
    	Writes.arraycopy(array, i, array, free, w, 1, true, false);
    	
    	if (lf >= 0)
        	encode(array, i + lf * w, x + fi * w, fi); // tags[lf] ^= fi (tags[lf] ?= 0 | tags[lf] = fi)
    	else { // <ft == 0>
    		ft = fi;
    	}
    	
        Writes.arraycopy(tmp, 0, array, i, w, 0.5, true, false);
        encode(array, i, x + ft * w, ft);
    }
    
    private void kita(int[] array, int[] tmp, int a, int b, int x, int w, int p, int c, boolean iv) {
    	int B = b, s = tmp.length;
    	b -= (b - a) % s;
    	BinaryInsertionSort bi = new BinaryInsertionSort(arrayVisualizer);
    	for(int i = a; i < b; i += 16) {
    		// binary insert small n
        	bi.customBinaryInsert(array, i, Math.min(i+16, b), 0.5);
    	}
    	int j = 16;
    	for (; j <= tmp.length / 4; j *= 4) {
    		for (int i = a; i + j < b; i += 4 * j) {
    			// ping-pong merge groups of 4
    			merge(array, tmp, i, i+j, Math.min(i+2*j, b), 0, true);
    			merge(array, tmp, Math.min(i+2*j, b), Math.min(i+3*j, b), Math.min(i+4*j, b), 2*j, true);
    			merge(tmp, array, 0, Math.min(2*j, b-i), Math.min(4*j, b-i), i, false);
    		}
    	}
    	for (; j <= tmp.length; j *= 2) {
    		for (int i = a; i + j < b; i += 2 * j) {
    			// tailmerge pairs
    			tailmerge(array, tmp, i, i+j, Math.min(i+2*j, b));
    		}
    	}
    	// encode linkedlist indices
    	for (int i = a; i < b; i += j) {
    		for (int i1 = i, j1 = 1; j1 < j / s && i1 + s < b; i1 += s, j1++) {
    			encode(array, i1, x + s + i1 - a, j1);
    		}
    	}
    	for (; j < b - a; j *= 2) {
    		for (int i = a; i + j < b; i += 2 * j) {
    			// kitamerge pairs
    			kitamerge(array, tmp, x+i-a, i, j/s, i+j, Math.min(j, b-i-j)/s, p, c, log(j/s), iv, s);
    		}
    	}
    	// sort blocks
    	indexll(array, a, b, x, s, w, p, c, iv);
    	if (b < B) {
    		// merge remaining fragment
    		bi.customBinaryInsert(array, b, B, 0.5);
    		tailmerge(array, tmp, a, b, B);
    	}
    }
    
    public void logkita(int[] array, int a, int b) {
    	int[] plgs = productLog(b-a);
    	int lg2 = plgs[1], lg = plgs[0];
    	int[] aux = Writes.createExternalArray(lg2);
    	int m = a + (b - a) / 2;
    	// quickselect middle
    	int[] bnds = quickselect(array, aux, a, b, m);
    	// block merge both halves
    	kita(array, aux, a, bnds[0], bnds[0], lg-1, array[bnds[0]], 0, true);
    	kita(array, aux, bnds[1], b, a, lg-1, array[bnds[1]-1], 1, false);
    }
    
	@Override
	public void runSort(int[] array, int sortLength, int bucketCount) throws Exception {
		logkita(array, 0, sortLength);
	}
}