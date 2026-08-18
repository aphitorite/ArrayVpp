package io.github.arrayv.sorts.exchange;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;

final public class DeepPopSort extends Sort {
    public DeepPopSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        this.setSortListName("Deep Pop");
        this.setRunAllSortsName("Deep Pop Sort");
        this.setRunSortName("Deep Popsort");
        this.setCategory("Exchange Sorts");
		this.setAuthors("Distray");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setQuestion("Set Pop order:", 1);
        this.setBogoSort(false);
    }
    
    // Bruh Moment Sort - Order n Pop Sort
    protected void bubbleSort(int[] array, int start, int end, boolean right) {
    	int swap = end, comp = right ? 1 : -1;
    	while(swap > start) {
    		int lastSwap = start;
    		for(int i=start; i<swap-1; i++) {
    			if(Reads.compareValues(array[i], array[i+1]) == comp) {
    				Writes.swap(array, i, i+1, 0.025, true, false);
    				lastSwap = i+1;
    			}
    		}
    		swap = lastSwap;
    	}
    }
    protected void bubblePop(int[] array, int start, int end, boolean right) {
    	int swap = end, comp = right ? 1 : -1;
    	while(swap > start) {
    		int lastSwap = start;
    		for(int i=start; i<swap-1; i++) {
    			if(Reads.compareValues(array[i], array[i+1]) == comp) {
    				Writes.swap(array, i, i+1, 0.025, true, false);
    				lastSwap = i+1;
    			} else if(lastSwap > start)
    				break;
    		}
    		swap = lastSwap;
    	}
    }
    protected void pop(int[] array, int start, int end, int order, boolean invert) {
    	if(start >= end)
    		return;
    	if(end-start <= 4 || order < 1) {
    		this.bubbleSort(array, start, end, !invert);
    		return;
    	}
    	int quarter = (end - start + 1) / 4, half = (end - start + 1) / 2;
    	if(order == 1) {
    		this.pop(array, start, start + quarter, order, !invert);
    		this.pop(array, start + quarter, start + half, order, invert);
    		this.pop(array, start + half, end - quarter, order, !invert);
    		this.pop(array, end - quarter, end, order, invert);
    		this.pop(array, start, start + half, order, !invert);
    		this.pop(array, start + half, end, order, invert);
    		this.bubblePop(array, start, end, !invert);
    	} else {
    		this.pop(array, start, start+quarter, order, invert);
    		this.pop(array, start+quarter, start+half, order, !invert);
    		this.pop(array, start+half, end-quarter, order, invert);
    		this.pop(array, end-quarter, end, order, !invert);
    		this.pop(array, start, start+half, order, invert);
    		this.pop(array, start+half, end, order, !invert);
    		this.pop(array, start, end, order-1, invert);
    	}
    }
    
    @Override
    public void runSort(int[] array, int currentLength, int pop) {
    	this.pop(array, 0, currentLength, pop, false);
    }
}