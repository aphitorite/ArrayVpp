package io.github.arrayv.sorts.merge;

import java.awt.Color;
import java.util.LinkedList;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.insert.BinaryInsertionSort;
import io.github.arrayv.sorts.templates.Sort;
import io.github.arrayv.utils.*;
import io.github.arrayv.visuals.Visual;

final public class UnstablePolymergeSortDistayStyle extends Sort {
	public UnstablePolymergeSortDistayStyle(ArrayVisualizer arrayVisualizer) {
		super(arrayVisualizer);
		
		this.setSortListName("Unstable Polymerge (Distay Private)");
		this.setRunAllSortsName("Unstable Polymerge Sort (Distay Private)");
		this.setRunSortName("Unstable Polymerge Sort (DistayStyle)");
		this.setCategory("Merge Sorts");
		this.setConstant("n log^3 n");
		this.setBucketSort(false);
		this.setRadixSort(false);
		this.setUnreasonablySlow(false);
		this.setUnreasonableLimit(0);
		this.setBogoSort(false);
        this.setAuthors("Distray");
	}
	
	private int idx;
	
	class run extends Renderable {
		public int a, b, i;
		public run(int a, int b, int i) {
			this.a = a; this.b = b; this.i = i;
			if(Renderer.visualSupportsRenderables()) {
				Renderer.registerRenderable(this);
			}
		}
		public void render(int[] blank, ArrayVisualizer arrayVisualizer, Renderer renderer, Highlights highlights) {
			if(i < 0) {
				Renderer.unregisterRenderable(this);
				return;
			}
			Color t = mainRender.getColor();
			mainRender.setColor(Visual.getIntColor(i, idx+1));
			drawBoundaryFXW(arrayVisualizer, a, -2);
			drawBoundaryFXW(arrayVisualizer, a, 2);
			mainRender.setColor(t);
		}
	}
	
	private void polymergeRoutine(int[] array, int a, int m, int b, int d) {
		if(a >= m || m >= b)
			return;
		Writes.recordDepth(d++);
		idx = 0;
		LinkedList<run> z = new LinkedList<>();
		z.add(new run(m, b, idx++));
		for(int i=a; i<m; i++) {
			int min = i, mi = -1;
			for(int j = 0; j < z.size(); j++) {
				run r = z.get(j);
				Color p = Color.getHSBColor(((float) r.i/(idx+1)), 0.7F, 1F);;
				for(int k = r.a; k < r.b; k++) {
					Highlights.colorCode(p, k);
				}
				if(Reads.compareIndices(array, min, r.a, 2.5, true) > 0) {
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
					z.addFirst(new run(m, m+1, idx++));
				} else {
					z.get(mi-1).b++;
				}
			}
		}
		for(int k = m; k < b; k++) {
			Highlights.clearColor(k);
		}
		Renderer.unregisterAllRenderables();
		for(int i = 0; i < z.size() - 1; i++) {
			Writes.recursion();
			polymergeRoutine(array, m, z.get(i).b, z.get(i+1).b, d);
		}
		z.clear();
	}
	
	private void polymerge(int[] array, int a, int b, int d) {
		Writes.recordDepth(d++);
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