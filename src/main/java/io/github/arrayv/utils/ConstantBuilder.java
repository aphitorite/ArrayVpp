package io.github.arrayv.utils;

import io.github.arrayv.main.ArrayVisualizer;

public class ConstantBuilder {
	private ArrayVisualizer arrayVisualizer;
	public ConstantBuilder(ArrayVisualizer aV) {
		this.arrayVisualizer = aV;
	}
	private long getTotal() {
		return arrayVisualizer.getReads().getComparisons() +
			   arrayVisualizer.getWrites().getMainWriteCount() +
			   arrayVisualizer.getWrites().getAuxWriteCount();
	}
	public String getConstant() {
		long targetConstant = arrayVisualizer.getConstant().apply((long) arrayVisualizer.getCurrentLength()),
			 current = getTotal();
		
		if(targetConstant == -1L)
			return "Constant \u2248 ---";

		double m = current / (double) targetConstant;

		return "Constant \u2248 " + String.format("%.3f", m);
	}
}