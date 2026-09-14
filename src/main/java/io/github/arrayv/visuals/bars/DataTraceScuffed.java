package io.github.arrayv.visuals.bars;

import java.awt.Color;
import java.awt.image.BufferedImage;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.utils.Highlights;
import io.github.arrayv.utils.Renderer;
import io.github.arrayv.visuals.Visual;
import com.scrtwpns.Mixbox;

final public class DataTraceScuffed extends Visual {
    public DataTraceScuffed(ArrayVisualizer ArrayVisualizer) {
        super(ArrayVisualizer);

        this.setListName("Data Trace");
        this.setCategory("Miscellaneous Visuals");
    }
    
    private static int MAX_HEIGHT = 2160;
    
    private Color[][] trace = new Color[MAX_HEIGHT][0];
    private int bloc = 0;

    public int[] getTopPosFor(int[] array, double idx, int val, ArrayVisualizer ArrayVisualizer, Renderer Renderer) {
    	return new int[] {
    		(int)(Renderer.getXScale()*(idx+0.5d))+20,
    		(int)(Renderer.getYOffset()-29)
    	};
    }
    public int[] getBottomPosFor(int[] array, double idx, int val, ArrayVisualizer ArrayVisualizer, Renderer Renderer) {
    	return new int[] {
    		(int)(Renderer.getXScale()*(idx+0.5d))+20,
    		(int)(Renderer.getYOffset() + Renderer.getViewSize() - 20)
    	};
    }
    
    private int maxWidth() {
    	int m = 0;
    	for(int i = 0; i < trace.length; i++) {
    		m = Math.max(m, trace[i].length);
    	}
    	return m;
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
    public void drawVisual(int[] array, int[] boundingBox, ArrayVisualizer arrayVisualizer, Renderer renderer, Highlights highlights) {
        if(renderer.isAuxActive() && array != arrayVisualizer.getArray()) return;
    	boolean fancy = highlights.fancyFinishActive(),
    			color = arrayVisualizer.colorEnabled(),
    			useAltVals = arrayVisualizer.doingStabilityCheck() && color;
  
    	int width = arrayVisualizer.currentWidth() - 20, height = arrayVisualizer.currentHeight() - 40, n = arrayVisualizer.getCurrentLength();
		int imgWidth = n < width ? Math.max(maxWidth(), n) : width, imgHeight = Math.min(height, MAX_HEIGHT);
		BufferedImage img = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB);
		int my = Math.min(n, imgWidth);
		double[] sclm = scales((double)imgWidth / (double)n, 1);
		trace[bloc] = new Color[my];
		for(int i = 0, val; i < my; i++) {
			int hm = highlights.containsMax(array, i, n, sclm[0]), v = hm < 0 ? ~hm : hm;
			boolean hl = hm >= 0;
            int nw = hl && imgWidth < n * 2 ? 1 : 0;
			val = useAltVals ? arrayVisualizer.getIndexValue(array[v]) : array[v];
			if(hl || highlights.hasColor(array, v)) {
				// set highlight color if highlighted
				if(hl)
					for(int j=0; j<=nw; j++) {
						if(i-nw+j>=0)
							trace[bloc][i-nw+j] = arrayVisualizer.getHighlightColor();
					}
				else if(color) {
					// transparent colorcode if list in color
					trace[bloc][i] = new Color(Mixbox.lerp(
						getIntColor(val, arrayVisualizer.getCurrentLength()).getRGB(),
						highlights.colorAt(v).getRGB(),
						0.67f
					));
				} else
					trace[bloc][i] = new Color(Mixbox.lerp(
						getGray(val, arrayVisualizer.getCurrentLength()).getRGB(),
						highlights.colorAt(v).getRGB(),
						0.67f
					));
			} else if (!fancy || v >= highlights.getFancyFinishPosition()) {
				if (color) {
					trace[bloc][i] = getIntColor(val, arrayVisualizer.getCurrentLength());
				} else {
					trace[bloc][i] = getGray(val, arrayVisualizer.getCurrentLength());
				}
			} else {
				trace[bloc][i] = Color.GREEN;
			}
		}
		for(int y = 0, yy = (bloc + (MAX_HEIGHT - imgHeight + 1)) % MAX_HEIGHT; y < imgHeight; y++, yy = (yy + 1) % MAX_HEIGHT) {
			int nn = trace[yy].length;
			if(nn > 0) {
				double xScl = (double)imgWidth / (double)nn;
				for(int x = 0, x2 = 0; x < nn; x++) {
		    		int wx = (int) ((x + 1) * Math.max(xScl, 1) - x2);
					for(int xx = x2; xx < x2 + wx; xx++) {
						img.setRGB(xx, y, trace[yy][x].getRGB());
					}
					x2 += wx;
				}
			}
		}
		this.mainRender.drawImage(img, 10, 32, width+10, height+32, 0, 0, imgWidth, imgHeight, null);
		bloc = (bloc + 1) % MAX_HEIGHT;
    }
}