package io.github.arrayv.visuals.dots;

import java.awt.Color;

import com.scrtwpns.Mixbox;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.utils.Highlights;
import io.github.arrayv.utils.Renderer;
import io.github.arrayv.visuals.Visual;

/*
 *
MIT License

Copyright (c) 2019 w0rthy
Copyright (c) 2020 MusicTheorist
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

public final class WaveDots extends Visual {
    public WaveDots(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);

        this.setListName("Sine Wave Dots");
        this.setCategory("Dot Visuals");
        this.setAuxable(true);
        this.setOverlayable(true);
        this.addSupportedFeatures("linkeddots");
    }

    public int[] getTopPosFor(int[] array, double idx, int val, ArrayVisualizer ArrayVisualizer, Renderer Renderer) {
        return new int[] {
    		(int)(Renderer.getXScale()*(idx+0.5d))+20,
    		Renderer.getYOffset() + (int)(((Renderer.getViewSize() - 20) / 2.5) * Math.sin((2 * Math.PI * ((double) val / Renderer.getArrayLength()))) + Renderer.halfViewSize() - 20)
    	};
    }
    public int[] getBottomPosFor(int[] array, double idx, int val, ArrayVisualizer ArrayVisualizer, Renderer Renderer) {
    	return getTopPosFor(array, idx, val, ArrayVisualizer, Renderer);
    }

    @Override
    public void drawVisual(int[] array, int[] boundingBox, ArrayVisualizer arrayVisualizer, Renderer renderer, Highlights Highlights) {
        int offset = 20 + (int) (renderer.getXScale()/2);

        if (arrayVisualizer.queryFeatureState("linkeddots") > 0) {
            int lastX = 0;
            int lastY = (int) (((renderer.getViewSize() - 20) / 2.5) * Math.sin((2 * Math.PI * ((double) array[0] / renderer.getArrayLength()))) + renderer.halfViewSize() - 20);
            this.mainRender.setStroke(arrayVisualizer.getCustomStroke(2));

            for (int i = 1, j = (int) renderer.getXScale(); i < renderer.getArrayLength(); i++) {
                if (Highlights.fancyFinishActive() && i < Highlights.getFancyFinishPosition()) {
                    this.mainRender.setColor(Color.GREEN);
                    this.mainRender.setStroke(arrayVisualizer.getCustomStroke(4));
                } else if (Highlights.containsPosition(i)) {
                    this.mainRender.setColor(arrayVisualizer.getHighlightColor());
                    this.mainRender.setStroke(arrayVisualizer.getCustomStroke(4));
                } else if (arrayVisualizer.colorEnabled()) {
                	if (Highlights.hasColor(array, i))
                		this.mainRender.setColor(new Color(Mixbox.lerp(
                			getIntColor(array[i], arrayVisualizer.getCurrentLength()).getRGB(),
    	                	Highlights.colorAt(array, i).getRGB(),
    	                	0.5f
    	                )));
                	else this.mainRender.setColor(getIntColor(array[i], arrayVisualizer.getCurrentLength()));
                } else if (Highlights.hasColor(array, i)) {
                    this.mainRender.setColor(Highlights.colorAt(array, i));
                } else this.mainRender.setColor(Color.WHITE);

                int y = (int) (((renderer.getViewSize() - 20) / 2.5) * Math.sin((2 * Math.PI * ((double) array[i] / renderer.getArrayLength()))) + renderer.halfViewSize() - 20);

                this.mainRender.drawLine(lastX + offset, renderer.getYOffset() + lastY, j + offset, renderer.getYOffset() + y);

                lastX = j;
                lastY = y;

                this.mainRender.setStroke(arrayVisualizer.getCustomStroke(2));

                int width = (int) (renderer.getXScale() * (i + 1)) - j;
                j += width;
            }
            this.mainRender.setStroke(arrayVisualizer.getDefaultStroke());
        } else {
            int dotS = renderer.getDotDimensions();

            for (int i = 0, j = 0; i < renderer.getArrayLength(); i++) {
                if (Highlights.fancyFinishActive() && i < Highlights.getFancyFinishPosition())
                    this.mainRender.setColor(Color.GREEN);
                else if (arrayVisualizer.colorEnabled()) {
                	if (Highlights.hasColor(array, i))
                		this.mainRender.setColor(new Color(Mixbox.lerp(
                			getIntColor(array[i], arrayVisualizer.getCurrentLength()).getRGB(),
    	                	Highlights.colorAt(array, i).getRGB(),
    	                	0.5f
    	                )));
                	else this.mainRender.setColor(getIntColor(array[i], arrayVisualizer.getCurrentLength()));
                } else if (Highlights.hasColor(array, i)) {
                    this.mainRender.setColor(Highlights.colorAt(array, i));
                } else this.mainRender.setColor(Color.WHITE);

                int y = (int) (((renderer.getViewSize() - 20) / 2.5) * Math.sin((2 * Math.PI * ((double) array[i] / renderer.getArrayLength()))) + renderer.halfViewSize() - 20);

                this.mainRender.fillRect(j + offset, renderer.getYOffset() + y, dotS, dotS);

                int width = (int) (renderer.getXScale() * (i + 1)) - j;
                j += width;
            }
            this.mainRender.setColor(arrayVisualizer.getHighlightColor());

            for (int i = 0, j = 0; i < renderer.getArrayLength(); i++) {
                if (Highlights.containsPosition(i)) {
                    int y = (int) (((renderer.getViewSize() - 20) / 2.5) * Math.sin((2 * Math.PI * ((double) array[i] / renderer.getArrayLength()))) + renderer.halfViewSize() - 20);
                    this.mainRender.fillRect(j + offset - (int)(1.5*dotS), renderer.getYOffset() + y - (int)(1.5*dotS), 4*dotS, 4*dotS);
                }
                int width = (int) (renderer.getXScale() * (i + 1)) - j;
                j += width;
            }
        }
        if (arrayVisualizer.externalArraysEnabled()) {
            this.mainRender.setColor(Color.BLUE);
            this.mainRender.fillRect(0, renderer.getYOffset() + renderer.getViewSize() - 20, arrayVisualizer.currentWidth(), 1);
        }
    }
}
