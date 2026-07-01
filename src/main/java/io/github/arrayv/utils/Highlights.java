package io.github.arrayv.utils;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.panes.JErrorPane;

import java.awt.Color;
import java.util.Arrays;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/*
 *
MIT License

Copyright (c) 2019 w0rthy

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 *
 */

public final class Highlights {
    public static final float HEAT = 0.133f;
    public static final float COOL = 0.00625f;

    // This is in desperate need of optimization.
    private volatile Map<int[], int[]> highlights;
    private volatile Map<int[], byte[]> markCounts;
    private volatile Map<int[], boolean[]> colorMarks;
    private volatile Map<int[], Color[]> colorColors;
    private volatile Map<int[], float[]> heatVals;
    
    private volatile Map<String, Color> defined;
    private static int[] main;

    private volatile int maxHighlightMarked;    // IMPORTANT: This stores the index one past the farthest highlight used, so that a value
                                                // of 0 means no highlights are in use, and iteration is more convenient.

                                                // The Highlights array is huge and slows down the visualizer if all its indices are read.
                                                // In an attempt to speed up scanning through all highlights while also giving anyone room
                                                // to use the full array, this variable keeps track of the farthest highlight in use. The
                                                // Highlights array thus only needs to be scanned up to index maxHighightMarked.

                                                // If an highlight is used with markArray() that is higher than maxPossibleMarked, the
                                                // variable is updated. If the farthest highlight is removed with clearMark(), the next
                                                // farthest highlight is found and updates maxIndexMarked.

                                                // Trivially, clearAllMarks() resets maxIndexMarked to zero. This variable also serves
                                                // as a subtle design hint for anyone who wants to add an algorithm to the app to highlight
                                                // array positions at low indices which are close together.

                                                // This way, the program runs more efficiently, and looks pretty. :)

    private volatile Map<int[], Integer> markCount;

    private boolean showFancyFinishes;
    private volatile boolean fancyFinish;
    private final AtomicInteger trackFinish = new AtomicInteger();
    
    private volatile boolean retainColorMarks = false;
    public static volatile boolean fancyFinishFix = true;

    private final ArrayVisualizer arrayVisualizer;
    private Delays Delays;

    public Highlights(ArrayVisualizer arrayVisualizer, int maximumLength) {
        this.arrayVisualizer = arrayVisualizer;

        try {
            this.defined = new HashMap<>();
            this.highlights = new IdentityHashMap<>();
            this.markCounts = new IdentityHashMap<>();
            this.colorMarks = new IdentityHashMap<>();
            this.colorColors = new IdentityHashMap<>();
            this.markCount = new IdentityHashMap<>();
            this.heatVals = new IdentityHashMap<>();
            
            main = arrayVisualizer.getArray();
            
            this.registerMarks(main);
            this.registerColors(main);
            this.registerHeat(main);
        } catch (OutOfMemoryError e) {
            JErrorPane.invokeCustomErrorMessage("Failed to allocate mark arrays. The program will now exit.");
            System.exit(1);
        }
        this.showFancyFinishes = true;
        this.maxHighlightMarked = 0;
    }

    public void postInit() {
        if (Delays != null) {
            throw new IllegalStateException();
        }
        this.Delays = arrayVisualizer.getDelays();
    }
    
    public void registerMarks(int[] array) {
    	int[] thisHL = new int[array.length];
    	byte[] thisMC = new byte[array.length];
    	highlights.put(array, thisHL);
    	markCounts.put(array, thisMC);
    	markCount.put(array, 0);

        Arrays.fill(thisHL, -1);
        Arrays.fill(thisMC, (byte)0);
    }
    
    public void unregisterMarks(int[] array) {
    	clearAllMarks(array);
    	unregisterHeat(array);
    	highlights.remove(array);
    	markCounts.remove(array);
    	markCount.remove(array);
    }
    
    public synchronized void registerHeat(int[] array) {
        try {
            if (!highlights.containsKey(array)) {
                throw new Exception("Highlights.registerHeat(): Array must be markable to use heatmaps!");
            } else {
            	float[] heat = new float[array.length];
            	Arrays.fill(heat, 0f);
            	heatVals.putIfAbsent(array, heat);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void unregisterHeat(int[] array) {
        heatVals.remove(array);
    }
    
    public synchronized void registerColors(int[] array) {
        boolean[] colorMark = new boolean[array.length];
        Color[] colorColor = new Color[array.length];
        colorMarks.putIfAbsent(array, colorMark);
        colorColors.putIfAbsent(array, colorColor);
    }
    public synchronized void unregisterColors(int[] array) {
        colorMarks.remove(array);
        colorColors.remove(array);
    }

    public boolean fancyFinishEnabled() {
        return this.showFancyFinishes;
    }
    public void toggleFancyFinishes(boolean showFancyFinishes) {
        this.showFancyFinishes = showFancyFinishes;
    }

    public boolean fancyFinishActive() {
        return this.fancyFinish;
    }
    public void toggleFancyFinish(boolean fancyFinish) {
        this.fancyFinish = fancyFinish;
    }

    public int getFancyFinishPosition() {
        return this.trackFinish.get();
    }
    public void incrementFancyFinishPosition() {
        this.trackFinish.incrementAndGet();
    }
    public void resetFancyFinish() {
        this.trackFinish.set(-1); // Magic number that clears the green sweep animation
    }

    public void toggleAnalysis(boolean analysis) {
        this.arrayVisualizer.toggleAnalysis(analysis);
    }

    public int getMaxHighlight() {
        return this.maxHighlightMarked;
    }

    public int getMarkCount(int[] array) {
        return this.markCount.getOrDefault(array, 0);
    }
    public int getMarkCount() {
        return this.getMarkCount(main);
    }

    private void decMarkCount(int[] array) {
        markCount.put(array, markCount.get(array) - 1);
    }
    private void incMarkCount(int[] array) {
        markCount.put(array, markCount.get(array) + 1);
    }

    private void incrementIndexMarkCount(int[] array, int i) {
    	byte[] thisMC = markCounts.get(array);
    	if (thisMC == null || i >= thisMC.length) return;
        if (thisMC[i] != (byte)-1) {
            if (thisMC[i] == 0) {
                incMarkCount(array);
            }
            thisMC[i]++;
        }
    }
    private void decrementIndexMarkCount(int[] array, int i) {
    	int[] thisHL = highlights.get(array);
    	byte[] thisMC = markCounts.get(array);
    	if (thisMC == null || i >= thisMC.length) return;
        if (thisMC[i] == (byte)-1) {
            int count = 0;
            for (int h = 0; h < this.maxHighlightMarked; h++) {
                if (thisHL[h] == i) {
                    count++;
                    if (count > 255) {
                        return;
                    }
                }
            }
        } else if (thisMC[i] == 0) {
        	decMarkCount(array);
        }
        thisMC[i]--;
    }

    // Consider revising highlightList().
    public int[] highlightList(int[] array) {
        return this.highlights.get(array);
    }
    public int[] highlightList() {
        return this.highlightList(main);
    }
    
    // get indices[i] given its scale
    private int ind(int i, double scl) {
    	return (int) Math.ceil((i+1)/scl-1);
    }
    
    // this costs O(h) best and O(n / w) worst [without fancy finish fix], h = highlight count
    public int containsMax(int[] array, int i, int n, double scale) {
    	byte[] thisMC = markCounts.get(array);
    	int ii = ind(i, scale);
    	if (thisMC == null || (ii >= n && n > 0)) return ~ii;
    	int[] thisHL = highlights.get(array);
    	if (thisHL == null) return ~ii;
    	if (scale == 1) {
     		return containsPosition(array, ii) ? ii : ~ii;
    	}
    	// how do i count highlights
    	// merged if statement: had the same code after the fancy finish fix
    	if (this.getMaxHighlight() > 4d / scale) {
     		// find the max by iterating through mark counts instead
    		// this would look similar to if every highlight was drawn
    		if (fancyFinishFix) {
	     		return containsPosition(array, ii) ? ii : ~ii;
    		} else {
	    		int v = ind(i - 1, scale), m = ii;
	    		for (; ++v < ii;) {
	    			if (thisMC[v] != 0 && array[v] > array[m]) m = v;
	    		}
	    		return thisMC[m] != 0 ? m : ~ii;
    		}
    	} else {
    		int l = ind(i - 1, scale) + 1;
    		int m = ~ii, v = -1;
    		for (; ++v < this.getMaxHighlight();) {
    			int vi = thisHL[v];
    			if (l <= vi && vi <= ii) {
    				m = vi;
    				break;
    			}
    		}
    		if (m < 0) return m;
	    	for (; ++v < this.getMaxHighlight(); ) {
    			int vi = thisHL[v];
    			if (l <= vi && vi <= ii && array[vi] > array[m]) {
	    			m = vi;
	    		}
    		}
    		return m;
    	}
    }
    
    public boolean containsPosition(int[] array, int arrayPosition) {
    	byte[] thisMC = markCounts.get(array);
    	if (thisMC == null || arrayPosition >= thisMC.length) return false;
        return thisMC[arrayPosition] != 0;
    }
    public boolean containsPosition(int arrayPosition) {
    	return containsPosition(main, arrayPosition);
    }
    
    private synchronized void markArrayInternal(int[] array, int marker, int markPosition) {
        try {
            if (markPosition < 0) {
                if (markPosition == -1) throw new Exception("Highlights.markArrayInternal(): Invalid position! -1 is reserved for the clearMark method.");
                else if (markPosition == -5) throw new Exception("Highlights.markArrayInternal(): Invalid position! -5 was the constant originally used to unmark numbers in the array. Instead, use the clearMark method.");
                else throw new Exception("Highlights.markArrayInternal(): Invalid position!");
            } else {
            	int[] thisHL = highlights.get(array);
            	if (thisHL == null || thisHL[marker] == markPosition) {
                    return;
                }
                Delays.disableStepping();
                if (thisHL[marker] != -1) {
                    decrementIndexMarkCount(array, thisHL[marker]);
                }
                if (!retainColorMarks) {
                    clearColor(markPosition);
                }
                thisHL[marker] = markPosition;
                this.heatUp(array, markPosition);
                incrementIndexMarkCount(array, markPosition);

                if (marker >= this.maxHighlightMarked) {
                    this.maxHighlightMarked = marker + 1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        arrayVisualizer.updateNow();
        Delays.enableStepping();
    }
    public synchronized <T> void markArray(T array, int marker, int markPosition) {
    	if (ArrayVList.class.isInstance(array))
    		markArrayInternal(((ArrayVList)array).__internal_array(), marker, markPosition);
    	else {
    		assert int[].class.isInstance(array) : "Highlights.markArray(): Not an integer array or ArrayVList!";
    		markArrayInternal((int[])array, marker, markPosition);
    	}
    }
    public synchronized void markArray(int marker, int markPosition) {
        markArrayInternal(main, marker, markPosition);
    }

    private synchronized void clearMarkInternal(int[] array, int marker) {
    	int[] thisHL = highlights.get(array);
        if (thisHL == null || thisHL[marker] == -1) {
            return;
        }
        Delays.disableStepping();
        decrementIndexMarkCount(array, thisHL[marker]);
        if (!retainColorMarks) {
            clearColor(array, thisHL[marker]);
        }
        thisHL[marker] = -1; // -1 is used as the magic number to unmark a position in the main array
        if (marker == this.maxHighlightMarked) {
            this.maxHighlightMarked = marker;
          decMax:
            while (maxHighlightMarked > 0) {
            	for (int[] a : highlights.keySet()) {
    				if (highlights.get(a).length >= maxHighlightMarked &&
    					highlights.get(a)[maxHighlightMarked-1] != -1) break decMax;
            	}
                maxHighlightMarked--;
            }
        }
        arrayVisualizer.updateNow();
        Delays.enableStepping();
    }
    public synchronized <T> void clearMark(T array, int marker) {
    	if (ArrayVList.class.isInstance(array))
    		clearMarkInternal(((ArrayVList)array).__internal_array(), marker);
    	else {
    		assert int[].class.isInstance(array) : "Highlights.clearMark(): Not an integer array or ArrayVList!";
    		clearMarkInternal((int[])array, marker);
    	}
    }
    public synchronized void clearMark(int marker) {
    	clearMarkInternal(main, marker);
    }

    public synchronized void clearAllMarksPossible() {
        Delays.disableStepping();
    	for (int[] a : highlights.keySet()) {
	        for (int i = 0; i < this.maxHighlightMarked; i++) {
	            if (highlights.get(a)[i] != -1) {
	                markCounts.get(a)[highlights.get(a)[i]] = 0;
	            }
        	}
	        Arrays.fill(this.highlights.get(a), 0, this.maxHighlightMarked, -1);
	        this.markCount.put(a, 0);
    	}
        this.maxHighlightMarked = 0;
        arrayVisualizer.updateNow();
        Delays.enableStepping();
    }

    private synchronized void clearAllMarksInternal(int[] array) {
        Delays.disableStepping();
        int[] thisHL = highlights.get(array);
        byte[] thisMC = markCounts.get(array);
        if(thisHL == null) return;
        for (int i = 0; i < this.maxHighlightMarked; i++) {
            if (thisHL[i] != -1) {
                thisMC[thisHL[i]] = 0;
            }
    	}
        Arrays.fill(thisHL, 0, this.maxHighlightMarked, -1);
        this.markCount.put(array, 0);

      decMax:
		while (maxHighlightMarked > 0) {
			for (int[] a : highlights.keySet()) {
				if (highlights.get(a).length >= maxHighlightMarked &&
					highlights.get(a)[maxHighlightMarked-1] != -1) break decMax;
		  	}
		    maxHighlightMarked--;
		}

        arrayVisualizer.updateNow();
        Delays.enableStepping();
    }
    public synchronized <T> void clearAllMarks(T array) {
    	if (ArrayVList.class.isInstance(array))
    		clearAllMarksInternal(((ArrayVList)array).__internal_array());
    	else {
    		assert int[].class.isInstance(array) : "Highlights.clearAllMarks(): Not an integer array or ArrayVList!";
    		clearAllMarksInternal((int[])array);
    	}
    }
    public synchronized void clearAllMarks() {
        clearAllMarksInternal(main);
    }
    
    public synchronized void __transferMarkInfo(int[] src, int[] dest) {
    	System.arraycopy(highlights.get(src), 0, highlights.get(dest), 0, Math.min(src.length, dest.length));
    	System.arraycopy(markCounts.get(src), 0, markCounts.get(dest), 0, Math.min(src.length, dest.length));
    	if (heatVals.containsKey(src) && heatVals.containsKey(dest))
    		System.arraycopy(heatVals.get(src), 0, heatVals.get(dest), 0, Math.min(src.length, dest.length));
    	markCount.put(dest, markCount.get(src));
    }
    
    public synchronized void __cutRange(int[] array, int from, int to) {
    	int[] thisHL = highlights.get(array);
    	for (int i = 0; i < this.maxHighlightMarked; i++) {
    		if (from <= thisHL[i] && thisHL[i] < to)
    			clearMarkInternal(array, i);
    		else if (to <= thisHL[i]) {
    			int HL = thisHL[i];
    			clearMarkInternal(array, i);
    			markArrayInternal(array, i, HL - to + from);
    		}
    	}
    }

    public synchronized boolean isRetainingColorMarks() {
        return retainColorMarks;
    }

    public synchronized void retainColorMarks(boolean retainColorMarks) {
        this.retainColorMarks = retainColorMarks;
    }

    public Set<String> getDeclaredColors() {
        return defined.keySet();
    }
    
    public Color getColorFromName(String color) {
        return defined.getOrDefault(color, Color.WHITE);
    }
    
    public synchronized void defineColor(String alias, Color col) {
        defined.put(alias, col);
    }

    public synchronized void clearColorList() {
        defined.clear();
        retainColorMarks = false;
    }

    public synchronized boolean[] getColorMarks(int[] array) {
        return colorMarks.get(array);
    }

    public synchronized Color[] getColorColors(int[] array) {
        return colorColors.get(array);
    }

    public synchronized boolean hasColor(int[] array, int position) {
        return colorMarks.containsKey(array) && getColorMarks(array)[position];
    }
    public synchronized boolean hasColor(int position) {
        return hasColor(main, position);
    }

    public synchronized Color colorAt(int[] array, int position) {
        return getColorColors(array)[position];
    }
    public synchronized Color colorAt(int position) {
        return colorAt(main, position);
    }

    // Ambitious function: Set the color directly
    public synchronized void setRawColor(int[] array, int position, Color color) {
        try {
            if (position < 0) {
                throw new Exception("Highlights.setRawColor(): Invalid position!");
            } else {
                boolean[] colorMark = getColorMarks(array);
                Delays.disableStepping();
                if (colorMark != null) {
                    colorMark[position] = true;
                    getColorColors(array)[position] = color;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        arrayVisualizer.updateNow();
        Delays.enableStepping();
    }

    // Convenience function: Set the color using a predefined alias
    public synchronized void setRawColor(int position, Color color) {
        setRawColor(main, position, color);
    }

    // Convenience function: Set the color using a predefined alias
    public synchronized void writeColor(int[] fromArray, int fromPosition, int[] toArray, int toPosition) {
        try {
            Delays.disableStepping();
            if (colorMarks.containsKey(fromArray) && colorMarks.containsKey(toArray)) {
                getColorMarks(toArray)[toPosition] = getColorMarks(fromArray)[fromPosition];
                getColorColors(toArray)[toPosition] = getColorColors(fromArray)[fromPosition];
            } else {
                throw new Exception("Highlights.writeColor(): One or more arrays not registered for colorcoding!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        arrayVisualizer.updateNow();
        Delays.enableStepping();
    }

    // Convenience function: Set the color using a predefined alias
    public synchronized void colorCode(int[] array, int position, String color) {
        try {
            if (position < 0) {
                throw new Exception("Highlights.colorCode(): Invalid position!");
            } else {
                boolean[] colorMark = getColorMarks(array);
                Delays.disableStepping();
                if (colorMark != null) {
                    colorMark[position] = true;
                    getColorColors(array)[position] = getColorFromName(color);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        arrayVisualizer.updateNow();
        Delays.enableStepping();
    }

    // Convenience function: Set the color using a predefined alias
    public synchronized void colorCode(int position, String color) {
        colorCode(main, position, color);
    }

    // Convenience function 2: Batch-colorcode a set of positions under one common name
    public synchronized void colorCode(int[] array, String color, int... positions) {
        for (int i : positions) {
            colorCode(array, i, color);
        }
    }

    // Convenience function 3: Batch-colorcode a set of positions under one common color
    public synchronized void colorCode(int[] array, Color color, int... positions) {
        for (int i : positions) {
            setRawColor(array, i, color);
        }
    }

    // Convenience function 4: Batch-colorcode a set of positions under one common name
    public synchronized void colorCode(String color, int... positions) {
        colorCode(main, color, positions);
    }

    // Convenience function 5: Batch-colorcode a set of positions under one common color
    public synchronized void colorCode(Color color, int... positions) {
        colorCode(main, color, positions);
    }

    public synchronized void clearColor(int[] array, int position) {
        boolean[] colorMark = getColorMarks(array);
        if (colorMark == null)
            return;
        Delays.disableStepping();
        if (colorMark[position]) {
            colorMark[position] = false;
            getColorColors(array)[position] = null;
        }
        arrayVisualizer.updateNow();
        Delays.enableStepping();
    }
    public synchronized void clearColor(int position) {
        clearColor(main, position);
    }

    public void swapColors(int[] array, int locA, int locB) {
        boolean[] colorMark = getColorMarks(array);
        Color[] colorColor = getColorColors(array);
        if (colorMark == null)
            return;
        boolean t0 = colorMark[locA];
        Color t1 = colorColor[locA];
        colorMark[locA] = colorMark[locB];
        colorMark[locB] = t0;
        colorColor[locA] = colorColor[locB];
        colorColor[locB] = t1;
    }
    public void swapColors(int locA, int locB) {
        swapColors(main, locA, locB);
    }
    
    public synchronized void clearAllColors(int[] array) {
        Delays.disableStepping();
        Arrays.fill(getColorMarks(array), false);
        arrayVisualizer.updateNow();
        Delays.enableStepping();
    }
    public synchronized void clearAllColors() {
        clearAllColors(main);
    }
    public synchronized void clearAllColorsReferenced() {
        for (boolean[] list : colorMarks.values()) {
            Arrays.fill(list, false);
        }
    }

    public synchronized float[] getHeatmap(int[] array) {
        return heatVals.get(array);
    }
    
    public synchronized float heatAt(int[] array, int position) {
    	if(!heatVals.containsKey(array)) return -1f;
        return getHeatmap(array)[position];
    }
    public synchronized float heatAt(int position) {
        return heatAt(main, position);
    }
    
    public synchronized void heatUp(int[] array, int heatPosition) {
    	float[] thisHM = heatVals.get(array);
    	if (thisHM == null || heatPosition < 0 || heatPosition >= array.length)
            return;
		thisHM[heatPosition] = 1f - ((1f - thisHM[heatPosition]) * (1f - HEAT));
    }
    
    public synchronized void coolDown(int[] array, int n) {
    	float[] thisHM = heatVals.get(array);
    	if (thisHM == null)
            return;
		for (int i = 0; i < n; i++) {
			if (!containsPosition(array, i))
				thisHM[i] *= 1f - COOL;
		}
    }
}
