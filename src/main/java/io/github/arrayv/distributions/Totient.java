package io.github.arrayv.distributions;

import java.io.*;
import java.util.*;

import io.github.arrayv.dialogs.LoadCustomDistributionDialog;
import io.github.arrayv.distributions.templates.Distribution;
import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.panes.JErrorPane;
import io.github.arrayv.utils.*;

public class Totient extends Distribution {
 // O(n)
        @Override
        public String getName() {
            return "Euler Totient Function";
        }
        @Override
        public void initializeArray(int[] array, ArrayVisualizer arrayVisualizer) {
            int n = arrayVisualizer.getCurrentLength();

            int[] minPrimeFactors = new int[n];
            List<Integer> primes = new ArrayList<>();

            array[0] = 0;
            array[1] = 1;
            for (int i = 2; i < n; i++) {
                if (minPrimeFactors[i] == 0) {
                    primes.add(i);

                    minPrimeFactors[i] = i;
                    array[i] = i - 1;
                }

                for (int prime : primes) {
                    if (i * prime >= n) break;

                    boolean last = prime == minPrimeFactors[i];

                    minPrimeFactors[i * prime] = prime;
                    array[i * prime] = array[i] * (last ? prime : prime - 1);

                    if (last) break;
                }
            }
        }
    
}
