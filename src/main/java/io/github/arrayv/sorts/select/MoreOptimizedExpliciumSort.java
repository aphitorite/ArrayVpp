package io.github.arrayv.sorts.select;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;


final public class MoreOptimizedExpliciumSort extends Sort {  
    public MoreOptimizedExpliciumSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        
        this.setSortListName("Explicium -Os");
        this.setRunAllSortsName("More Optimized Explicium Sort");
        this.setRunSortName("More Optimized Expliciumsort");
        this.setCategory("Selection Sorts");
  	    this.setAuthors("Distray");
        this.setConstant("n log n");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
    }
    
    private static final int DELETED/*!!*/ = 1;
    private int BITS = 16;

    // Get a pointer from the tree
    private int getClass(int[] tree, int idx) {
        int shift = idx % BITS, tid = idx / BITS;
        if(tid >= tree.length) return 1;
        return (tree[tid] >>> shift) & 0x1;
    }
    // Set a pointer in the tree
    private void setClass(int[] tree, int idx, int s) {
        int shift = idx % BITS, tid = idx / BITS;
        if(tid < tree.length)
            Writes.write(tree, tid, (tree[tid] & ~(1 << shift)) | (s << shift), 0.5, true, true);
    }
    // Dig for the winning node of the tree in O(log n) time
    private int digFor(int[] tree, int idx, int fv, int n) {
        int ci;
        // Repeat until maximum depth:
        while(n > 1) {
            ci = getClass(tree, idx);
            int m = (n + 1) / 2;
            fv += m * ci;
            n -= m;
            idx = 2 * idx + 1 + ci;
        }
        // A node is marked as deleted if the "0s" bit is set.
        return getClass(tree, idx) == DELETED ? -1 : fv;
    }
    // Find a node in the tree without reading the classes
    private int[] digReadless(int a, int from, int n, int d) {
        int fv = a;
        // Until the desired depth:
        while(d-- > 0) {
            // Simulate digging through the tree until the target position is reached.
            int m = (n + 1) / 2;
            fv += (from - fv) - (from - fv) % m;
            n -= m;
        }
        // res[0]: location of node in main list
        // res[1]: size of branches
        return new int[] {fv, n - (n + 1) / 2};
    }

    private int make(int[] array, int[] tree, int a, int b, int c, int idx, int d) {
        Writes.recordDepth(d++);
        if(a >= c) {
            return -1;
        }
        if(a >= b) {
            Highlights.markArray(1, a);
            return a;
        }
        int nxt = 2 * idx + 1,       // Location of left child in tree
            m = a + (b - a + 1) / 2; // Middle value
        
        // Build the two children.
        Writes.recursion();
        int l = make(array, tree, a, m-1, c, nxt,   d);
        Writes.recursion();
        int r = make(array, tree, m, b,   c, nxt+1, d);
        
        // Get the winning (or accessible) node, and set the parent to point to them.
        if(r < 0) {
            setClass(tree, idx, 0);
            return l;
        } else if(l < 0) {
            setClass(tree, idx, 1);
            return r;
        } else {
            int mi = Reads.compareIndices(array, l, r, 0.5, true) <= 0 ? l : r;
            setClass(tree, idx, mi == l ? 0 : 1);
            return mi;
        }
    }
    
    // O(log^2 n) access O(log n) moves winner pruning method
    private void removeWinner(int[] array, int[] tree, int a, int n) {
        int ci;
        int idx = 0, from = a, d = 0, np = n;
        
        // Dig for the winning node.
        while(n > 1) {
            d++;
            ci = getClass(tree, idx);
            int m = (n + 1) / 2;
            from += m * ci;
            n -= m;
            idx = 2 * idx + 1 + ci;
        }
        
        // First layer:
        int pidx = (idx - 1) / 2, pc = idx - (2 * pidx + 1);
        // Remove the winning node.
        setClass(tree, idx, DELETED);
        // If its sibling still exists, point to it instead.
        if(getClass(tree, 2 * pidx + 2 - pc) != DELETED) {
            setClass(tree, pidx, pc ^ 1);
        }
        // Go up a layer.
        idx = pidx; d--;
        
        // Second onwards:
        while(idx > 0) {
            // Get the parent index.
            pidx = (idx - 1) / 2; d--;

            // Find our current parent in the tree readlessly, and get its branch info
            int[] qv = digReadless(a, from, np, d);
            int q = qv[0], v = qv[1];
            
            // Get the winning nodes for both branches.
            int vl = digFor(tree, 2 * pidx + 1, q, v);
            int vr = digFor(tree, 2 * pidx + 2, q + v, v);
            int bb = (vl <= -1 ? 1 : 0) + (vr <= -1 ? 2 : 0);
            
            // Get the parent's winning branch.
            pc = idx - (2 * pidx + 1);
            
            // If the "winning" branch is invalid, but the sibling branch is okay:
            if(bb - 1 == pc) {
                // Set the parent to point to the sibling branch
                setClass(tree, pidx, pc ^ 1);
            // If both branches are okay:
            } else if(bb == 0) {
                // Set the parent to point to the branch with the better winning node
                int nc = Reads.compareIndices(array, vl, vr, 0.5, true) <= 0 ? 0 : 1;
                if(pc != nc)
                    setClass(tree, pidx, nc);
            }
            // Go up a layer.
            idx = pidx;
        }
    }
    public void Explic(int[] array, int a, int b) {
        int treeLen = (1 << (32 - Integer.numberOfLeadingZeros(b - a - 1)));
        
        int[] tree = Writes.createExternalArray((2 * treeLen - 1) / BITS + 1), // Tree of indices
              out = Writes.createExternalArray(b - a); // Sorted result
        make(array, tree, a, a + treeLen, b, 0, 0);
        int t = 0;
        while(true) {
            // Dig for the winning node.
            int w = digFor(tree, 0, a, treeLen);
            // Push the value to the output array.
            Writes.write(out, t++, array[w], 1, true, true);
            if(t < b-a) // Remove the winner and rebuild the tree.
                removeWinner(array, tree, a, treeLen);
            else
                break;
        }
        // Put the sorted output back into the main array.
        Writes.arraycopy(out, 0, array, a, b - a, 1, true, false);
        Writes.deleteExternalArrays(out, tree);
    }
    @Override 
    public void runSort(int[] array, int length, int bucketCount) {
        Explic(array, 0, length);
    }
}