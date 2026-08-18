package io.github.arrayv.visuals.templates;

import java.awt.Color;

import com.scrtwpns.Mixbox;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.utils.Highlights;
import io.github.arrayv.visuals.features.HeatMap;

public class Colorize {
	private static final ArrayVisualizer ARRAYVISUALIZER = ArrayVisualizer.getInstance();
	private static final Highlights HIGHLIGHTS = ARRAYVISUALIZER.getHighlights();
	
	private static int MISSES = 0;

	private static int __clamp(int v, int mi, int ma) {
		return Math.max(mi, Math.min(v, ma));
	}
	public static interface ColSampler {
		Color apply(int[] array, int idx, int actinglen);
	}
	
	private static int trueval(int[] array, int idx) {
		return ARRAYVISUALIZER.doingStabilityCheck() && ARRAYVISUALIZER.colorEnabled() ? ARRAYVISUALIZER.getIndexValue(array[idx]) : array[idx];
	}
	public static Color gray(int[] array, int idx, int actinglen) {
		if (HIGHLIGHTS.containsPosition(array, idx))
			return ARRAYVISUALIZER.getHighlightColor();
		int c = (int)(255 * (double)Math.max(0, Math.min(array[idx], actinglen))/actinglen);
		Color ret = new Color(c, c, c);
		return HIGHLIGHTS.hasColor(array, idx) ? new Color(Mixbox.lerp(ret.getRGB(), HIGHLIGHTS.colorAt(array, idx).getRGB(), 0.5f)) : ret;
	}
	public static Color graybright(int[] array, int idx, int actinglen) {
		if (HIGHLIGHTS.containsPosition(array, idx))
			return ARRAYVISUALIZER.getHighlightColor();
		int c = (int)(232 * (double)Math.max(0, Math.min(array[idx], actinglen))/actinglen) + 23;
		Color ret = new Color(c, c, c);
		return HIGHLIGHTS.hasColor(array, idx) ? new Color(Mixbox.lerp(ret.getRGB(), HIGHLIGHTS.colorAt(array, idx).getRGB(), 0.5f)) : ret;
	}
	public static Color snow(int[] array, int idx, int actinglen) {
		if (HIGHLIGHTS.containsPosition(array, idx))
			return ARRAYVISUALIZER.getHighlightColor();
		if (HIGHLIGHTS.hasColor(array, idx))
			return HIGHLIGHTS.colorAt(array, idx);
		return Color.white;
	}
	public static Color hueAlways(int[] array, int idx, int actinglen) {
		if (HIGHLIGHTS.containsPosition(array, idx))
			return ARRAYVISUALIZER.getHighlightColor();
        Color ret = Color.getHSBColor(((float) trueval(array, idx) / actinglen), 1.0F, 1.0F);
		return HIGHLIGHTS.hasColor(array, idx) ? new Color(Mixbox.lerp(ret.getRGB(), HIGHLIGHTS.colorAt(array, idx).getRGB(), 0.5f)) : ret;
	}
	public static Color hue(int[] array, int idx, int actinglen) {
		if (!ARRAYVISUALIZER.colorEnabled()) return null;
		return hueAlways(array, idx, actinglen);
	}
	private static int multx2i(int a, int b) {
		return b<64?(a*b)/127:b<128?(a*(b+1))/127:255-(((255-a)*(255-b))/128);
	}
	private static Color multx2(Color a, Color b) {
		return new Color(multx2i(a.getRed(), b.getRed()), multx2i(a.getGreen(), b.getGreen()), multx2i(a.getBlue(), b.getBlue()));
	}
	public static Color hueMixed(int[] array, int idx, int actinglen) {
		if (HIGHLIGHTS.containsPosition(array, idx))
			return ARRAYVISUALIZER.getHighlightColor();
		int c = (int)(255 * (double)Math.max(0, Math.min(array[idx], actinglen))/actinglen);
        Color ret = multx2(Color.getHSBColor(((float) trueval(array, idx) / (float)Math.floor(Math.sqrt(actinglen))), 1.0F, 1.0F), new Color(c, c, c));
		return HIGHLIGHTS.hasColor(array, idx) ? new Color(Mixbox.lerp(ret.getRGB(), HIGHLIGHTS.colorAt(array, idx).getRGB(), 0.5f)) : ret;
	}
	public static Color heatmap(int[] array, int idx, int actinglen) {
		if (ARRAYVISUALIZER.queryFeatureState("heat") < 1) return null;
		return HeatMap.getColor(array, idx);
	}
	public static Color fancyFinish(int[] array, int idx, int actinglen) {
		if (!HIGHLIGHTS.fancyFinishActive() || idx >= HIGHLIGHTS.getFancyFinishPosition())
			return null;
		return Color.green;
	}
	public static Color bestFit(int[] array, int i, int n, ColSampler... opts) {
		Color tmp;
		i = __clamp(i, 0, array.length - 1);
		for (ColSampler opt : opts) {
			tmp = opt.apply(array, i, n);
			if (tmp != null) return tmp;
		}
		return (MISSES++ & 1) == 1 ? Color.black : Color.magenta;
	}
}
