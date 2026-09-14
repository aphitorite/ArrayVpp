package io.github.arrayv.distributions;

import java.io.*;
import java.util.*;

import io.github.arrayv.dialogs.LoadCustomDistributionDialog;
import io.github.arrayv.distributions.templates.Distribution;
import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.panes.JErrorPane;
import io.github.arrayv.utils.*;

public class Fsd extends Distribution {
// fly straight dangit (OEIS A133058)
        public String getName() {
            return "Fly Straight, Damnit!";
        }
        @Override
        public void initializeArray(int[] array, ArrayVisualizer arrayVisualizer) {
            int currentLen = arrayVisualizer.getCurrentLength();
            int[] fsd = new int[currentLen];

            double max;
            max = fsd[0] = fsd[1] = 1;
            for (int i = 2; i < currentLen; i++) {
                int g = gcd(fsd[i-1], i);
                fsd[i] = fsd[i-1]/g + (g==1 ? i+1 : 0);
                if (fsd[i] > max) max = fsd[i];
            }

            double scale = Math.min((currentLen-1)/max, 1);
            for (int i = 0; i < currentLen; i++)
                array[i] = (int)(fsd[i]*scale);
        }

        public int gcd(int a, int b) {
            if (b==0) return a;
            return gcd(b, a%b);
        }
    
}
