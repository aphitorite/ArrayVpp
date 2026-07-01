package io.github.arrayv.visuals.bars;

import java.awt.Color;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.utils.Highlights;
import io.github.arrayv.utils.Renderer;
import io.github.arrayv.visuals.Visual;
import io.github.arrayv.visuals.features.HeatMap;

import com.scrtwpns.Mixbox;

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
    
    // 0 = a only, 1 = b only
    private Color lerp(Color a, Color b, double b1) {
    	double b2 = 1d - b1;
    	return new Color(
    		(int)(a.getRed()*b2+b.getRed()*b1),
    		(int)(a.getGreen()*b2+b.getGreen()*b1),
    		(int)(a.getBlue()*b2+b.getBlue()*b1)
    	);
    }

    @Override
    public void drawVisual(int[] array, int[] boundingBox, ArrayVisualizer arrayVisualizer, Renderer renderer, Highlights Highlights) {
    	// precalculate the bottom of the list area
    	int bottom = boundingBox[3];
    	double yScale = (double) (boundingBox[3] - boundingBox[2]) / (double) (arrayVisualizer.getCurrentLength());
    	double xScale = (double) (boundingBox[1] - boundingBox[0]) / (double) (renderer.getArrayLength());
		// get lengths
		int n = renderer.getArrayLength();
		// keep booleans we're accessing here
    	boolean fancy = Highlights.fancyFinishActive(),
    			color = arrayVisualizer.colorEnabled(),
    			change = fancy || color,
    			useAltVals = arrayVisualizer.doingStabilityCheck() && color,
    			heatMap = arrayVisualizer.queryFeatureState("heat") > 0;

    	// calculate the min and max of the scales
    	double[] scl = scales(xScale, 1);
    	// keep the intended "indice length"
    	int m = Math.min(boundingBox[1] - boundingBox[0], n);
    	int val;

    	// change the array color at the start
		Color col = fancy ? Color.GREEN : Color.WHITE;
    	for (int i = 0, j = 0; i < m; i++) {
    		// get the width of the indice
    		int width = (int) ((i + 1) * scl[1] - j);
    		// get the highlight index (given the list, the indice, the upper bound, and the scale),
    		// turn it back into a raw value
    		int hm = Highlights.containsMax(array, i, n, scl[0]), v = hm < 0 ? ~hm : hm;
    		boolean hl = hm >= 0;
    		if (heatMap) col = HeatMap.getColor(array, v);
    		if (!heatMap || col == null) {
    			if (hl || Highlights.hasColor(array, v)) {
	    			// set highlight color if highlighted
					if (hl)
						col = arrayVisualizer.getHighlightColor();
					else if (color) {
						// transparent colorcode if list in color
						val = useAltVals ? arrayVisualizer.getIndexValue(array[v]) : array[v];
		                col = new Color(Mixbox.lerp(
		                	getIntColor(val, arrayVisualizer.getCurrentLength()).getRGB(),
		                	Highlights.colorAt(array, v).getRGB(),
		                	0.5f
		                ));
					} else col = Highlights.colorAt(array, v);
					change = true;
				} else if (change) {
		            if (!fancy || v >= Highlights.getFancyFinishPosition()) {
		            	if (color) {
		            		val = useAltVals ? arrayVisualizer.getIndexValue(array[v]) : array[v];
			                col = getIntColor(val, arrayVisualizer.getCurrentLength());
		            	} else {
		            		col = Color.WHITE;
		            		change = false;
		            	}
		            }
	            }
    		}
    		this.mainRender.setColor(col);
            try {
            	val = useAltVals ? arrayVisualizer.getStabilityValue(array[v]) : array[v];
            } catch(ArrayIndexOutOfBoundsException e) {
            	// fuck you
            	val = 0;
            }
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
