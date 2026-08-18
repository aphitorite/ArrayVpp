package io.github.arrayv.sorts.esoteric;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.BogoSorting;
import io.github.arrayv.utils.Tasque;
import io.github.arrayv.utils.TasqueManager;


final public class DeltaSort extends BogoSorting {
    public DeltaSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        
        this.setSortListName("Delta");
        this.setRunAllSortsName("Delta Sort");
        this.setRunSortName("Deltasort");
        this.setCategory("Esoteric Sorts");
        this.setAuthors("Distray");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(true);
        this.setUnreasonableLimit(1024);
        this.setBogoSort(false);
    }
    
	@SuppressWarnings("unchecked")
	private void sort(int[] array, int start, int end) {
		TasqueManager<Integer> tasqueManager = new TasqueManager<>();
		tasqueManager.queueTasques(
			new Tasque<Integer>(start, end)
		);
		while(tasqueManager.hasTasques()) {
			Tasque<Integer> k = tasqueManager.pullFirst();
			int a = k.getAttribute(0), b = k.getAttribute(1);
			boolean foundLower = false;
			if(a>=b)
				continue;
			for(int j=a; j<b; j++) {
				Highlights.markArray(1, j);
				Delays.sleep(0.025);
				if(Reads.compareValues(array[a], array[j]) > 0) {
					Writes.swap(array, j, a, 0.075, true, false);
					foundLower = true;
					tasqueManager.queueTasques(
						new Tasque<Integer>(a, j),
						new Tasque<Integer>(j, b)
					);
				}
			}
			if(!foundLower) {
				tasqueManager.queueTasques(
					new Tasque<Integer>(a+1, b)
				);
			}
			//System.out.println(tasqueManager);
		}
	}

    @Override
    public void runSort(int[] array, int currentLength, int bucketCount) {
    	while(!isArraySorted(array, currentLength)) {
    		this.sort(array, 0, currentLength);
    	}
    }
}