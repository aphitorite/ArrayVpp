package io.github.arrayv.sorts.merge;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.hybrid.LazixioSort;
import io.github.arrayv.sorts.templates.Sort;
import io.github.arrayv.utils.Reads;


final public class TournamergeSort extends Sort {
    public TournamergeSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        
        this.setSortListName("Tournamerge");
        this.setRunAllSortsName("Tournamerge Sort");
        this.setRunSortName("Tournamergesort");
        this.setCategory("Merge Sorts");
        this.setConstant("n log n");
        this.setQuestion("Set the base of this sort:", 5);
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
        this.setAuthors("Distray");
        Reads = arrayVisualizer.getReads();
    }
    
    public static Reads Reads;
    public LazixioSort fallback;
    
    static class Mode {
    	public static int[] ptrs,
    	                    array;
    	public boolean stem;
    	public int ptr, offset;
    	public Mode parent, left, right, winner;
    	public Mode(int index) {
    		ptr = index;
    		offset = 0;
    		stem = false;
    	}
    	public Mode(boolean stem) {
    		ptr = -1;
    		offset = 0;
    		this.stem = stem;
    	}
    	public int index() {
    		if(ptr < 0) return -1;
    		return ptrs[ptr] + offset;
    	}
    	public void build() {
    		if(left == null || left.ptr < 0) {
    			if(right == null || right.ptr < 0) {
    				ptr = -1;
    				stem = false;
    				left = right = winner = null;
    				return;
    			}
    			winner=right;
    			ptr = right.ptr;
    			offset = right.offset;
    		} else if(right == null || right.ptr < 0) {
    			winner=left;
    			ptr = left.ptr;
    			offset = left.offset;
    		} else if(Reads.compareIndices(array, left.index(), right.index(), 1, true) > 0) {
    			winner=right;
    			ptr = right.ptr;
    			offset = right.offset;
    		} else {
    			winner=left;
    			ptr = left.ptr;
    			offset = left.offset;
    		}
    	}
    	public void increase() {
    		offset++;
    		if(index() >= ptrs[ptr+1])
    			ptr = -1;
    	}
    }
    
    private Mode build(Mode l, Mode r) {
    	Mode t = new Mode(true);
    	l.parent=r.parent=t;
    	t.left=l;t.right=r;
    	t.build();return t;
    }
    
    private Mode deepbuild(int[] array, int start, int end) {
    	if(end<=start) {
    		return new Mode(start);
    	}
    	int mid = start+(end-start)/2;
    	return build(deepbuild(array, start, mid), deepbuild(array, mid+1, end));
    }
    private void incwinner(Mode root) {
    	if(root.winner == null)
    		return;
    	if(root.winner.stem) {
    		incwinner(root.winner);
    	} else {
    		root.winner.increase();
    		do {
    			root.build();
    			root=root.parent;
    		} while(root!=null);
    	}
    }
    private void merge(int[] array, int[] tmp, boolean aux, int... ptrs) {
    	Mode.ptrs=ptrs;
    	Mode.array=array;
    	Mode root = deepbuild(array, 0, ptrs.length-2);
    	int t = ptrs[0];
    	while(root.winner != null) {
    		Highlights.markArray(1, root.index());
    		Writes.write(tmp, t++, array[root.index()], 1, true, aux);
    		incwinner(root);
    	}
    }
    public void mergeSort(int[] array, int[] tmp, boolean aux, int start, int end, int base) {
    	if(end-start <= base*base && !aux) {
    		fallback.mergeRuns(array, start, end);
    		return;
    	}
    	int[] locs = new int[base+1];
    	for(int i=0; i<base; i++) {
    		locs[i] = start+(i*(end-start)/base);
    	}
    	locs[base] = end;
    	for(int i=0; i<base; i++) {
    		mergeSort(tmp, array, !aux, locs[i], locs[i+1], base);
    	}
    	merge(tmp, array, aux, locs);
    }

    @Override
    public int validateAnswer(int answer) {
        if (answer < 2) return 2;
        return answer;
    }
    
    @Override
    public void runSort(int[] array, int length, int b) {
    	fallback = new LazixioSort(arrayVisualizer);
    	int[] tmp = Writes.createExternalArray(length);
    	this.mergeSort(array, tmp, false, 0, length, b);
    	Writes.deleteExternalArray(tmp);
    }
}