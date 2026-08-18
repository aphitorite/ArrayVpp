package io.github.arrayv.sorts.hybrid;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;

/*
 * 
MIT No Attribution

Copyright (c) 2025-2026 Distray

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
 *
 */

final public class CobaltSortAlt extends Sort {
    public CobaltSortAlt(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        
        this.setSortListName("Cob.Alt");
        this.setRunAllSortsName("Cobalt Sort (Block Merge Sort)");
        this.setRunSortName("Cobalt Sort");
        this.setCategory("Block Merge Sorts");
        this.setAuthors("Distray");
        this.setConstant("n log n");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
        this.setAuthors("Distray");
    }
    
    /*
     * Ferrite Sort: A block merge. I never said it was practical,
     * I just said it was a block merge.
     */

    private static final double KEYCOLLECT_CONSTANT = 1;
    private static final int EASY_TOLERANCE = 24;
    private static final int MIN_KEYS = 4;
    private static final int MINSORT_N = 16;

    // potgte
    private int pot(int v) {
    	if(v < 3) return v;
    	int w = 1;
    	while(w < v) w *= 2;
    	return w;
    }

    private void rotate(int[] array, int a, int m, int b) {
    	if(a >= b) return;
    	while(a < m && m < b) {
    		for(int i = m, j = b; i > a;) {
    			Writes.swap(array, --i, --j, 2, true, false);
    		}
    		b = a + b - m;
    		m = a + (m - a) % (b - a);
    	}
    }
    private void insert(int[] array, int from, int to) {
    	if(from == to) return;
    	int c = from > to ? 0 : 1, back = Math.min(from, to), len = Math.abs(from - to);
    	int tmp = array[from];
    	Writes.arraycopy(array, back + c, array, back + 1 - c, len, 1, true, false);
    	Writes.write(array, to, tmp, 1, true, false);
    }
    private void multiSwap(int[] array, int a, int b, int s) {
    	while(s-->0)Writes.swap(array, a++, b++, 1, true, false);
    }

    private int binSearch(int[] array, int l, int r, int k, int SR) {
    	while(l < r) {
    		int m = l + (r - l) / 2;
    		int c = Reads.compareIndexValue(array, m, k, 0.5, true);
    		if(c < SR) l = m + 1;
    		else       r = m;
    	}
    	return l;
    }
    private void rotateMerge(int[] array, int a, int m, int b) {
    	if(a >= m || m >= b) return;
		int m1, m2, m3;
		if(m-a >= b-m) {
			m1 = a + (m - a) / 2;
			m2 = binSearch(array, m, b, array[m1], 0);
			m3 = m1+(m2-m);
		} else {
			m2 = m+(b-m)/2;
			m1 = binSearch(array, a, m, array[m2], 1);
			m3 = (m2++)-(m-m1);
		}
		rotate(array, m1, m, m2);
		
		if(m2-m3 > 1 && b-m2 > 0) this.rotateMerge(array, m3+1, m2, b);
		if(m1-a > 0 && m3-m1 > 0) this.rotateMerge(array, a, m1, m3);
	}
    private int mergeStatic(int[] array, int a, int m, int b, int t, boolean cpy, int force) {
    	if((force == 0 && m - a < b - m) || force == 1) {
    		for(int i = a, j = t; i < m && cpy;) {
    			Writes.swap(array, i++, j++, 2, true, false);
    		}
    		int l = t, le = t + m - a, r = m;
    		while(l < le && r < b) {
    			if(Reads.compareIndices(array, l, r, 1, true) <= 0) {
    				Writes.swap(array, a++, l++, 1, true, false);
    			} else {
    				Writes.swap(array, a++, r++, 1, true, false);
    			}
    		}
    		while(l < le)
			Writes.swap(array, a++, l++, 1, true, false);
    		return le - t;
    	} else {
    		for(int i = m, j = t; i < b && cpy;) {
    			Writes.swap(array, i++, j++, 2, true, false);
    		}
    		int l = m - 1, rl = b - m, r = t + rl - 1;
    		while(l >= a && r >= t) {
    			if(Reads.compareIndices(array, l, r, 1, true) > 0) {
    				Writes.swap(array, --b, l--, 1, true, false);
    			} else {
    				Writes.swap(array, --b, r--, 1, true, false);
    			}
    		}
    		while(r >= t)
			Writes.swap(array, --b, r--, 1, true, false);
    		return rl;
    	}
    }
    private void mergeTo(int[] array, int a, int m, int b, int t, int force) {
    	if((force == 0 && m - a <= b - m) || force == 1 || force == 3) {
    		int l = a, r = m, to = t;
    		while(t != l && t != r && l < m && (force != 3 || l < to) && r < b) {
    			if(Reads.compareIndices(array, l, r, 1, true) <= 0) {
    				Writes.swap(array, t++, l++, 1, true, false);
    			} else {
    				Writes.swap(array, t++, r++, 1, true, false);
    			}
    		}
    		if((t != l && t != r) || force != 3) {
	    		while(l < m && (force != 3 || l < to))
	    			Writes.swap(array, t++, l++, 1, true, false);
	    		while(r < b)
	    			Writes.swap(array, t++, r++, 1, true, false);
    		}
    	} else {
    		int l = m - 1, r = b - 1;
    		if(force != 4) t += b - a - 1;
    		while(t != l && t != r && l >= a && r >= m) {
    			if(Reads.compareIndices(array, l, r, 1, true) > 0) {
    				Writes.swap(array, t--, l--, 1, true, false);
    			} else {
    				Writes.swap(array, t--, r--, 1, true, false);
    			}
    		}
    		if((t != l && t != r) || force != 4) {
        		while(l >= a)
    				Writes.swap(array, t--, l--, 1, true, false);
	    		while(r >= m)
	    			Writes.swap(array, t--, r--, 1, true, false);
    		}
    	}
    }
    private void dualMergeBW(int[] array, int a, int m, int b, int t) {
		int l = m - 1, r = b - 1;
		while(t != l && t != r && l >= a && r >= m) {
			if(Reads.compareIndices(array, l, r, 1, true) > 0) {
				Writes.swap(array, t--, l--, 1, true, false);
			} else {
				Writes.swap(array, t--, r--, 1, true, false);
			}
		}
		if(r < m) {
			while(l >= a) {
				Writes.swap(array, t--, l--, 1, true, false);
			}
		} else {
			int bl = l, br = r;
			t = l + 1;
			l = a; r = m;
			while(t != r && l <= bl && r <= br) {
				if(Reads.compareIndices(array, l, r, 1, true) <= 0) {
					Writes.swap(array, t++, l++, 1, true, false);
				} else {
					Writes.swap(array, t++, r++, 1, true, false);
				}
			}
			while(l <= bl && t < r) {
				Writes.swap(array, t++, l++, 1, true, false);
			}
		}
    }

    private void build(int[] array, int a, int b) {
    	for(int j = 2; j <= b - a; j += 2) {
    		for(int i = j, p = 1; i % 2 == 0; i >>>= 1, p *= 2) {
    			int A = a + j - 2 * p, M = a + j - p;
    			if(Reads.compareIndices(array, A, M, 1, true) > 0) {
    				int tmp = array[A];
    				Writes.write(array, A, array[M], 1, true, false);
    				sift(array, M, M, a+j, tmp, 1);
    			}
    		}
    	}
    	int n = b - a, j = 1;
    	while(n % 2 == 0) {n >>>= 1; j *= 2;}
    	int k = j;
    	n >>>= 1; j *= 2;
		for(; n > 0; j *= 2, n >>>= 1)
			if(n % 2 == 1) {
				int A = b - k - j, M = b - k;
				if(Reads.compareIndices(array, A, M, 1, true) > 0) {
    					int tmp = array[A];
    					Writes.write(array, A, array[M], 1, true, false);
    					sift(array, M, M, b, tmp, 1);
    				}
				k += j;
			}
    }

    private void sift(int[] array, int a, int a1, int b, int tmp, int steps) {
    	int b1 = b;
    	// until minimum is hit for any root element:
    	for(;;) {
	    	int p = pot(b - a), lp = 0, min, minp;
	    	// b is coerced to potgte of its length
	    	b = a + p;
	    	
	    	// find first "right child" after a1
	    	do {
	    		lp = p;
	    		minp = p = (p + 1) / 2;
	    		min = b - p;
	    	} while(p != lp && b - p <= a1);
	    	
	    	// traverse "right children", find minimum root after a1
	    	while(p != lp) {
	    		while(b - p > a1) {
	        		b -= p;
	        		if(b < b1 && min != b && (min >= b1 || Reads.compareIndices(array, min, b, 0.1, true) >= 0)) {
	        			min = b;
	        			minp = p;
	        		}
	        		p = (p + 1) / 2;
	    		}
	    		lp = p;
	    		p = (p + 1) / 2;
	    	}
	    	// push array[a1]/tmp to child whose root maintains leftmost invariant, if not minimum
	    	if(min < b1 && min > a1 && Reads.compareValueIndex(array, tmp, min, 1, true) > 0) {
	    		Writes.write(array, a1, array[min], 1, true, false);
	    		// iterate onto child with likely broken invariant
			a = a1 = min;
			b = Math.min(min + minp, b1);
			steps++;
	    	} else { // write tmp only if moves were made prior
	    		if(steps > 0) Writes.write(array, a1, tmp, 1, true, false);
	    		break;
	    	}
    	}
    }

    private void velvetSort(int[] array, int a, int b) {
    	if(a >= b - 1) return;
    	// build list according to invariant
    	build(array, a, b);
        for(int i=a+1; i<b-1; i++)
        	// pick lowest element
        	sift(array, a, i, b, array[i], 0);
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

    private void lazyMerge(int[] array, int start, int mid, int end) {
    	if(mid-start < end-mid) {
    		while(mid < end) {
    			int search = binSearch(array, mid, end, array[start], 0);
    			if(search != start) {
    				rotate(array, start, mid, search);
    				start += search - mid;
    				mid = search;
    			}
    			if(start >= mid || mid >= end)
    				break;
    			do {
    				start++;
    			} while(start < mid && Reads.compareValues(array[start], array[mid]) <= 0);
    		}
    	} else {
    		while(start < mid) {
    			int search = binSearch(array, start, mid, array[end-1], 1);
    			if(search != mid) {
    				rotate(array, search, mid, end);
    				end -= mid - search;
    				mid = search;
    			}
    			if(mid >= end || mid <= start)
    				break;
    			do {
    				end--;
    			} while(mid < end && Reads.compareValues(array[mid-1], array[end-1]) <= 0);
    		}
    	}
    }
    
    // restrained key merger algorithm: O(k log^2 k) + O(n log k) worst.
    private int dupeMax(double c, int k) {
    	return (int)(Math.sqrt(c) * k) + 1;
    }
    private int keyBlock(double c, int k, int t) {
    	return Math.min((int)(c * k) + 1, t - k);
    }
    private int binSearchE(int[] array, int l, int r, int k) {
    	while(l < r) {
    		int m = l + (r - l) / 2;
    		int c = Reads.compareIndexValue(array, m, k, 0.5, true);
    		if(c == 0) return -1;
    		if(c < 0)  l = m + 1;
    		else       r = m;
    	}
    	return l;
    }
    private long collectKeysEasy(int[] array, int a, int b, int kCount) {
    	int kStart = a, kFound = 1, i;
    	for(i = kStart + 1; i < b && kFound < kCount; i++) {
    		int sr = binSearchE(array, kStart, kStart + kFound, array[i]);
			Highlights.markArray(3, i);
    		if(sr != -1) {
    			int disp = i - (kStart + kFound);
    			if(disp > EASY_TOLERANCE) {
    				rotate(array, kStart, kStart + kFound, i);
    				sr += disp;
    				kStart += disp;
    			}
    			if(sr != i) insert(array, i, sr);
    			kFound++;
    		}
    	}
		Highlights.clearMark(3);
    	rotate(array, a, kStart, kStart + kFound);
    	return (long)(kFound) | ((long)(i) << 32L);
    }
    private int collectKeys(int[] array, int a, int b, int kCount) {
    	int kStart = a, keys, i;
    	long res = collectKeysEasy(array, kStart, b, MIN_KEYS);
    	keys = (int)(res); i = (int)(res >> 32);
    	
    	for(; keys < kCount && i < b;) {
    		int nxt = keyBlock(KEYCOLLECT_CONSTANT, keys, kCount),
        		d2FB = dupeMax(KEYCOLLECT_CONSTANT, keys);
    		
    		// find first unique
			while(i < b && binSearchE(array, kStart, kStart + keys, array[i]) == -1) {
				Highlights.markArray(3, i);
				i++;
			}
			Highlights.clearMark(3);
			if(i < b) {
	    		int mKeys = 1;
				int mStart = i;
				for(i++; d2FB > 0 && mKeys < nxt && i < b; i++) {
					boolean isUnique = binSearchE(array, kStart, kStart + keys, array[i]) != -1;
					for(int p = 1, j = mStart + mKeys; isUnique && p <= mKeys; p <<= 1) {
						if((mKeys / p & 1) == 1) {
							isUnique = isUnique && binSearchE(array, j - p, j, array[i]) != -1;
							j -= p;
						}
					}
					if(isUnique) {
						rotate(array, mStart, mStart + mKeys, i);
						mStart = i - mKeys;
						mKeys++;
						int mEnd = i + 1;
						for(int j = 1; (mKeys / j & 1) == 0; j <<= 1) {
							this.rotateMerge(array, mEnd-(j<<1), mEnd-j, mEnd);
						}
					} else {
						d2FB--;
					}
				}
				int j = 1, mEnd = mStart + mKeys;
				for(;j < mKeys && (mKeys / j & 1) == 0; j <<= 1);
				int k = j, sortLen = 0;
				for(j <<= 1; mKeys / j > 0; j <<= 1) {
					if((mKeys / j & 1) == 1) {
						sortLen = Math.max(sortLen, this.mergeStatic(array, mEnd - j - k, mEnd - k, mEnd, kStart, true, 0));
						k += j;
					}
				}
				this.velvetSort(array, kStart, kStart + sortLen);
				rotate(array, kStart, kStart + keys, mStart);
				this.rotateMerge(array, mStart - keys, mStart, mStart + mKeys);
				kStart = mStart - keys; keys += mKeys;
			}
    	}
		rotate(array, a, kStart, kStart + keys);
		return keys;
    }
    private void blockSelectInv(int[] array, int k, int a, int m, int b, int s, int f) {
    	int tb = (b - a) / s,
    		km = (m - a) / s;
    	for(int i = 0, bl = km; i < tb; i++) {
    		if(a + bl * s == f && bl < tb - 1) bl++;
    		int min = i;
    		for(int j = km; j <= bl; j++) {
    			if(j > i && Reads.compareIndices(array, a + min * s, a + j * s, 1, true) > 0) {
    				min = j;
    			}
    		}
    		Writes.swap(array, k + i, a + min * s, 5, true, false);
    		multiSwap(array, a + i * s, a + min * s, s);
    		if(min == bl && bl < tb - 1) bl++;
    	}
    }
    private int smartMergeFWBufSt(int[] array, int k, int t, int a, int m, int b, int s) {
    	int bi = t, i = a, bj = m, j = m, kp = k;
    	while(bi < m && bj < b) {
            boolean left = i-bi > 0 && (i-bi >= s || Reads.compareIndices(array, bi+s-1, bj+s-1, 1, true) <= 0);
            int p = left ? bi : bj;
            for(int c = 0; c < s; c++) {
                int v = j == b || (i < m && Reads.compareIndices(array, i, j, 0.5, true) <= 0) ? i++ : j++;
                Writes.swap(array, p++, v, 0.5, false, false);
            }
            if(p - s >= a) Writes.swap(array, kp++, p - s, 1, true, false);
            if(left) bi = p;
            else     bj = p;
    	}
    	int p = bj < b ? bj : bi;
    	if(bj < b) bj += s;
    	else	   bi += s;
    	while((bi+=s)<=m) {
            Writes.swap(array, kp++, bi - s, 1, true, false);
    	}
    	while((bj+=s)<=b) {
            Writes.swap(array, kp++, bj - s, 1, true, false);
    	}
    	multiSwap(array, p, b - s, s);
    	blockSelectInv(array, k, a, m, b - s, s, p);
    	return b - s;
    }
    private int smartMergeFW(int[] array, int k, int t, int a, int m, int b, int s) {
    	return smartMergeFWBufSt(array, k, t, a, m, b, s);
    }
    private void pingPongMerge(int[] array, int a, int b, int t) {
    	int n = b - a;
    	int j = Math.min(MINSORT_N, n);
    	for(int i = a; i < b; i += j) {
    		insertRun(array, i, Math.min(i + j, b), false);
    	}
    	boolean inT = false;
    	for(; j < n; j *= 2) {
        	for(int i = 0; i < n; i += j * 2) {
        		if(i + j > n) {
        			multiSwap(array, a + i, t + i, n - i);
        		} else {
        			int fr = inT ? t : a,
        			    to = inT ? a : t,
        			    l = i,
        			    m = i + j,
        			    r = Math.min(m + j, n);
        			mergeTo(array, fr + l, fr + m, fr + r, to + l, 0);
        		}
        	}
        	inT = !inT;
    	}
    	if(inT)
    		multiSwap(array, a, t, n);
    }
    private int buildBlocks(int[] array, int t, int a, int b, int s) {
    	int bb = 0, ot = t, tl = t;
    	for(int i = a; i < b; i += s, bb++) {
    		int l = i - s, m = i, r = Math.min(i + s, b);
    		pingPongMerge(array, m, r, t);
    		if(bb % 2 == 1) {
    			// merge
    			mergeTo(array, l, m, r, t, 1);
    			tl = t;
    			t += r - l;
    		}
    	}
    	// dual merge buffer back
    	if(bb % 4 >= 2) {
    		rotate(array, tl, t, t + s);
    		t = tl;
    	}
    	int v = 2 * s;
    	if(t > ot) {
	    	while(t > ot) {
	    		dualMergeBW(array, t - 2 * v, t - v, t, t + s - 1);
	    		t -= 2 * v;
	    	}
	    	return 2 * v;
    	}
    	return v;
    }
    public void cobalt(int[] array, int a, int b) {
    	int n = b - a, sqrtn = 0;
	while(1<<(++sqrtn<<1)<n);
	sqrtn = 1<<sqrtn;
    	int bsz = collectKeys(array, a, b, sqrtn);
    	int nb = Math.floorDiv(n - bsz, bsz) + 1, // remove fragment
    	    key = collectKeys(array, a + bsz, b, nb);
    	multiSwap(array, a, a + bsz, key);
    	int bm = a + key, m = a + bsz + key;
    	int j = buildBlocks(array, bm, m, b, bsz);
    	int v = 2 * Math.floorDiv(b - m, 2 * bsz), bb = m + v * bsz;
    	for(int t = bm; j < bb - m; j *= 2) {
    		for(int i = m; i + j < bb; i += 2 * j) {
    			int l = i, m1 = i + j, r = Math.min(i + 2 * j, bb);
    			t = smartMergeFW(array, a, t, l, m1, r, bsz);
    		}
    		if(2 * j >= bb - m && bb < b) {
    			mergeStatic(array, bm, t, t + b - bb, bb, false, 2);
    			t += b - bb;
    			bb = b;
    		}
    		rotate(array, bm, t, t + bsz);
    		t = bm;
    	}
    	multiSwap(array, a, a + bsz, key);
    	velvetSort(array, a, a + bsz);
    	lazyMerge(array, a, a + bsz, m);
    	lazyMerge(array, a, m, b);
    }
    @Override
    public void runSort(int[] array, int length, int bucketCount) {
    	cobalt(array, 0, length);
    }
}
