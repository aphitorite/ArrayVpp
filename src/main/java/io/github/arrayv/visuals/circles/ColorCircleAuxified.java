package io.github.arrayv.visuals.circles;

import java.awt.Color;


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

public final class ColorCircleAuxified extends VisualNoAntialiasing {

    public ColorCircleAuxified(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);

        this.setListName("Color Circle (Auxiliary Retool)");
        this.setCategory("Circle Visuals");
        this.setOverlayable(true);
        this.setAuxable(true);
        this.setMaximumAuxLists(8);
    }

    public int[] getTopPosFor(int[] array, double idx, int val, ArrayVisualizer ArrayVisualizer, Renderer Renderer) {
    	int[] box = this.getBoundingBox(array, Renderer.renderedInstances() - Renderer.isWhichArray(array), Renderer.renderedInstances(), ArrayVisualizer, Renderer);
    	int n = Renderer.getArrayLengthFor(array);
        int left = box[0], right = box[1],
            top = box[2], bottom = box[3],
        	hw = (right - left) / 2, hh = (bottom - top) / 2,
        	cw = (right + left) / 2, ch = (bottom + top) / 2,
        	len = box[4], pos = box[5];
        double[] p = pos(idx, n, len, pos, 0);
        return new int[] {
        		cw + (int)(hw * p[0]),
        		ch + (int)(hh * p[1])
        };
    }
    public int[] getBottomPosFor(int[] array, double idx, int val, ArrayVisualizer ArrayVisualizer, Renderer Renderer) {
    	return new int[] {
    		ArrayVisualizer.windowWidth()/2,
    		ArrayVisualizer.windowHeight()/2
    	};
    }

    public int[] getBoundingBox(int[] array, int index, int length, ArrayVisualizer arrayVisualizer, Renderer renderer) {
    	int cx = arrayVisualizer.windowWidth() / 2, cy = arrayVisualizer.windowHeight() / 2 + 10,
    		radm = (int) (Math.min(arrayVisualizer.windowWidth(), arrayVisualizer.windowHeight() - 20) / 2.2);
    	int nmeta = length;
    	if(nmeta == 0 || index == length) {
    		return new int[] {
    			cx - radm, cx + radm,
    			cy - radm, cy + radm,
    			1, 0
    		};
    	}
    	int dia = radm / 2, pad = 16-(int)Math.floor((1d - Math.sqrt(0.5)) * radm);
    	return new int[] {
    		cx + radm + pad, cx + radm + dia + pad,
    		cy - radm, cy - radm + dia,
    		// extension: tile coordinates
    		nmeta, index
    	};
    }
    
    private double[] pos(double pos, int len, int nmeta, int idx, double rot) {
    	return new double[] {
    		Math.cos(Math.PI * ((2d * pos / len + 2d * idx) / nmeta - 0.5 + rot)),
    		Math.sin(Math.PI * ((2d * pos / len + 2d * idx) / nmeta - 0.5 + rot))
    	};
    }

    @Override
    public void drawVisual(int[] array, int[] boundingBox, ArrayVisualizer arrayVisualizer, Renderer renderer, Highlights Highlights) {
        int left = boundingBox[0], right = boundingBox[1],
        	top = boundingBox[2], bottom = boundingBox[3],
        	hw = (right - left) / 2, hh = (bottom - top) / 2,
        	cw = (right + left) / 2, ch = (bottom + top) / 2,
        	len = boundingBox[4], pos = boundingBox[5];

        int n = renderer.getArrayLength();

        int p = Math.min(hw,hh) / 12;

        int[] x  = new int[3];
        int[] y  = new int[3];

        int[] px = new int[3];
        int[] py = new int[3];

        this.extraRender.setColor(Color.WHITE);

        x[0] = cw; y[0] = ch;

        double[] co = pos(-1, n, len, pos, 0);
        x[2] = cw + (int)(hw * co[0]);
        y[2] = ch + (int)(hh * co[1]);

        for (int i = 0; i < n; i++) {
            x[1] = x[2];
            y[1] = y[2];
            co = pos(i, n, len, pos, 0);
            x[2] = cw + (int)(hw * co[0]);
            y[2] = ch + (int)(hh * co[1]);

            if (Highlights.containsPosition(array, i) && (!Highlights.fancyFinishActive() || i >= Highlights.getFancyFinishPosition())) {
                if (arrayVisualizer.analysisEnabled()) this.extraRender.setColor(Color.LIGHT_GRAY);
                else                                  this.extraRender.setColor(Color.WHITE);

                co = pos(i - 0.5, n, len, pos, 0);
                px[0] = cw + (int)((hw + p / 4) * co[0]);
                py[0] = ch + (int)((hh + p / 4) * co[1]);

                co = pos(i - 0.5, n, len, pos, -1d/6d);
                px[1] = px[0] + (int)(p * co[0]);
                py[1] = py[0] + (int)(p * co[1]);

                co = pos(i - 0.5, n, len, pos, 1d/6d);
                px[2] = px[0] + (int)(p * co[0]);
                py[2] = py[0] + (int)(p * co[1]);

                this.extraRender.fillPolygon(px, py, 3);
            }

            if (x[1] != x[2] || y[1] != y[2]) {
        		this.mainRender.setColor(
        			Colorize.bestFit(array, i, arrayVisualizer.getCurrentLength(),
        				Colorize::fancyFinish,
        				Colorize::hue,
        				Colorize::graybright
        			)
        		);
            	
            	this.mainRender.fillPolygon(x, y, 3);
            }
        }
    }
}
