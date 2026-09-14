package io.github.arrayv.distributions;

import java.io.*;
import java.util.*;

import io.github.arrayv.dialogs.LoadCustomDistributionDialog;
import io.github.arrayv.distributions.templates.Distribution;
import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.panes.JErrorPane;
import io.github.arrayv.utils.*;

public class Blancmange extends Distribution {

        public String getName() {
            return "Blancmange Curve";
        }
        @Override
        public void initializeArray(int[] array, ArrayVisualizer arrayVisualizer) {
            int currentLen = arrayVisualizer.getCurrentLength();
            int floorLog2 = (int)(Math.log(currentLen)/Math.log(2));

            for (int i = 0; i < currentLen; i++) {
                int value = (int)(currentLen * curveSum(floorLog2, (double)i/currentLen));
                array[i] = value;
            }
        }

        public double curveSum(int n, double x) {
            double sum = 0;
            while (n >= 0)
                sum += curve(n--, x);
            return sum;
        }

        public double curve(int n, double x) {
            return triangleWave((1 << n) * x) / (1 << n);
        }

        public double triangleWave(double x) {
            return Math.abs(x - (int)(x + 0.5));
        }
    
}
