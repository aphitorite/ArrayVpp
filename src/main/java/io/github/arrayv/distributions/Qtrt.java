package io.github.arrayv.distributions;

import java.io.*;
import java.util.*;

import io.github.arrayv.dialogs.LoadCustomDistributionDialog;
import io.github.arrayv.distributions.templates.Distribution;
import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.panes.JErrorPane;
import io.github.arrayv.utils.*;

public class Qtrt extends Distribution {

        public String getName() {
            return "Fifth Root (Centered)";
        }
        @Override
        public void initializeArray(int[] array, ArrayVisualizer arrayVisualizer) {
            int currentLen = arrayVisualizer.getCurrentLength();
            int p = 5;
            double h = currentLen/2d;

            for (int i = 0; i < currentLen; i++) {
                double val  = i/h - 1,
                       root = val < 0 ? -Math.pow(-val, 1d/p) : Math.pow(val, 1d/p);

                array[i] = (int)(h * (root + 1));
            }
        }
    
}
