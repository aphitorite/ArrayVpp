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

final public class UnstableMicrokitaSort extends Sort {
	public UnstableMicrokitaSort(ArrayVisualizer arrayVisualizer) {
		super(arrayVisualizer);
		
		this.setSortListName("Unstable Microkita");
		this.setRunAllSortsName("Unstable Microkita Sort");
		this.setRunSortName("Unstable Microkitasort");
		this.setCategory("Hybrid Sorts");
		this.setConstant("n log n");
		this.setBucketSort(false);
		this.setRadixSort(false);
		this.setUnreasonablySlow(false);
		this.setUnreasonableLimit(0);
		this.setBogoSort(false);
	}
	/*
	 * Unstable Microkita: Unstable Kita, golfed down to only 2x sqrt n buffer
	 */

	private static final int MINSORT = 16;
	
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
	
	private void blockSwap(int[] array, int a, int b, int s) {
		if (a != b) for (; s-- > 0;) Writes.swap(array, a++, b++, 0.5, true, false);
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
	private void velvetSort(int[] array, int a, int b) {
		if (a >= b - 1) return;
		build(array, a, b);
	    for (int i = a + 1; i < b - 1; i++)
	    	sift(array, a, i, b, array[i], 0);
	}

	private int medOf3(int[] array, int a, int b, int c) {
		int d;
		if (Reads.compareIndices(array, a, b, 0.5, true) > 0) {
			d = b; b = a;
		} else
			d = a;
		if (Reads.compareIndices(array, b, c, 0.5, true) > 0) {
			if (Reads.compareIndices(array, d, c, 0.5, true) > 0) return d;
			return c;
		}
		return b;
	}
	private int ninther(int[] array, int a, int b) {
		if (b - a <= 9) return a + (b - a) / 2;
		int len = b - a, half = len / 2, quart = len / 4, eight = len / 8;
		int c = medOf3(array, a, a + eight, a + quart);
		int d = medOf3(array, a + quart + eight, a + half, a + half + eight);
		int e = medOf3(array, b - quart, b - eight, b - 1);
		return medOf3(array, c, d, e);
	}
	private int pseudomo27(int[] array, int a, int b) {
		if (b - a < 64) return ninther(array, a, b);
		int d = (b - a + 1) / 8;
		int m0 = ninther(array, a, a + 2 * d);
		int m1 = ninther(array, a + 3 * d, a + 5 * d);
		int m2 = ninther(array, a + 6 * d, b);
		return medOf3(array, m0, m1, m2);
	}
	private int gaprank(int[] array, int a, int b, int g, int r) {
		int re = 0;
		while (a < b) {
			if (a != r && Reads.compareIndices(array, a, r, 0.25, true) < 0) re++;
			a += g;
		}
		return re;
	}
	private int median(int[] array, int a, int b) {
		int s = 1;
		while (s * s < b - a) s *= 2;

		if ((s /= 2) < 2) return ninther(array, a, b);
		int mid = (b - a - 1) / s / 2 + 1, e = (b - a) / 8, cm = a + (b - a) / 2, cr = 0;

		for (int i = 0; i < e; i += s) {
			int p = pseudomo27(array, a + i, b - e + i), r = gaprank(array, a, b, s, p);
			if (__abs(cr - mid) > __abs(r - mid)) {
				cm = p;
				cr = r;
			}
		}
		return cm;
	}

	private int[] partition(int[] array, int a, int b, int p) {
		b--;
		int A, B;
		int c = A = a, d = B = b, c1 = 0, d1 = 0, C = 0;
		for (;;) {
			// find next out-of-place element
			while (a <= b && (C = Reads.compareIndexValue(array, a, p, 0.5, true)) <= 0) {
				if (C == 0) { // swap to c if equal to pivot
					Writes.swap(array, c++, a, 0.25, true, false);
					c1++;
				}
				a++;
			}
			// find next out-of-place element
			while (a <= b && (C = Reads.compareIndexValue(array, b, p, 0.5, true)) >= 0) {
				if (C == 0) { // swap to d if equal to pivot
					Writes.swap(array, d--, b, 0.25, true, false);
					d1++;
				}
				b--;
			}
			if (a == b) b--;
			if (a < b)
				// swap both elements
				Writes.swap(array, a++, b--, 1, true, false);
			else {
				if (b - c >= c1) // transport equals to middle left
					for (int i = c; c1-- > 0;)
						Writes.swap(array, b--, --i, 0.1, true, false);
				else { // transport inequals to left
					for (int i = A, j = c; j <= b;)
						Writes.swap(array, i++, j++, 0.1, true, false);
					b -= c1;
				}
				if (d - a >= d1) // transport equals to middle right
					for (int i = d; d1-- > 0;)
						Writes.swap(array, a++, ++i, 0.1, true, false);
				else { // transport inequals to right
					for (int i = B, j = d; j >= a;)
						Writes.swap(array, i--, j--, 0.1, true, false);
					a += d1;
				}
				return new int[] {b + 1, a - 1};
			}
		}
	}
	private int[] quickselect(int[] array, int a, int b, int r) {
		int j = 0, m[];
		boolean bad = false;
		while (b - a > MINSORT) {
			int p = bad ? median(array, a, b) : pseudomo27(array, a, b);
			m = partition(array, a, b, array[p]);
			bad = (m[0] - a) * 8 <= b - a || (b - m[1]) * 8 < b - a;

			if (m[0] <= r && r <= m[1]) return new int[] {j, m[0], m[1]};
			else if (r < m[0])          b = m[0];
			else                        a = m[1] + 1;
			j++;
		}
		insertRun(array, a, b);
		return new int[] {a, b};
	}
	
	private void mergeStatic(int[] array, int a, int m, int b, int t, boolean copyBack) {
		int i = a, j = m, T = t;
		while (i < m && j < b)
			if (Reads.compareIndices(array, i, j, 1, true) <= 0)
				Writes.swap(array, T++, i++, 0.5, true, false);
			else
				Writes.swap(array, T++, j++, 0.5, true, false);
		if (copyBack) {
			while (i < m) Writes.swap(array, b - m + i, i++, 1.5, true, false);
			blockSwap(array, t, a, T - t);
		} else {
			while (i < m) Writes.swap(array, T++, i++, 1.5, true, false);
			while (j < b) Writes.swap(array, T++, j++, 1.5, true, false);
		}
	}
	private void tailMerge(int[] array, int a, int m, int b, int t, boolean copy) {
		if (copy) blockSwap(array, m, t, b - m);
		int i = m - 1, j = t + b - m - 1;
		while (i >= a && j >= t)
			if (Reads.compareIndices(array, i, j, 1, true) > 0)
				Writes.swap(array, --b, i--, 0.5, true, false);
			else
				Writes.swap(array, --b, j--, 0.5, true, false);
		while (j >= t) Writes.swap(array, --b, j--, 0.5, true, false);
	}
	
	private int pingPongHalves(int[] array, int a, int b, int t, int tmax) {
		int T = MINSORT;
		
		for (int i = a; i < b; i += T)
			insertRun(array, i, Math.min(i + T, b));
		
		for (; T <= tmax / 2; T *= 4)
			for (int i = a; i + T < b; i += 4 * T)
				if (i + 3 * T >= b) {
					mergeStatic(array, i, i + T, Math.min(i + 2 * T, b), t, true);
					if (i + 2 * T < b) tailMerge(array, i, i + 2 * T, b, t, true);
				} else {
					int m = i + 2 * T, r = Math.min(i + 4 * T, b);
					mergeStatic(array, i, i + T, m, t, false);
					mergeStatic(array, m, m + T, r, i, false);
					tailMerge(array, i, i + r - m, r, t, false);
				}
		
		if (T == tmax) {
			for (int i = a; i + T < b; i += 2 * T)
				tailMerge(array, i, i + T, Math.min(i + 2 * T, b), t, true);
			return T * 2;
		}
		return T;
	}
	
	private boolean blockLess(int[] array, int A, int a, int b, final int s) {
		int c1 = Reads.compareIndices(array, A + a * s, A + b * s, 0.05, true);
		return c1 > 0 || (c1 == 0 && Reads.compareIndices(array, A + (a + 1) * s - 1, A + (b + 1) * s - 1, 0.05, true) > 0);
	}
	private int findNext(int[] array, int a, int b, int k, int p, int lv, final int s) {
		int m = -1;
		for (int i = 0; i < (b - a) / s; i++)
			if (a + i * s != lv && Reads.compareIndexValue(array, k + i, p, 0.1, true) <= 0 && (m < 0 || blockLess(array, a, m, i, s)))
				m = i;
		return m < 0 ? m : a + m * s;
	}
	private int incrptr(int[] array, int a, int b, int k, int p, int P, final int s) {
		if ((++P - a) % s == 0) {
			int n = findNext(array, a, b, k, p, P - s, s);
			return n < 0 ? -P : n;
		} else return P;
	}
	private int incrbuf(int[] array, int a, int b, int k, int p, int B, int P, final int s) {
		if (B >= 0 && (B - a) % s == 0)
			Writes.swap(array, B, k + (B - a) / s, 5, true, false);
		if ((++B - a) % s == 0)
			return P - (P - a) % s;
		else return B;
	}
	private void blockMerge(int[] array, int a, int m, int b, int k, int t, int p, final int s) {
		int K = k + (m - a) / s;
		int i = findNext(array, a, m, k, p, -1, s), j = findNext(array, m, b, K, p, -1, s),
		    l = i, r = j;
		for (int c = 0; c < s; c++)
			if (Reads.compareIndices(array, i, j, 0.5, true) <= 0) {
				Writes.swap(array, t + c, i, 1, true, false);
				i = incrptr(array, a, m, k, p, i, s);
			} else {
				Writes.swap(array, t + c, j, 1, true, false);
				j = incrptr(array, m, b, K, p, j, s);
			}
		while (l >= 0 && r >= 0) {
			boolean L = i != l && (i < 0 || l % s == i % s || Reads.compareIndices(array, l + s - 1, r + s - 1, 1, true) <= 0);
			for (int c = 0; c < s; c++) {
				boolean CL = i >= 0 && (j < 0 || Reads.compareIndices(array, i, j, 0.5, true) <= 0);
				Writes.swap(array, L ? l : r, CL ? i : j, 1, true, false);
				if (L) l = incrbuf(array, a, m, k, p, l, i, s);
				else   r = incrbuf(array, m, b, K, p, r, j, s);
				if (CL) i = incrptr(array, a, m, k, p, i, s);
				else    j = incrptr(array, m, b, K, p, j, s);
			}
		}
		if (l >= 0) blockSwap(array, t, l, s);
		else        blockSwap(array, t, r, s);
		for (int kv = 0; kv < (b - a) / s; kv++) {
			if (a + kv * s == l || a + kv * s == r) continue;
			if (Reads.compareIndexValue(array, k + kv, p, 0.5, true) > 0)
				Writes.swap(array, k + kv, a + kv * s, 5, true, false);
		}
	}
	public void usMicroKita(int[] array, int a, int b) {
		int bLen = 1 << ((32 - Integer.numberOfLeadingZeros(b - a - 1)) / 2);
		int k = a + bLen, tLen = (b - a) / bLen - 2,
			a1 = k + tLen;
		int[] v = quickselect(array, a, b, a1);
		int a2 = v[v.length - 2], a3 = v[v.length - 1];
		if (v.length == 2)
			while (Reads.compareIndices(array, a1, a3, 1, true) == 0) a3++;
		
		int p = array[a1], b1 = b - (b - a3) % bLen;
		
		for (int j = pingPongHalves(array, a3, b1, a, bLen); j <= b1 - a3; j *= 2)
			for (int i = a3; i + j < b1; i += 2 * j)
				blockMerge(array, i, i + j, Math.min(i + 2 * j, b1), k, a, p, bLen);
		
		int ttLen = (b1 - a3) / bLen;
		for (int i = 0; i < ttLen - 1; i++) {
			int mi = i;
			for (int ii = i + 1; ii < ttLen; ii++)
				if (blockLess(array, a3, mi, ii, bLen)) mi = ii;
			if (mi > i) blockSwap(array, a3 + i * bLen, a3 + mi * bLen, bLen);
		}
		
		if (b1 < b) {
			pingPongHalves(array, b1, b, a, bLen);
			tailMerge(array, a3, b1, b, a, true);
		}
		velvetSort(array, a, a2);
	}
	
	@Override
	public void runSort(int[] array, int length, int bucketCount) {
		usMicroKita(array, 0, length);
	}
}