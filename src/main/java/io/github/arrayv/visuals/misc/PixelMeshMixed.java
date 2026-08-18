package io.github.arrayv.visuals.misc;

import java.awt.Color;
import java.awt.image.BufferedImage;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.utils.Highlights;
import io.github.arrayv.utils.Renderer;
import io.github.arrayv.visuals.Visual;
import com.scrtwpns.Mixbox;

/*
 * 
MIT License

Copyright (c) 2020-2021 ArrayV 4.0 Team

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

final public class PixelMeshMixed extends Visual {
	public PixelMeshMixed(ArrayVisualizer ArrayVisualizer) {
		super(ArrayVisualizer);
		
        this.setListName("Pixel Mesh (Mixed Mesh)");
        this.setCategory("Miscellaneous Visuals");
        this.setColorable(stance.ALWAYS);
        this.setOverlayable(true);
	}
	private int __clamp(int v, int mi, int ma) {
		return Math.max(mi, Math.min(v, ma));
	}
	private int multx2i(int a, int b) {
		return __clamp(b<64?(a*b)/127:b<128?(a*(b+1))/127:255-(((255-a)*(255-b))/128), 0, 255);
	}
	private Color multx2(Color a, Color b) {
		return new Color(multx2i(a.getRed(), b.getRed()), multx2i(a.getGreen(), b.getGreen()), multx2i(a.getBlue(), b.getBlue()));
	}
    
    public int[] getTopPosFor(int[] array, double idx, int val, ArrayVisualizer ArrayVisualizer, Renderer Renderer) {
    	int width = ArrayVisualizer.windowWidth()-40;
        int height = ArrayVisualizer.windowHeight()-50;
        int length = ArrayVisualizer.getCurrentLength();

        int sqrt = (int)Math.ceil(Math.sqrt(length));

        double xStep = (double)width / sqrt;
        double yStep = (double)height / sqrt;
        
        int y = (int)idx/sqrt;
        int x = (int)idx-y*sqrt;
        return new int[] {
    		20 + (int)((x+0.5) * xStep),
            40 + (int)((y+0.5) * yStep)
        };
    }
    
    public int[] getBottomPosFor(int[] array, double idx, int val, ArrayVisualizer ArrayVisualizer, Renderer Renderer) {
    	int width = ArrayVisualizer.windowWidth()-40;
        int height = ArrayVisualizer.windowHeight()-50;
        int length = ArrayVisualizer.getCurrentLength();

        int sqrt = (int)Math.ceil(Math.sqrt(length));

        double xStep = (double)width / sqrt;
        double yStep = (double)height / sqrt;
        
        int y = (int)idx/sqrt;
        int x = (int)idx-y*sqrt;
        return new int[] {
    		20 + (int)((x+1) * xStep),
            40 + (int)((y+1) * yStep)
        };
    }
	
	public void drawVisual(int[] array, int[] boundingBox, ArrayVisualizer ArrayVisualizer, Renderer Renderer, Highlights Highlights) {
		if(Renderer.isAuxActive()) return;
		
		int width = ArrayVisualizer.windowWidth();
		int height = ArrayVisualizer.windowHeight()-50;
		int length = ArrayVisualizer.getCurrentLength();
		
		int sqrt = (int)Math.ceil(Math.sqrt(length));
		int square = sqrt*sqrt;
		double scale = (double)length / square;
		
		Color currColor;
		int imgWidth = Math.min(sqrt, width), imgHeight = Math.min(sqrt, height);
		BufferedImage img = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB);
		
		double xScale = (double)sqrt/imgWidth;
		double yScale = (double)sqrt/imgHeight;
		
		for(int y = 0; y < imgHeight; y++) {
			int yi = (int)(y * yScale);
			
			for(int x = 0; x < imgWidth; x++) {
				int xi  = (int)(x * xScale);
				int idx = (int)((yi*sqrt + xi) * scale);
				
				if(Highlights.containsPosition(idx)) {
					if(ArrayVisualizer.analysisEnabled()) currColor = Color.LIGHT_GRAY;
					else								  currColor = Color.WHITE;
				} else currColor = multx2(getIntColor(array[idx]*sqrt, length), getGray(array[idx]/sqrt+1,(length-1)/sqrt+2));
				Color c = null;
				
				if(Highlights.fancyFinishActive() && idx < Highlights.getFancyFinishPosition()) {
					c = Color.GREEN;
				} else if(Highlights.hasColor(array, idx)) {
		            c = Highlights.colorAt(array, idx);
				}
				
				if(c != null) currColor = new Color(Mixbox.lerp(currColor.getRGB(), c.getRGB(), 0.5f));
				
				img.setRGB(x, y, currColor.getRGB());
			}
		}
		this.mainRender.drawImage(img, 10, 40, width-10, height+40, 0, 0, imgWidth, imgHeight, null);
	}
}