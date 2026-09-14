package io.github.arrayv.distributions.templates;

import io.github.arrayv.main.ArrayVisualizer;

public abstract class Distribution {
    public String getId() {
        return getClass().getSimpleName();
    }

    public abstract String getName();

    public void selectDistribution(int[] array, ArrayVisualizer arrayVisualizer) {
    }

    public abstract void initializeArray(int[] array, ArrayVisualizer arrayVisualizer);
}