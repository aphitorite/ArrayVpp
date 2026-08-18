 package io.github.arrayv.sorts.esoteric;
 
 import io.github.arrayv.main.ArrayVisualizer;
 import io.github.arrayv.sorts.templates.Sort;
 
public final class GradualStoogeSort extends Sort {
	 public GradualStoogeSort(ArrayVisualizer arrayVisualizer) {
		 super(arrayVisualizer);
		 
		 this.setSortListName("Gradually More & More Terrifying Stooge");
		 this.setRunAllSortsName("Gradually More & More Terrifying Stooge Sort");
		 this.setRunSortName("G.M&M.T. Stoogesort");
		 this.setCategory("Esoteric Sorts");
	     this.setAuthors("Potassium");
		 this.setBucketSort(false);
		 this.setRadixSort(false);
		 this.setUnreasonablySlow(true);
		 this.setUnreasonableLimit(1024);
		 this.setBogoSort(false);
	 }
	 
	 public void stoogeSort(int[] A, int i, int j) {
		 if (Reads.compareValues(A[i], A[j]) == 1) {
			 Writes.swap(A, i, j, 0.005D, true, false);
		 }
		 
		 Delays.sleep(0.0025D);
		 
		 Highlights.markArray(1, i);
		 Highlights.markArray(2, j);
		 
		 if (j > i) {
			 int t = 1;
			 
			 Highlights.markArray(3, j - t);
			 Highlights.markArray(4, i + t);
			 
			 stoogeSort(A, i, j - t);
			 stoogeSort(A, i + t, j);
			 stoogeSort(A, i, j - t);
		 } 
	 }
 
	 @Override
	 public void runSort(int[] array, int currentLength, int bucketCount) {
		 for (int i = 1; i < currentLength; i++)
			 stoogeSort(array, 0, i); 
	 }
 }