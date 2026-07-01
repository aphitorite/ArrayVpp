package io.github.arrayv.sorts.insert;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;
import io.github.arrayv.utils.ShellsortGaps;

// Shell sort variant retrieved from:
// https://www.cs.princeton.edu/~rs/talks/shellsort.ps

public final class ShellSort extends Sort {
    public ShellSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);

        this.setSortListName("Shell");
        this.setRunAllSortsName("Shell Sort");
        this.setRunSortName("Shellsort");
        this.setCategory("Insertion Sorts");
        this.setConstant("n cbrt n");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
        this.setAuthors("Donald Shell");
        this.setUseShellsortGaps(true);
    }

    private void shellSort(int[] array, int n, int[] gaps) {
        for(int k : gaps) {
			for(int j = k; j < n; j++) {
				if(Reads.compareIndices(array, j-k, j, 0.5, true) > 0) {
					Highlights.clearMark(2);
					
					int i = j;
					int t = array[j];
					
					do Writes.write(array, i, array[i -= k], 0.5, true, false);
					while(i >= k && Reads.compareIndexValue(array, i-k, t, 0.5, true) > 0);
					
					Writes.write(array, i, t, 0.5, true, false);
				}
			}
		}
    }

    public void runSort(int[] array, int currentLength) {
        ShellsortGaps seq = this.arrayVisualizer.getSelectedGapSequence();
        this.shellSort(array, currentLength, seq.getGaps(currentLength));
    }

    public void runSort(int[] array, int currentLength, int[] gaps) {
        this.shellSort(array, currentLength, gaps);
    }

    @Override
    public void runSort(int[] array, int currentLength, int bucketCount) {
        ShellsortGaps seq = this.arrayVisualizer.getSelectedGapSequence();
        this.arrayVisualizer.setHeading("Shellsort (" + seq.getName() + " gaps)");
        this.runSort(array, currentLength, seq.getGaps(currentLength));
    }
}
