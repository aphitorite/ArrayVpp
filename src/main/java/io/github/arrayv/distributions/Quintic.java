package io.github.arrayv.distributions;

import java.io.*;
import java.util.*;

import io.github.arrayv.dialogs.LoadCustomDistributionDialog;
import io.github.arrayv.distributions.templates.Distribution;
import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.panes.JErrorPane;
import io.github.arrayv.utils.*;

public class Quintic extends Distribution {

        public String getName() {
            return "Quintic (Centered)";
        }
        @Override
        public void initializeArray(int[] array, ArrayVisualizer arrayVisualizer) {
            int currentLen = arrayVisualizer.getCurrentLength();
            int power = 5;
            double mid = (currentLen-1)/2d;

            for (int i = 0; i < currentLen; i++)
                array[i] = (int)(Math.pow(i - mid, power)/Math.pow(mid, power-1) + mid);
        }
    
}
