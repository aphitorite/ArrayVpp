package io.github.arrayv.visuals.bars;

import java.awt.Color;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.utils.Highlights;
import io.github.arrayv.utils.Renderer;
import io.github.arrayv.visuals.Visual;
import io.github.arrayv.visuals.features.HeatMap;

import com.scrtwpns.Mixbox;

public final class BarGraphTiled extends Visual {
    public BarGraphTiled(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);

        this.setListName("Bar Graph (Tiled Auxiliary)");
        this.setCategory("Bar Visuals");
        this.setAuxable(true);
        this.setOverlayable(true);
        this.setMaximumAuxLists(96);
        this.addSupportedFeatures("heat");
    }

    public int[] getTopPosFor(int[] array, double idx, int val, ArrayVisualizer ArrayVisualizer, Renderer Renderer) {
    	int[] box = this.getBoundingBox(array, Renderer.renderedInstances() - Renderer.isWhichArray(array), Renderer.renderedInstances(), ArrayVisualizer, Renderer);
    	int n = Renderer.getArrayLengthFor(array);
    	int trueval = ArrayVisualizer.doingStabilityCheck() && ArrayVisualizer.colorEnabled() ? ArrayVisualizer.getStabilityValue(val) : val;
    	double xs = (box[1] - box[0]) / (double) n;
        int y = (int) ((trueval + 1) * ((box[3] - box[2]) / (double) n));
    	return new int[] {
    		(int)(xs*(idx+0.5d))+box[0],
    		(int)(box[3]-y)
    	};
    }
    public int[] getBottomPosFor(int[] array, double idx, int val, ArrayVisualizer ArrayVisualizer, Renderer Renderer) {
    	int[] box = this.getBoundingBox(array, Renderer.renderedInstances() - Renderer.isWhichArray(array), Renderer.renderedInstances(), ArrayVisualizer, Renderer);
    	int n = Renderer.getArrayLengthFor(array);
    	double xs = (box[1] - box[0]) / (double) n;
    	return new int[] {
        	(int)(xs*(idx+0.5d))+box[0],
    		(int)(box[3])
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

    public int[] getBoundingBox(int[] array, int index, int length, ArrayVisualizer arrayVisualizer, Renderer renderer) {
    	int top = 39, bottom = arrayVisualizer.currentHeight() - 20,
    		left = 15, right = arrayVisualizer.currentWidth() - 20,
    		width = right - left, height = bottom - top;
    	int nmeta = length, v, vrows, vleft;
    	if(nmeta == 0) {
    		return new int[] {
    			left + 5, right,
    			top + 5, bottom
    		};
    	}
    	if(nmeta % (int)(Math.sqrt(nmeta * 2)) == 0) {
    		v = (int) Math.sqrt(nmeta * 2);
    		vrows = nmeta / v;
    	} else {
    		v = (int) Math.sqrt(nmeta - 1) + 1;
    		vrows = ((2 * nmeta - 1) / v + 1) / 2;
    	}
		vleft = nmeta - (vrows - 1) * v;
    	int vc = index == length ? 0 : index >= (vrows - 1) * v ? index - (vrows - 1) * v : index % v,
    		vcm = index == length ? 1 : index >= (vrows - 1) * v ? vleft : v,
    		vr = index == length ? vrows : index >= (vrows - 1) * v ? vrows - 1 : index / v,
    		vrm = vrows + 1;
    	return new int[] {
    		left + (width * vc) / vcm + 5, left + (width * (vc + 1)) / vcm,
    		top + (height * vr) / vrm + 5, top + (height * (vr + 1)) / vrm,
    		// extension: tile coordinates
    		vc, vcm, vr, vrm
    	};
    }

    @Override
    public void drawVisual(int[] array, int[] boundingBox, ArrayVisualizer arrayVisualizer, Renderer renderer, Highlights Highlights) {
    	// precalculate the bottom of the list area
    	int left = boundingBox[0], right = boundingBox[1],
    		top = boundingBox[2], bottom = boundingBox[3];
    	double yScale = (double) (bottom - top) / (double) (arrayVisualizer.getCurrentLength());
    	double xScale = (double) (right - left) / (double) (renderer.getArrayLength());
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

            this.mainRender.fillRect(j + left - nw, bottom - h, width + nw, h);
            j += width;
    	}
        if (arrayVisualizer.externalArraysEnabled() && boundingBox.length == 8) {
            this.mainRender.setColor(Color.BLUE);
            int X = boundingBox[4], XM = boundingBox[5], Y = boundingBox[6], YM = boundingBox[7];
            if(X + 1 < XM)
            	this.mainRender.fillRect(right + 2, top - 5, 1, bottom - top + 7);
            if(Y + 1 < YM)
            	this.mainRender.fillRect(left - 5, bottom + 2, right - left + 10, 1);
        }
    }
}
