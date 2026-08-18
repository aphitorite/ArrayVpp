package io.github.arrayv.sorts.exchange;

import java.util.HashMap;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;

final public class JerrysBubbleSort extends Sort {
    public JerrysBubbleSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        
        this.setSortListName("Jerry's Bubble Sort");
        this.setRunAllSortsName("Jerry's Bubble Sort (you know the place)");
        this.setRunSortName("Jerry's Bubblesort (you know the place)");
        this.setCategory("Exchange Sorts");
		this.setAuthors("Distray");
        this.setConstant("n^3");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
    }
    
    class Frame {
    	public int[] array;
    	private int[] state;
    	public int scanStep;
    	public int scanLen;
    	public boolean looping;
    	public Frame(int[] array, int i, int j, int n) {
    		this.array = array;
    		this.state = new int[n];
    		this.scanStep = i;
    		this.scanLen = j;
    		this.looping = false;
    	}
    	public void revert() {
    		Writes.arraycopy(state, 0, array, 0, state.length, 0, false, false);
    		this.looping = true;
    	}
    	public void free() {
    		Writes.deleteExternalArray(this.state);
    	}
    	public Frame clone() {
    		Frame cl = new Frame(array, scanStep, scanLen, state.length);
    		cl.state = Writes.copyOfArray(array, state.length);
    		Writes.changeAuxWrites(state.length);
    		return cl;
    	}
    	public boolean singleStep() {
    		if(scanStep == scanLen - 1) {scanStep = 0; scanLen--;}
    		boolean v = Reads.compareIndices(array, scanStep, scanStep + 1, 0.05, true) > 0;
    		if(v) Writes.swap(array, scanStep, scanStep + 1, 0.05, true, false);
    		scanStep++;
    		return v;
    	}
    	public boolean atTarget(Frame now) {
    		if(now == null) return true;
    		return (scanStep > now.scanStep && scanLen == now.scanLen) || (scanLen < now.scanLen);
    	}
    }

    private boolean hasKey(int v, HashMap<Integer, Frame> steps) {
    	return steps.containsKey((Integer)(v));
    }

    private void freeIfPresent(int v, HashMap<Integer, Frame> steps) {
    	if(steps.containsKey((Integer)(v))) steps.get(v).free();
    }
    private Frame[] bubbleStep(Frame current, Frame next, HashMap<Integer, Frame> steps) {
    	if(current.scanLen == 1) return null;
    	if(!current.singleStep() && !current.looping && hasKey(current.array[current.scanStep], steps)) {
    		Frame now = steps.get(current.array[current.scanStep]);
    		now.revert();
    		return new Frame[] {now, current};
    	}
    	if(current.atTarget(next)) {
    		current.looping=false;
    		freeIfPresent(current.array[current.scanStep-1], steps);
    		freeIfPresent(current.array[current.scanStep], steps);
    		steps.put(current.array[current.scanStep-1], current.clone());
    		steps.put(current.array[current.scanStep], current.clone());
    	}
    	return new Frame[] {current, next};
    }
    
    @Override
    public void runSort(int[] array, int length, int bucketCount) {
    	HashMap<Integer, Frame> steps = new HashMap<>();
    	Frame[] now = new Frame[] {
    		new Frame(array, 0, length, length),
    		null
    	};
    	while((now = bubbleStep(now[0], now[1], steps)) != null);
    }
}