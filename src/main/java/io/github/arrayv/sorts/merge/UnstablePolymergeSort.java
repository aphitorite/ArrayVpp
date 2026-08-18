package io.github.arrayv.sorts.merge;

import java.util.LinkedList;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.insert.BinaryInsertionSort;
import io.github.arrayv.sorts.templates.Sort;

final public class UnstablePolymergeSort extends Sort {
	public UnstablePolymergeSort(ArrayVisualizer arrayVisualizer) {
		super(arrayVisualizer);
		
		this.setSortListName("Unstable Polymerge");
		this.setRunAllSortsName("Unstable Polymerge Sort");
		this.setRunSortName("Unstable Polymerge Sort");
		this.setCategory("Merge Sorts");
		this.setAuthors("Distray");
		this.setConstant("n log^3 n");
		this.setBucketSort(false);
		this.setRadixSort(false);
		this.setUnreasonablySlow(false);
		this.setUnreasonableLimit(0);
		this.setBogoSort(false);
	}
	
	class run {
		public int a, b;
		public run(int a, int b) {
			this.a = a; this.b = b;
		}
	}
	
	private void polymergeRoutine(int[] array, int a, int m, int b, int d) {
		if(a >= m || m >= b)
			return;
		Writes.recordDepth(d++);
		LinkedList<run> z = new LinkedList<>();
		z.add(new run(m, b));
		for(int i=a; i<m; i++) {
			int min = i, mi = -1;
			for(int j = 0; j < z.size(); j++) {
				run r = z.get(j);
				if(Reads.compareIndices(array, min, r.a, 1, true) > 0) {
					min = r.a;
					mi = j;
				}
			}
			if(min != i) {
				Writes.swap(array, min, i, 1, true, false);
				min = ++z.get(mi).a;
				if(min == z.get(mi).b)
					z.remove(mi);
				if(mi == 0) {
					z.addFirst(new run(m, m+1));
				} else {
					z.get(mi-1).b++;
				}
			}
		}
		/*for(int i=m; z.size() > 1 && i<b; i++) {
			int min = i, mi = -1;
			for(int j = 1; j < z.size(); j++) {
				run r = z.get(j);
				if(Reads.compareIndices(array, min, r.a, 1, true) > 0) {
					min = r.a;
					mi = j;
				}
			}
			if(min != i) {
				Writes.swap(array, min, i, 1, true, false);
				min = ++z.get(mi).a;
				if(min == z.get(mi).b)
					z.remove(mi);
				if(mi == 0 || Reads.compareIndices(array, min-2, min-1, 1, true) > 0) {
					z.add(mi, new run(min-1, min));
				} else {
					z.get(mi-1).b++;
				}
			}
			if(++z.getFirst().a == z.getFirst().b) {
				z.removeFirst();
			}
		}*/
		for(int i = 0; i < z.size() - 1; i++) {
			Writes.recursion();
			polymergeRoutine(array, m, z.get(i).b, z.get(i+1).b, d);
		}
		z.clear();
	}
	
	private void polymerge(int[] array, int a, int b, int d) {
		int m = a + (b - a) / 2;
		if(a == m) return;
		if(b - a < 64) {
			BinaryInsertionSort bz = new BinaryInsertionSort(arrayVisualizer);
			bz.customBinaryInsert(array, a, b, 0.5);
			return;
		}
		Writes.recursion();
		polymerge(array, a, m, d);
		Writes.recursion();
		polymerge(array, m, b, d);
		polymergeRoutine(array, a, m, b, d);
	}
	
	@Override
	public void runSort(int[] array, int length, int bucketCount) {
		polymerge(array, 0, length, 0);
	}
}