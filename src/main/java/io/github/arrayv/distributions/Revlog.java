package io.github.arrayv.distributions;

import java.io.*;
import java.util.*;

import io.github.arrayv.dialogs.LoadCustomDistributionDialog;
import io.github.arrayv.distributions.templates.Distribution;
import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.panes.JErrorPane;
import io.github.arrayv.utils.*;

public class Revlog extends Distribution {

        public String getName() {
            return "Decreasing Random";
        }
        @Override
        public void initializeArray(int[] array, ArrayVisualizer arrayVisualizer) {
            int currentLen = arrayVisualizer.getCurrentLength();
            Random random = new Random();

            for (int i = 0; i < currentLen; i++){
                int r = random.nextInt(currentLen - i) + i;
                array[i] = r;
            }
        }
    
}
