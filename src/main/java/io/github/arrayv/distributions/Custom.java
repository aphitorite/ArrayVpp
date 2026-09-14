package io.github.arrayv.distributions;

import java.io.*;
import java.util.*;

import io.github.arrayv.dialogs.LoadCustomDistributionDialog;
import io.github.arrayv.distributions.templates.Distribution;
import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.panes.JErrorPane;
import io.github.arrayv.utils.*;

public class Custom extends Distribution {

        private int[] refarray;
        private int length;
        public String getName() {
            return "Custom";
        }
        @Override
        public void selectDistribution(int[] array, ArrayVisualizer arrayVisualizer) {
            LoadCustomDistributionDialog dialog = new LoadCustomDistributionDialog();
            File file = dialog.getFile();
            if (file == null) return;
            Scanner scanner;
            try {
                scanner = new Scanner(file);
            } catch (FileNotFoundException e) {
                JErrorPane.invokeErrorMessage(e);
                return;
            }
            scanner.useDelimiter("\\s+");
            this.refarray = new int[arrayVisualizer.getMaximumLength()];
            int current = 0;
            while (scanner.hasNext()) {
                this.refarray[current++] = Integer.parseInt(scanner.next());
            }
            this.length = current;
            scanner.close();
        }
        @Override
        public void initializeArray(int[] array, ArrayVisualizer arrayVisualizer) {
            if (this.refarray == null) {
                for (int i = 0; i < arrayVisualizer.getCurrentLength(); i++)
                    array[i] = 0;
                return;
            }
            int currentLen = arrayVisualizer.getCurrentLength();
            double scale = (double)this.length / currentLen;
            for (int i = 0; i < currentLen; i++) {
                array[i] = (int)(this.refarray[(int)(i * scale)] / scale);
            }
        }
    
}
