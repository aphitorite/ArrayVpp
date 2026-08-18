package io.github.arrayv.visuals;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.utils.Highlights;
import io.github.arrayv.utils.Renderable;
import io.github.arrayv.utils.Renderer;

import java.awt.RenderingHints;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Map.Entry;
import java.awt.*;

public abstract class Visual {
	public static enum stance {
		NEVER(-1),
		AGNOSTIC(0),
		ALWAYS(1);
		public final int type;
		stance(int swing) {
			this.type = swing;
		}
	};
    private boolean visualEnabled;

    private String listName;
    private String category;
    
    private stance colorable;
    private boolean overlayable;
    private boolean auxable;
    private int maxAuxLists;
    private IdentityHashMap<RenderingHints.Key, Object> renderingHints;
    private String[] featuresSupported;
    
    protected Graphics2D mainRender;
    protected Graphics2D extraRender;

    public Visual(ArrayVisualizer arrayVisualizer) {
        this.updateRender(arrayVisualizer);
        
        this.enableVisual(true);        
        this.setListName("");
        this.setCategory("");
        this.setColorable(stance.AGNOSTIC);
        this.setAuxable(false);
        this.setOverlayable(false);
        this.setMaximumAuxLists(7);
        this.setUpRenderingHints();
        this.addSupportedFeatures();
    }

    protected void enableVisual(boolean Bool) {
        this.visualEnabled = Bool;
    }

    protected void setListName(String ID) {
        this.listName = ID;
    }

    protected void setCategory(String ID) {
        this.category = ID;
    }

    protected void setColorable(stance Stance) {
        this.colorable = Stance;
    }

    protected void setAuxable(boolean auxable) {
        this.auxable = auxable;
    }

    protected void setMaximumAuxLists(int maxCount) {
        this.maxAuxLists = maxCount;
    }

    protected void setOverlayable(boolean overlayable) {
        this.overlayable = overlayable;
    }
    
    @SuppressWarnings("serial")
	protected void addSupportedFeatures(String... fids) {
    	if (this.featuresSupported == null)
    		this.featuresSupported = new String[0];
    	String[] prevFeatures = this.featuresSupported;
    	this.featuresSupported = new ArrayList<String>() {{
    		addAll(Arrays.asList(prevFeatures));
    		addAll(Arrays.asList(fids));
    	}}.toArray(new String[0]);
    }
    
    private void setUpRenderingHints() {
    	this.renderingHints = new IdentityHashMap<>();
    }
    
    protected void addRenderingHints(Object... hints) {
    	assert hints.length % 2 == 0 : "[Visual].addRenderingHints(): Missing key or value!";
    	assert hints.length > 0 : "[Visual].addRenderingHints(): No rendering hints requested!";
    	for(int i = 0; i < hints.length; i += 2) {
    		this.renderingHints.put((RenderingHints.Key) hints[i], hints[i + 1]);
    	}
    }

    public boolean isVisualEnabled() {
        return this.visualEnabled;
    }

    public String getListName() {
        return this.listName;
    }

    public String getCategory() {
        return this.category;
    }

    public stance getColorability() {
        return this.colorable;
    }

    public boolean isAuxable() {
        return this.auxable;
    }
    public int getMaximumAuxLists() {
        return this.maxAuxLists;
    }

    public boolean isOverlayable() {
        return this.overlayable;
    }
    
    public boolean supportsExtraFeature(String name) {
    	return Arrays.asList(this.featuresSupported).contains(name);
    }

    
    public Object[][] getRenderingHints() {
    	ArrayList<Object[]> ents = new ArrayList<>();
    	for (Entry<RenderingHints.Key, Object> e : renderingHints.entrySet()) {
    		ents.add(new Object[] {e.getKey(), e.getValue()});
    	}
    	return ents.toArray(new Object[0][]);
    }

    public void updateRender(ArrayVisualizer arrayVisualizer) {
        Renderable.mainRender = this.mainRender = arrayVisualizer.getMainRender();
        this.extraRender = arrayVisualizer.getExtraRender();
    }

    public static Color getIntColor(int i, int length) {
        return Color.getHSBColor(((float) i / length), 1.0F, 1.0F);
    }
	
	public static Color getGray(int t, int n) {
		int c = (int)(255 * (double)Math.max(0, Math.min(t, n))/n);
		return new Color(c, c, c);
	}

    public static void markBar(Graphics2D bar, boolean color, boolean rainbow, boolean analysis) {
        if (color || rainbow) {
            if (analysis) bar.setColor(Color.LIGHT_GRAY);
            else          bar.setColor(Color.WHITE);
        } else if (analysis) bar.setColor(Color.BLUE);
        else                 bar.setColor(Color.RED);
    }
    private static void markBarFancy(Graphics2D bar, boolean color, boolean rainbow) {
        if (!color && !rainbow) bar.setColor(Color.RED);
        else                    bar.setColor(Color.BLACK);
    }

    public static void lineMark(Graphics2D line, double width, boolean color, boolean analysis) {
        line.setStroke(new BasicStroke((float) (9f * (width / 1280f))));
        if (color) line.setColor(Color.BLACK);
        else if (analysis) line.setColor(Color.BLUE);
        else line.setColor(Color.RED);
    }

    public static void markLineFancy(Graphics2D line, double width) {
        line.setColor(Color.GREEN);
        line.setStroke(new BasicStroke((float) (9f * (width / 1280f))));
    }

    public static void clearLine(Graphics2D line, boolean color, int[] array, int i, int length, double width) {
        if (color) line.setColor(getIntColor(array[i], length));
        else line.setColor(Color.WHITE);
        line.setStroke(new BasicStroke((float) (3f * (width / 1280f))));
    }

    public static void setRectColor(Graphics2D rect, boolean color, boolean analysis) {
        if (color) rect.setColor(Color.WHITE);
        else if (analysis) rect.setColor(Color.BLUE);
        else rect.setColor(Color.RED);
    }

    //The longer the array length, the more bars marked. Makes the visual easier to see when bars are thinner.
    public static void colorMarkedBars(int logOfLen, int index, Highlights Highlights, Graphics2D mainRender, boolean colorEnabled, boolean rainbowEnabled, boolean analysis) {
        switch(logOfLen) {
            // @checkstyle:off LeftCurlyCheck|IndentationCheck
            case 15: if (Highlights.containsPosition(index - 15)) { markBar(mainRender, colorEnabled, rainbowEnabled, analysis); break; }
                     if (Highlights.containsPosition(index - 14)) { markBar(mainRender, colorEnabled, rainbowEnabled, analysis); break; }
                     if (Highlights.containsPosition(index - 13)) { markBar(mainRender, colorEnabled, rainbowEnabled, analysis); break; }
                     if (Highlights.containsPosition(index - 12)) { markBar(mainRender, colorEnabled, rainbowEnabled, analysis); break; }
                     if (Highlights.containsPosition(index - 11)) { markBar(mainRender, colorEnabled, rainbowEnabled, analysis); break; }
            case 14: if (Highlights.containsPosition(index - 10)) { markBar(mainRender, colorEnabled, rainbowEnabled, analysis); break; }
                     if (Highlights.containsPosition(index - 9))  { markBar(mainRender, colorEnabled, rainbowEnabled, analysis); break; }
                     if (Highlights.containsPosition(index - 8))  { markBar(mainRender, colorEnabled, rainbowEnabled, analysis); break; }
            case 13: if (Highlights.containsPosition(index - 7))  { markBar(mainRender, colorEnabled, rainbowEnabled, analysis); break; }
                     if (Highlights.containsPosition(index - 6))  { markBar(mainRender, colorEnabled, rainbowEnabled, analysis); break; }
                     if (Highlights.containsPosition(index - 5))  { markBar(mainRender, colorEnabled, rainbowEnabled, analysis); break; }
            case 12: if (Highlights.containsPosition(index - 4))  { markBar(mainRender, colorEnabled, rainbowEnabled, analysis); break; }
                     if (Highlights.containsPosition(index - 3))  { markBar(mainRender, colorEnabled, rainbowEnabled, analysis); break; }
            case 11: if (Highlights.containsPosition(index - 2))  { markBar(mainRender, colorEnabled, rainbowEnabled, analysis); break; }
            case 10: if (Highlights.containsPosition(index - 1))  { markBar(mainRender, colorEnabled, rainbowEnabled, analysis); break; }
            default: if (Highlights.containsPosition(index))        markBar(mainRender, colorEnabled, rainbowEnabled, analysis);
            // @checkstyle:on LeftCurlyCheck|IndentationCheck
        }
    }

    public static void drawFancyFinish(int logOfLen, int index, int position, Graphics2D mainRender, boolean colorEnabled, boolean rainbowEnabled) {
        switch(logOfLen) {
            // @checkstyle:off LeftCurlyCheck
            case 15: if (index == position - 14) { markBarFancy(mainRender, colorEnabled, rainbowEnabled); break; }
            case 14: if (index == position - 13) { markBarFancy(mainRender, colorEnabled, rainbowEnabled); break; }
            case 13: if (index == position - 12) { markBarFancy(mainRender, colorEnabled, rainbowEnabled); break; }
            case 12: if (index == position - 11) { markBarFancy(mainRender, colorEnabled, rainbowEnabled); break; }
            case 11: if (index == position - 10) { markBarFancy(mainRender, colorEnabled, rainbowEnabled); break; }
            case 10: if (index == position - 9)  { markBarFancy(mainRender, colorEnabled, rainbowEnabled); break; }
            case 9:  if (index == position - 8)  { markBarFancy(mainRender, colorEnabled, rainbowEnabled); break; }
            case 8:  if (index == position - 7)  { markBarFancy(mainRender, colorEnabled, rainbowEnabled); break; }
            case 7:  if (index == position - 6)  { markBarFancy(mainRender, colorEnabled, rainbowEnabled); break; }
            case 6:  if (index == position - 5)  { markBarFancy(mainRender, colorEnabled, rainbowEnabled); break; }
            case 5:  if (index == position - 4)  { markBarFancy(mainRender, colorEnabled, rainbowEnabled); break; }
            case 4:  if (index == position - 3)  { markBarFancy(mainRender, colorEnabled, rainbowEnabled); break; }
            case 3:  if (index == position - 2)  { markBarFancy(mainRender, colorEnabled, rainbowEnabled); break; }
            case 2:  if (index == position - 1)  { markBarFancy(mainRender, colorEnabled, rainbowEnabled); break; }
            default: if (index == position)        markBarFancy(mainRender, colorEnabled, rainbowEnabled);
            // @checkstyle:on LeftCurlyCheck
        }
    }

    public static void drawFancyFinishLine(int logOfLen, int index, int position, Graphics2D mainRender, double width, boolean colorEnabled) {
        switch(logOfLen) {
            // @checkstyle:off LeftCurlyCheck
            case 15: if (index == position - 14) { lineMark(mainRender, width, colorEnabled, false); break; }
            case 14: if (index == position - 13) { lineMark(mainRender, width, colorEnabled, false); break; }
            case 13: if (index == position - 12) { lineMark(mainRender, width, colorEnabled, false); break; }
            case 12: if (index == position - 11) { lineMark(mainRender, width, colorEnabled, false); break; }
            case 11: if (index == position - 10) { lineMark(mainRender, width, colorEnabled, false); break; }
            case 10: if (index == position - 9)  { lineMark(mainRender, width, colorEnabled, false); break; }
            case 9:  if (index == position - 8)  { lineMark(mainRender, width, colorEnabled, false); break; }
            case 8:  if (index == position - 7)  { lineMark(mainRender, width, colorEnabled, false); break; }
            case 7:  if (index == position - 6)  { lineMark(mainRender, width, colorEnabled, false); break; }
            case 6:  if (index == position - 5)  { lineMark(mainRender, width, colorEnabled, false); break; }
            case 5:  if (index == position - 4)  { lineMark(mainRender, width, colorEnabled, false); break; }
            case 4:  if (index == position - 3)  { lineMark(mainRender, width, colorEnabled, false); break; }
            case 3:  if (index == position - 2)  { lineMark(mainRender, width, colorEnabled, false); break; }
            case 2:  if (index == position - 1)  { lineMark(mainRender, width, colorEnabled, false); break; }
            default: if (index == position)        lineMark(mainRender, width, colorEnabled, false);
            // @checkstyle:on LeftCurlyCheck
        }
    }

    public int[] getTopPosFor(int[] array, double idx, int val, ArrayVisualizer arrayVisualizer, Renderer renderer) {
    	return new int[0];
    }
    public int[] getTopPos(int[] array, int idx, ArrayVisualizer arrayVisualizer, Renderer renderer) {
    	return this.getTopPosFor(array, idx, array[idx], arrayVisualizer, renderer);
    }
    public int[] getBottomPosFor(int[] array, double idx, int val, ArrayVisualizer ArrayVisualizer, Renderer Renderer) {
    	return new int[0];
    }
    public int[] getBottomPos(int[] array, int idx, ArrayVisualizer arrayVisualizer, Renderer renderer) {
    	return this.getTopPosFor(array, idx, array[idx], arrayVisualizer, renderer);
    }

    public void bringUp() {}
    public int[] getBoundingBox(int[] array, int index, int length, ArrayVisualizer arrayVisualizer, Renderer renderer) {
    	int ysize = renderer.getViewSize();
    	return new int[] {
    		20, arrayVisualizer.currentWidth() - 20,     // X_LEFT : X_RIGHT
    		64 + ysize * index, 44 + ysize * (index + 1) // Y_TOP  : Y_BOTTOM
    	};
    }
    public int[] getBoundingBox(int[][] arrays, int index, int eff_index, int eff_length, ArrayVisualizer arrayVisualizer, Renderer renderer) {
    	return this.getBoundingBox(arrays[eff_index], index, eff_length, arrayVisualizer, renderer);
    }
    public abstract void drawVisual(int[] array, int[] boundingBox, ArrayVisualizer arrayVisualizer, Renderer renderer, Highlights highlights);
    public void pullDown() {}
}
