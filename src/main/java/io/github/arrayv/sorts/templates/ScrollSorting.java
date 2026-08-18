package io.github.arrayv.sorts.templates;

import java.util.Random;

import io.github.arrayv.main.ArrayVisualizer;

public abstract class ScrollSorting extends Sort {
    protected ScrollSorting(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
    }

    protected static final int MIN_SORT_LENGTH = 3072;
    protected static final double DELAY = 0.25;
    protected static final double SWAP_DELAY = 2 * DELAY;

    protected boolean auxWrite() {
        return false;
    }

    protected final Random rand = new Random();

    protected static double log2(double x) {
        return Math.log(x) / Math.log(2.0);
    }

    protected static int ceilLog2(int x) {
        return (x <= 1) ? 0 : 32 - Integer.numberOfLeadingZeros(x - 1);
    }

    protected static int floorLog2(int x) {
        return 31 - Integer.numberOfLeadingZeros(x);
    }

    protected static int gValue(int m) {
        return (int) Math.pow(2.0, Math.ceil(log2(log2(m))));
    }

    protected int scrollBufferReq(int m) {
        int g = gValue(m);
        double lm = log2(m);
        int s = 2 * (int) Math.ceil((lm * lm) / 4.0) * g - 1;
        int snMax = (m * 2) / s;
        if (snMax < 2) return m;
        int snp = 1 << floorLog2(snMax);
        int rn = snp * 8;
        return ((s * (snMax + 1)) >> 1) + rn + Math.max((s >> 1) + 1, rn * (g << 1));
    }

    protected int scrollActive(int n) {
        int l = 0, r = n;
        while (l < r) {
            int mid = (l + r + 1) >> 1;
            if (mid + scrollBufferReq(mid) < n) l = mid;
            else r = mid - 1;
        }
        return l;
    }

    protected int scrollBitVectorReq(int m) {
        int g = gValue(m);
        double lm = log2(m);
        int s = 2 * (int) Math.ceil((lm * lm) / 4.0) * g - 1;
        int snMax = (m * 2) / s;
        if (snMax < 2) return 0;
        int p = ceilLog2(snMax);
        int v = (int) Math.ceil(log2(lm));
        int snp = 1 << floorLog2(snMax);
        int rn = snp * 8;
        return rn * p + rn * v;
    }

    protected void cyclicResolve(int[] arr, int[] ind, int l, int n) {
        for (int i = 0; i < n; i++) {
            int nextIdx = ind[i];
            if (nextIdx != i) {
                int temp = arr[l + i];
                int curr = i;
                while (nextIdx != i) {
                    Writes.write(arr, l + curr, arr[l + nextIdx], DELAY, true, auxWrite());
                    ind[curr] = curr;
                    curr = nextIdx;
                    nextIdx = ind[curr];
                }
                Writes.write(arr, l + curr, temp, DELAY, true, auxWrite());
                ind[curr] = curr;
            }
        }
    }

    protected void bufferedResolve(int[] arr1, int[] arr2, int[] ind, int l1, int l2, int n) {
        for (int i = 0; i < n; i++) {
            Writes.swap(arr1, l1 + ind[i], l2 + i, SWAP_DELAY, true, auxWrite());
        }
    }

    protected final class TableMergeInsertionSort {
        private final int[] arr;
        private final int l;

        TableMergeInsertionSort(int[] arr, int l) {
            this.arr = arr;
            this.l = l;
        }

        private boolean cmp(int a, int b) {
            int c = Reads.compareIndices(arr, l + a, l + b, DELAY, true);
            if (a < b) return c <= 0;
            return c < 0;
        }

        private void blockSwap(int[] array, int a, int b, int s) {
            while (s > 0) {
                s--;
                int temp = array[a];
                array[a] = array[b];
                array[b] = temp;
                a--;
                b--;
            }
        }

        private void blockInsert(int[] array, int a, int b, int s) {
            while (a - s >= b) {
                blockSwap(array, a - s, a, s);
                a -= s;
            }
        }

        private void blockReversal(int[] array, int a, int b, int s) {
            b -= s;
            while (b > a) {
                blockSwap(array, a, b, s);
                a += s;
                b -= s;
            }
        }

        private int blockSearch(int[] array, int a, int b, int s, int val) {
            while (a < b) {
                int m = a + (((b - a) / s) / 2) * s;
                if (cmp(val, array[m])) {
                    b = m;
                } else {
                    a = m + s;
                }
            }
            return a;
        }

        private void order(int[] array, int a, int b, int s) {
            int i = a;
            int j = i + s;
            while (j < b) {
                blockInsert(array, j, i, s);
                i += s;
                j += 2 * s;
            }
            int m = a + (((b - a) / s) / 2) * s;
            blockReversal(array, m, b, s);
        }

        public void sort(int[] array, int length) {
            int k = 1;
            while (2 * k <= length) {
                int i = 2 * k - 1;
                while (i < length) {
                    if (cmp(array[i], array[i - k])) {
                        blockSwap(array, i - k, i, k);
                    }
                    i += 2 * k;
                }
                k *= 2;
            }

            while (k > 0) {
                int a = k - 1;
                int i = a + 2 * k;
                int g = 2;
                int p = 4;
                while (i + 2 * k * g - k <= length) {
                    order(array, i, i + 2 * k * g - k, k);
                    int b = a + k * (p - 1);
                    i += k * g - k;
                    int j = i;
                    while (j < i + k * g) {
                        blockInsert(array, j, blockSearch(array, a, b, k, array[j]), k);
                        j += k;
                    }
                    i += k * g + k;
                    g = p - g;
                    p *= 2;
                }
                while (i < length) {
                    blockInsert(array, i, blockSearch(array, a, i, k, array[i]), k);
                    i += 2 * k;
                }
                k /= 2;
            }
        }
    }

    protected final class KWayMergeSort {
        private final int k;
        private final int[] ind;
        private final int[] rL;
        private final int[] rR;

        private int[][] arr;
        private int[] lArr;
        private int side;

        private int[] activeArr;
        private int activeL;
        private int rLen;

        public KWayMergeSort(int k) {
            this.k = k;
            this.ind = new int[k << 1];
            this.rL = new int[k];
            this.rR = new int[k];
        }

        public void sort(int[] array, int al, int ar, int[] barr, int bl, int o) {
            this.arr = new int[][] { array, barr };
            this.lArr = new int[] { al, bl };
            int n = ar - al;
            if (n < 2) return;
            int r = calcMinRun(n, k);
            int pn = calcMergePasses(n, r, k);
            int i = 0;
            while (true) {
                int rl = i * r;
                int rr = Math.min(rl + r, n);
                int rp = rr - rl;
                for (int j = 0; j < rp; j++) ind[j] = j;
                new TableMergeInsertionSort(arr[0], lArr[0] + rl).sort(ind, rp);
                if (((pn & 1) ^ o) != 0) {
                    bufferedResolve(arr[0], arr[1], ind, lArr[0] + rl, lArr[1] + rl, rp);
                    side = 1;
                } else {
                    cyclicResolve(arr[0], ind, lArr[0] + rl, rp);
                    side = 0;
                }
                if (rr == n) break;
                i++;
            }
            while (r < n) {
                int newR = r * k;
                i = 0;
                while (true) {
                    int gL = i * newR;
                    int gR = Math.min(gL + newR, n);
                    if (gL == gR) break;
                    kWayMerge(arr[side], lArr[side], arr[side ^ 1], lArr[side ^ 1], gL, gR, r);
                    if (gR == n) break;
                    i++;
                }
                side ^= 1;
                r = newR;
            }
        }

        private int calcMinRun(int n, int k) {
            int r = 0;
            while (n >= (k << 1)) {
                r |= n & 1;
                n >>= 1;
            }
            return n + r;
        }

        private int calcMergePasses(int n, int r, int k) {
            double rn = (n - 1) / r + 1;
            int p = 0;
            while (rn > 1) {
                rn /= k;
                p++;
            }
            return p;
        }

        private void kWayMerge(int[] arr1, int l1, int[] arr2, int l2, int l, int r, int rLen) {
            int n = r - l;
            int rn = (n - 1) / rLen + 1;
            activeArr = arr1;
            activeL = l1 + l;
            this.rLen = rLen;
            for (int i = 0; i < rn; i++) {
                rL[i] = 0;
                rR[i] = rLen;
            }
            rR[rn - 1] = n - rLen * (rn - 1);
            for (int i = 0; i < rn; i++) {
                ind[k + i] = i;
            }
            for (int i = rn; i < k; i++) {
                ind[k + i] = -1;
            }
            for (int i = k - 1; i > 0; i--) {
                updateVal(i);
            }
            int root = 1;
            while (root < k && ind[(root << 1) + 1] == -1) {
                root = root << 1;
            }
            int i = l2 + l;
            while (ind[root] != -1) {
                int winner = ind[root];
                int winnerIdx = activeL + winner * rLen + rL[winner];
                int winnerTreeIdx = k + winner;
                Writes.swap(arr1, winnerIdx, i, SWAP_DELAY, true, auxWrite());
                rL[winner]++;
                if (rL[winner] == rR[winner]) {
                    ind[winnerTreeIdx] = -1;
                }
                while (winnerTreeIdx != root) {
                    winnerTreeIdx = winnerTreeIdx >> 1;
                    updateVal(winnerTreeIdx);
                }
                i++;
            }
        }

        private int minVal(int a, int b) {
            if (b == -1) return a;
            if (a == -1) return b;
            if (Reads.compareIndices(activeArr, activeL + b * rLen + rL[b],
                    activeL + a * rLen + rL[a], DELAY, true) < 0) {
                return b;
            }
            return a;
        }

        private void updateVal(int pos) {
            ind[pos] = minVal(ind[pos << 1], ind[(pos << 1) + 1]);
        }
    }

    protected final class MedianOfMediansSelector {
        private final int bfprtGroupSize = 5;
        private final int th = 16;



        private void insertionSort(int[] arr, int l, int r) {
            for (int i = l + 1; i < r; i++) {
                int temp = arr[i];
                int j = i;
                while (j > l && Reads.compareIndexValue(arr, j - 1, temp, DELAY, true) > 0) {
                    Writes.write(arr, j, arr[j - 1], DELAY, true, auxWrite());
                    j--;
                }
                Writes.write(arr, j, temp, DELAY, true, auxWrite());
            }
        }

        private int getMedianIndex(int[] arr, int l, int r) {
            int n = r - l;
            if (n <= 1) return l;

            if (n == 2) {
                return Reads.compareIndices(arr, l + 1, l, DELAY, true) < 0 ? l : l + 1;
            }

            if (n == 3) {
                int i0 = l, i1 = l + 1, i2 = l + 2;
                if (Reads.compareIndices(arr, i1, i0, DELAY, true) < 0) {
                    if (Reads.compareIndices(arr, i2, i1, DELAY, true) < 0) return i1;
                    return Reads.compareIndices(arr, i2, i0, DELAY, true) < 0 ? i2 : i0;
                } else {
                    if (Reads.compareIndices(arr, i2, i0, DELAY, true) < 0) return i0;
                    return Reads.compareIndices(arr, i2, i1, DELAY, true) < 0 ? i2 : i1;
                }
            }

            if (n == 4) {
                int i0 = l, i1 = l + 1;
                int i2 = l + 2, i3 = l + 3;
                if (Reads.compareIndices(arr, i1, i0, DELAY, true) < 0) {
                    int t = i0;
                    i0 = i1;
                    i1 = t;
                }
                if (Reads.compareIndices(arr, i3, i2, DELAY, true) < 0) {
                    int t = i2;
                    i2 = i3;
                    i3 = t;
                }
                if (Reads.compareIndices(arr, i2, i0, DELAY, true) < 0) {
                    int t0 = i0, t1 = i1, t2 = i2, t3 = i3;
                    i0 = t2;
                    i1 = t3;
                    i2 = t0;
                    i3 = t1;
                }
                if (Reads.compareIndices(arr, i1, i2, DELAY, true) < 0) return i2;
                else if (Reads.compareIndices(arr, i3, i1, DELAY, true) < 0) return i3;
                else return i1;
            }

            if (n == 5) {
                int ia = l, ib = l + 1;
                int ic = l + 2, id = l + 3;
                int ie = l + 4;
                if (Reads.compareIndices(arr, ib, ia, DELAY, true) < 0) {
                    int t = ia;
                    ia = ib;
                    ib = t;
                }
                if (Reads.compareIndices(arr, id, ic, DELAY, true) < 0) {
                    int t = ic;
                    ic = id;
                    id = t;
                }
                if (Reads.compareIndices(arr, id, ib, DELAY, true) < 0) {
                    int t = ia;
                    ia = ic;
                    ic = t;
                    t = ib;
                    ib = id;
                    id = t;
                }
                int[] chain;
                if (Reads.compareIndices(arr, ie, ib, DELAY, true) < 0) {
                    if (Reads.compareIndices(arr, ie, ia, DELAY, true) < 0) chain = new int[] { ie, ia, ib, id };
                    else chain = new int[] { ia, ie, ib, id };
                } else {
                    if (Reads.compareIndices(arr, ie, id, DELAY, true) < 0) chain = new int[] { ia, ib, ie, id };
                    else chain = new int[] { ia, ib, id, ie };
                }
                if (Reads.compareIndices(arr, ic, chain[1], DELAY, true) < 0) return chain[1];
                else if (Reads.compareIndices(arr, ic, chain[2], DELAY, true) < 0) return ic;
                else return chain[2];
            }

            insertionSort(arr, l, r);
            return l + ((r - l) >> 1);
        }

        private int hoarePartition(int[] arr, int l, int r) {
            int i = l + 1;
            int j = r - 1;
            int pivot = arr[l];

            while (true) {
                while (i <= j && Reads.compareIndexValue(arr, i, pivot, DELAY, true) < 0) i++;
                while (i <= j && Reads.compareIndexValue(arr, j, pivot, DELAY, true) > 0) j--;
                if (i >= j) break;
                Writes.swap(arr, i, j, SWAP_DELAY, true, auxWrite());
                i++;
                j--;
            }

            Writes.swap(arr, l, j, SWAP_DELAY, true, auxWrite());
            return j;
        }

        private void selectPivotFast(int[] arr, int l, int r) {
            int sampleSize = (int) Math.ceil(Math.sqrt(r - l));
            sampleSize |= 1;
            for (int i = 0; i < sampleSize; i++) {
                int sampleId = l + i + rand.nextInt(r - (l + i));
                Writes.swap(arr, l + i, sampleId, SWAP_DELAY, true, auxWrite());
            }
            int pivotIdx = selectImpl(arr, l, l + sampleSize, sampleSize >> 1);
            Writes.swap(arr, l, pivotIdx, SWAP_DELAY, true, auxWrite());
        }

        private void selectPivotAccurate(int[] arr, int l, int r) {
            int groupSize = bfprtGroupSize;
            int t = l;
            int it = l + groupSize;

            while (it <= r) {
                int groupStart = it - groupSize;
                int groupMedianIdx = getMedianIndex(arr, groupStart, it);
                Writes.swap(arr, t, groupMedianIdx, SWAP_DELAY, true, auxWrite());
                t++;
                it += groupSize;
            }

            int rem = r - (it - groupSize);
            if (rem > 0) {
                int lastStart = it - groupSize;
                int groupMedianIdx = getMedianIndex(arr, lastStart, r);
                Writes.swap(arr, t, groupMedianIdx, SWAP_DELAY, true, auxWrite());
                t++;
            }

            if (t - l > 1) {
                int pivotIdx = selectImpl(arr, l, t, (t - l - 1) / 2);
                Writes.swap(arr, l, pivotIdx, SWAP_DELAY, true, auxWrite());
            }
        }

        public int selectImpl(int[] arr, int l, int r, int rank) {
            int badPartition = 0;
            while (r - l > th) {
                if (badPartition != 0) {
                    selectPivotAccurate(arr, l, r);
                    badPartition = 0;
                } else {
                    selectPivotFast(arr, l, r);
                }
                int j = hoarePartition(arr, l, r);
                int pivotRank = j - l;
                if (rank == pivotRank) return j;
                if (rank < pivotRank) {
                    if (j - l > 3 * ((r - l) >> 2)) badPartition = 1;
                    r = j;
                } else {
                    if (r - j - 1 > 3 * ((r - l) >> 2)) badPartition = 1;
                    rank -= (pivotRank + 1);
                    l = j + 1;
                }
            }
            insertionSort(arr, l, r);
            return l + rank;
        }

        public void selectMulti(int[] arr, int l, int r, int badPartition, int[] targets) {
            if (targets.length == 0) return;
            if (r - l <= th) {
                if (l < r) insertionSort(arr, l, r);
                return;
            }
            Writes.recursion();
            if (badPartition != 0) {
                selectPivotAccurate(arr, l, r);
                badPartition = 0;
            } else {
                selectPivotFast(arr, l, r);
            }
            int j = hoarePartition(arr, l, r);
            if (j - l < (r - l) >> 2 || r - j - 1 < (r - l) >> 2) badPartition = 1;

            int leftCount = 0, rightCount = 0;
            for (int target : targets) {
                if (target < j) leftCount++;
                else if (target > j) rightCount++;
            }
            int[] leftTargets = new int[leftCount];
            int[] rightTargets = new int[rightCount];
            leftCount = 0;
            rightCount = 0;
            for (int target : targets) {
                if (target < j) leftTargets[leftCount++] = target;
                else if (target > j) rightTargets[rightCount++] = target;
            }
            selectMulti(arr, l, j, badPartition, leftTargets);
            selectMulti(arr, j + 1, r, badPartition, rightTargets);
        }
    }

    protected final class PartitionHeapSort {
        private final int th = 64;
        private int t = 0;
        private int n = 0;

        public void sort(int[] arr, int l, int r) {
            int badPartition = 0;

            while (r - l > th) {
                int j;

                if (badPartition == 1) {
                    new MedianOfMediansSelector().selectPivotAccurate(arr, l, r);
                } else {
                    new MedianOfMediansSelector().selectPivotFast(arr, l, r);
                }

                j = hoarePartition(arr, l, r);

                if (j - l < r - j) {
                    if (j - l < (r - l) >> 2) badPartition = 1;
                    else badPartition = 0;

                    t = l - 1;
                    n = j - l;
                    int i = r - 1;

                    int cnt = n / 2;
                    while (cnt > 0) {
                        int temp = arr[t + cnt];
                        maxSiftDown(arr, cnt, temp);
                        cnt--;
                    }

                    cnt = n;
                    while (cnt > 1) {
                        cnt--;
                        int temp = arr[i];
                        Writes.write(arr, i, arr[l], DELAY, true, auxWrite());
                        maxSendDown(arr, 1, temp);
                        i--;
                    }

                    Writes.swap(arr, l, i, SWAP_DELAY, true, auxWrite());
                    Writes.swap(arr, j, i - 1, SWAP_DELAY, true, auxWrite());
                    r = i - 1;
                } else {
                    if (r - j < (r - l) >> 2) badPartition = 1;
                    else badPartition = 0;

                    j++;
                    t = j - 1;
                    n = r - j;
                    int i = l;

                    int cnt = n / 2;
                    while (cnt > 0) {
                        int temp = arr[t + cnt];
                        minSiftDown(arr, cnt, temp);
                        cnt--;
                    }

                    cnt = n;
                    while (cnt > 1) {
                        cnt--;
                        int temp = arr[i];
                        Writes.write(arr, i, arr[j], DELAY, true, auxWrite());
                        minSendDown(arr, 1, temp);
                        i++;
                    }

                    Writes.swap(arr, i, j, SWAP_DELAY, true, auxWrite());
                    Writes.swap(arr, i + 1, j - 1, SWAP_DELAY, true, auxWrite());
                    l = i + 2;
                }
            }

            insertionSort(arr, l, r);
        }

        private int hoarePartition(int[] arr, int l, int r) {
            int i = l + 1;
            int j = r - 1;
            int pivot = arr[l];

            while (true) {
                while (i <= j && Reads.compareIndexValue(arr, i, pivot, DELAY, true) > 0) i++;
                while (i <= j && Reads.compareIndexValue(arr, j, pivot, DELAY, true) < 0) j--;
                if (i >= j) break;
                Writes.swap(arr, i, j, SWAP_DELAY, true, auxWrite());
                i++;
                j--;
            }

            Writes.swap(arr, j, l, SWAP_DELAY, true, auxWrite());
            return j;
        }

        private void minSiftDown(int[] arr, int root, int temp) {
            int pos = root;
            int c = pos * 2;
            while (c < n) {
                if (Reads.compareIndices(arr, t + c + 1, t + c, DELAY, true) < 0) c++;
                if (Reads.compareIndexValue(arr, t + c, temp, DELAY, true) < 0) {
                    Writes.write(arr, t + pos, arr[t + c], DELAY, true, auxWrite());
                    pos = c;
                    c = pos * 2;
                } else {
                    break;
                }
            }
            if (c == n && Reads.compareIndexValue(arr, t + c, temp, DELAY, true) < 0) {
                Writes.write(arr, t + pos, arr[t + c], DELAY, true, auxWrite());
                pos = c;
            }
            Writes.write(arr, t + pos, temp, DELAY, true, auxWrite());
        }

        private void maxSiftDown(int[] arr, int root, int temp) {
            int pos = root;
            int c = pos * 2;
            while (c < n) {
                if (Reads.compareIndices(arr, t + c, t + c + 1, DELAY, true) < 0) c++;
                if (Reads.compareIndexValue(arr, t + c, temp, DELAY, true) > 0) {
                    Writes.write(arr, t + pos, arr[t + c], DELAY, true, auxWrite());
                    pos = c;
                    c = pos * 2;
                } else {
                    break;
                }
            }
            if (c == n && Reads.compareIndexValue(arr, t + c, temp, DELAY, true) > 0) {
                Writes.write(arr, t + pos, arr[t + c], DELAY, true, auxWrite());
                pos = c;
            }
            Writes.write(arr, t + pos, temp, DELAY, true, auxWrite());
        }

        private void minSendDown(int[] arr, int root, int temp) {
            int pos = root;
            int c = pos * 2;
            while (c < n) {
                if (Reads.compareIndices(arr, t + c + 1, t + c, DELAY, true) < 0) c++;
                Writes.write(arr, t + pos, arr[t + c], DELAY, true, auxWrite());
                pos = c;
                c = pos * 2;
            }
            if (c == n) {
                Writes.write(arr, t + pos, arr[t + c], DELAY, true, auxWrite());
                pos = c;
            }
            Writes.write(arr, t + pos, temp, DELAY, true, auxWrite());
        }

        private void maxSendDown(int[] arr, int root, int temp) {
            int pos = root;
            int c = pos * 2;
            while (c < n) {
                if (Reads.compareIndices(arr, t + c, t + c + 1, DELAY, true) < 0) c++;
                Writes.write(arr, t + pos, arr[t + c], DELAY, true, auxWrite());
                pos = c;
                c = pos * 2;
            }
            if (c == n) {
                Writes.write(arr, t + pos, arr[t + c], DELAY, true, auxWrite());
                pos = c;
            }
            Writes.write(arr, t + pos, temp, DELAY, true, auxWrite());
        }

        private void insertionSort(int[] arr, int l, int r) {
            int i = l + 1;
            while (i < r) {
                int temp = arr[i];
                int j = i;
                while (j > l && Reads.compareIndexValue(arr, j - 1, temp, DELAY, true) > 0) {
                    Writes.write(arr, j, arr[j - 1], DELAY, true, auxWrite());
                    j--;
                }
                Writes.write(arr, j, temp, DELAY, true, auxWrite());
                i++;
            }
        }
    }

    protected interface ScrollBitVector {
        boolean read(int pos);
        void write(int pos);
    }

    protected final class BitVector implements ScrollBitVector {
        private final int[] arr;
        private final int l;
        private final int r;
        private final int n;

        BitVector(int[] arr, int l, int r, int n) {
            this.arr = arr;
            this.l = l;
            this.r = r;
            this.n = n;
        }

        @Override
        public boolean read(int pos) {
            return Reads.compareIndices(arr, l + pos, r + pos, DELAY, true) > 0;
        }

        @Override
        public void write(int pos) {
            Writes.swap(arr, l + pos, r + pos, SWAP_DELAY, true, auxWrite());
        }
    }

    protected final class BitVectorEasy implements ScrollBitVector {
        private final int[] v;
        private final int w;

        public BitVectorEasy(int n, int w) {
            this.w = w;
            this.v = new int[(n - 1) / w + 1];
        }

        @Override
        public boolean read(int pos) {
            return ((v[pos / w] >> (pos % w)) & 1) != 0;
        }

        @Override
        public void write(int pos) {
            v[pos / w] ^= (1 << (pos % w));
        }
    }

    protected final class Scroll {
        private final int[] arr;
        private final int al;
        private final int ar;
        private final int bl;
        private final ScrollBitVector bv;
        private final KWayMergeSort smallSort;

        private final int m;
        private final int g;
        private final int s;
        private final int sMin;
        private final int snMax;

        private int sn;
        private int alpha;
        private int snp;

        private int sMinOver2;
        private int rn;
        private int p;
        private int v;
        private int sl;
        private int sr;
        private int fl;
        private int gl;
        private int pl;
        private int vl;
        private int l0;
        private int r0;

        public Scroll(int[] arr, int al, int ar, int bl, int br, ScrollBitVector bv, KWayMergeSort smallSort) {
            this.arr = arr;
            this.al = al;
            this.ar = ar;
            this.bl = bl;
            this.bv = bv;
            this.smallSort = smallSort;

            this.m = ar - al;
            this.g = gValue(m);
            double lm = log2(m);
            this.s = 2 * (int) Math.ceil((lm * lm) / 4.0) * g - 1;
            this.sMin = s / 2;
            this.sn = 0;
            this.snMax = (m * 2) / s;
            if (snMax < 2) {
                smallSort.sort(arr, al, ar, arr, bl, 0);
                return;
            }
            this.alpha = 0;
            this.snp = 1;
            int snpMax = 1 << floorLog2(snMax);
            this.rn = snpMax << 3;
            this.p = ceilLog2(snMax);
            this.v = (int) Math.ceil(log2(lm));
            this.sl = bl;
            this.sr = sl + (((snMax + 1) >> 1) * s);
            this.fl = sr;
            this.gl = fl + rn;
            this.pl = 0;
            this.vl = pl + rn * p;
            this.l0 = 0;
            this.r0 = s;

            int segL = seg(0);
            smallSort.sort(arr, al, al + sMin, arr, segL, 1);
            l0 = sMin;
            int i = sMin;
            while (i < m) {
                insert(al + i);
                i++;
            }
            i = 0;
            for (int j = 0; j < snp; j++) {
                int gapL = j << 3;
                int k = 0;
                while (isActive(frame(gapL + k))) {
                    int rep = gapL + k;
                    int seg = readVal(ptr(rep), ptr(rep) + p);
                    writeVal(cint(rep), cint(rep) + v, 0);
                    cacheFlush(rep, seg(seg) + segSize(seg));
                    repExchangeNoCache(i, rep);
                    i++;
                    k++;
                }
            }
            for (int ii = sn; ii > ((snMax - 1) >> 1); ii--) {
                int curr = ii;
                int nextIdx = readVal(ptr(curr - 1), ptr(curr - 1) + p);
                while (nextIdx != ii) {
                    segExchange(curr, nextIdx);
                    writeVal(ptr(curr - 1), ptr(curr - 1) + p, curr);
                    curr = nextIdx;
                    nextIdx = readVal(ptr(curr - 1), ptr(curr - 1) + p);
                }
                if (curr != ii) {
                    writeVal(ptr(curr - 1), ptr(curr - 1) + p, curr);
                }
            }
            i = ar;
            for (int rep = sn - 1; rep >= 0; rep--) {
                int seg = readVal(ptr(rep), ptr(rep) + p);
                writeVal(ptr(rep), ptr(rep) + p, 0);
                i = segFlush(seg, i);
                i--;
                Writes.swap(arr, i, frame(rep), SWAP_DELAY, true, auxWrite());
            }
            i = segFlush(0, i);
        }

        private int frame(int i) {
            return fl + i;
        }

        private int cache(int i) {
            return gl + i * (g << 1);
        }

        private int seg(int i) {
            if (i < (snMax + 1) >> 1) return sl + i * s;
            return al + (i - ((snMax + 1) >> 1)) * s;
        }

        private int ptr(int i) {
            return pl + i * p;
        }

        private int cint(int i) {
            return vl + i * v;
        }

        private boolean isActive(int i) {
            return Reads.compareIndices(arr, i, ar, DELAY, true) < 0;
        }

        private void repExchange(int i, int j) {
            if (i == j) return;
            Writes.swap(arr, frame(i), frame(j), SWAP_DELAY, true, auxWrite());
            int iCl = cache(i);
            int jCl = cache(j);
            for (int gI = 0; gI < (g << 1); gI++) {
                Writes.swap(arr, iCl + gI, jCl + gI, SWAP_DELAY, true, auxWrite());
            }
            int iP = ptr(i);
            int jP = ptr(j);
            for (int pI = 0; pI < p; pI++) {
                boolean iB = bv.read(iP + pI);
                boolean jB = bv.read(jP + pI);
                if (iB != jB) {
                    bv.write(iP + pI);
                    bv.write(jP + pI);
                }
            }
            int iVl = cint(i);
            int jVl = cint(j);
            for (int vI = 0; vI < v; vI++) {
                boolean iB = bv.read(iVl + vI);
                boolean jB = bv.read(jVl + vI);
                if (iB != jB) {
                    bv.write(iVl + vI);
                    bv.write(jVl + vI);
                }
            }
        }

        private void repExchangeNoCache(int i, int j) {
            if (i == j) return;
            Writes.swap(arr, frame(i), frame(j), SWAP_DELAY, true, auxWrite());
            int iP = ptr(i);
            int jP = ptr(j);
            for (int pI = 0; pI < p; pI++) {
                boolean iB = bv.read(iP + pI);
                boolean jB = bv.read(jP + pI);
                if (iB != jB) {
                    bv.write(iP + pI);
                    bv.write(jP + pI);
                }
            }
        }

        private void segExchange(int i, int j) {
            int seg1L = seg(i);
            int seg2L = seg(j);
            for (int k = 0; k < s; k++) {
                Writes.swap(arr, seg1L + k, seg2L + k, SWAP_DELAY, true, auxWrite());
            }
        }

        private int readVal(int l, int r) {
            int res = 0;
            int d = 1;
            for (int i = l; i < r; i++) {
                if (bv.read(i)) res += d;
                d <<= 1;
            }
            return res;
        }

        private void writeVal(int l, int r, int val) {
            for (int i = l; i < r; i++) {
                int tB = val & 1;
                boolean aB = bv.read(i);
                if ((tB != 0) ^ aB) {
                    bv.write(i);
                }
                val >>= 1;
            }
        }

        private int increment(int l, int r) {
            int i = l;
            while (i < r) {
                bv.write(i);
                if (bv.read(i)) return i;
                i++;
            }
            return r;
        }

        private void arepConstruct(int pos, int f) {
            Writes.swap(arr, frame(pos), f, SWAP_DELAY, true, auxWrite());
            writeVal(ptr(pos), ptr(pos) + p, sn);
        }

        private int gapSize(int gap) {
            int gapL = gap << 3;
            int k = 0;
            while (isActive(frame(gapL + k))) k++;
            return k;
        }

        private int segSize(int seg) {
            int segL = seg(seg);
            int l = sMin;
            int r = s - 1;
            while (l < r) {
                int mid = (l + r) >> 1;
                if (!isActive(segL + mid)) r = mid;
                else l = mid + 1;
            }
            return l;
        }

        private int slSearch(int f) {
            int l = 0;
            int r = snp - 1;
            while (l < r) {
                int mid = (l + r + 1) >> 1;
                if (Reads.compareIndices(arr, frame(mid << 3), f, DELAY, true) <= 0) l = mid;
                else r = mid - 1;
            }
            int gapL = l << 3;
            l = 0;
            r = 7;
            while (l < r) {
                int mid = (l + r) >> 1;
                if (Reads.compareIndices(arr, frame(gapL + mid), f, DELAY, true) > 0) r = mid;
                else l = mid + 1;
            }
            return gapL + l - 1;
        }

        private void slInsert(int f) {
            int pos = slSearch(f) + 1;
            int gap = pos >> 3;
            int gapSize = gapSize(gap);
            int i = (gap << 3) + gapSize;
            while (i > pos) {
                repExchange(i, i - 1);
                i--;
            }
            arepConstruct(pos, f);
            if (gapSize == 7) {
                rebalance(gap);
            }
            if (sn == snp << 1) {
                i = flatten(0, snp);
                snp = sn;
                alpha++;
                rebuild(1, 0, i);
            }
        }

        private void rebalance(int gap) {
            int l = gap;
            int d = 1;
            int v = snp + gap;
            int z = 8;
            int z1 = 0;
            int z2 = 0;
            while (true) {
                int maxZ = Math.max(z1, z2);
                int minZ = Math.min(z1, z2);
                if (maxZ - minZ > Math.max(1, z / alpha)) break;
                z1 = z;
                z2 = 0;
                boolean odd = (v & 1) == 1;
                int r = odd ? l : l + (d << 1);
                int startI = odd ? l - d : l + d;
                for (int i = startI; i < r; i++) {
                    z2 += gapSize(i);
                }
                z = z1 + z2;
                if (odd) l = l - d;
                d <<= 1;
                v >>= 1;
            }
            int r = l + d;
            int i = flatten(l, r);
            rebuild(v, l << 3, i);
        }

        private int flatten(int l, int r) {
            int i = l << 3;
            for (int j = l; j < r; j++) {
                int gapL = j << 3;
                int k = 0;
                while (k < 8 && isActive(frame(gapL + k))) {
                    repExchange(gapL + k, i);
                    i++;
                    k++;
                }
            }
            return i;
        }

        private void rebuild(int pos, int l, int r) {
            if (pos >= snp) {
                int gap = pos - snp;
                int gapL = gap << 3;
                for (int i = r - 1; i >= l; i--) {
                    repExchange(gapL + i - l, i);
                }
                return;
            }
            Writes.recursion();
            int mid = (l + r) >> 1;
            rebuild((pos << 1) + 1, mid, r);
            rebuild(pos << 1, l, mid);
        }

        private boolean cacheInsert(int line, int pos) {
            int lineL = cache(line);
            int probe = lineL + rand.nextInt(g << 1);
            while (isActive(probe)) {
                probe = lineL + rand.nextInt(g << 1);
            }
            Writes.swap(arr, pos, probe, SWAP_DELAY, true, auxWrite());
            return increment(cint(line), cint(line) + v) == cint(line) + v;
        }

        private void cacheFlush(int line, int pos) {
            int lineL = cache(line);
            for (int i = lineL; i < lineL + (g << 1); i++) {
                if (isActive(i)) {
                    Writes.swap(arr, pos, i, SWAP_DELAY, true, auxWrite());
                    pos++;
                }
            }
        }

        private void segSplit(int seg) {
            int segL = seg(seg);
            int segMid = segL + sMin;
            sn++;
            int newSegL = seg(sn);
            smallSort.sort(arr, segMid, segL + s, arr, newSegL, 0);
            int i = newSegL + sMin - 1;
            int a = segMid - 1;
            int b = segL + s - 1;
            while (i >= newSegL) {
                if (Reads.compareIndices(arr, a, b, DELAY, true) > 0) {
                    Writes.swap(arr, i, a, SWAP_DELAY, true, auxWrite());
                    a--;
                } else {
                    Writes.swap(arr, i, b, SWAP_DELAY, true, auxWrite());
                    b--;
                }
                i--;
            }
            if (a >= segL && Reads.compareIndices(arr, a, b, DELAY, true) > 0) {
                slInsert(a);
                a--;
            } else {
                slInsert(b);
                b--;
            }
            i = segL + sMin - 1;
            while (a >= segL && b >= segMid) {
                if (Reads.compareIndices(arr, a, b, DELAY, true) > 0) {
                    Writes.swap(arr, i, a, SWAP_DELAY, true, auxWrite());
                    a--;
                } else {
                    Writes.swap(arr, i, b, SWAP_DELAY, true, auxWrite());
                    b--;
                }
                i--;
            }
            while (b >= segMid) {
                Writes.swap(arr, i, b, SWAP_DELAY, true, auxWrite());
                b--;
                i--;
            }
        }

        private void insert(int x) {
            int rep = slSearch(x);
            if (rep == -1) {
                int segL = seg(0);
                Writes.swap(arr, x, segL + l0, SWAP_DELAY, true, auxWrite());
                l0++;
                if (l0 == r0) {
                    segSplit(0);
                    l0 = sMin;
                }
                return;
            }
            if (cacheInsert(rep, x)) {
                int seg = readVal(ptr(rep), ptr(rep) + p);
                int segL = seg(seg);
                int segSize = segSize(seg);
                cacheFlush(rep, segL + segSize);
                if (segSize == s - g) {
                    segSplit(seg);
                }
            }
        }

        private int segFlush(int seg, int i) {
            int segL = seg(seg);
            int segMid = segL + sMin;
            int segR = segL + segSize(seg);
            smallSort.sort(arr, segMid, segR, arr, gl, 0);
            int a = segMid - 1;
            int b = segR - 1;
            while (a >= segL && b >= segMid) {
                i--;
                if (Reads.compareIndices(arr, b, a, DELAY, true) >= 0) {
                    Writes.swap(arr, i, b, SWAP_DELAY, true, auxWrite());
                    b--;
                } else {
                    Writes.swap(arr, i, a, SWAP_DELAY, true, auxWrite());
                    a--;
                }
            }
            while (a >= segL) {
                i--;
                Writes.swap(arr, i, a, SWAP_DELAY, true, auxWrite());
                a--;
            }
            while (b >= segMid) {
                i--;
                Writes.swap(arr, i, b, SWAP_DELAY, true, auxWrite());
                b--;
            }
            return i;
        }
    }

    protected void buildBitBuffer(int[] array, int l, int r, int bvSize) {
        int[] targets = {
            l + bvSize - 1,
            l + bvSize + scrollActive(r - l - (bvSize << 1)),
            r - bvSize
        };
        new MedianOfMediansSelector().selectMulti(array, l, r, 0, targets);
        new PartitionHeapSort().sort(array, l, l + bvSize - 1);
        new PartitionHeapSort().sort(array, r - bvSize + 1, r);
    }

    protected void scrollSort(int[] array, int total) {
        if (total < MIN_SORT_LENGTH) {
            new PartitionHeapSort().sort(array, 0, total);
            return;
        }
        int bvSize = scrollBitVectorReq(scrollActive(total));
        buildBitBuffer(array, 0, total, bvSize);
        BitVector bv = new BitVector(array, 0, total - bvSize, bvSize);
        KWayMergeSort smallSort = new KWayMergeSort(gValue(total));
        boolean flag = false;
        if (Reads.compareIndices(array, bvSize - 1, total - bvSize, DELAY, true) < 0) {
            int l = bvSize;
            int r = total - bvSize;
            while (scrollBufferReq(r - l) > r - l) {
                int n = r - l;
                int m = scrollActive(n);
                int pivot;
                if (flag) {
                    pivot = new MedianOfMediansSelector().selectImpl(array, l, r, m);
                } else {
                    pivot = l + m;
                    flag = true;
                }
                int j = pivot;
                for (int i = pivot - 1; i >= l; i--) {
                    if (Reads.compareIndices(array, i, pivot, DELAY, true) >= 0) {
                        j--;
                        Writes.swap(array, i, j, SWAP_DELAY, true, auxWrite());
                    }
                }
                Writes.swap(array, j, pivot, SWAP_DELAY, true, auxWrite());
                new Scroll(array, l, j, j + 1, r, bv, smallSort);
                int eq = j + 1;
                for (int i = eq; i < r; i++) {
                    if (Reads.compareIndices(array, i, j, DELAY, true) <= 0) {
                        Writes.swap(array, i, eq, SWAP_DELAY, true, auxWrite());
                        eq++;
                    }
                }
                l = eq;
            }
            new PartitionHeapSort().sort(array, l, r);
        }
    }
}
