package io.github.arrayv.utils;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.visuals.Visual;
import io.github.arrayv.visuals.VisualFeature;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
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

// TODO: Many of these methods should exist solely in visual classes

public final class Renderer {
    private static final class WindowState {
        private final boolean windowUpdated;
        private final boolean windowResized;

        private WindowState(boolean windowUpdate, boolean windowResize) {
            this.windowUpdated = windowUpdate;
            this.windowResized = windowResize;
        }

        public boolean updated() {
            return this.windowUpdated;
        }

        public boolean resized() {
            return this.windowResized;
        }
    }

    private volatile double xScale;
    private volatile double yScale;

    private final AtomicInteger yoffset = new AtomicInteger();
    private volatile int vsize;
    private volatile boolean auxActive;

    private volatile int length;

    private volatile int amt;

    private int linkedpixdrawx; //TODO: Change names
    private int linkedpixdrawy;

    private int doth; //TODO: Change names
    private int dotw;
    private int dots; //TODO: Change name to dotDims/dotDimensions
    
    private static ArrayVisualizer ownArrayVisualizer;
    private static List<? super Renderable> renderables;
    private static volatile int[][] arraysLast = new int[0][];
    private static volatile int countLast = 0;

    public Renderer(ArrayVisualizer arrayVisualizer) {
    	ownArrayVisualizer = arrayVisualizer;
        arrayVisualizer.setWindowHeight();
        arrayVisualizer.setWindowWidth();
        renderables = Collections.synchronizedList(new ArrayList<>());
    }

    public double getXScale() {
        return this.xScale;
    }
    public double getYScale() {
        return this.yScale;
    }
    public int getOffset() {
        return this.amt;
    }
    public int getYOffset() {
        return this.yoffset.get();
    }
    public int getViewSize() {
        return this.vsize;
    }
    public int halfViewSize() {
        return this.vsize / 2;
    }
    public int getArrayLength() {
        return this.length;
    }
    public int getDotWidth() {
        return this.dotw;
    }
    public int getDotHeight() {
        return this.doth;
    }
    public int getDotDimensions() {
        return this.dots;
    }
    public int getLineX() {
        return this.linkedpixdrawx;
    }
    public int getLineY() {
        return this.linkedpixdrawy;
    }
    public static boolean visualSupportsRenderables() {
    	return ownArrayVisualizer.getActiveVisual().isOverlayable();
    }

    public static void registerRenderable(Renderable r) {
    	renderables.add(r);
    }

    public static void registerRenderables(Renderable... r) {
    	renderables.addAll(Arrays.asList(r));
    }

    public static void unregisterRenderable(Renderable r) {
    	while(renderables.remove(r));
    }

    public static void unregisterAllRenderables() {
    	renderables.clear();
    }

    public static int[] renderedInstance(int index) {
    	return index < 0 || index >= arraysLast.length ? null : arraysLast[index];
    }

    public int renderedInstances() {
    	return countLast;
    }
    
    public <T> int isWhichArray(T array) {
    	if(ArrayVList.class.isInstance(array)) {
    		int v = ownArrayVisualizer.getArrayVLists().indexOf((ArrayVList) array);
    		return v < 0 ? v : v + ownArrayVisualizer.getArrays().size();
    	}
    	return ownArrayVisualizer.getArrays().indexOf((int[]) array);
    }
    
    public <T> int[] renderedInstanceOf(T array) {
    	int which = isWhichArray(array);
    	return renderedInstance(which);
    }
    
    public <T> int getArrayLengthFor(T array) {
    	int which = isWhichArray(array);
    	return which == 0 ? ownArrayVisualizer.getCurrentLength() : which < 0 ? 0 : renderedInstance(which).length;
    }

    public void setOffset(int amount) {
        this.amt = amount;
    }
    public void setLineX(int x) {
        this.linkedpixdrawx = x;
    }
    public void setLineY(int y) {
        this.linkedpixdrawy = y;
    }

    public static void createRenders(ArrayVisualizer arrayVisualizer) {
        arrayVisualizer.createImage();
        arrayVisualizer.fillVisual();
        arrayVisualizer.setMainRender();
        arrayVisualizer.setExtraRender();
        arrayVisualizer.updateRendersForActiveVisual();
    }

    public static void initializeVisuals(ArrayVisualizer arrayVisualizer) {
        Renderer.createRenders(arrayVisualizer);
        arrayVisualizer.repositionFrames();
    }

    public static void updateGraphics(ArrayVisualizer arrayVisualizer) {
        Renderer.createRenders(arrayVisualizer);
        arrayVisualizer.updateRendersForActiveVisual();
    }

    private static WindowState checkWindowResizeAndReposition(ArrayVisualizer arrayVisualizer) {
        boolean windowUpdate = false;
        boolean windowResize = false;

        if (arrayVisualizer.currentHeight() != arrayVisualizer.windowHeight()) {
            windowUpdate = true;
            windowResize = true;
        }
        if (arrayVisualizer.currentWidth() != arrayVisualizer.windowWidth()) {
            windowUpdate = true;
            windowResize = true;
        }
        if (arrayVisualizer.currentX() != arrayVisualizer.windowXCoordinate()) {
            windowUpdate = true;
        }
        if (arrayVisualizer.currentY() != arrayVisualizer.windowYCoordinate()) {
            windowUpdate = true;
        }

        return new WindowState(windowUpdate, windowResize);
    }

    public void updateVisualsStart(ArrayVisualizer arrayVisualizer) {
        WindowState windowState = checkWindowResizeAndReposition(arrayVisualizer);

        if (windowState.updated()) {
            arrayVisualizer.repositionFrames();
            arrayVisualizer.updateCoordinates();

            /*
            if (v != null && v.isVisible())
                v.reposition();
            */

            if (windowState.resized()) {
                arrayVisualizer.updateDimensions();
                updateGraphics(arrayVisualizer);
            }
        }

        arrayVisualizer.renderBackground();

        //CURRENT = WINDOW
        //WINDOW = C VARIABLES

        this.yScale = (double) (this.vsize) / arrayVisualizer.getCurrentLength();

        this.dotw = (int) (2 * (arrayVisualizer.currentWidth()  / 640.0));
        
        this.yoffset.set(64);
        
        this.vsize = (arrayVisualizer.currentHeight() - this.yoffset.get()) / (arrayVisualizer.externalArraysEnabled() ? Math.min(arrayVisualizer.getArrays().size() + arrayVisualizer.getArrayVLists().size(), arrayVisualizer.getActiveVisual().getMaximumAuxLists() + 1) : 1);
    }

    private void updateVisualsPerArray(ArrayVisualizer arrayVisualizer, int[] array, int length) {

        //CURRENT = WINDOW
        //WINDOW = C VARIABLES

        this.xScale = (double) (arrayVisualizer.currentWidth() - 40) / length;

        this.amt = 0; //TODO: rename to barCount

        this.linkedpixdrawx = 0;
        this.linkedpixdrawy = 0;

        this.doth = (int) (2 * (this.vsize / 480.0));
        this.dots = (this.dotw + this.doth) / 2; //TODO: Does multiply/divide by 2 like this cancel out??

        this.length = length;

        arrayVisualizer.resetMainStroke();
    }

    public void drawVisual(Visual activeVisual, int[][] arrays, ArrayVisualizer arrayVisualizer, Highlights Highlights) {
    	arraysLast = arrays;
    	int vis_count = countLast = arrayVisualizer.externalArraysEnabled() ? Math.min(arrays.length - 1, activeVisual.getMaximumAuxLists()) : 0;
    	int arrays_count = arrayVisualizer.getArrays().size();
    	int[] box;
        for (VisualFeature f : arrayVisualizer.getVisualFeatures()) {
        	if (arrayVisualizer.queryFeatureState(f.getListID()) > 0)
        		f.globalPrerender();
        }
        if (arrayVisualizer.externalArraysEnabled()) {
            this.auxActive = true;
            for (int i = vis_count; i > 0; i--) {
                if (arrays[i] != null && arrays[i].length > 0) {
                    this.updateVisualsPerArray(arrayVisualizer, arrays[i], i >= arrays_count ? arrayVisualizer.getArrayVLists().get(i - arrays_count).size() : arrays[i].length);
                    box = activeVisual.getBoundingBox(arrays, vis_count - i, i, vis_count, arrayVisualizer, this);
                    for (VisualFeature f : arrayVisualizer.getVisualFeatures()) {
                    	if (arrayVisualizer.queryFeatureState(f.getListID()) > 0)
                    		f.localPrerender(arrays[i], this.length);
                    }
                    activeVisual.drawVisual(arrays[i], box, arrayVisualizer, this, Highlights);
                    for (VisualFeature f : arrayVisualizer.getVisualFeatures()) {
                    	if (arrayVisualizer.queryFeatureState(f.getListID()) > 0)
                    		f.localPostrender(arrays[i], this.length);
                    }
                    this.yoffset.addAndGet(this.vsize); // for compatibility
                }
            }
            this.auxActive = false;
        }
        this.updateVisualsPerArray(arrayVisualizer, arrays[0], arrayVisualizer.getCurrentLength());
        box = activeVisual.getBoundingBox(arrays, vis_count, 0, vis_count, arrayVisualizer, this);
        for (VisualFeature f : arrayVisualizer.getVisualFeatures()) {
        	if (arrayVisualizer.queryFeatureState(f.getListID()) > 0)
        		f.localPostrender(arrays[0], this.length);
        }
        activeVisual.drawVisual(arrays[0], box, arrayVisualizer, this, Highlights);
        for (VisualFeature f : arrayVisualizer.getVisualFeatures()) {
        	if (arrayVisualizer.queryFeatureState(f.getListID()) > 0)
        		f.localPostrender(arrays[0], this.length);
        }
        if(visualSupportsRenderables()) {
        	synchronized (renderables) {
        		Iterator<? super Renderable> it = renderables.iterator();
		        while(it.hasNext()) {
		        	((Renderable)it.next()).render(arrays[0], arrayVisualizer, this, Highlights);
		        }
        	}
        }
        for (VisualFeature f : arrayVisualizer.getVisualFeatures()) {
        	if (arrayVisualizer.queryFeatureState(f.getListID()) > 0)
        		f.globalPostrender();
        }
    }

    public boolean isAuxActive() {
        return auxActive;
    }

    public void setAuxActive(boolean auxActive) {
        this.auxActive = auxActive;
    }
}
