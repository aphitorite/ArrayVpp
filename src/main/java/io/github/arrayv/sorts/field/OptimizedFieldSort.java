package io.github.arrayv.sorts.field;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.distribute.MSDRadixSort;
import io.github.arrayv.sorts.distribute.BogoSort;
import io.github.arrayv.sorts.hybrid.GrailSort;
import io.github.arrayv.sorts.insert.InsertionSort;
import io.github.arrayv.sorts.merge.PanicSort;
import io.github.arrayv.sorts.root.HalfSort;
import io.github.arrayv.sorts.hybrid.SqrtSort;
import io.github.arrayv.sorts.select.CycleSort;
import io.github.arrayv.sorts.select.OptimizedSelectionSort;
import io.github.arrayv.sorts.select.SmoothSort;
import io.github.arrayv.sorts.templates.BogoSorting;

public final class OptimizedFieldSort extends BogoSorting {
	BogoSort verify;
	int fieldSortMaxDepth;
	int fieldSortHeap;
	int fieldSortMerge;
	int fieldSortType;
	int fieldSortRadix;
	public OptimizedFieldSort(ArrayVisualizer arrayVisualizer) {
		super(arrayVisualizer);
		
		this.setSortListName("Optimized Field");
		this.setRunAllSortsName("Optimized Field Sort"); 
		this.setRunSortName("Optimized Fieldsort"); 
		this.setCategory("Hybrid Sorts");
		this.setBucketSort(false);
		this.setRadixSort(false);
		this.setUnreasonablySlow(false);
		this.setUnreasonableLimit(0);
		this.setBogoSort(false);
        this.setAuthors("Potassium");
		
		this.verify = new BogoSort(this.arrayVisualizer);
		this.fieldSortMaxDepth = 0;
		this.fieldSortHeap = 0;
		this.fieldSortMerge = 0;
		this.fieldSortType = 0;
		this.fieldSortRadix = 0;
	}
	
	private int partition(int[] array, int lo, int hi) {
		int pivot = array[hi];
		int i = lo;
		int j = lo;
		for (j = lo; j <= hi; j++) {
			Highlights.markArray(1, j);
			if (Reads.compareValues(array[j], pivot) < 0) {
				Writes.swap(array, i, j, 1.0D, true, false);
				i++;
			} 
			Delays.sleep(1.0D);
		} 
		Writes.swap(array, i, hi, 1.0D, true, false);
		return i;
	}
	
	public void siftDown(int[] array, int root, int dist, int start, double sleep, boolean isMax) {
		int compareVal = 0;
		if (isMax) {
			compareVal = -1;
		} else {
			compareVal = 1;
		} 
		while (root <= dist / 2) {
			int leaf = 2 * root;
			if (leaf < dist && Reads.compareValues(array[start + leaf - 1], array[start + leaf]) == compareVal)
				leaf++; 
			Highlights.markArray(1, start + root - 1);
			Highlights.markArray(2, start + leaf - 1);
			Delays.sleep(sleep);
			if (Reads.compareValues(array[start + root - 1], array[start + leaf - 1]) == compareVal) {
				Writes.swap(array, start + root - 1, start + leaf - 1, 0.0D, true, false);
				root = leaf;
				continue;
			} 
			break;
		} 
	}
	public void heapify(int[] arr, int low, int high, double sleep, boolean isMax) {
		int length = high - low;
		for (int i = length / 2; i >= 1; i--)
			siftDown(arr, i, length, low, sleep, isMax); 
	}
	public void heapSort(int[] arr, int start, int length, double sleep, boolean isMax) {
		heapify(arr, start, length, sleep, isMax);
		for (int i = length - start; i > 1; i--) {
			Writes.swap(arr, start, start + i - 1, sleep, true, false);
			siftDown(arr, 1, i - 1, start, sleep, isMax);
		} 
		if (!isMax)
			Writes.reversal(arr, start, start + length - 1, 1.0D, true, false); 
	}
	
	public void initFieldSort(int[] array, int lo, int hi, int length, int depth, int heap, int merge, int type, int radix) {
		PanicSort sort = new PanicSort(this.arrayVisualizer);
		if (isRangeSorted(array, lo, hi, true, false))
			return; 
		if (isRangeReversed(array, lo, hi, true, false)) {
			Writes.reversal(array, lo, hi, 0.5D, true, false);
			return;
		} 
		if (isRangeSorted(array, lo + (int)Math.sqrt((hi - lo)), (lo + hi) / 2 - (int)Math.sqrt((hi - lo)), true, false) && isRangeSorted(array, (lo + hi) / 2 + (int)Math.sqrt((hi - lo)), hi - (int)Math.sqrt((hi - lo)), true, false)) {
			sort.mergeSort2(array, lo, hi + 1, false, hi - lo, length);
			return;
		} 
		if (isRangeSorted(array, lo + (int)Math.sqrt((hi - lo)), (lo + hi) / 2 - (int)Math.sqrt((hi - lo)), true, false) && isRangeReversed(array, (lo + hi) / 2 + (int)Math.sqrt((hi - lo)), hi - (int)Math.sqrt((hi - lo)), true, false)) {
			Writes.reversal(array, (lo + hi) / 2, hi, 0.5D, true, false);
			sort.mergeSort2(array, lo, hi + 1, false, hi - lo, length);
			return;
		} 
		if (isRangeReversed(array, lo + (int)Math.sqrt((hi - lo)), (lo + hi) / 2 - (int)Math.sqrt((hi - lo)), true, false) && isRangeSorted(array, (lo + hi) / 2 + (int)Math.sqrt((hi - lo)), hi - (int)Math.sqrt((hi - lo)), true, false)) {
			Writes.reversal(array, lo, (lo + hi) / 2, 0.5D, true, false);
			sort.mergeSort2(array, lo, hi + 1, false, hi - lo, length);
			return;
		} 
		if (isRangeReversed(array, lo + (int)Math.sqrt((hi - lo)), (lo + hi) / 2 - (int)Math.sqrt((hi - lo)), true, false) && isRangeReversed(array, (lo + hi) / 2 + (int)Math.sqrt((hi - lo)), hi - (int)Math.sqrt((hi - lo)), true, false)) {
			Writes.reversal(array, lo, hi, 0.5D, true, false);
			sort.mergeSort2(array, lo, hi + 1, false, hi - lo, length);
			return;
		} 
		if (isRangeReversed(array, lo + (int)Math.sqrt((hi - lo)), (lo + hi) / 2 - (int)Math.sqrt((hi - lo)), true, false)) {
			shufflePartition(array, (lo + hi) / 2, hi, length, depth, heap, merge, type, radix);
			Writes.reversal(array, lo, hi, 0.5D, true, false);
			sort.mergeSort2(array, lo, hi + 1, false, hi - lo, length);
			return;
		} 
		if (isRangeReversed(array, (lo + hi) / 2 + (int)Math.sqrt((hi - lo)), hi - (int)Math.sqrt((hi - lo)), true, false)) {
			shufflePartition(array, lo, (lo + hi) / 2, length, depth, heap, merge, type, radix);
			Writes.reversal(array, lo, hi, 0.5D, true, false);
			sort.mergeSort2(array, lo, hi + 1, false, hi - lo, length);
			return;
		} 
		if (isRangeSorted(array, lo + (int)Math.sqrt((hi - lo)), (lo + hi) / 2 - (int)Math.sqrt((hi - lo)), true, false)) {
			shufflePartition(array, (lo + hi) / 2, hi, length, depth, heap, merge, type, radix);
			sort.mergeSort2(array, lo, hi + 1, false, hi - lo, length);
			return;
		} 
		if (isRangeSorted(array, (lo + hi) / 2 + (int)Math.sqrt((hi - lo)), hi - (int)Math.sqrt((hi - lo)), true, false)) {
			shufflePartition(array, lo, (lo + hi) / 2, length, depth, heap, merge, type, radix);
			sort.mergeSort2(array, lo, hi + 1, false, hi - lo, length);
			return;
		} 
		shufflePartition(array, lo, hi, length, depth, heap, merge, type, radix);
	}
	public void shufflePartition(int[] array, int lo, int hi, int length, int depth, int heap, int merge, int type, int radix) {
		int i;
		for (i = lo; i < hi; i = (int)(i + Math.sqrt((hi - lo))))
			Writes.swap(array, i, BogoSorting.randInt(i, hi), 5.0D, true, false); 
		for (i = lo; i < lo + Math.sqrt((hi - lo)) - 1.0D; i++)
			Writes.swap(array, i, BogoSorting.randInt(i, hi), 5.0D, true, false); 
		for (i = hi; i > hi - Math.sqrt((hi - lo)) + 1.0D; i--)
			Writes.swap(array, i, BogoSorting.randInt(lo, i), 5.0D, true, false); 
		int j;
		for (j = lo; j < hi; j++) {
			if (Reads.compareValues(array[j], array[j + 1]) == 1)
				Writes.swap(array, j, j + 1, 0.075D, true, false); 
			Highlights.markArray(1, j);
			Highlights.markArray(2, j + 1);
			Delays.sleep(0.1D);
		} 
		for (j = hi; j > lo; j--) {
			if (Reads.compareValues(array[j], array[j - 1]) == -1)
				Writes.swap(array, j, j - 1, 0.075D, true, false); 
			Highlights.markArray(1, j);
			Highlights.markArray(2, j - 1);
			Delays.sleep(0.1D);
		} 
		fieldSort(array, lo + 1, hi - 1, length - 1, depth, heap, merge, type, radix);
	}
	public void fieldSort(int[] array, int lo, int hi, int length, int depth, int heap, int merge, int type, int radix) {
		if (hi - lo > 2 * (int)Math.sqrt(length) && depth > 0 && !isRangeSorted(array, lo, hi, false, true)) {
			int p = partition(array, lo, hi);
			fieldSort(array, lo, p - 1, length, depth - 1, heap, merge, type, radix);
			fieldSort(array, p + 1, hi, length, depth - 1, heap, merge, type, radix);
		} else if (hi - lo > 2 * (int)Math.cbrt(length) && depth > 0 && !isRangeSorted(array, lo, hi + 1, false, true)) {
			SmoothSort sort;
			OptimizedSelectionSort sortX;
			CycleSort sortA;
			switch (heap) {
				case 0:
					heapSort(array, lo, hi + 1, 1.0D, true);
					break;
				case 1:
					heapSort(array, lo, hi + 1, 1.0D, false);
					break;
				case 2:
					sort = new SmoothSort(this.arrayVisualizer);
					sort.smoothSort(array, lo, hi + 1, true);
					break;
				case 3:
					sortX = new OptimizedSelectionSort(this.arrayVisualizer);
					sortX.selectionSort(array, lo, hi + 1, 0);
					break;
				case 4:
					sortA = new CycleSort(this.arrayVisualizer);
					sortA.cycleSort(array, lo, hi + 1, 0);
					break;
			} 
		} else if (hi - lo > Math.max(length, 6 * (int)Math.sqrt(length)) && depth <= 0 && !isRangeSorted(array, lo, hi + 1, false, true)) {
			int highestpower = Reads.analyzeMaxLog(array, length, radix, 0.5D, true);
			MSDRadixSort sort = new MSDRadixSort(this.arrayVisualizer);
			sort.radixMSD(array, hi - lo, lo, hi, radix, highestpower);
		} else if (hi - lo <= Math.max(length, 6 * (int)Math.sqrt(length)) && depth <= 0 && !isRangeSorted(array, lo, hi + 1, false, true)) {
			PanicSort sort;
			GrailSort sort1;
			int[] ExtBuf;
			int bufferLen;
			int tempLen;
			int numKeys;
			int[] DynExtBuf;
			int[] extBuf;
			int[] tags;
			SqrtSort sort2;
			int bufferLen2;
			int[] extBuf2;
			HalfSort sort3;
			InsertionSort sort4;
			switch (merge) {
				case 0:
					sort = new PanicSort(this.arrayVisualizer);
					sort.mergeSort2(array, lo, hi + 1, false, hi - lo, length);
					break;
				case 1:
					sort1 = new GrailSort(this.arrayVisualizer);
					switch (type) {
						case 0:
							sort1.grailCommonSort(array, lo, hi - lo + 1, null, 0, 0);
							break;
						case 1:
							ExtBuf = Writes.createExternalArray(sort1.getStaticBuffer());
							sort1.grailCommonSort(array, lo, hi - lo + 1, ExtBuf, 0, sort1.getStaticBuffer());
							Writes.deleteExternalArray(ExtBuf);
							break;
						case 2:
							tempLen = 1;
							for (; tempLen * tempLen < length; tempLen *= 2);
							DynExtBuf = Writes.createExternalArray(tempLen);
							sort1.grailCommonSort(array, lo, hi - lo + 1, DynExtBuf, 0, tempLen);
							Writes.deleteExternalArray(DynExtBuf);
							break;
					} 
				case 2:
					switch (type) {
						case 0:
							bufferLen = 1;
							for (; bufferLen * bufferLen < hi - lo + 1; bufferLen *= 2);
							numKeys = (hi - lo - 1) / bufferLen + 2;
							extBuf = Writes.createExternalArray(bufferLen);
							tags = Writes.createExternalArray(numKeys);
							sort2 = new SqrtSort(arrayVisualizer);
							sort2.sqrtCommonSort(array, lo, hi - lo + 1, extBuf, 0, tags, false);
							Writes.deleteExternalArray(extBuf);
							Writes.deleteExternalArray(tags);
							break;
						case 1:
							bufferLen2 = 1;
							for (; bufferLen2 * 2 < hi - lo + 1; bufferLen2 *= 2);
							extBuf2 = Writes.createExternalArray(bufferLen2);
							sort3 = new HalfSort(arrayVisualizer);
							sort3.sqrtCommonSort(array, lo, hi - lo + 1, extBuf2, 0, false);
							Writes.deleteExternalArray(extBuf2);
							sort4 = new InsertionSort(arrayVisualizer);
							sort4.customInsertSort(array, lo, hi + 1, 0.5D, false);
							break;
					} 
					break;
			} 
		} else if (!isRangeSorted(array, lo, hi + 1, false, true)) {
			InsertionSort sort = new InsertionSort(this.arrayVisualizer);
			sort.customInsertSort(array, lo, hi + 1, 0.5D, false);
		} 
	}
	public void runSort(int[] array, int currentLength, int bucketCount) {
		fieldSort(array, 0, currentLength - 1, currentLength, 
				  this.fieldSortMaxDepth, this.fieldSortHeap,
				  this.fieldSortMerge, this.fieldSortType, this.fieldSortRadix);
	}
}