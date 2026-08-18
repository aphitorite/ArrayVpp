package io.github.arrayv.sorts.epsilon;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.BogoSorting;
 
 /*******************************************
  *          The Epsilon Committee          *
  * --------------------------------------- *
  * We've got 99 problems, and practicality *
  * ain't one                               *
  * ======================================= *
  * Author: Distray                         *
  *******************************************/
 
 public final class PackwatchSort extends BogoSorting {
	   public PackwatchSort(ArrayVisualizer arrayVisualizer) {
		    super(arrayVisualizer);
		     
		    this.setSortListName("Packwatch");
		    this.setRunAllSortsName("Packwatch Sort");
		    this.setRunSortName("Packwatch Sort"); // TECRipbozo
		    this.setCategory("Epsilon Committee Sorts");
		    this.setAuthors("Distray");
		    this.setBucketSort(false);
		    this.setRadixSort(false);
		    this.setUnreasonablySlow(true);
		    this.setUnreasonableLimit(5);
			this.setBogoSort(true);
	   }
	   public void runSort(int[] array, int length, int bucketCount) {
		   int n = 2;
		   
		   int[] swaps = Writes.createExternalArray(2 * length);
		   
		   while(true) {
			   while(true) {
				   for(int i=0; i<length; i++) {
					   Writes.write(swaps, 2*i, randInt(0, n), 0.05, true, true);
					   Writes.write(swaps, 2*i+1, randInt(0, n), 0.05, true, true);
					   Highlights.markArray(1, swaps[2*i]);
					   Highlights.markArray(2, swaps[2*i+1]);
				   }
				   
				   boolean sorted = true;
				   for(int i=0; i<n-1; i++) {
					   for(int j=0; j<length; j++) {
						   int inda =     i == swaps[2*j] ? swaps[2*j+1] :     i == swaps[2*j+1] ? swaps[2*j] : i,
							   indb = (i+1) == swaps[2*j] ? swaps[2*j+1] : (i+1) == swaps[2*j+1] ? swaps[2*j] : (i+1);
						   sorted = sorted && Reads.compareIndices(array, inda, indb, 0.005, true) <= 0;
					   }
				   }
				   if(sorted) {
					   Writes.swap(array, randInt(0, n), randInt(0, n), 1, true, false);
					   break;
				   } else {
					   for(int j=0; j<2*length; j+=2) {
						   Writes.swap(array, swaps[j], swaps[j+1], 1, true, false);
					   }
				   }
			   }
			   if(isArraySorted(array, n)) {
				   boolean unique = true, sorted = true;
				   for(int i = 0; i < 2 * length - 1; i++) {
					   int c = Reads.compareOriginalIndices(swaps, i, i+1, 0, false);
					   if(c == 0) unique = false;
					   if(c > 0) sorted = false;
				   }
				   if(n == length && sorted) break;
				   
				   if(unique) {
					   n++;
					   continue;
				   }
			   }
			   n = 2;
		   }
		   Writes.deleteExternalArray(swaps);
	   }
 }