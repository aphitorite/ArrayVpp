package io.github.arrayv.sorts.exchange;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;

import java.util.HashMap;

final public class AlternateThreeSmoothCombSort extends Sort {
    public AlternateThreeSmoothCombSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        
        this.setSortListName("Alternate 3-Smooth Comb");
        this.setRunAllSortsName("Alternate 3-Smooth Combsort");
        this.setRunSortName("Alternate 3-Smooth Combsort");
        this.setCategory("Exchange Sorts");
        this.setComparisonBased(true);
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
		this.setAuthors("aphitorite");
		this.setConstant("n log^2 n");
    }
	
	private final int[] THREE_SMOOTH = {0, 1, 2, 3, 4, 6, 8, 9, 12, 16, 18, 24, 27, 32, 36, 48, 54, 64, 72, 81, 96, 108, 
	                                    128, 144, 162, 192, 216, 243, 256, 288, 324, 384, 432, 486, 512, 576, 648, 729, 
										768, 864, 972, 1024, 1152, 1296, 1458, 1536, 1728, 1944, 2048, 2187, 2304, 2592, 
										2916, 3072, 3456, 3888, 4096, 4374, 4608, 5184, 5832, 6144, 6561, 6912, 7776, 8192, 
										8748, 9216, 10368, 11664, 12288, 13122, 13824, 15552, 16384, 17496, 18432, 19683, 
										20736, 23328, 24576, 26244, 27648, 31104, 32768, 34992, 36864, 39366, 41472, 46656, 
										49152, 52488, 55296, 59049, 62208, 65536, 69984, 73728, 78732, 82944, 93312, 98304, 
										104976, 110592, 118098, 124416, 131072, 139968, 147456, 157464, 165888, 177147, 186624, 
										196608, 209952, 221184, 236196, 248832, 262144, 279936, 294912, 314928, 331776, 354294,
										373248, 393216, 419904, 442368, 472392, 497664, 524288, 531441, 559872, 589824, 629856, 
										663552, 708588, 746496, 786432, 839808, 884736, 944784, 995328, 1048576, 1062882, 1119744, 
										1179648, 1259712, 1327104, 1417176, 1492992, 1572864, 1594323, 1679616, 1769472, 1889568, 
										1990656, 2097152, 2125764, 2239488, 2359296, 2519424, 2654208, 2834352, 2985984, 3145728, 
										3188646, 3359232, 3538944, 3779136, 3981312, 4194304, 4251528, 4478976, 4718592, 4782969, 
										5038848, 5308416, 5668704, 5971968, 6291456, 6377292, 6718464, 7077888, 7558272, 7962624, 
										8388608, 8503056, 8957952, 9437184, 9565938, 10077696, 10616832, 11337408, 11943936, 12582912, 
										12754584, 13436928, 14155776, 14348907, 15116544, 15925248, 16777216, 17006112, 17915904, 
										18874368, 19131876, 20155392, 21233664, 22674816, 23887872, 25165824, 25509168, 26873856, 
										28311552, 28697814, 30233088, 31850496, 33554432};
										
	private HashMap<Integer, Integer> threeSmooth; 
	
	private int nextGap(int x) {
		return threeSmooth.get(x);
	}
	
	private void compSwap(int[] array, int a, int b) {
		if(Reads.compareIndices(array, a, b, 0.25, true) > 0)
			Writes.swap(array, a, b, 0.75, true, false);
	}

    @Override
    public void runSort(int[] array, int n, int bucketCount) {
		this.threeSmooth = new HashMap<Integer, Integer>();
		
		for(int i = 1; i < THREE_SMOOTH.length; i++)
			this.threeSmooth.put(THREE_SMOOTH[i], THREE_SMOOTH[i-1]);
		
		int m = 33554432;
		while(m >= n) m = this.nextGap(m);
		
		for(int i = m; m > 0; i++) {
			if(i == n) { i = n-1-m; m = this.nextGap(m); }
			for(int j = i, k = m; k > 0 && j >= k; j -= k+1, k = this.nextGap(k))
				this.compSwap(array, j-k, j);
		}
    }
}