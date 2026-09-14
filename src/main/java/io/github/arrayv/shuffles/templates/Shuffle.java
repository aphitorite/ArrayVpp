package io.github.arrayv.shuffles.templates;

import java.util.Random;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.utils.Delays;
import io.github.arrayv.utils.Highlights;
import io.github.arrayv.utils.Reads;
import io.github.arrayv.utils.Writes;

public abstract class Shuffle {
    protected ArrayVisualizer arrayVisualizer;
    protected Delays Delays;
    protected Highlights Highlights;
    protected Writes Writes;
    protected Reads Reads;
    protected Random random;

    public Shuffle() {
        this.arrayVisualizer = ArrayVisualizer.getInstance();
        this.Delays = arrayVisualizer.getDelays();
        this.Highlights = arrayVisualizer.getHighlights();
        this.Writes = arrayVisualizer.getWrites();
        this.Reads = arrayVisualizer.getReads();
    }

    public String getId() {
        return getClass().getSimpleName();
    }

    public abstract String getName();

    public abstract void shuffleArray(int[] array);

    protected void makeRandom(ArrayVisualizer arrayVisualizer) {
        if (arrayVisualizer.isSeeded()) {
            random = new Random(1337);
        } else {
            random = new Random();
        }
    }

    protected void sort(int[] array, int[] dest_array, int dest_start, int start, int end, double sleep, Writes Writes) {
        if (dest_start < 0) dest_start = start;
        int min = array[start], max = min;
        for (int i = start + 1; i < end; i++) {
            if (array[i] < min) min = array[i];
            else if (array[i] > max) max = array[i];
        }

        int size = max - min + 1;
        int[] holes = new int[size];

        for (int i = start; i < end; i++)
            Writes.write(holes, array[i] - min, holes[array[i] - min] + 1, 0, false, true);

        for (int i = 0; i < size; i++) {
            while (holes[i] > 0) {
                Writes.write(holes, i, holes[i] - 1, 0, false, true);
                Writes.write(dest_array, dest_start++, i + min, sleep, true, false);
            }
        }
    }

    protected void shuffle(int[] array, int start, int end, double sleep, Writes Writes) {
        for (int i = start; i < end; i++) {
            int randomIndex = random.nextInt(end - i) + i;
            Writes.swap(array, i, randomIndex, sleep, true, false);
        }
    }
}