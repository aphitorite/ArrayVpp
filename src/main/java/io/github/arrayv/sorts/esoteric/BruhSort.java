 package io.github.arrayv.sorts.esoteric;
 
 import io.github.arrayv.main.ArrayVisualizer;
 import io.github.arrayv.sorts.templates.BogoSorting;
 
 public final class BruhSort extends BogoSorting {
	  public BruhSort(ArrayVisualizer arrayVisualizer) {
		  super(arrayVisualizer);
	  
		  this.setSortListName("Bruh");
		  this.setRunAllSortsName("Bruh Sort");
     	  this.setRunSortName("Bruh (sort)");
     	  this.setCategory("Esoteric Sorts");
     	  this.setBucketSort(false);
     	  this.setRadixSort(false);
     	  this.setUnreasonablySlow(false);
     	  this.setUnreasonableLimit(0);
     	  this.setBogoSort(false);
        this.setAuthors("Distray, EilrahcF");
	  }
 
	  // If it isn't sorted, decrease every number by 1 and cap it.
	  // Maximum of 10 iterations, then it gives up and returns a sorted one.
	  public void runSort(int[] array, int length, int bucketCount) {
		  int workDone = 0,
		      mentalCapacity = 10;
		  for(; workDone <= mentalCapacity
		  && !isArraySorted(array, length); workDone++) {
			  for(int i=0; i<length; i++)
				  if(Reads.compareValues(array[i], i) != 0 && array[i] > 0) {
					  Writes.write(array, i, array[i]-1, 1, true, false);
				  }
		  }
		  if(workDone > mentalCapacity) {
			  arrayVisualizer.setCurrentLength(8);
			  for(int i=0; i<8; i++) {
				  array[i] = i;
			  }
		  }
	  }
 }