package io.github.arrayv.distributions;

import java.io.*;
import java.util.*;

import io.github.arrayv.dialogs.LoadCustomDistributionDialog;
import io.github.arrayv.distributions.templates.Distribution;
import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.panes.JErrorPane;
import io.github.arrayv.utils.*;

public class Cantor extends Distribution {

        public String getName() {
            return "Cantor Function";
        }
        @Override
        public void initializeArray(int[] array, ArrayVisualizer arrayVisualizer) {
            int currentLen = arrayVisualizer.getCurrentLength();

            cantor(array, 0, currentLen, 0, currentLen-1);
        }

        public void cantor(int[] array, int a, int b, int min, int max) {
            if (b-a < 1 || max == min) return;

            int mid = (min+max)/2;
            if (b-a == 1) {
                array[a] = mid;
                return;
            }

            int t1 = (a+a+b)/3, t2 = (a+b+b+2)/3;

            for (int i = t1; i < t2; i++)
                array[i] = mid;

            this.cantor(array, a, t1, min, mid);
            this.cantor(array, t2, b, mid+1, max);
        }
    
}
