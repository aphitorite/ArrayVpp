package io.github.arrayv.utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import io.github.arrayv.main.ArrayVisualizer;
import java.util.function.UnaryOperator;
import java.util.function.BinaryOperator;

public abstract class Renderable {
	public static interface Sampler {
		int apply(BufferedImage input, double x, double y);
	}

	public static Graphics2D mainRender;

	private static final double BC_A = -0.5;

	protected static double __clamp(double v, double mi, double ma) {
		return Math.max(mi, Math.min(v, ma));
	}
	
	// macro: we can't be sure x/y are in bounds, and getting pixel values out-of-bounds will error out.
	protected static int __getRGB(BufferedImage img, int x, int y) {
		return x < 0 || y < 0 || x >= img.getWidth() || y >= img.getHeight() ? 0 : img.getRGB(x, y);
	}
	
	public static int nearest(BufferedImage fb, double x, double y) {
		return __getRGB(fb, (int) Math.round(x), (int) Math.round(y));
	}
	
	public static int bilinearInt(int a, int b, int c, int d, double r1, double r2) {
		return (int) ((a * (1d - r1) + b * r1) * (1d - r2) + (c * (1d - r1) + d * r1) * r2);
	}
	public static int bilinearByte(int a, int b, int c, int d, double r1, double r2, int shift) {
		return bilinearInt((a >>> shift) & 0xFF, (b >>> shift) & 0xFF, (c >>> shift) & 0xFF, (d >>> shift) & 0xFF, r1, r2);
	}
	public static int bilinear(BufferedImage fb, double x, double y) {
		int x0 = (int) Math.floor(x), x1 = (int) Math.ceil(x), y0 = (int) Math.floor(y), y1 = (int) Math.ceil(y),
		    c0 = __getRGB(fb, x0, y0), c1 = __getRGB(fb, x1, y0), c2 = __getRGB(fb, x0, y1), c3 = __getRGB(fb, x1, y1);
		return (bilinearByte(c0, c1, c2, c3, x % 1d, y % 1d, 16) << 16) | (bilinearByte(c0, c1, c2, c3, x % 1d, y % 1d, 8) << 8) | bilinearByte(c0, c1, c2, c3, x % 1d, y % 1d, 0);
	}
	
	private static double[][] __dotProd(double[][] a, double[][] b) {
		double[] C = new double[b.length];
		double[][] O = new double[a.length][b[0].length]; 
		for (int XB = 0; XB < b[0].length; XB++) {
			for(int YB = 0; YB < b.length; YB++) {
				C[YB] = b[YB][XB];
			}
			for (int YA = 0; YA < a.length; YA++) {
				for (int XA = 0; XA < a[0].length; XA++) {
					O[YA][XB] += a[YA][XA] * C[XA];
				}
			}
		}
		return O;
	}
	private static double[][] __snippetMatrix(BufferedImage fb, int[] cols, int[] rows, UnaryOperator<Integer> filter) {
		double[][] O = new double[cols.length][rows.length];
		for (int y = 0; y < rows.length; y++) {
			for (int x = 0; x < cols.length; x++) {
				O[x][y] = filter.apply(__getRGB(fb, cols[x], rows[y]));
			}
		}
		return O;
	}
	public static int bicubic(BufferedImage fb, double x, double y) {
		// stolen from ref. impl
		BinaryOperator<Double> u = (s, a) ->
			(s >= 0 && s <= 1) ?
				(a + 2d) * (s * s * s) - (a + 3d) * (s * s) + 1d :
			(s > 1 && s <= 2) ?
				a * (s * s * s) - (5d * a) * (s * s) + (8d * a) * s - 4d * a :
			0d;
		double xD = x % 1d, yD = y % 1d;
		int XF = (int) Math.floor(x), YF = (int) Math.floor(y);
		
		double[][] L = new double[][] {
			new double[] {u.apply(1d + xD, BC_A), u.apply(xD, BC_A), u.apply(1d - xD, BC_A), u.apply(2d - xD, BC_A)}
		};
		double[][] R = new double[][] {
			new double[] {u.apply(1d + yD, BC_A)}, new double[] {u.apply(     yD, BC_A)},
			new double[] {u.apply(1d - yD, BC_A)}, new double[] {u.apply(2d - yD, BC_A)}
		};
		int[] V = new int[3];
		for (int w = 0; w < V.length; w++) {
			final int W = w;
			double[][] M = __snippetMatrix(fb,
				new int[] {XF - 1, XF, XF + 1, XF + 2},
				new int[] {YF - 1, YF, YF + 1, YF + 2},
				(v) -> (((v >>> (16 - 8 * W)) & 0xFF))
			);
			V[W] = (int) __clamp(__dotProd(__dotProd(L, M), R)[0][0], 0d, 255d);
		}
		return (V[0] << 16) | (V[1] << 8) | V[2];
	}

	public static Color samplePixel(ArrayVisualizer arrayVisualizer, double x, double y, Sampler s) {
		return new Color(s.apply(arrayVisualizer.getFramebuffer(), x, y));
	}
	public static Color samplePixel(ArrayVisualizer arrayVisualizer, double x, double y) {
		return samplePixel(arrayVisualizer, x, y, Renderable::nearest);
	}
	
	public static void drawBoundary(ArrayVisualizer arrayVisualizer, double idx, double width, int height)  {
		int[] topPosA = arrayVisualizer.getTopPosFor(arrayVisualizer.getArray(), idx, height),
		      topPosB = arrayVisualizer.getTopPosFor(arrayVisualizer.getArray(), idx + width, height),
		      btmPosA = arrayVisualizer.getBottomPosFor(arrayVisualizer.getArray(), idx, height),
		      btmPosB = arrayVisualizer.getBottomPosFor(arrayVisualizer.getArray(), idx + width, height);
		mainRender.fillPolygon(new int[] {
		    topPosA[0], topPosB[0], btmPosB[0], btmPosA[0]
		}, new int[] {
		    topPosA[1], topPosB[1], btmPosB[1], btmPosA[1]
		}, 4);
	}
	public static void drawBoundary(ArrayVisualizer arrayVisualizer, double idx, double width)  {
		drawBoundary(arrayVisualizer, idx, width, arrayVisualizer.getCurrentLength());
	}
	public static void drawBoundaryFXW(ArrayVisualizer arrayVisualizer, double idx, int pixels, int height)  {
		double v = (double)pixels / (arrayVisualizer.currentWidth() - 40) * arrayVisualizer.getCurrentLength();
		drawBoundary(arrayVisualizer, idx + (pixels < 0 ? v - 0.5 : 0.5 - v), v, height);
	}
	public static void drawBoundaryFXW(ArrayVisualizer arrayVisualizer, double idx, int pixels)  {
		double v = (double)pixels / (arrayVisualizer.currentWidth() - 40) * arrayVisualizer.getCurrentLength();
		drawBoundary(arrayVisualizer, idx + (pixels < 0 ? v - 0.5 : 0.5 - v), v, arrayVisualizer.getCurrentLength());
	}
	
	public abstract void render(int[] array, ArrayVisualizer arrayVisualizer, Renderer renderer, Highlights highlights);
}
