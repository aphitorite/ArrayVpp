package io.github.arrayv.visuals.dots;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.utils.Highlights;
import io.github.arrayv.utils.Renderer;
import io.github.arrayv.visuals.Visual;

final public class ScatterPlotTrace extends Visual {
    public ScatterPlotTrace(ArrayVisualizer ArrayVisualizer) {
        super(ArrayVisualizer);

        this.setListName("Pixel Trace");
        this.setCategory("Dot Visuals");
    }
	
	private final int X_OFFSET = 20;
	private final int Y_OFFSET = 45;
	private final int CYCLE_SPEED = 180; // higher = slower, lower = faster but can reduce gradient granularity
	
	private int cachedLen = 0, cachedHeight = 0, cachedWidth, cshift;
	private double idxScale, valScale;
	private int change;
	private int radius;
	
	private BufferedImage trace;

	@Override
    public void drawVisual(int[] array, int[] boundingBox, ArrayVisualizer arrayVisualizer, Renderer renderer, Highlights highlights) {
		if(renderer.isAuxActive() && array != arrayVisualizer.getArray()) return;

		int w = arrayVisualizer.windowWidth()  - 40;
		int h = arrayVisualizer.windowHeight() - 65;
		int n = arrayVisualizer.getCurrentLength();
		
		int imgW = Math.min(w, n);
		int imgH = Math.min(h, n);
		
		if(n != this.cachedLen || h != this.cachedHeight || w != this.cachedWidth) {
			this.cachedLen    = n;
			this.cachedHeight = h;
			this.cachedWidth  = w;
			imgW = Math.min(w, n);
			imgH = Math.min(h, n);
			
			this.idxScale = (double)imgW/n;
			this.valScale = (double)imgH/n;
			this.cshift = 0;
			this.change = -1;
			
			this.trace = new BufferedImage(imgW+1, imgH+1, BufferedImage.TYPE_INT_RGB);
		}
		
		// type 1
		/*int currColor = getIntColor(this.cshift, CYCLE_SPEED).getRGB();
		  
		for(int i = 0; i < n; i++) {
			if(array[i] < 0 || array[i] > n-1) continue;
			this.trace.setRGB((int)(i * this.idxScale), imgH-1 - (int)(array[i] * this.valScale), currColor);
		}
		
		this.mainRender.drawImage(this.trace,
		                          0 + X_OFFSET, Y_OFFSET,
		                          w + X_OFFSET, h + Y_OFFSET,
		                          0, 0,
		                          imgW, imgH,
		                          null);
								  
		this.cshift = (this.cshift+1) % CYCLE_SPEED;*/
		
		if((this.change = (this.change+1)%8) == 0) {
			Graphics2D g2d = this.trace.createGraphics();
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
			g2d.setColor(new Color(0, 0, 0, 16));
			g2d.fillRect(0, 0, imgW+1, imgH+1);
			g2d.dispose();
		}
		
		int m = imgW > 540 ? 1 : 0;
		
		for(int i = 0; i < n; i++) {
			if(array[i] < 0 || array[i] > n-1) continue;
			int currColor = arrayVisualizer.colorEnabled() ? getIntColor(array[i], n).getRGB() : 0xFFFFFF;
			int x = (int)(i * this.idxScale);
			int y = imgH - (int)(array[i] * this.valScale);
			
			this.trace.setRGB(x,   y,   currColor);
			
			if(m > 0) {
				this.trace.setRGB(x,   y-1, currColor);
				this.trace.setRGB(x+1, y,   currColor);
				this.trace.setRGB(x+1, y-1, currColor);
			}
		}
		
		this.mainRender.drawImage(this.trace, X_OFFSET, Y_OFFSET, w + X_OFFSET, h + Y_OFFSET, 0, 0, imgW+m, imgH+m, null);
	}
}