package io.github.arrayv.sorts.hybrid;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;

/*
 * 
MIT License

Copyright (c) 2026 Distray

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 *
 */

final public class VanadiumSort extends Sort {
	public VanadiumSort(ArrayVisualizer arrayVisualizer) {
		super(arrayVisualizer);

		this.setSortListName("Vanadium");
		this.setRunAllSortsName("Vanadium Sort");
		this.setRunSortName("Vanadium Sort");
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
	 * Vanadium Sort: How many times have I upgraded the metal now?
	 * A 1x W(n) buffer in-place Pure Log Merge sort derived from
	 * Cobalt(II), derived from Cobalt(alt), derived from Cobalt,
	 * derived from Ferrite, with some pointers taken from Flanlaina.
	 */

	private static final double KEYCOLLECT_CONSTANT = 1.0;
	private static final int EASY_TOLERANCE = 24;
	private static final int MIN_KEYS = 4;
	private static final int MINSORT_N = 8;
	private static final int SPLITMERGE_MIN = 16;

	private int log(int v) {
		return 32 - Integer.numberOfLeadingZeros(v - 1);
	}

	private int productLog(int n) {
		int r = 1;
		while ((r << r) < n) r++;
		return r;
	}

	// potgte
	private int pot(int v) {
		if (v < 3) return v;
		int w = 1;
		while (w < v) w *= 2;
		return w;
	}
	// To maintain compatibility. UniV does not have Math.abs yet.
	private int __abs(int v) {
		return v < 0 ? -v : v;
	}
	private void multiSwap(int[] array, int a, int b, int s) {
		if (a != b) while (s-- > 0) Writes.swap(array, a++, b++, 1, true, false);
	}
	private void multiSwap3(int[] array, int a, int m, int b, int s) {
		if (m == b || a == m)
			multiSwap(array, a, b, s);
		else while (s-- > 0) {
			int tmp = array[a];
			Writes.write(array, a++, array[b], 0.33, true, false);
			Writes.write(array, b++, array[m], 0.33, true, false);
			Writes.write(array, m++, tmp, 0.33, true, false);
		}
	}
	private void insert(int[] array, int from, int to) {
		if (from == to) return;
		int c = from > to ? 0 : 1, back = Math.min(from, to), len = __abs(from - to);
		int tmp = array[from];
		Writes.arraycopy(array, back + c, array, back + 1 - c, len, 1, true, false);
		Writes.write(array, to, tmp, 1, true, false);
	}

	private void rotate(int[] array, int L, int M, int R) {
		while (R - M != M - L) {
			int a = L, m = Math.min(M, L + R - M), r = Math.max(M, L + R - M), b = R;
			if (a == m) return;
			if (a + 1 == m) {
				if (R - M == 1) insert(array, R - 1, L);
				else            insert(array, L, R - 1);
				return;
			}
			int mt = m, rt = r, temp;
			if (M - L < R - M) {
				while (a < mt) {
					temp = array[a];
					Writes.write(array, a++, array[m], 1, true, false);
					Writes.write(array, m++, array[r], 1, true, false);
					Writes.write(array, r++, temp, 1, true, false);
					if (m == rt) m = mt;
				}
				M = m;
			} else {
				while (b > rt) {
					temp = array[--b];
					Writes.write(array, b, array[--r], 1, true, false);
					Writes.write(array, r, array[--m], 1, true, false);
					Writes.write(array, m, temp, 1, true, false);
					if (r == mt) r = rt;
				}
				M = r;
			}
			L = mt; R = rt;
		}
		multiSwap(array, L, M, M - L);
	}

	private int binSearch(int[] array, int l, int r, int k, int SR) {
		while (l < r) {
			int m = l + (r - l) / 2;
			int c = Reads.compareIndexValue(array, m, k, 0.5, true);
			if (c < SR) l = m + 1;
			else       r = m;
		}
		return l;
	}
	private void rotateMerge(int[] array, int a, int m, int b) {
		if (a >= m || m >= b) return;
		int m1, m2, m3;
		if (m - a >= b - m) {
			m1 = a + (m - a) / 2;
			m2 = binSearch(array, m, b, array[m1], 0);
			m3 = m1 + (m2 - m);
		} else {
			m2 = m + (b - m) / 2;
			m1 = binSearch(array, a, m, array[m2], 1);
			m3 = (m2++) - (m - m1);
		}
		rotate(array, m1, m, m2);

		if (m2 - m3 > 1 && b - m2 > 0) rotateMerge(array, m3 + 1, m2, b);
		if (m1 - a > 0 && m3 - m1 > 0) rotateMerge(array, a, m1, m3);
	}
	private int mergeStatic(int[] array, int a, int m, int b, int t, boolean cpy, int force) {
		if ((force == 0 && m - a < b - m) || force == 1) {
			for (int i = a, j = t; i < m && cpy;)
				Writes.swap(array, i++, j++, 2, true, false);
			int l = t, le = t + m - a, r = m;
			while (l < le && r < b) {
				if (Reads.compareIndices(array, l, r, 1, true) <= 0)
					Writes.swap(array, a++, l++, 1, true, false);
				else
					Writes.swap(array, a++, r++, 1, true, false);
			}
			while (l < le)
				Writes.swap(array, a++, l++, 1, true, false);
			return le - t;
		} else {
			for (int i = m, j = t; i < b && cpy;)
				Writes.swap(array, i++, j++, 2, true, false);
			int l = m - 1, rl = b - m, r = t + rl - 1;
			while (l >= a && r >= t) {
				if (Reads.compareIndices(array, l, r, 1, true) > 0)
					Writes.swap(array, --b, l--, 1, true, false);
				else
					Writes.swap(array, --b, r--, 1, true, false);
			}
			while (r >= t)
				Writes.swap(array, --b, r--, 1, true, false);
			return rl;
		}
	}
	private void mergeTo(int[] array, int a, int m, int b, int t, int force) {
		if ((force == 0 && m - a <= b - m) || force == 1 || force == 3) {
			int l = a, r = m, to = t;
			while (t != l && t != r && l < m && (force != 3 || l < to) && r < b) {
				if (Reads.compareIndices(array, l, r, 1, true) <= 0)
					Writes.swap(array, t++, l++, 1, true, false);
				else
					Writes.swap(array, t++, r++, 1, true, false);
			}
			if ((t != l && t != r) || force != 3) {
				while (l < m && (force != 3 || l < to))
					Writes.swap(array, t++, l++, 1, true, false);
				while (r < b)
					Writes.swap(array, t++, r++, 1, true, false);
			}
		} else {
			int l = m - 1, r = b - 1;
			if (force != 4) t += b - a - 1;
			while (t != l && t != r && l >= a && r >= m) {
				if (Reads.compareIndices(array, l, r, 1, true) > 0)
					Writes.swap(array, t--, l--, 1, true, false);
				else
					Writes.swap(array, t--, r--, 1, true, false);
			}
			if ((t != l && t != r) || force != 4) {
				while (l >= a)
					Writes.swap(array, t--, l--, 1, true, false);
				while (r >= m)
					Writes.swap(array, t--, r--, 1, true, false);
			}
		}
	}
	private void dualMergeBW(int[] array, int a, int m, int b, int t) {
		int l = m - 1, r = b - 1;
		while (t != l && t != r && l >= a && r >= m) {
			if (Reads.compareIndices(array, l, r, 1, true) > 0)
				Writes.swap(array, t--, l--, 1, true, false);
			else
				Writes.swap(array, t--, r--, 1, true, false);
		}
		if (r < m) {
			while (l >= a)
				Writes.swap(array, t--, l--, 1, true, false);
		} else {
			int bl = l, br = r;
			t = l + 1;
			l = a; r = m;
			while (t != r && l <= bl && r <= br) {
				if (Reads.compareIndices(array, l, r, 1, true) <= 0)
					Writes.swap(array, t++, l++, 1, true, false);
				else
					Writes.swap(array, t++, r++, 1, true, false);
			}
			while (l <= bl && t < r) {
				Writes.swap(array, t++, l++, 1, true, false);
			}
		}
	}

	private int doubleSearch(int[] array, int l, int a, int b, int r, int key, boolean leftSearch) {
		while (a < b) {
			int m = a + ((b - a) / 2);
			boolean comp = Reads.compareValueIndex(array, key, m == l ? r : m == r ? l : m, 1, true) < (leftSearch ? 1 : 0);

			if (comp) b = m;
			else      a = m + 1;
		}
		return b;
	}
	private void insertRun(int[] array, int start, int end) {
		int n = end - start, m = n / 2, k = m;
		while (k-- > 0) {
			int i = start + k, j = start + 2 * m - k - 1;
			if (i >= j) continue;
			int vi, vj, l, r;
			if (Reads.compareIndices(array, i, j, 1, true) > 0) {
				vi = array[j]; vj = array[i];
				l = doubleSearch(array, j, i, j, i, vi, false);
				r = doubleSearch(array, i, l, j, j, vj, true);
			} else {
				vi = array[i]; vj = array[j];
				r = doubleSearch(array, j, i + 1, j, j, vj, false);
				l = doubleSearch(array, i, i, r, i, vi, true);
			}
			while (++i < l)
				Writes.write(array, i - 1, array[i], 0.5, true, false);
			Writes.write(array, i - 1, vi, 0.5, true, false);
			while (r < j) {
				int t = array[r];
				Writes.write(array, r, vj, 0.5, true, false);
				vj = t;
				r++;
			}
			Writes.write(array, j, vj, 0.5, true, false);
		}
		if (n != m * 2) {
			int l = start, r = end - 1, j = r, vj = array[j];
			while (l < r) {
				int M = l + (r - l) / 2;
				if (Reads.compareIndices(array, M, j, 0.0625, true) > 0)
					r = M;
				else
					l = M + 1;
			}
			while (--j >= l)
				Writes.write(array, j + 1, array[j], 0.5, true, false);
			Writes.write(array, l, vj, 0.5, true, false);
		}
	}

	private void lazyMerge(int[] array, int start, int mid, int end) {
		if (mid - start < end - mid) {
			while (mid < end) {
				int search = binSearch(array, mid, end, array[start], 0);
				if (search != start) {
					rotate(array, start, mid, search);
					start += search - mid;
					mid = search;
				}
				if (start >= mid || mid >= end)
					break;
				do start++; while (start < mid && Reads.compareValues(array[start], array[mid]) <= 0);
			}
		} else {
			while (start < mid) {
				int search = binSearch(array, start, mid, array[end - 1], 1);
				if (search != mid) {
					rotate(array, search, mid, end);
					end -= mid - search;
					mid = search;
				}
				if (mid >= end || mid <= start)
					break;
				do end--; while (mid < end && Reads.compareValues(array[mid - 1], array[end - 1]) <= 0);
			}
		}
	}
	private void lazyMergeII(int[] array, int start, int mid, int end) {
		// [from In - Place Merge II]
		int i = start, j = mid, k;
		while (j < end) {
			mid = k = j;
			if (Reads.compareValues(array[j - 1], array[j]) <= 0) return;

			while (Reads.compareIndices(array, i, j, 0.125, true) <= 0) i++;
			Writes.swap(array, i++, j++, 1, true, false);

			while (i < mid && j < end) {
				if (Reads.compareIndices(array, k, j, 0.125, true) <= 0) {
					Writes.swap(array, i++, k++, 1, true, false);
					if (k == j) k = mid;
				} else {
					rotate(array, mid, k, j);
					Writes.swap(array, i++, j++, 1, true, false);
					k = mid;
				}
			}
			rotate(array, mid, k, j);
		}
		rotate(array, i, mid, end);
	}

	private void build(int[] array, int a, int b) {
		for (int j = 2; j <= b - a; j += 2) {
			for (int i = j, p = 1; i % 2 == 0; i >>>= 1, p *= 2) {
				int A = a + j - 2 * p, M = a + j - p;
				if (Reads.compareIndices(array, A, M, 1, true) > 0) {
					int tmp = array[A];
					Writes.write(array, A, array[M], 1, true, false);
					sift(array, M, M, a + j, tmp, 1);
				}
			}
		}
		int n = b - a, j = 1;
		while (n % 2 == 0) {n >>>= 1; j *= 2;}
		int k = j;
		n >>>= 1; j *= 2;
		for (; n > 0; j *= 2, n >>>= 1)
			if (n % 2 == 1) {
				int A = b - k - j, M = b - k;
				if (Reads.compareIndices(array, A, M, 1, true) > 0) {
						int tmp = array[A];
						Writes.write(array, A, array[M], 1, true, false);
						sift(array, M, M, b, tmp, 1);
					}
				k += j;
			}
	}
	private void sift(int[] array, int a, int a1, int b, int tmp, int steps) {
		int b1 = b;
		for (;;) {
			int p = pot(b - a), lp = 0, min, minp;
			b = a + p;
			do {
				lp = p;
				minp = p = (p + 1) / 2;
				min = b - p;
			} while (p != lp && b - p <= a1);
			while (p != lp) {
				while (b - p > a1) {
					b -= p;
					if (b < b1 && min != b && (min >= b1 || Reads.compareIndices(array, min, b, 0.1, true) >= 0)) {
						min = b;
						minp = p;
					}
					p = (p + 1) / 2;
				}
				lp = p;
				p = (p + 1) / 2;
			}
			if (min < b1 && min > a1 && Reads.compareValueIndex(array, tmp, min, 1, true) > 0) {
				Writes.write(array, a1, array[min], 1, true, false);
				a = a1 = min;
				b = Math.min(min + minp, b1);
				steps++;
			} else {
				if (steps > 0) Writes.write(array, a1, tmp, 1, true, false);
				break;
			}
		}
	}
	private void velvetSort(int[] array, int a, int b, int max) {
		if (a >= b - 1) return;
		if (max < a) max = b - 2;
		build(array, a, b);
		for (int i = a + 1; i <= max; i++)
			sift(array, a, i, b, array[i], 0);
	}

	private int binSearchE(int[] array, int l, int r, int k) {
		while (l < r) {
			int m = l + (r - l) / 2;
			int c = Reads.compareIndexValue(array, m, k, 0.5, true);
			if (c == 0) return -1;
			if (c < 0)  l = m + 1;
			else        r = m;
		}
		return l;
	}
	private long collectKeysEasy(int[] array, int a, int b, int kCount) {
		int kStart = a, kFound = 1, i;
		for (i = kStart + 1; i < b && kFound < kCount; i++) {
			int sr = binSearchE(array, kStart, kStart + kFound, array[i]);
			Highlights.markArray(3, i);
			if (sr != -1) {
				int disp = i - (kStart + kFound);
				if (disp > EASY_TOLERANCE) {
					rotate(array, kStart, kStart + kFound, i);
					sr += disp;
					kStart += disp;
				}
				if (sr != i) insert(array, i, sr);
				kFound++;
			}
		}
		Highlights.clearMark(3);
		rotate(array, a, kStart, kStart + kFound);
		return (long)(kFound) | ((long)(i) << (long)32);
	}

	private int dupeMax(double c, int k) {
		return (int)(Math.sqrt(c) * k) + 1;
	}
	private int keyBlock(double c, int k, int t) {
		return Math.min((int)(c * k) + 1, t - k);
	}
	private int collectKeys(int[] array, int a, int b, int kCount) {
		int kStart = a, keys, i;
		long res = collectKeysEasy(array, kStart, b, MIN_KEYS);
		keys = (int)(res); i = (int)(res >> 32);

		for (; keys < kCount && i < b;) {
			int nxt = keyBlock(KEYCOLLECT_CONSTANT, keys, kCount),
			    d2FB = dupeMax(KEYCOLLECT_CONSTANT, keys);

			// find first unique
			while (i < b && binSearchE(array, kStart, kStart + keys, array[i]) == -1) {
				Highlights.markArray(3, i);
				i++;
			}
			Highlights.clearMark(3);
			if (i < b) {
				int mKeys = 1;
				int mStart = i;
				for (i++; d2FB > 0 && mKeys < nxt && i < b; i++) {
					boolean isUnique = binSearchE(array, kStart, kStart + keys, array[i]) != -1;
					for (int p = 1, j = mStart + mKeys; isUnique && p <= mKeys; p <<= 1)
						if ((mKeys / p & 1) == 1) {
							isUnique = isUnique && binSearchE(array, j - p, j, array[i]) != -1;
							j -= p;
						}
					if (isUnique) {
						rotate(array, mStart, mStart + mKeys, i);
						mStart = i - mKeys;
						mKeys++;
						int mEnd = i + 1;
						for (int j = 1; (mKeys / j & 1) == 0; j <<= 1)
							rotateMerge(array, mEnd - (j << 1), mEnd - j, mEnd);
					} else d2FB--;
				}
				int j = 1, mEnd = mStart + mKeys;
				for (; j < mKeys && (mKeys / j & 1) == 0; j <<= 1);
				int k = j, sortLen = 0;
				for (j <<= 1; mKeys / j > 0; j <<= 1) {
					if ((mKeys / j & 1) == 1) {
						sortLen = Math.max(sortLen, mergeStatic(array, mEnd - j - k, mEnd - k, mEnd, kStart, true, 0));
						k += j;
					}
				}
				velvetSort(array, kStart, kStart + sortLen, -1);
				rotate(array, kStart, kStart + keys, mStart);
				rotateMerge(array, mStart - keys, mStart, mStart + mKeys);
				kStart = mStart - keys; keys += mKeys;
			}
		}
		rotate(array, a, kStart, kStart + keys);
		return keys;
	}

	private boolean pingPongMerge(int[] array, int a, int b, int t) {
		int n = b - a;
		int j = Math.min(MINSORT_N, n);
		for (int i = a; i < b; i += j)
			insertRun(array, i, Math.min(i + j, b));
		boolean inT = false;
		for (; j < n; j *= 2) {
			for (int i = 0; i < n; i += j * 2) {
				if (i + j >= n)
					multiSwap(array, a + i, t + i, n - i);
				else {
					int fr = inT ? t : a,
					    to = inT ? a : t,
					    l = i,
					    m = i + j,
					    r = Math.min(m + j, n);
					mergeTo(array, fr + l, fr + m, fr + r, to + l, 1);
				}
			}
			inT = !inT;
		}
		return inT;
	}
	private int buildBlocks(int[] array, int t, int a, int b, int s) {
		int bb = -1, ot = t, tl = t, tbl = t;
		boolean tLast = false;
		for (int i = a; i < b; i += s) {
			bb++;
			int l = i - s, m = i, r = Math.min(i + s, b);
			boolean tNow = pingPongMerge(array, m, r, t);
			if (bb % 2 == 1) {
				if (tNow && tLast) { // tail merge w/ buffer
					t += r - m;
					mergeStatic(array, l - s, m - s, r - s, t, true, 0);
				} else if (tNow && !tLast) // merge w/ buffered tail. this can only happen with a fragment.
					mergeStatic(array, l, m, r, t, false, 2);
				else if (!tNow && tLast) { // merge w/ buffered tail. this can only happen with a fragment.
					mergeStatic(array, l - s, m - s, r - s, m, false, 2);
					t = r - s;
				} else { // forwards w/ buffer
					mergeTo(array, l, m, r, t, 1);
					t += r - l;
				}
			} else {
				if (bb % 4 == 0)
					tl = t;
				tbl = t;
				if (tNow)
					t += r - m;
				tLast = tNow;
			}
		}

		if (bb % 4 <= 2) {
			rotate(array, tl, t, t + s);
			t = tl; tbl = t - 2 * s;
		}
		
		// dual merge buffer back
		int v = 2 * s;
		if (t > ot) {
			while (t > ot) {
				dualMergeBW(array, tbl - v, tbl, t, t + s - 1);
				t = tbl - v; tbl = t - v;
			}
			return 2 * v;
		}
		return v;
	}

	private int get(int[] array, int a, int p, int l, int c, int b) {
		int v = 0, i = 0;
		while (l-- > 0) {
			v |= (Reads.compareIndexValue(array, a + i, p, 0.1, true) < c ? b : b ^ 1) << i;
			i++;
		}
		return v;
	}
	private void encode(int[] array, int a, int b, int v) {
		while (v != 0) {
			if ((v & 1) == 1) Writes.swap(array, a, b, 1, true, false);
			v>>>=1; a++; b++;
		}
	}

	private void multiSwapAndFree(int[] array, int a, int b, int m, int s, int v) {
		for (int i = 0; i < s; i++) {
			if ((v & 1) == 0) // swap
				Writes.swap(array, a + i, b + i, 1, true, false);
			else { // exchange 3
				int DVAL = array[b + i];
				Writes.write(array, b + i, array[m + i], 1, true, false);
				Writes.write(array, m + i, array[a + i], 1, true, false);
				Writes.write(array, a + i, DVAL, 1, true, false);
			}
			v >>>= 1;
		}
	}
	private void blockcycle(int[] array, int a, int s, int m, int l, int w, int p, int c, int b) {
		for (int k = 0; k < s; k++) {
			int z = get(array, a + k * l, p, w, c, b);
			while (z != k && z > 0) {
				multiSwapAndFree(array, a + k * l, a + z * l, m + (z - 1) * w, l, z); // swap and free tag
				z = get(array, a + k * l, p, w, c, b);
			}
			encode(array, a + k * l, m + (k - 1) * w, z); // free tag if present
		}
	}
	private void blockMergeInternal(int[] array, int t, final int s, int a, int m, int b, int p, int piv, int pCmp, int bit) {
		if (Reads.compareIndices(array, m - 1, m, 0.5, true) <= 0) return;
		if (m - a == s || b - m == s) {
			mergeStatic(array, a, m, b, t, true, 0);
			return;
		}
		int i = a, j = m, c = 0, bc = 0,
			w = log((b - a) / s - 1), l = a, r = m;
		// get buffer block
		while (c < s) {
			if (Reads.compareIndices(array, i, j, 0.5, true) <= 0) 
				Writes.swap(array, t + c++, i++, 0.5, true, false);
			else
				Writes.swap(array, t + c++, j++, 0.5, true, false);
		}

		// [from Adaptive Half Logota]
		while (l < m && r < b) {
			boolean L = i - l > 0 && (i - l == s || Reads.compareIndices(array, l + s - 1, r + s - 1, 1, true) <= 0);
			int k = L ? l : r;
			for (c = 0; c < s; c++) {
				if (i < m && (j == b || Reads.compareIndices(array, i, j, 0.5, true) <= 0))
					Writes.swap(array, k + c, i++, 0.5, true, false);
				else
					Writes.swap(array, k + c, j++, 0.5, true, false);
			}
			if(L) l += s;
			else  r += s;
			encode(array, k, p + (bc - 1) * w, bc++);
		}

		// swap in buffer block
		if (l < m) multiSwap3(array, t, a, l, s);
		else       multiSwap3(array, t, a, r, s);

		// tag remaining blocks on left
		while ((l += s) < m)
			encode(array, l, p + (bc - 1) * w, bc++);

		// blockcycle
		blockcycle(array, a + s, bc, p, s, w, piv, pCmp, bit);
	}
	private void blockMergeHelper(int[] array, int t, final int s, int a, int m, int b, int p, int piv, int pCmp, int bit) {
		int l = m - a, r = b - m;
		if (l <= s || r <= s) {
			mergeStatic(array, a, m, b, t, true, 0);
			return;
		}
		// pointer from Flanlaina: handle both fragments instead
		int A = a + l % s, B = b - r % s;
		blockMergeInternal(array, t, s, A, m, B, p, piv, pCmp, bit);

		// merge fragment(s)
		mergeStatic(array, a, A, b, t, true, 0);
		mergeStatic(array, a, B, b, t, true, 0);
	}
	private void blockMerge(int[] array, int t, final int s, int a, int m, int b) {
		// [from Pure Log Merge]
		int l = m - a, r = b - m, c = (l + r + 1) / 2, med, la = 0, lb;
		if (r <= s || l <= s) {
			mergeStatic(array, a, m, b, t, true, 0);
			return;
		}

		if (r < l) {
			lb = r;
			while (la < lb) {
				int lm = (la + lb) >>> 1;
				if (Reads.compareIndices(array, m + lm, a + (c - lm) - 1, 0.25, true) <= 0) 
					la = lm + 1;
				else 
					lb = lm;
			}
			if (la == 0) med = a + c;
			else         med = Reads.compareIndices(array, m + la - 1, a + (c - la) - 1, 0.25, true) > 0 ? m + la : a + c - la;
		} else {
			lb = l;
			while (la < lb) {
				int lm = (la + lb) >>> 1;
				if (Reads.compareIndices(array, a + lm, m + (c - lm) - 1, 0.25, true) < 0) 
					la = lm + 1;
				else 
					lb = lm;
			}
			if (l == r && la == l) med = m;
			else if (la == 0)      med = m + c;
			else                   med = Reads.compareIndices(array, a + la - 1, m + (c - la) - 1, 0.25, true) >= 0 ? a + la : m + c - la;
		}
		med = array[med - 1];

		int m1 = binSearch(array, a, m, med, 0);
		int m2 = binSearch(array, m, b, med, 1);
		int ms2 = m - binSearch(array, m1, m, med, 1);
		int ms1 = binSearch(array, m, m2, med, 0) - m;

		rotate(array, m - ms2, m, m2);             //ABCABC -> ABABCC
		rotate(array, m1, m - ms2, m + ms1 - ms2); //ABABCC -> AABBCC

		blockMergeHelper(array, t, s, a, m1, m1 + ms1, a + c, med, 0, 0);
		blockMergeHelper(array, t, s, m2 - ms2, m2, b, a,   med, 1, 1);
	}

	private void redistribute(int[] array, int a, int m, int b, boolean sorted) {
		// adapted from Single-Pivot Pache and other buffered algos I've done.
		// selective heap has substantially improved it.
		int carry = 0, t = m;
		while (m - a > SPLITMERGE_MIN) {
			int kmerging = (m - a) / 2, kreal = (m - a + 1) / 2,
				m1 = a + kmerging;
			if (!sorted) {
				if (carry > 0) Writes.swap(array, m - 1, a, 1, true, false);
				velvetSort(array, a + carry, m, a + kreal);
			} else sorted = false;
			// rotate to first greater value
			int f = binSearch(array, t, b, array[a], 0);
			rotate(array, a, m, f);
			a += f - m;
			m1 += f - m;
			m = f;
			// merge lower half of keys into sorted part
			int l = a, r = m, le = m1;
			t = a + kreal;
			while (l < le && r < b) {
				if (Reads.compareValues(array[l], array[r]) <= 0)
					Writes.swap(array, t++, l++, 1, true, false);
				else
					Writes.swap(array, t++, r++, 1, true, false);
			}
			while (l < le)
				Writes.swap(array, t++, l++, 1, true, false);
			m = a + kreal;
			carry = kreal - kmerging;
		}
		if (!sorted) {
			if (carry > 0) Writes.swap(array, m - 1, a, 1, true, false);
			velvetSort(array, a + carry, m, -1);
		}
		lazyMerge(array, a, m, b);
	}

	public void vanadium(int[] array, int a, int b) {
		int W = productLog(b - a),
		    bsz = collectKeys(array, a, b, W);
		if (bsz < W || b - a <= MINSORT_N) {
			for (int i = a; i < b; i += MINSORT_N)
				insertRun(array, i, Math.min(i + MINSORT_N, b));
			for (int j = MINSORT_N; j < b - a; j *= 2)
				for (int i = a; i + j < b; i += j * 2)
					lazyMergeII(array, i, i + j, Math.min(i + 2 * j, b));
			return;
		}
		int m = a + bsz,
		    j = buildBlocks(array, a, m, b, bsz);

		// merge fragment
		int b1 = b - (b - m) % j,
		    b2 = b - (b - m) % (j / 2);
		blockMerge(array, a, bsz, b1, b2, b);

		// for my sanity, vanadium is static buffer.
		for (; j < b - m; j *= 2)
			for (int i = m; i + j < b; i += 2 * j)
				blockMerge(array, a, bsz, i, i + j, Math.min(i + 2 * j, b));

		redistribute(array, a, m, b, false);
	}

	@Override
	public void runSort(int[] array, int length, int bucketCount) {
		vanadium(array, 0, length);
	}
}
