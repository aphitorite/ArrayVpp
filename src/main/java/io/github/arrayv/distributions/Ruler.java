package io.github.arrayv.distributions;

import java.io.*;
import java.util.*;

import io.github.arrayv.dialogs.LoadCustomDistributionDialog;
import io.github.arrayv.distributions.templates.Distribution;
import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.panes.JErrorPane;
import io.github.arrayv.utils.*;

public class Ruler extends Distribution {

        public String getName() {
            return "Ruler";
        }
        @Override
        public void initializeArray(int[] array, ArrayVisualizer arrayVisualizer) {
            int currentLen = arrayVisualizer.getCurrentLength();
            int step = Math.max(1, currentLen/256);
            int floorLog2 = (int)(Math.log((double)currentLen/step)/Math.log(2));
            int lowest;
            //noinspection StatementWithEmptyBody
            for (lowest = step; 2*lowest <= currentLen/4; lowest*=2);
            boolean[] digits = new boolean[floorLog2+2];

            int i, j;
            for (i = 0; i+step <= currentLen; i+=step) {
                //noinspection StatementWithEmptyBody
                for (j = 0; digits[j]; j++);
                digits[j] = true;

                for (int k = 0; k < step; k++) {
                    int value = currentLen/2 - Math.min((1 << j)*step, lowest);
                    array[i+k] = value;
                }

                for (int k = 0; k < j; k++) digits[k] = false;
            }

            //noinspection StatementWithEmptyBody
            for (j = 0; digits[j]; j++);
            digits[j] = true;
            while (i < currentLen) {
                int value = Math.max(currentLen/2 - (1 << j)*step, currentLen/4);
                array[i++] = value;
            }
        }
    
}
