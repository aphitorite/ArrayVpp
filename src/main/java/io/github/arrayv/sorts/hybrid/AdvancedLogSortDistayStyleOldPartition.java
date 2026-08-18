package io.github.arrayv.sorts.hybrid;

import io.github.arrayv.sorts.templates.Sort;
import java.awt.Color;
import io.github.arrayv.main.ArrayVisualizer;

final public class AdvancedLogSortDistayStyleOldPartition extends Sort {
	public AdvancedLogSortDistayStyleOldPartition(ArrayVisualizer arrayVisualizer) {
		super(arrayVisualizer);
		
		this.setSortListName("Advanced Log (Colorcoded, Old Fragment Fallback)");
		this.setRunAllSortsName("Advanced Log Sort (Colorcoded, Old Fragment Fallback)");
		this.setRunSortName("Advanced Logsort (Colorcoded, Old Fragment Fallback)");
		this.setCategory("Hybrid Sorts");
        this.setAuthors("Distray");
		this.setConstant("n log n");
		this.setBucketSort(false);
		this.setRadixSort(false);
		this.setUnreasonablySlow(false);
		this.setUnreasonableLimit(0);
		this.setBogoSort(false);
	}
	private static final boolean CLEAN_BUFFER = false;
	
	private int log(int v) {
		return 32-Integer.numberOfLeadingZeros(v-1);
	}
	
	private int[] productLog(int n) {
		int r = 1;
		while((r<<r)+r-1 < n) r++;
		int q = 0;
		while(1<<q < r) q++;
		return new int[] {r, 1<<q};
	}
	
	private void multiSwap(int[] array, int a, int b, int s) {
		while(s-- > 0) {
            Highlights.swapColors(a, b);
			Writes.swap(array, a++, b++, 1, true, false);
		}
	}
    
    private void arraycopy_CSCC(int[] src, int osrc, int[] dst, int odst, int len, double sleep, boolean mark, boolean auxwrite) {
    	if(src == dst && osrc == odst) return;
    	if(src == dst && osrc < odst && odst - osrc < len) {
	        for(int i=len-1; i>=0; i--) {
	            Highlights.writeColor(src, osrc+i, dst, odst+i);
	            Writes.write(dst, odst+i, src[osrc+i], sleep, mark, auxwrite);
	            if(CLEAN_BUFFER) Writes.visualClear(src, osrc + i);
	        }
    	} else {
	        for(int i=0; i<len; i++) {
	            Highlights.writeColor(src, osrc+i, dst, odst+i);
	            Writes.write(dst, odst+i, src[osrc+i], sleep, mark, auxwrite);
	            if(CLEAN_BUFFER) Writes.visualClear(src, osrc + i);
	        }
    	}
    }
    
    private void arraycopy_CSSC(int[] src, int osrc, int[] dst, int odst, int len, double sleep, boolean mark, boolean auxwrite, String use) {
    	if(src == dst && osrc == odst) return;
    	if(src == dst && osrc < odst && odst - osrc < len) {
	        for(int i=len-1; i>=0; i--) {
	            Highlights.colorCode(use, odst+i);
	            Writes.write(dst, odst+i, src[osrc+i], sleep, mark, auxwrite);
	            if(CLEAN_BUFFER) Writes.visualClear(src, osrc + i);
	        }
    	} else {
	        for(int i=0; i<len; i++) {
	            Highlights.colorCode(use, odst+i);
	            Writes.write(dst, odst+i, src[osrc+i], sleep, mark, auxwrite);
	            if(CLEAN_BUFFER) Writes.visualClear(src, osrc + i);
	        }
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
	
	private void rotate(int[] array, int a, int m, int b) {
		int n = b - a, l = m - a;
		if(n == 0) return;
		l %= n;
		if(l == 0) return;

		Color vc = Highlights.colorAt(a);
        for (int i = 0, j = 0, v = array[a + j], jx = j;
        		 i < n; i++) {
            int j2 = (j - l + n) % n;
            int v2 = array[a + j2];
            Color vc2 = Highlights.colorAt(a + j2);
            Writes.write(array, a + j2, v, 1, true, false);
            Highlights.colorCode(vc, a + j2);
            v = v2; vc = vc2;
            
            if(j2 == jx) {
            	jx = j = (j - 1 + n) % n;
            	v = array[a + j];
            	vc = Highlights.colorAt(a + j);
            } else {
            	j = j2;
            }
        }
	}
	
	private int medOf3(int[] array, int a, int b, int c, String use) {
    	int d;
        
        Highlights.colorCode(use, a, b, c);
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
    		return a+(b-a)/2;
    	int len = b - a, half = len / 2, quart = len / 4, eight = len / 8;
    	int c = medOf3(array, a, a+eight, a+quart, "medof3");
    	int d = medOf3(array, a+quart+eight, a+half, a+half+eight, "medof3");
    	int e = medOf3(array, b-quart, b-eight, b-1, "medof3");
    	int f = medOf3(array, c, d, e, "medof9");
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
				 return this.medOf3(array, m0, m1, m2, "medof27");
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
				 return this.medOf3(array,
					this.medOf3(array, m0, m1, m2, "medof27"),
					this.medOf3(array, m3, m4, m5, "medof27"),
					this.medOf3(array, m6, m7, m8, "medof27"),
				 "medof81");
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
				 return this.medOf3(array,
					this.medOf3(array, m0, m1, m2, "medof81"),
					this.medOf3(array, m3, m4, m5, "medof81"),
					this.medOf3(array, m6, m7, m8, "medof81"),
				 "medof243");
			}
	 }

	 // get rank of r between [a,a+g...b)
    private int gaprank(int[] array, int a, int b, int g, int r) {
    	int re = 0;
    	while(a < b) {
    		if(a != r) {
    			Highlights.colorCode("sqrank", a);
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
		Highlights.colorCode("rank243", cm);
    	return cm;
    }

	// mangled by Quiltflower
	private void insertRun(int[] array, int start, int end, boolean d) {
		boolean invert = d;
		int l, r, m, j, t;
		for(int i=start+1; i<end; i++) {
			// if next element in place, do nothing
			if(invert ^ Reads.compareIndices(array, i-1, i, 0.01, true) <= 0) {
				continue;
			}
			// if next element goes at start, flip sorted area
			if(invert ^ Reads.compareIndices(array, start, i, 0.01, true) > 0) {
				Writes.reversal(array, start, i-1, 0.5, true, false);
				invert = !invert;
				continue;
			}
			// binary search for next element's position
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
			// insert into target position
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
    
    private void encode(int[] array, int a, int b, int v) {
    	while(v>0) {
    		if(v%2==1) Writes.swap(array, a, b, 1, true, false);
    		v/=2; a++; b++;
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
    
    private void multiSwapAndFree(int[] array, int a, int b, int m, int s, int v) {
    	for(int i = 0; i < s; i++) {
    		if((v & 1) == 0) {
                Highlights.swapColors(a + i, b + i);
    			Writes.swap(array, a + i, b + i, 1, true, false);
    		} else {
    			int DVAL = array[b+i];
                Highlights.swapColors(m + i, b + i);
                Highlights.swapColors(a + i, m + i);
    			Writes.write(array, b+i, array[m+i], 1, true, false);
    			Writes.write(array, m+i, array[a+i], 1, true, false);
    			Writes.write(array, a+i, DVAL, 1, true, false);
    		}
    		v >>>= 1;
    	}
    }
	
	private void blockcycle(int[] array, int a, int m, int b, int frag, int l, int w, int p, int c, boolean i) {
		if(frag!=b-1) multiSwap(array, a+frag*l, a+(b-1)*l, l);
		for(int k = 0; k < b - 1; k++) {
			int z = get(array, a+k*l, p, w, c, i);
			while(z != k && z > 0) {
				multiSwapAndFree(array, a+k*l, a+z*l, m+z*l, l, z);
				z = get(array, a+k*l, p, w, c, i);
			}
			encode(array, a+k*l, m+k*l, z);
		}
	}
	
	private int rotatePart(int[] array, int a, int b, int p, int c) {
		if(a < b) {
			int m = a + (b - a) / 2;
			int l1 = rotatePart(array, a,   m, p, c);
			int l2 = rotatePart(array, m+1, b, p, c);
			rotate(array, a+l1, m+1, m+l2+1);
			return l1 + l2;
		} else {
			int v = Reads.compareIndexValue(array, a, p, 0.5, true) < c ? 1 : 0;
			Highlights.colorCode(v==1?"fraglo":"fraghi", a);
			return v;
		}
	}
    
    // advanced log partition: hybrid aeos-muku* (*not muku)
	// includes a tweaked form of aphiBP typing, giving it ~4n worst case writes at the cost of a guaranteed redistribution phase.
	// typical aeos typing w/ minimum space worstcase: 1.5n type + 0.5n tag + 1n sort + 0.5n index + 0.5n untag + 0.5n redist
	// also still really buggy.
    private int partition(int[] array, int[] tmp, int a, int B, int p, int c, final int s) {
    	int b = B - (B - a) % s;

		if(a == b) { // easy partition
			int tt = 0;
			for(int i = a; i < B; i++) {
	    		if(Reads.compareIndexValue(array, i, p, 0.5, true) < c) {
	    			if(tt > 0) {
	    				Writes.write(array, i - tt, array[i], 2, true, false);
	    				if(CLEAN_BUFFER) Writes.visualClear(array, i);
	    				else {
	    					Highlights.colorCode("buffer", i);
	    				}
	    			}
					Highlights.colorCode("lo", i - tt);
	    		} else {
	    			Writes.write(tmp, tt++, array[i], 2, true, true);
    				if(CLEAN_BUFFER) Writes.visualClear(array, i);
    				else {
    					Highlights.colorCode("buffer", i);
    				}
	    		}
			}
			Writes.arraycopy(tmp, 0, array, B - tt, tt, 2, true, false);
			return B - tt;
		}

    	// get one block of buffer
    	int id = a, t = 0,  // aeos/muku vals 
    		lb = 0, rb = 0, // muku block tally
    		lc = 0, rc = 0, // muku blockbuf count
    		li = 0, ri = 0, // muku block index
    		ii = -1;        // tracker index
    	while(id < b && t < s) {
    		if(Reads.compareIndexValue(array, id, p, 0.5, true) < c) {
    			if(lc < 0) lc++;
    			if(lc == 0) { // if block marked full, go to next
    				li = ++ii;
    				lb++;
    			}
    			if(t > 0) {
	    			Writes.write(array, id - t, array[id], 2, true, false);
	    			if(CLEAN_BUFFER) Writes.visualClear(array, id);
    				else {
    					Highlights.colorCode("buffer", id);
    				}
    			}
				Highlights.colorCode("lo", id - t);
    			if(++lc == s) { // if block full, mark as such
    				lc = -1;
    			}
    		} else {
    			Writes.write(tmp, t++, array[id], 2, true, true);
				if(CLEAN_BUFFER) Writes.visualClear(array, id);
				else {
					Highlights.colorCode("buffer", id);
				}
    		}
    		id++;
    	}
    	
    	// muku typing
    	while(id < b) {
    		if(Reads.compareIndexValue(array, id, p, 0.5, true) < c) {
    			if(lc < 0) lc++;
    			if(lc == 0) { // if block marked full, go to next
    				li = ++ii;
    				lb++;
    			}
    			int x = a + li * s + lc++;
				if(id != x) {
	    			Writes.write(array, x, array[id], 1, true, false);
					if(CLEAN_BUFFER) Writes.visualClear(array, id);
    				else {
    					Highlights.colorCode("buffer", id);
    				}
				}
				Highlights.colorCode("lo", x);
    			if(lc == s) { // if block full, mark as such
    				lc = -1;
    			}
    		} else {
    			if(rc < 0) rc++;
    			if(rc == 0) { // if block marked full, go to next
    				ri = ++ii;
    				rb++;
    			}
    			int x = a + ri * s + rc++;
				if(id != x) {
	    			Writes.write(array, x, array[id], 1, true, false);
					if(CLEAN_BUFFER) Writes.visualClear(array, id);
    				else {
    					Highlights.colorCode("buffer", id);
    				}
				}
				Highlights.colorCode("hi", x);
    			if(rc == s) { // if block full, mark as such
    				rc = -1;
    			}
    		}
    		id++;
    	}
    	boolean lf = lc > 0, rf = rc > 0;
    	int lb_c = lb - (lf ? 1 : 0),
    		rb_c = rb - (rf ? 1 : 0); // corrected vars (block tally minus fragments)
    	int min  = Math.min(lb, rb);
		int M    = log(min);
    	if(min > 0) {
    		// tag blocks
    		for(int i = 0, j = 0, k = 0; i < min - 1; i++) {
    			while(j == ri || Reads.compareIndexValue(array, a+j*s+M, p, 0.5, true) >= c) j++;
    			while(k == li || Reads.compareIndexValue(array, a+k*s+M, p, 0.5, true) < c) k++;
    			encode(array, a+j++*s, a+k++*s, i);
    		}
    		int ca, cm, cf;
    		// sort blocks
    		if(lb < rb) {
    			for(int i = lb + rb - 1, j = 0; i >= 0; i--) {
    				if(i != li && (i == ri || Reads.compareIndexValue(array, a+i*s+M, p, 0.5, true) >= c)) {
    					multiSwap(array, a+i*s, a+(i+j)*s, s);
    					if(i + j == li) li = i;
    					if(i == ri) ri = i + j;
    				} else j++;
    			}
    			ca = a;
    			cm = a + lb * s;
    			cf = li;
    		} else {
    			for(int i = 0, j = 0; i < lb + rb; i++) {
    				if(i != ri && (i == li || Reads.compareIndexValue(array, a+i*s+M, p, 0.5, true) < c)) {
    					if(i == li) li = j;
    					if(j == ri) ri = i;
    					multiSwap(array, a+i*s, a+j++*s, s);
    				}
    			}
    			ca = a + lb * s;
    			cm = a;
    			cf = ri - lb;
    		}
    		// block cycle with fragment
			if(min > 1) blockcycle(array, ca, cm, min, cf, s, M, p, c, lb<rb);
    	}
    	
		int mid,
			bm = lc < 0  ? lb * s : lb_c * s + lc,
			be = rc < 0  ? rb * s : rb_c * s + rc,
			fm = lc <= 0 ? 0 : s - lc;

		// handle fragment
    	if(b < B) {
    		// in-place
    		int lo = rotatePart(array, b, B-1, p, c);
			if(rb == 0) { // missing full buffer
				arraycopy_CSCC(array, b, array, a + bm, lo, 1, true, false);
				arraycopy_CSSC(tmp, 0, array, a + bm + lo, t, 1, true, false, "bufhi");
			} else {
    			int sz = Math.min(lo, fm);
    			
    			// copy section of fragment
				arraycopy_CSCC(array, b, array, a + bm, sz, 1, true, false);
    			
    			// a
    			int vi = a + lb * s + be;
    			if(lo <= fm) {
    				arraycopy_CSCC(array, a + lb * s, array, b + lo - be, be, 1, true, false);
    				arraycopy_CSSC(tmp, 0, array, a + bm + lo, t, 1, true, false, "bufhi");
    			} else {
    				// rotate fragment and buffer over. use dolphin rotations to minimize overhead.
    				// it looks like garbage and is nowhere near cache friendly,
    				// but it's the best i got. there is literally no free space.
    				arraycopy_CSCC(array, b + sz, array, vi, lo - sz, 1, true, false);
    				arraycopy_CSSC(tmp, 0, array, vi + lo - sz, t, 1, true, false, "bufhi");
    				rotate(array, a + lb * s, vi, b + lo);
    			}
			}
			mid = a + bm + lo;
    	} else {
			arraycopy_CSCC(array, a + lb * s, array, b - be, be, 1, true, false);
			arraycopy_CSSC(tmp, 0, array, mid = a + bm, t, 1, true, false, "bufhi");
    	}
    	return mid;
    }
	
	public void advLogSort(int[] array, int[] tmp, int a, int b, int s, int depth) {
		Writes.recordDepth(depth++);
		int p, m;
		boolean bad = false;
		while(b - a > 32) {
			if(bad) {
				p = array[rankof243s(array, a, b)];
				bad = false;
			} else
				p = array[pseudomo81(array, a, b)];
			m = partition(array, tmp, a, b, p, 1, s); 
			//m = partitionStandard(array, tmp, a, b, p, 1);
			if(m == b) { // pivot is highest rank
				// repartition with different bias
				m = partition(array, tmp, a, b, p, 0, s);
				batchColorCode_reverse(m, b, "sorted");
				b = m;
				//b = m = partitionStandard(array, tmp, a, b, p, 0);
				continue;
			}
			int left = m - a, right = b - m;
			bad = left >= 6 * right || right >= 6 * left;
			if(left > right) {
				Writes.recursion();
				advLogSort(array, tmp, m, b, s, depth);
				b = m;
			} else {
				Writes.recursion();
				advLogSort(array, tmp, a, a = m, s, depth);
			}
		}

		insertRun(array, a, b, false);
		batchColorCode_reverse(a, b, "sorted");
	}
    
	@Override
	public void runSort(int[] array, int length, int bucketCount) {
		int l = productLog(length)[0];
		int[] tmp = Writes.createExternalArray(l);
        Highlights.retainColorMarks(true);
        Highlights.defineColor("medof3", new Color(142, 225, 225));
        Highlights.defineColor("medof9", new Color(142, 170, 225));
        Highlights.defineColor("medof27", new Color(170, 142, 225));
        Highlights.defineColor("medof81", new Color(170, 142, 225));
        Highlights.defineColor("medof243", new Color(225, 142, 170));
        Highlights.defineColor("sqrank", new Color(142, 142, 142));
        Highlights.defineColor("rank243", new Color(142, 225, 170));
        Highlights.defineColor("buffer", new Color(71, 71, 71));
        Highlights.defineColor("lo", new Color(255, 245, 142));
        Highlights.defineColor("hi", new Color(170, 255, 142));
        Highlights.defineColor("fraglo", new Color(204, 195, 114));
        Highlights.defineColor("fraghi", new Color(136, 204, 114));
        Highlights.defineColor("bufhi", new Color(255, 189, 142));
        Highlights.defineColor("sorted", new Color(0, 255, 0));
		advLogSort(array, tmp, 0, length, l, 0);
	}
}