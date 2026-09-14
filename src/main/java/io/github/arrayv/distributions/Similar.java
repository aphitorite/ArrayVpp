package io.github.arrayv.distributions;

import java.io.*;
import java.util.*;

import io.github.arrayv.dialogs.LoadCustomDistributionDialog;
import io.github.arrayv.distributions.templates.Distribution;
import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.panes.JErrorPane;
import io.github.arrayv.utils.*;

public class Similar extends Distribution {

        public String getName() {
            return "Few Unique";
        }
        @Override
        public void initializeArray(int[] array, ArrayVisualizer arrayVisualizer) {
            int currentLen = arrayVisualizer.getCurrentLength();
            Random random = new Random();

            int l = 0, r, t = Math.min(currentLen, 8);
            for (int i = 0; i < t; i++)
                if (random.nextDouble() < 0.5) l++;
            r = currentLen-(t-l);

            int i = 0;
            for (; i < l; i++)          array[i] = (int) (currentLen * 0.25);
            for (; i < r; i++)          array[i] = currentLen / 2;
            for (; i < currentLen; i++) array[i] = (int) (currentLen * 0.75);
        }
    
}
