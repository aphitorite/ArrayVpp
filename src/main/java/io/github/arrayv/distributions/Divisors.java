package io.github.arrayv.distributions;

import java.io.*;
import java.util.*;

import io.github.arrayv.dialogs.LoadCustomDistributionDialog;
import io.github.arrayv.distributions.templates.Distribution;
import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.panes.JErrorPane;
import io.github.arrayv.utils.*;

public class Divisors extends Distribution {
//O(n^1.5)
        public String getName() {
            return "Sum of Divisors";
        }
        @Override
        public void initializeArray(int[] array, ArrayVisualizer arrayVisualizer) {
            int currentLen = arrayVisualizer.getCurrentLength();
            int[] n = new int[currentLen];

            n[0] = 0;
            n[1] = 1;
            double max = 1;

            for (int i = 2; i < currentLen; i++) {
                n[i] = sumDivisors(i);
                if (n[i] > max) max = n[i];
            }

            double scale = Math.min((currentLen-1)/max, 1);
            for (int i = 0; i < currentLen; i++) {
                array[i] = (int)(n[i]*scale);
            }
        }

        public int sumDivisors(int n) {
            int sum = n+1;
            for (int i = 2; i <= (int)Math.sqrt(n); i++) {
                if (n % i == 0) {
                    if (i == n/i) sum += i;
                    else          sum += i + n/i;
                }
            }
            return sum;
        }
    
}
