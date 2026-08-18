package io.github.arrayv.sorts.select;

import java.util.Arrays;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;


final public class OptimizedExpliciumSort extends Sort {  
    public OptimizedExpliciumSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        
        this.setSortListName("Explicium -O2");
        this.setRunAllSortsName("Optimized Explicium Sort");
        this.setRunSortName("Optimized Expliciumsort");
        this.setCategory("Selection Sorts");
  	    this.setAuthors("Distray");
        this.setConstant("n log n");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
    }

    private static final int FINAL = 0x2;
    private static final int DELETED/*!!*/ = 0x3;
    
    // 8 classes per val (max per int = 16, max per long = 32)
    private int CPV = 8;
    
    // Expliciumsort: A variant of Classic Tournament Sort that runs faster.
    // *Made by Distray*

    private int getClass(int[] tree, int idx) {
        int shift = 2 * (idx % CPV), tid = idx / CPV;
        if(tid >= tree.length) return DELETED;
        return (tree[tid] >>> shift) & 0x3;
    }
    private void setClass(int[] tree, int idx, int s) {
        int shift = 2 * (idx % CPV), tid = idx / CPV;
        if(tid < tree.length)
            Writes.write(tree, tid, (tree[tid] & ~(3 << shift)) | (s << shift), 0.5, true, true);
    }
    // Dig for the winning node of the tree in O(log n) time
    private int digFor(int[] tree, int idx, int fv, int n) {
        int ci;
        boolean isValid = false;
        // Repeat until a special node is hit:
        while((ci = getClass(tree, idx)) != DELETED) {
            // Ensure final nodes are treated as valid
            isValid = true;
            if(ci == FINAL) break;
            // Go down a layer in the tree
            int m = n / 2 + 1;
            if(ci == 0x1) {
                fv += m;
                n -= m;
            } else n = m - 1;
            idx = 2 * idx + 1 + ci;
        }
        // If valid, return the location of the found node in the main list
        return isValid ? fv : -1;
    }
    // Find a node in the tree without reading the classes
    private int[] digReadless(int a, int from, int n, int d) {
        int fv = a;
        // until the desired depth:
        while(d-- > 0) {
            // simulate digging through the tree until the target position is reached.
            int m = n / 2 + 1;
            if(from - fv >= m) {
                fv += m;
                n -= m;
            } else n = m - 1;
        }
        // res[0]: location of node in main list
        // res[1]: size of left branch
        // res[2]: size of right branch
        return new int[] {fv, n <= 2 ? 1 : n / 2 + 1, (n - 1) / 2};
    }

    private int make(int[] array, int[] tree, int a, int b, int c, int idx, int d) {
        Writes.recordDepth(d++);
        if(a >= c)
            return -1;
        if(a >= b) {
            Highlights.markArray(1, a);
            return a;
        }
        int nxt = 2 * idx + 1,   // Location of left child in tree
            m = a + (b - a) / 2; // Middle value
        
        // Build the two children.
        Writes.recursion();
        int l = make(array, tree, a,   m, c, nxt,   d);
        Writes.recursion();
        int r = make(array, tree, m+1, b, c, nxt+1, d);
        
        // Get the winning node, and set the parent to point to them.
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
    
    // given the structure of this, it is likely O(log^2 n) time, O(log n) best.
    private void removeWinner(int[] array, int[] tree, int a, int n) {
        int ci;
        int idx = 0, from = a, d = 0, np = n;
        
        // Dig for the winning node.
        while((ci = getClass(tree, idx)) < FINAL) {
            d++;
            int m = n / 2 + 1;
            if(ci == 0x1) {
                from += m;
                n -= m;
            } else n = m - 1;
            idx = 2 * idx + 1 + ci;
        }
        
        // First layer:
        // Remove the winning node if its sibling still exists.
        int pidx = (idx - 1) / 2, pc = getClass(tree, pidx);
        boolean hasOrphan = getClass(tree, 2 * pidx + 2 - pc) == DELETED;
        if(!hasOrphan) {
            setClass(tree, idx, DELETED);
            setClass(tree, pidx, pc ^ 1);
        }
        // Go up a layer.
        idx = pidx; d--;
        
        // Second onwards:
        while(idx > 0) {
            // Get the parent index.
            pidx = (idx - 1) / 2; d--;

            // Find our current parent in the tree readlessly, get branch info
            int[] qv = digReadless(a, from, np, d);
            int q = qv[0], i = qv[1], j = qv[2];
            
            // Get the winning nodes for both branches.
            // We mark our orphan node as invalid here to ensure only the topmost node is pruned.
            int vl = hasOrphan && 2 * pidx + 1 == idx ? -1 : digFor(tree, 2 * pidx + 1, q, i - 1);
            int vr = hasOrphan && 2 * pidx + 2 == idx ? -1 : digFor(tree, 2 * pidx + 2, q + i, j);
            int bb = (vl <= -1 ? 1 : 0) + (vr <= -1 ? 2 : 0);
            
            // Get the parent's winning branch.
            pc = getClass(tree, pidx);
            
            // If the "winning" branch is deleted or an orphan node, but the sibling branch is okay:
            if(bb - 1 == pc) {
                // If we still have our orphan node, prune it
                if(hasOrphan) {
                        setClass(tree, idx, DELETED);
                        hasOrphan = false;
                }
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
        int treeLen = (1 << (32 - Integer.numberOfLeadingZeros(b - a - 1))) - 1;
        
        int[] tree = Writes.createExternalArray((2 * treeLen) / CPV + 1), // Tree of indices
              out = Writes.createExternalArray(b - a); // Sorted result
        Arrays.fill(tree, CPV == 16 ? 0xAAAAAAAA : (0xAAAAAAAA & ((1 << (2 * CPV)) - 1)));
        Writes.changeAuxWrites(tree.length);
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