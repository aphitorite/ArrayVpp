package io.github.arrayv.visuals.circles;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.utils.Highlights;
import io.github.arrayv.utils.Renderer;
import io.github.arrayv.visuals.templates.Colorize;
import io.github.arrayv.visuals.templates.VisualNoAntialiasing;

/*
 *
MIT License

Copyright (c) 2019 w0rthy
Copyright (c) 2021 ArrayV 4.0 Team

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

public final class DisparityCircle extends VisualNoAntialiasing {

    public DisparityCircle(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);

        this.setListName("Disparity Circle");
        this.setCategory("Circle Visuals");
        this.setOverlayable(true);
        this.addSupportedFeatures("heat");
    }

    public int[] getTopPosFor(int[] array, double idx, int val, ArrayVisualizer ArrayVisualizer, Renderer Renderer) {
    	int width  = ArrayVisualizer.windowWidth();
        int height = ArrayVisualizer.windowHeight();
        int n = ArrayVisualizer.getCurrentLength();
        double r = Math.min(width, height)/2.5;
        double disp = (1 + Math.cos((Math.PI * (val - idx)) / (ArrayVisualizer.getCurrentLength() * 0.5))) * 0.5;
        return new int[] {
        		width/2 + (int)(disp * r * Math.cos(Math.PI * (2d*idx / n - 0.5))),
        		height/2 + (int)(disp * r * Math.sin(Math.PI * (2d*idx / n - 0.5)))
        };
    }
    public int[] getBottomPosFor(int[] array, double idx, int val, ArrayVisualizer ArrayVisualizer, Renderer Renderer) {
    	return new int[] {
    		ArrayVisualizer.windowWidth()/2,
    		ArrayVisualizer.windowHeight()/2
    	};
    }

    @Override
    public void drawVisual(int[] array, int[] boundingBox, ArrayVisualizer arrayVisualizer, Renderer renderer, Highlights Highlights) {
        if (renderer.isAuxActive()) return;

        int width  = arrayVisualizer.windowWidth();
        int height = arrayVisualizer.windowHeight();

        int n = arrayVisualizer.getCurrentLength();
        double r = Math.min(width, height)/2.5;

        this.extraRender.setStroke(arrayVisualizer.getThickStroke());
        this.extraRender.setColor(arrayVisualizer.getHighlightColor());

        int[] x =  {width/2, 0, 0};
        int[] y = {height/2, 0, 0};

        double disp = (1 + Math.cos((Math.PI * (array[n-1] - (n-1))) / (arrayVisualizer.getCurrentLength() * 0.5))) * 0.5;
        x[2] =  width/2 + (int)(disp * r * Math.cos(Math.PI * (2d*(n-1) / n - 0.5)));
        y[2] = height/2 + (int)(disp * r * Math.sin(Math.PI * (2d*(n-1) / n - 0.5)));

        for (int i = 0; i < n; i++) {
            x[1] = x[2];
            y[1] = y[2];

            disp = (1 + Math.cos((Math.PI * (array[i] - i)) / (arrayVisualizer.getCurrentLength() * 0.5))) * 0.5;
            x[2] =  width/2 + (int)(disp * r * Math.cos(Math.PI * (2d*i / n - 0.5)));
            y[2] = height/2 + (int)(disp * r * Math.sin(Math.PI * (2d*i / n - 0.5)));

    		this.mainRender.setColor(
    			Colorize.bestFit(array, i, n,
    				Colorize::heatmap,
    				Colorize::fancyFinish,
    				Colorize::hue,
    				Colorize::snow
    			)
    		);

            this.mainRender.fillPolygon(x, y, 3);
        }
        this.extraRender.setStroke(arrayVisualizer.getDefaultStroke());
    }
}
