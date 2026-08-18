package io.github.arrayv.visuals.bars;

import java.awt.Color;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.utils.Highlights;
import io.github.arrayv.utils.Renderer;
import io.github.arrayv.visuals.Visual;
import io.github.arrayv.visuals.templates.Colorize;

public final class BarGraph extends Visual {
    public BarGraph(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);

        this.setListName("Bar Graph");
        this.setCategory("Bar Visuals");
        this.setAuxable(true);
        this.setOverlayable(true);
        this.addSupportedFeatures("heat");
    }

    public int[] getTopPosFor(int[] array, double idx, int val, ArrayVisualizer ArrayVisualizer, Renderer Renderer) {
    	int trueval = ArrayVisualizer.doingStabilityCheck() && ArrayVisualizer.colorEnabled() ? ArrayVisualizer.getStabilityValue(val) : val;
        int y = (int) (((Renderer.getViewSize() - 20)) - (trueval + 1) * Renderer.getYScale());
    	return new int[] {
    		(int)(Renderer.getXScale()*(idx+0.5d))+20,
    		(int)(Renderer.getYOffset()+y)
    	};
    }
    public int[] getBottomPosFor(int[] array, double idx, int val, ArrayVisualizer ArrayVisualizer, Renderer Renderer) {
    	return new int[] {
    		(int)(Renderer.getXScale()*(idx+0.5d))+20,
    		(int)(Renderer.getYOffset() + Renderer.getViewSize() - 20)
    	};
    }
    // returns wgre
    private double[] scales(double a, double b) {
    	double[] s = new double[] {b, b};
    	s[a>b?1:0]=a;
    	return s;
    }

    @Override
    public void drawVisual(int[] array, int[] boundingBox, ArrayVisualizer arrayVisualizer, Renderer renderer, Highlights Highlights) {
    	// precalculate the bottom of the list area
    	int bottom = boundingBox[3];
    	double yScale = (double) (boundingBox[3] - boundingBox[2]) / (double) (arrayVisualizer.getCurrentLength());
    	double xScale = (double) (boundingBox[1] - boundingBox[0]) / (double) (renderer.getArrayLength());
		// get lengths
		int n = renderer.getArrayLength(), nmain = arrayVisualizer.getCurrentLength();
		// keep booleans we're accessing here
    	boolean color = arrayVisualizer.colorEnabled(),
    			useAltVals = arrayVisualizer.doingStabilityCheck() && color;

    	// calculate the min and max of the scales
    	double[] scl = scales(xScale, 1);
    	// keep the intended "indice length"
    	int m = Math.min(boundingBox[1] - boundingBox[0], n);
    	int val;

    	for (int i = 0, j = 0; i < m; i++) {
    		// get the width of the indice
    		int width = (int) ((i + 1) * scl[1] - j);
    		// get the highlight index (given the list, the indice, the upper bound, and the scale),
    		// turn it back into a raw value
    		int hm = Highlights.containsMax(array, i, n, scl[0]), v = hm < 0 ? ~hm : hm;
    		boolean hl = hm >= 0;
    		this.mainRender.setColor(
    			Colorize.bestFit(array, v, nmain,
    				Colorize::heatmap,
    				Colorize::fancyFinish,
    				Colorize::hue,
    				Colorize::snow
    			)
    		);
            val = useAltVals ? arrayVisualizer.getStabilityValue(array[v]) : array[v];
            int h = (int) ((val + 1) * yScale);
            int nw = hl && width == 1 ? 1 : 0;

            this.mainRender.fillRect(j + boundingBox[0] - nw, bottom - h, width + nw, h);
            j += width;
    	}
        if (arrayVisualizer.externalArraysEnabled()) {
            this.mainRender.setColor(Color.BLUE);
            this.mainRender.fillRect(0, bottom, arrayVisualizer.currentWidth(), 1);
        }
    }
}
