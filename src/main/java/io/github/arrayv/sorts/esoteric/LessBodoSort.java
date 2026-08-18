 package io.github.arrayv.sorts.esoteric;
 
 import io.github.arrayv.main.ArrayVisualizer;
 import io.github.arrayv.sorts.templates.BogoSorting;
 
 public final class LessBodoSort extends BogoSorting {
	 public LessBodoSort(ArrayVisualizer arrayVisualizer) {
		 super(arrayVisualizer);
		 
		 this.setSortListName("Less Bodo");
		 this.setRunAllSortsName("Less Bodo Sort");
		 this.setRunSortName("Less Bodo Sort");
		 this.setCategory("Esoteric Sorts");
		 this.setAuthors("Distray");
		 this.setBucketSort(false);
		 this.setRadixSort(false);
		 this.setUnreasonablySlow(true);
		 this.setUnreasonableLimit(2048);
		 this.setBogoSort(true);
	 }
	 
	 public void runSort(int[] array, int length, int bucketCount) {
	 int s = 0;
		 while (s < length - 1 && !isRangeSorted(array, s, length, false, true)) {
			 int index = BogoSorting.randInt(s, length - 1),
				 index2 = BogoSorting.randInt(0, length - 1);
			 while(index < length - 1 && Reads.compareValues(array[index], array[index2]) == 1) {
				 Writes.swap(array, index, ++index, 0.075, true, false);
				 index2 = BogoSorting.randInt(0, length - 1);
			 }
			 for(int i=s; i<length; i++) {
				 boolean brk = false;
				 for(int j=i+1; j<length; j++) {
					 if(Reads.compareValues(array[i], array[j]) > 0) {
						 brk = true;
						 break;
					 }
				 }
				 if(brk)
					 break;
				 s++;
			 }
		 } 
	 }
 }