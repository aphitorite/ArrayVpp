package io.github.arrayv.sorts.exchange;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.insert.BlockInsertionSortNeon;
import io.github.arrayv.sorts.templates.Sort;
import io.github.arrayv.utils.IndexedRotations;


/* 
  ,~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~.
  { What's up, my sorty bois? Today, we're gonna be going to a public pool and }
  } attempting to contract STG-22 Alpha! <...>                                 {
  {                                                                            }
  } Okay, so we're at the pool. Next to nobody's here but that's gonna change, {
  { because we've got some good friends who are brimming with the pathogen!    }
  }                                                                            {
  { Come on over, Parke and Marce! -- ["W-Why did yuwu bwring uws hewe??? OwO"]}
  } We need to catch STG-22 Alpha right this second, because- uh... *uhh...*   {
  { ... Just please touch us! ["Okway, suwure dwing!! UwU"]                    }
  }                                                                            {
  { <scene deleted for own sanity>                                             }
  }                                                                            {
  { Don't quite feel anything yeE*AAAAAAAAAA*                                  }
  } [Significant portion of video is an artifact-filled mess past this point,  {
  { presumably out of regret for recording]                                    }
  }                                                                            {
  { ...i did dish tuwu myshewlf... OnO                                         }
  }                                                                            {
  { [circa 2026: i don't like that i wrote this, but i'm keeping it to prove   }
  } it existed.]                                                               {
  {                  ["#3 of Distray's Pop the Top Lineup"]                    }
  `~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~'
*/
final public class InPlaceOptimizedSafeStalinSort extends Sort {
    public InPlaceOptimizedSafeStalinSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        
        this.setSortListName("In-Place Optimized Safe Stalin");
        this.setRunAllSortsName("In-Place Optimized Safe Stalin Sort");
        this.setRunSortName("In-Place Optimized Safe Stalinsort");
        this.setCategory("Hybrid Sorts");
		this.setAuthors("Distray");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(true);
        this.setUnreasonableLimit(2048);
        this.setBogoSort(false);
    }
    
    private int buildStalinRuns(int[] array, int start, int end) {
    	int runs = 0;
    	for(int i=start; i<end; i++) {
    		for(int j=i+1; j<end; j++) {
    			if(Reads.compareValues(array[j], array[i]) >= 0) {
    				if(++i != j) Writes.swap(array, j, i, 1, true, false);
    			}
    		}
    		runs++;
    	}
    	return runs;
    }
    
    private int getRun(int[] array, int start, int end) {
    	int left = start;
    	while(left < end && Reads.compareIndices(array, left, left+1, 0.1, true) <= 0) {
    		left++;
    	}
    	return left;
    }
    
    
    @Override
    public void runSort(int[] array, int currentLength, int bucketCount) {
    	int runs;
    	do {
    		runs = buildStalinRuns(array, 0, currentLength);
    		if(runs < 3) break;
    		int left = getRun(array, 0, currentLength), right = getRun(array, left+1, currentLength);
    		int mid = left;
    		while(mid > 0 && Reads.compareIndices(array, mid, right, 1, true) >= 0) {
    			mid--;
    		}
    		IndexedRotations.neon(array, 0, left+1, currentLength, 1, true, false);
    		currentLength -= left-mid;
    	} while(runs > 2);
    	if(runs == 2) {
    		BlockInsertionSortNeon neon = new BlockInsertionSortNeon(arrayVisualizer);
    		neon.insertionSort(array, 0, currentLength);
    	}
    }
}
