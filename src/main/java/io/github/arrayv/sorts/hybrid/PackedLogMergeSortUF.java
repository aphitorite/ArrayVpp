package io.github.arrayv.sorts.hybrid;

import io.github.arrayv.main.ArrayVisualizer;

import io.github.arrayv.sorts.insert.BinaryInsertionSort;
import io.github.arrayv.sorts.insert.InsertionSort;
import io.github.arrayv.sorts.templates.Sort;

import java.awt.Color;


final public class PackedLogMergeSortUF extends Sort {
    public PackedLogMergeSortUF(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        
        this.setSortListName("Packed Log Merge (Uncomfortably Fancy)");
        this.setRunAllSortsName("Uncomfortably Fancy Packed Log Merge Sort");
        this.setRunSortName("Uncomfortably Fancy Packed Log Mergesort");
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
        if(v == 0) return 0;
        return 31 - Integer.numberOfLeadingZeros(v);
    }
    
    private int log(int v, int base) {
        return (int) (Math.log(v) / Math.log(base));
    }
    
    private InsertionSort is;
    
    //median selection and sort helper code for aeosqsort by Anonymous0726
    
    private int medianOf3(int[] array, int[] indices, String use) {
        // small length cases
        
        // maybe an error would be better but w/e
        if(indices.length == 0) return -1;
        
        Highlights.colorCode(use, indices);
        
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
                
        return medianOf3(array, meds, "medofmed");
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
        
        return medianOf3(array, meds, "medof3");
    }
    
    // median of 3
    private int m3(int[] array, int l0, int l1, int l2, String use) {
        Highlights.colorCode(use, l0, l1, l2);
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
    private int medianDepth(int[] array, int start, int end, int depth) {
        if(end-start < 9 || depth <= 0) {
            return m3(array, start, start+(end-start)/2, end, "medof3");
        }
        int e = (end - start) / 8;
        int m0 = medianDepth(array, start, start + 2 * e, --depth);
        int m1 = medianDepth(array, start + 3 * e, start + 5 * e, depth);
        int m2 = medianDepth(array, start + 6 * e, end, depth);
        return m3(array, m0, m1, m2, "medofdepth");
    }
    
    private void multiSwap(int[] array, int a, int b, int s) {
        for(int i=0; i<s; i++) {
            Highlights.swapColors(a+i, b+i);
            Writes.swap(array,a+i,b+i,1,true,false);
        }
    }
    
    private void arraycopy_ClearSrc(int[] src, int osrc, int[] dst, int odst, int len, double sleep, boolean mark, boolean auxwrite, String use) {
        for(int i=0; i<len; i++) {
            Highlights.colorCode(odst+i, use);
            Writes.write(dst, odst+i, src[osrc+i], sleep, mark, auxwrite);
            Writes.visualClear(src, osrc + i);
        }
    }
    
    private void arraycopy_CSNC(int[] src, int osrc, int[] dst, int odst, int len, double sleep, boolean mark, boolean auxwrite) {
        for(int i=0; i<len; i++) {
            Writes.write(dst, odst+i, src[osrc+i], sleep, mark, auxwrite);
            Writes.visualClear(src, osrc + i);
        }
    }
    
    private void reversearraycopy_CSCC(int[] src, int osrc, int[] dst, int odst, int len, double sleep, boolean mark, boolean auxwrite) {
        for(int i=len-1; i>=0; i--) {
            Highlights.writeColor(src, osrc+i, dst, odst+i);
            Writes.write(dst, odst+i, src[osrc+i], sleep, mark, auxwrite);
            Writes.visualClear(src, osrc + i);
        }
    }
    
    // swap across auxarrays
    private void swapCopy(int[] src, int srcstart, int[] dst, int dststart, int srclen, int dstlen, double sleep, boolean mark, boolean toauxwrite, String use) {
        int i=0, tmp;
        for(; i<Math.min(srclen, dstlen); i++) {
            tmp = src[srcstart+i];
            Writes.write(src, srcstart+i, dst[dststart+i], sleep/2d, mark, toauxwrite);
            Highlights.colorCode(srcstart+i, use);
            Writes.write(dst, dststart+i, tmp, sleep/2d, mark, !toauxwrite);
        }
        for(; i<srclen; i++) {
            Writes.write(dst, dststart+i, src[srcstart+i], sleep, mark, !toauxwrite);
            Writes.visualClear(src, srcstart + i);
        }
        for(; i<dstlen; i++) {
            Writes.write(src, srcstart+i, dst[dststart+i], sleep, mark, toauxwrite);
            Highlights.colorCode(srcstart+i, use);
            Writes.visualClear(dst, dststart + i);
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
    
    private void set(int[] array, int lo, int hi, int val, int log, boolean bit, boolean packed) {
        int o = packed ? Integer.bitCount(val) : 0;
        while(log-- > 0) {
            if((val % 2 == 1) == bit) {;
                Highlights.colorCode(hi + log, "tag");
                if(packed) {
                    Writes.swap(array, lo+o, hi + log, 1, true, false);
                    Highlights.colorCode(lo + o--, "tag");
                } else {
                    Writes.swap(array, lo + log, hi + log, 1, true, false);
                    Highlights.colorCode(lo + log, "tag");
                }
            }
            val /= 2;
        }
    }
    
    private int get(int[] array, int block, int piv, int log, int bias, boolean bit) {
        int v = 0, s = 1;
        while(log-- > 0) {
            v |= (bit ^ Reads.compareIndexValue(array, block+log, piv, 1, true) > -bias) ? s : 0;
            s *= 2;
        }
        return v;
    }
    
    private void merge(int[] array, int[] buf, int a, int m, int b, int t, boolean aux) {
        int l = a, r = m;
        while(l < m && r < b) {
            if(Reads.compareIndices(array, l, r, 1, true) <= 0) {
                Writes.write(buf, t++, array[l], 1, true, aux);
                Writes.visualClear(array, l++);
            } else {
                Writes.write(buf, t++, array[r], 1, true, aux);
                Writes.visualClear(array, r++);
            }
        }
        while(l < m && l < b) {
            Writes.write(buf, t++, array[l], 1, true, aux);
            Writes.visualClear(array, l++);
        }
        while(r < b) {
            Writes.write(buf, t++, array[r], 1, true, aux);
            Writes.visualClear(array, r++);
        }
    }
    
    private void merge(int[] array, int[] tmp, int a, int b, int o, boolean startaux) {
        for(int s=startaux?o:a, i=s, j=0; j<b-a; i+=8, j+=8) {
            is.customInsertSort(startaux?tmp:array, i, Math.min(i+8, s+b-a), 0.5, startaux);
        }
        boolean A = !startaux;
        for(int j=8; j<b-a; j*=2) {
            int t=A?o:a, f=A?a:o;
            for(int i=0; i<b-a; i+=2*j) {
                merge(A?array:tmp, A?tmp:array, f+i, f+i+j, Math.min(f+i+2*j, f+b-a), t+i, A);
            }
            A=!A;
        }
        if(!A) arraycopy_CSNC(tmp, o, array, a, b-a, 1, true, false);
    }
    
    private int w(int n) {
        int a = 1, b = 32;
        
        while(a < b) {
            int m = (a+b)/2;
            
            if(log(n/m-1)+1 > m) a = m+1;
            else                 b = m;
        }
        return 2 << log(a-1);
    }
    
    private int partition(int[] array, int[] tmp, int a, int b, int p, final int blk, int B, boolean pattern) {
        int lb = 0, rb = 0, l = 0, r = 0, t = a;
        boolean invert = false;
        for(int i=a; i<b; i++) {
            int cmp = Reads.compareIndexValue(array, i, p, 1, true);
            int s = cmp > -B ? r : l, u = cmp > -B ? l : r, sb = cmp > -B ? rb : lb;
            if(invert ^ cmp > -B) {
                Writes.write(tmp, s++, array[i], 0.25, true, true);
                Writes.visualClear(array, i);
                if(s == blk) {
                    if(pattern) {
                        swapCopy(array, t, tmp, 0, u, blk, 1, true, false, cmp>-B?"high":"low");
                        invert=!invert;
                    } else {
                        reversearraycopy_CSCC(array, t, array, i-u+1, l, 1, true, false);
                           arraycopy_ClearSrc(tmp, 0, array, t, blk, 1, true, false, "high");
                    }
                    sb++;
                    s = 0;
                    t += blk;
                }
            } else {
                if(t+s<i) {
                    Writes.write(array, t+s, array[i], 0.25, true, true);
                    Writes.visualClear(array, i);
                }
                Highlights.colorCode(t+s, cmp>-B?"high":"low");
                if(++s == blk) {
                    sb++;
                    s = 0;
                    t += blk;
                }
            }
            if(cmp > -B) {
                r = s;
                l = u;
                rb = sb;
            } else {
                r = u;
                l = s;
                lb = sb;
            }
        }
        int m = Math.min(lb, rb);
        if(m > 0) {
            int j, k, M = log(m-1)+1;
            j = k = a;
            for(int i = 0; i < m; i++) {
                while(Reads.compareValues(array[j+M], p) <= -B) j += blk;
                while(Reads.compareValues(array[k+M], p) > -B) k += blk;
                set(array, j, k, i, M, lb < rb, false);
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
                    int w = get(array, i, p, M, B, false);
                    while(h != w) { // index sort
                        multiSwap(array, a+w*blk, i, blk);
                        w = get(array, i, p, M, B, false);
                    }
                    set(array, i, k+h*blk, h, M, lb < rb, false); // compareless clear the block tag
                }
            } else {
                for(j = k = a; j < t; j += blk) {
                    if(Reads.compareValues(array[j+M], p) <= -B) {
                        multiSwap(array, j, k, blk);
                        k += blk;
                    }
                }
                for(int h = 0; k < t; k += blk, h++) {
                    int w = get(array, k, p, M, B, false);
                    while(h != w) {
                        multiSwap(array, k+(w-h)*blk, k, blk);
                        w = get(array, k, p, M, B, false);
                    }
                    set(array, k, a+h*blk, h, M, lb < rb, false); // compareless clear the block tag
                }
            }
        }
        if(invert) {
            if(l > 0)
                reversearraycopy_CSCC(array, a+lb*blk, array, b-r-rb*blk, r+rb*blk, 1, true, false);
            arraycopy_ClearSrc(tmp, 0, array, a+lb*blk, l, 1, true, false, "lowfrag");
        } else {
            arraycopy_ClearSrc(tmp, 0, array, b-r, r, 1, true, false, "highfrag");
            arraycopy_CSNC(array, t, tmp, 0, l, 1, true, false);
            if(l > 0)
                reversearraycopy_CSCC(array, a+lb*blk, array, b-r-rb*blk, rb*blk, 1, true, false);
            arraycopy_ClearSrc(tmp, 0, array, a+lb*blk, l, 1, true, false, "lowfrag");
        }
        return a+l+lb*blk;
    }
    private int[] quickSelect(int[] array, int[] buf, int a, int b, int r) {
        int c = -1;
        while(b-a >= 16) {
            int p = medianDepth(array, a, b, log(b-a, 6));
            int m = partition(array, buf, a, b, array[p], buf.length, 0, false);
            c = 0;
            if(m == a || m == b) {
                m = partition(array, buf, a, b, array[p], buf.length, 1, true);
                c = 1;
            }
            if(m == a || m == b) {
                return new int[] {a, b, c};
            }
            if(m > r)
                b = m;
            else
                a = m;
        }
        if(b - a < 16) {
            BinaryInsertionSort s = new BinaryInsertionSort(arrayVisualizer);
            s.customBinaryInsert(array, a, b, 1);
            a = b;
        }
        return new int[] {a, b, c};
    }
    
    // thanks to aphitorite for this updated version
    private int onesRange(int val) {
    	int r = 0;
        for(int j = 0, k = val-1; k > 0; j++, k /= 2) {
            if(k%2 == 1) r += (k/2 + 1) << j;
            else         r += (k/2 - Integer.bitCount(k/2)) << j;
        }
        return r;
    }
    private void tailMerge(int[] array, int[] buf, int start, int mid, int end, boolean cpy, String use) {
        if(cpy)
               arraycopy_CSNC(array, mid, buf, 0, end-mid, 1, true, true);
        int l=mid-1, r=end-mid-1, t=end;
        while(l >= start && r >= 0) {
            t--;
            Highlights.colorCode(t, use);
            if(Reads.compareValues(array[l], buf[r]) > 0) {
                Writes.write(array, t, array[l], 1, true, false);
                Writes.visualClear(array, l--);
            } else {
                Writes.write(array, t, buf[r], 1, true, false);
                Writes.visualClear(buf, r--);
            }
        }
        while(r >= 0) {
            Writes.write(array, --t, buf[r], 1, true, false);
            Highlights.colorCode(t, use);
            Writes.visualClear(buf, r--);
        }
    }
    private boolean ratioBad(int a, int b, int l, int r, int blk) {
        return onesRange((r-l)/blk) > b-a;
    }
    private void blockMerge(int[] array, int[] buf, int a, int m, int b, int bit, int blk, int piv, boolean invert, int bias) {
        if(Reads.compareIndices(array, m-1, m, 1, true) <= 0) {
            batchColorCode_reverse(a, b, "sorted");
            return;
        }
        int l = a, r = m, bufs[] = new int[] {a, m}, j = bit, w = 0, ml = log((b-a)/blk-1)+1;
        for(int i = 0, c = 0; i < buf.length && (l < m || r < b); i++) {
            if(l < m && (r == b || Reads.compareValues(array[l], array[r]) <= 0)) {
                Writes.write(buf, i, array[l], 1, true, true);
                Writes.visualClear(array, l++);
            } else {
                Writes.write(buf, i, array[r], 1, true, true);
                Writes.visualClear(array, r++);
            }
            if(++c == blk) {
                c = 0;
                j += Integer.bitCount(w++);
            }
        }
        while(l < m || r < b) {
            int idx = l - bufs[0] < r - bufs[1] ? 1 : 0;
            for(int c = 0; c < blk; c++) {
                Highlights.colorCode(bufs[idx], "blockmerge");
                if(l < m && (r == b || Reads.compareValues(array[l], array[r]) <= 0)) {
                    Writes.write(array, bufs[idx]++, array[l], 1, true, false);
                    Writes.visualClear(array, l++);
                } else {
                    Writes.write(array, bufs[idx]++, array[r], 1, true, false);
                    Writes.visualClear(array, r++);
                }
            }
            if(w >= (1<<ml)) // if this triggers, let me know
                System.exit(0);
            set(array, j, bufs[idx]-blk, w, ml, true, true);
            j += Integer.bitCount(w++);
        }
        int x = w = 0;
        j = bit;
        while(bufs[0] < l) {
            arraycopy_ClearSrc(buf, x, array, bufs[0], blk, 1, true, false, "blockmerge");
            x += blk;
            set(array, j, bufs[0], w, ml, true, true);
            bufs[0] += blk;
            j += Integer.bitCount(w++);
        }
        while(bufs[1] < r) {
            arraycopy_ClearSrc(buf, x, array, bufs[1], blk, 1, true, false, "blockmerge");
            x += blk;
            set(array, j, bufs[1], w, ml, true, true);
            bufs[1] += blk;
            j += Integer.bitCount(w++);
        }
        j = bit;
        for(int i=a, h=0; i<b; i+=blk, h++) {
            int dst = get(array, i, piv, ml, bias, invert);
            while(h != dst) {
                multiSwap(array, i, a+dst*blk, blk);
                dst = get(array, i, piv, ml, bias, invert);
            }
            set(array, j, i, h, ml, true, true);
            j += Integer.bitCount(h);
        }
    }
    private void sortHalf(int[] array, int[] buf, int a, int c, int bb, int blk, int piv, boolean invert, int bias) {
        int b = c - ((c - a) % blk);
        for(int i = a; i < b; i += buf.length) {
            merge(array, buf, i, Math.min(i+buf.length, b), 0, false);
        }
        for(int j = buf.length; j < b - a; j *= 2) {
            for(int i = a; i + j < b; i += 2 * j) {
                if(j > buf.length) {
                    blockMerge(array, buf, i, i + j, Math.min(i + 2 * j, b), bb, blk, piv, invert, bias);
                } else {
                    tailMerge(array, buf, i, i + j, Math.min(i + 2 * j, b), true, "merge");
                }
            }
        }
        merge(array, buf, b, c, 0, false);
        if(b < c)
            tailMerge(array, buf, a, b, c, true, "fragmerge");
    }
    private void packedLM(int[] array, int[] buf, int a, int b, int blk) {
    	int c = a, d = b;
    	do {
	        int m = medianOfMedians(array, a, b-(~(b-a)&1)); // medianDepth(array, a, b-1, log(b-a, 9));
	        int piv = array[m];
	        int bp = 0;
	        int p = partition(array, buf, a, b, piv, buf.length, 0, false);
	        int[] p2 = new int[] {p, p};
	        if(p == a || p == b) {
	            p = partition(array, buf, a, b, piv, buf.length, 1, true);
	            bp = 1;
	        }
	        if(p == a || p == b)
	            return;
	        if(ratioBad(c, p, p, b, blk)) {
	            sortHalf(array, buf, a, p, p, blk, piv, false, bp);
	            a = p;
	        } else if(ratioBad(p, d, a, p, blk)) {
	            sortHalf(array, buf, p, b, a, blk, piv, true, bp);
	            b = p;
	        } else {
		        sortHalf(array, buf, a, p2[0], p2[0], blk, piv, false, bp);
		        sortHalf(array, buf, p2[1], b, c, blk, piv, true, bp);
		        batchColorCode_reverse(c, d, "sorted");
		        break;
	        }
    	} while(true);
    }
    public void packedLogMerge(int[] array, int a, int b, boolean useLog) {
        Highlights.retainColorMarks(true);
        Highlights.defineColor("medof3", new Color(142, 225, 225));
        Highlights.defineColor("medofmed", new Color(142, 142, 255));
        Highlights.defineColor("medofdepth", new Color(142, 142, 142));
        Highlights.defineColor("low", new Color(255, 245, 142));
        Highlights.defineColor("high", new Color(142, 255, 152));
        Highlights.defineColor("lowfrag", new Color(255, 142, 142));
        Highlights.defineColor("highfrag", new Color(255, 142, 255));
        Highlights.defineColor("tag", new Color(114, 204, 156));
        Highlights.defineColor("blockmerge", new Color(255, 200, 142));
        Highlights.defineColor("merge", new Color(200, 254, 142));
        Highlights.defineColor("fragmerge", new Color(142, 188, 255));
        Highlights.defineColor("sorted", new Color(0, 255, 0));
        
        int lg = useLog ? log(b-a-1) + 1 : w(b-a);
        int[] buf = Writes.createExternalArray(2 * lg);
        is = new InsertionSort(arrayVisualizer);
        packedLM(array, buf, a, b, lg);
        Writes.deleteExternalArray(buf);
    }
    @Override
    public void runSort(int[] array, int nmemb, int bucketCount) {
        packedLogMerge(array, 0, nmemb, true);
    }
}