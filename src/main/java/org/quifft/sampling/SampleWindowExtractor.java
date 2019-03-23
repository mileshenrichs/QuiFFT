package org.quifft.sampling;

import org.quifft.params.WindowFunction;

public class SampleWindowExtractor {

    // full-length waveform of original audio file
    private int[] wave;

    // size of window as defined by FFT parameters (excludes zero-padding)
    private int windowSize;

    // windowing function to be applied to input signal
    private WindowFunction windowFunction;

    // number of zeroes to be appended to windowed signal
    private int zeroPadLength;

    // number of frames in complete FFT; computed from wave length and window size
    private int numFrames;

    public SampleWindowExtractor(int[] wave, int windowSize, WindowFunction windowFunction, int zeroPadLength) {
        this.wave = wave;
        this.windowSize = windowSize;
        this.windowFunction = windowFunction;
        this.zeroPadLength = zeroPadLength;

        this.numFrames = (int) Math.ceil((double) wave.length / windowSize);
    }

    /**
     * Extracts the {@code i}th sampling window from a full-length waveform
     * @param i index of window to be extracted
     * @return a single window extracted from full-length audio waveform
     */
    public int[] extractWindow(int i) {
        int[] window = new int[windowSize + zeroPadLength];

        // copy section of original waveform into sample array
        int s = windowSize * (i + 1);
        if(i < numFrames - 1) {
            System.arraycopy(wave, s - windowSize, window, 0, windowSize);
        } else {
            int remaining = wave.length % windowSize;
            if(remaining == 0) remaining = windowSize;
            System.arraycopy(wave, s - windowSize, window, 0, remaining);
        }

        // apply windowing function to extracted sample
        applyWindowingFunction(window);

        return window;
    }

    private void applyWindowingFunction(int[] window) {
        if(windowFunction != WindowFunction.RECTANGULAR) {
            // todo: apply window function
        }
    }

}
