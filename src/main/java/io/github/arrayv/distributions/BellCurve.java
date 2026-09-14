package io.github.arrayv.distributions;

import java.io.*;
import java.util.*;

import io.github.arrayv.dialogs.LoadCustomDistributionDialog;
import io.github.arrayv.distributions.templates.Distribution;
import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.panes.JErrorPane;
import io.github.arrayv.utils.*;

public class BellCurve extends Distribution {

        public String getName() {
            return "Bell Curve";
        }
        @Override
        public void initializeArray(int[] array, ArrayVisualizer arrayVisualizer) {
            int currentLen = arrayVisualizer.getCurrentLength();
            double step = 8d / currentLen;
            double position = -4;
            int constant = 1264;
            double factor = currentLen / 512d;

            for (int i = 0; i < currentLen; i++) {
                double square = Math.pow(position, 2);
                double negativeSquare = 0 - square;
                double halfNegSquare = negativeSquare / 2d;
                double numerator = constant * factor * Math.pow(Math.E, halfNegSquare);

                double doublePi = 2 * Math.PI;
                double denominator = Math.sqrt(doublePi);

                array[i] = (int) (numerator / denominator);
                position += step;
            }
        }
    
}
