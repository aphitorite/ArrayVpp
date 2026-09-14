package io.github.arrayv.distributions;

import java.io.*;
import java.util.*;

import io.github.arrayv.dialogs.LoadCustomDistributionDialog;
import io.github.arrayv.distributions.templates.Distribution;
import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.panes.JErrorPane;
import io.github.arrayv.utils.*;

public class Modulo extends Distribution {

        public String getName() {
            return "Modulo Function";
        }
        @Override
        public void initializeArray(int[] array, ArrayVisualizer arrayVisualizer) {
            int n = arrayVisualizer.getCurrentLength();

            for (int i = 0; i < n; i++) array[i] = 2*(n%(i+1));
        }
    
}
