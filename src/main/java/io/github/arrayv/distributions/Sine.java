package io.github.arrayv.distributions;

import java.io.*;
import java.util.*;

import io.github.arrayv.dialogs.LoadCustomDistributionDialog;
import io.github.arrayv.distributions.templates.Distribution;
import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.panes.JErrorPane;
import io.github.arrayv.utils.*;

public class Sine extends Distribution {

        public String getName() {
            return "Sine Wave";
        }
        @Override
        public void initializeArray(int[] array, ArrayVisualizer arrayVisualizer) {
            int currentLen = arrayVisualizer.getCurrentLength();
            int n = currentLen-1;
            double c = 2*Math.PI/n;

            for (int i = 0; i < currentLen; i++)
                array[i] = (int)(n * (Math.sin(c * i)+1)/2);
        }
    
}
