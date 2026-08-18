package io.github.arrayv.sorts.select;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;


final public class AphiTournamentSort extends Sort {  
    public AphiTournamentSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        
        this.setSortListName("Aphitorite's Tournament");
        this.setRunAllSortsName("Aphitorite's Tournament Sort");
        this.setRunSortName("Aphitorite's Tournamentsort");
        this.setCategory("Selection Sorts");
  	    this.setAuthors("aphitorite, Distray");
        this.setConstant("n log n");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
    }
    
	public int[] array;
    
    private class Node {
    	public int index;
    	private boolean tournament;
    	public Node parent = null,
    			 childLeft = null,
    			childRight = null,
    			    winner = null;
    	public Node(int index) {
    		this.index = index;
    		this.tournament = false;
    	}
    	public Node(boolean tournament) {
    		this.index = -1;
    		this.tournament = tournament;
    	}
    	public void build() {
    		if(childLeft.index < 0) {
    			if(childRight.index < 0) {
	        		this.index = -1;
	    			tournament = false;
	    			childLeft = childRight = null;
	    			return;
    			}
    			if(winner != childRight)
    				Writes.changeAuxWrites(1);
    			Writes.changeAuxWrites(1);
    			winner = childRight; 
    			index = childRight.index;
    		} else if(childRight.index < 0) {
    			if(winner != childLeft)
    				Writes.changeAuxWrites(1);
    			winner = childLeft; 
    			index = childLeft.index;
    		} else if(indices(childLeft.index, childRight.index) == 1) {
    			if(index != childRight.index)
    				Writes.changeAuxWrites(1);
    			winner = childRight; 
    			index = childRight.index;
    		} else {
    			if(index != childLeft.index)
    				Writes.changeAuxWrites(1);
    			Writes.changeAuxWrites(1);
    			winner = childLeft; 
    			index = childLeft.index;
    		}
    	}
    	private int indices(int a, int b) {
    		return AphiTournamentSort.this.comp(AphiTournamentSort.this.array[a], AphiTournamentSort.this.array[b]);
    	}
    }
    
	public Node build(Node left, Node right) {
		Node t = new Node(true);
		left.parent = right.parent = t;
		t.childLeft = left;
		t.childRight = right;
		Highlights.markArray(1, left.index);
		Highlights.markArray(2, right.index);
		Writes.changeAuxWrites(1);
		Writes.changeAllocAmount(1);
		Delays.sleep(2.5);
		t.build();
		return t;
	}
    
    public int comp(int a, int b) {
    	return Reads.compareValues(a, b);
    }
    
    public Node brackets(int start, int end) {
    	if(end-start == 0) {
    		Writes.changeAllocAmount(1);
    		return new Node(start);
    	}
    	if(end-start == 1) {
    		Node a = new Node(start),
    			 b = new Node(end);
    		
    		return build(a, b);
    	}
    	int mid = start + (end - start) / 2;
    	
    	Node match = build(brackets(start, mid), brackets(mid+1, end));
    	return match;
    }
    
    public void removeWinner(Node root) {
    	if(root.winner.tournament) {
    		if(root.childLeft != null && root.childLeft.index >= 0)
    			Highlights.markArray(1, root.childLeft.index);
    		if(root.childRight != null && root.childRight.index >= 0)
    			Highlights.markArray(1, root.childRight.index);
    		Delays.sleep(1.25);
    		removeWinner(root.winner);
    	} else {
			Writes.changeAllocAmount(-1);
    		root.winner.index = -1;
    		do {
    			root.build();
    			root = root.parent;
    		} while(root != null);
    	}
    }
    
    @Override 
    public void runSort(int[] array, int length, int bucketCount) {
    	this.array = array;
    	Node root = brackets(0, length-1);
    	int[] output = Writes.createExternalArray(length);
    	int x = 0;
    	while(x < length) {
    		int j = root.index;
    		Highlights.markArray(1, j);
    		Writes.write(output, x++, array[j], 1, true, true);
    		removeWinner(root);
    	}
    	Writes.arraycopy(output, 0, array, 0, length, 1, true, false);
    	Writes.deleteExternalArray(output);
    }
}