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

public class BSTTraversal extends Shuffle {

        public String getName() {
            return "BST Traversal";
        }
        @Override
        public void shuffleArray(int[] array) {
            int currentLen = arrayVisualizer.getCurrentLength();
            int[] temp = Arrays.copyOf(array, currentLen);

            // credit to sam walko/anon

            class Subarray {
                private int start;
                private int end;
                Subarray(int start, int end) {
                    this.start = start;
                    this.end = end;
                }
            }

            Queue<Subarray> q = new LinkedList<Subarray>();
            q.add(new Subarray(0, currentLen));
            int i = 0;

            while (!q.isEmpty()) {
                Subarray sub = q.poll();
                if (sub.start != sub.end) {
                    int mid = (sub.start + sub.end)/2;
                    Writes.write(array, i, temp[mid], 0, true, false);
                    if (arrayVisualizer.shuffleEnabled()) Delays.sleep(1);
                    i++;
                    q.add(new Subarray(sub.start, mid));
                    q.add(new Subarray(mid+1, sub.end));
                }
            }
        }
    
}
