package io.github.arrayv.shuffles;

import java.util.*;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.shuffles.templates.Shuffle;
import io.github.arrayv.sorts.select.MaxHeapSort;
import io.github.arrayv.sorts.select.PoplarHeapSort;
import io.github.arrayv.sorts.select.SmoothSort;
import io.github.arrayv.sorts.select.TriangularHeapSort;
import io.github.arrayv.sorts.templates.PDQSorting;
import io.github.arrayv.utils.*;

public class PDQBad extends Shuffle {

        boolean delay;
        double sleep;

        int[] temp;
        boolean hasCandidate;
        int gas, frozen, candidate;

        final class PDQPair {
            private int pivotPosition;
            private boolean alreadyPartitioned;

            private PDQPair(int pivotPos, boolean presorted) {
                this.pivotPosition = pivotPos;
                this.alreadyPartitioned = presorted;
            }

            public int getPivotPosition() {
                return this.pivotPosition;
            }

            public boolean getPresortBool() {
                return this.alreadyPartitioned;
            }
        }

        public String getName() {
            return "PDQ Adversary";
        }
        @Override
        public void shuffleArray(int[] array) {
            int currentLen = arrayVisualizer.getCurrentLength();
            delay = arrayVisualizer.shuffleEnabled();
            sleep = delay ? 1 : 0;

            int[] copy = new int[currentLen];

            hasCandidate = false;
            frozen = 1;
            temp = new int[currentLen];
            gas = currentLen;
            for (int i = 0; i < currentLen; i++) {
                Writes.write(copy, i, array[i], 0, false, false);
                Writes.write(array, i, i, 0, false, false);
                Writes.write(temp, i, gas, sleep, true, true);
            }

            pdqLoop(array, 0, currentLen, false, PDQSorting.pdqLog(currentLen));

            for (int i = 0; i < currentLen; i++) {
                Writes.write(array, i, copy[temp[i] - 1], sleep, true, false);
            }
        }

        protected int compare(int ap, int bp) {
            Reads.addComparison();
            int a, b;
            if (!hasCandidate) {
                candidate = 0;
                hasCandidate = true;
            }

            a = ap;
            b = bp;

            if (temp[a] == gas && temp[b] == gas)
                if (a == candidate)
                    temp[a] = frozen++;
                else
                    temp[b] = frozen++;

            if (temp[a] == gas) {
                candidate = a;
                return 1;
            }

            if (temp[b] == gas) {
                candidate = b;
                return -1;
            }

            if (temp[a] < temp[b])
                return -1;
            if (temp[a] > temp[b])
                return 1;
            return 0;
        }

        protected void pdqLoop(int[] array, int begin, int end, boolean branchless, int badAllowed) {
            boolean leftmost = true;

            while (true) {
                int size = end - begin;

                if (size < 24) {
                    if (leftmost) this.pdqInsertSort(array, begin, end);
                    else this.pdqUnguardInsertSort(array, begin, end);
                    return;
                }

                int halfSize = size / 2;
                if (size > 128) {
                    this.pdqSortThree(array, begin, begin + halfSize, end - 1);
                    this.pdqSortThree(array, begin + 1, begin + (halfSize - 1), end - 2);
                    this.pdqSortThree(array, begin + 2, begin + (halfSize + 1), end - 3);
                    this.pdqSortThree(array, begin + (halfSize - 1), begin + halfSize, begin + (halfSize + 1));
                    Writes.swap(array, begin, begin + halfSize, 1, true, false);
                    Highlights.clearMark(2);
                } else this.pdqSortThree(array, begin + halfSize, begin, end - 1);

                if (!leftmost && !(compare(array[begin - 1], array[begin]) < 0)) {
                    begin = this.pdqPartLeft(array, begin, end) + 1;
                    continue;
                }

                PDQPair partResult = this.pdqPartRight(array, begin, end);

                int pivotPos = partResult.getPivotPosition();
                boolean alreadyParted = partResult.getPresortBool();

                int leftSize = pivotPos - begin;
                int rightSize = end - (pivotPos + 1);
                boolean highUnbalance = leftSize < size / 8 || rightSize < size / 8;

                if (highUnbalance) {
                    if (--badAllowed == 0) {
                        int length = end - begin;
                        for (int i = length / 2; i >= 1; i--) {
                            siftDown(array, i, length, begin, sleep, true);
                        }
                        return;
                    }

                    if (leftSize >= 24) {
                        Writes.swap(array, begin,           begin + leftSize / 4, sleep, true, false);
                        Writes.swap(array, pivotPos-1,   pivotPos - leftSize / 4, sleep, true, false);

                        if (leftSize > 128) {
                            Writes.swap(array, begin+1,           begin + (leftSize / 4 + 1), sleep, true, false);
                            Writes.swap(array, begin+2,           begin + (leftSize / 4 + 2), sleep, true, false);
                            Writes.swap(array, pivotPos-2,     pivotPos - (leftSize / 4 + 1), sleep, true, false);
                            Writes.swap(array, pivotPos-3,     pivotPos - (leftSize / 4 + 2), sleep, true, false);
                        }
                    }

                    if (rightSize >= 24) {
                        Writes.swap(array, pivotPos+1,   pivotPos + (1 + rightSize / 4), sleep, true, false);
                        Writes.swap(array, end-1,                   end - rightSize / 4, sleep, true, false);

                        if (rightSize > 128) {
                            Writes.swap(array, pivotPos+2,   pivotPos + (2 + rightSize / 4), sleep, true, false);
                            Writes.swap(array, pivotPos+3,   pivotPos + (3 + rightSize / 4), sleep, true, false);
                            Writes.swap(array, end-2,             end - (1 + rightSize / 4), sleep, true, false);
                            Writes.swap(array, end-3,             end - (2 + rightSize / 4), sleep, true, false);
                        }
                    }
                    Highlights.clearMark(2);
                } else {
                    if (alreadyParted && pdqPartialInsertSort(array, begin, pivotPos)
                                        && pdqPartialInsertSort(array, pivotPos + 1, end))
                        return;
                }

                this.pdqLoop(array, begin, pivotPos, branchless, badAllowed);
                begin = pivotPos + 1;
                leftmost = false;
            }
        }

        private void siftDown(int[] array, int root, int dist, int start, double sleep, boolean isMax) {
            int compareVal = 0;

            if (isMax) compareVal = -1;
            else compareVal = 1;

            while (root <= dist / 2) {
                int leaf = 2 * root;
                if (leaf < dist && compare(array[start + leaf - 1], array[start + leaf]) == compareVal) {
                    leaf++;
                }
                Highlights.markArray(1, start + root - 1);
                Highlights.markArray(2, start + leaf - 1);
                if (compare(array[start + root - 1], array[start + leaf - 1]) == compareVal) {
                    Writes.swap(array, start + root - 1, start + leaf - 1, 0, true, false);
                    root = leaf;
                } else break;
            }
        }

        private PDQPair pdqPartRight(int[] array, int begin, int end) {
            int pivot = array[begin];
            int first = begin;
            int last = end;

            while (compare(array[++first], pivot) < 0) {
                Highlights.markArray(1, first);
            }

            if (first - 1 == begin)
                while (first < last && !(compare(array[--last], pivot) < 0)) {
                    Highlights.markArray(2, last);
                }
            else
                while (!(compare(array[--last], pivot) < 0)) {
                    Highlights.markArray(2, last);
                }

            boolean alreadyParted = first >= last;

            while (first < last) {
                Writes.swap(array, first, last, 1, true, false);
                while (compare(array[++first], pivot) < 0) {
                    Highlights.markArray(1, first);
                }
                while (!(compare(array[--last], pivot) < 0)) {
                    Highlights.markArray(2, last);
                }
            }
            Highlights.clearMark(2);

            int pivotPos = first - 1;
            Writes.write(array, begin, array[pivotPos], delay ? 1 : 0, true, false);
            Writes.write(array, pivotPos, pivot, delay ? 1 : 0, true, false);

            return new PDQPair(pivotPos, alreadyParted);
        }

        private boolean pdqPartialInsertSort(int[] array, int begin, int end) {
            if (begin == end) return true;

            double sleep = delay ? 1/3d : 0;

            int limit = 0;
            for (int cur = begin + 1; cur != end; ++cur) {
                if (limit > 8) return false;

                int sift = cur;
                int siftMinusOne = cur - 1;

                if (compare(array[sift], array[siftMinusOne]) < 0) {
                    int tmp = array[sift];

                    do {
                        Writes.write(array, sift--, array[siftMinusOne], sleep, true, false);
                    } while (sift != begin && compare(tmp, array[--siftMinusOne]) < 0);

                    Writes.write(array, sift, tmp, sleep, true, false);
                    limit += cur - sift;
                }
            }
            return true;
        }

        private int pdqPartLeft(int[] array, int begin, int end) {
            int pivot = array[begin];
            int first = begin;
            int last = end;

            while (compare(pivot, array[--last]) < 0) {
                Highlights.markArray(2, last);
            }

            if (last + 1 == end)
                while (first < last && !(compare(pivot, array[++first]) < 0)) {
                    Highlights.markArray(1, first);
                }
            else
                while (!(compare(pivot, array[++first]) < 0)) {
                    Highlights.markArray(1, first);
                }

            while (first < last) {
                Writes.swap(array, first, last, 1, true, false);
                while (compare(pivot, array[--last]) < 0) {
                    Highlights.markArray(2, last);
                }
                while (!(compare(pivot, array[++first]) < 0)) {
                    Highlights.markArray(1, first);
                }
            }
            Highlights.clearMark(2);

            int pivotPos = last;
            Writes.write(array, begin, array[pivotPos], delay ? 1 : 0, true, false);
            Writes.write(array, pivotPos, pivot, delay ? 1 : 0, true, false);

            return pivotPos;
        }

        private void pdqSortThree(int[] array, int a, int b, int c) {
            this.pdqSortTwo(array, a, b);
            this.pdqSortTwo(array, b, c);
            this.pdqSortTwo(array, a, b);
        }

        private void pdqSortTwo(int[] array, int a, int b) {
            if (compare(array[b], array[a]) < 0) {
                Writes.swap(array, a, b, 1, true, false);
            }
            Highlights.clearMark(2);
        }

        private void pdqInsertSort(int[] array, int begin, int end) {
            if (begin == end) return;

            double sleep = delay ? 1/3d : 0;

            for (int cur = begin + 1; cur != end; ++cur) {
                int sift = cur;
                int siftMinusOne = cur - 1;

                if (compare(array[sift], array[siftMinusOne]) < 0) {
                    int tmp = array[sift];
                    do {
                        Writes.write(array, sift--, array[siftMinusOne], sleep, true, false);
                    } while (sift != begin && compare(tmp, array[--siftMinusOne]) < 0);

                    Writes.write(array, sift, tmp, sleep, true, false);
                }
            }
        }

        private void pdqUnguardInsertSort(int[] array, int begin, int end) {
            if (begin == end) return;

            double sleep = 1/3d;

            for (int cur = begin + 1; cur != end; ++cur) {
                int sift = cur;
                int siftMinusOne = cur - 1;

                if (compare(array[sift], array[siftMinusOne]) < 0) {
                    int tmp = array[sift];

                    do {
                        Writes.write(array, sift--, array[siftMinusOne], sleep, true, false);
                    } while (compare(tmp, array[--siftMinusOne]) < 0);

                    Writes.write(array, sift, tmp, sleep, true, false);
                }
            }
        }
    
}
