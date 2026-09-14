package io.github.arrayv.distributions;

import java.io.*;
import java.util.*;

import io.github.arrayv.dialogs.LoadCustomDistributionDialog;
import io.github.arrayv.distributions.templates.Distribution;
import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.panes.JErrorPane;
import io.github.arrayv.utils.*;

public class PerlinNoise extends Distribution {

        public String getName() {
            return "Perlin Noise";
        }
        @Override
        public void initializeArray(int[] array, ArrayVisualizer arrayVisualizer) {
            int currentLen = arrayVisualizer.getCurrentLength();
            Random random = new Random();

            int[] perlinNoise = new int[currentLen];

            double step = 1d / currentLen;
            double randomStart = (double) (random.nextInt(currentLen));
            int octave = (int) (Math.log(currentLen) / Math.log(2));

            for (int i = 0; i < currentLen; i++) {
                int value = (int) (io.github.arrayv.utils.PerlinNoise.returnFracBrownNoise(randomStart, octave) * currentLen);
                perlinNoise[i] = value;
                randomStart += step;
            }

            int minimum = Integer.MAX_VALUE;
            for (int i = 0; i < currentLen; i++) {
                if (perlinNoise[i] < minimum) {
                    minimum = perlinNoise[i];
                }
            }
            minimum = Math.abs(minimum);
            for (int i = 0; i < currentLen; i++) {
                perlinNoise[i] += minimum;
            }

            double maximum = Double.MIN_VALUE;
            for (int i = 0; i < currentLen; i++) {
                if (perlinNoise[i] > maximum) {
                    maximum = perlinNoise[i];
                }
            }
            double scale = currentLen / maximum;
            if (scale < 1.0 || scale > 1.8) {
                for (int i = 0; i < currentLen; i++) {
                    perlinNoise[i] = (int) (perlinNoise[i] * scale);
                }
            }

            for (int i = 0; i < currentLen; i++) {
                array[i] = Math.min(perlinNoise[i], currentLen-1);
            }
        }
    
}
