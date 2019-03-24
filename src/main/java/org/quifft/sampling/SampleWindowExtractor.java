package org.quifft.sampling;

import org.quifft.params.WindowFunction;
import org.quifft.params.WindowFunctionGenerator;

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

    /**
     * Constructs a SampleWindowExtractor to take windows from an input signal for use in FFTs
     * @param wave full-length waveform of original audio file
     * @param windowSize size of window as defined by FFT parameters (excludes zero-padding)
     * @param windowFunction windowing function to be applied to input signal
     * @param zeroPadLength number of zeroes to be appended to windowed signal
     */
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

    /**
     * Modifies a sample window by performing element-wise multiplication of samples with window function coefficients
     * @param window sample window to which windowing function should be applied
     */
    private void applyWindowingFunction(int[] window) {
        if(windowFunction != WindowFunction.RECTANGULAR) {
            double[] coefficients = WindowFunctionGenerator.generateWindow(windowSize, windowFunction);
            for(int i = 0; i < windowSize; i++) {
                window[i] = (int) Math.round(window[i] * coefficients[i]);
            }
        }
    }

}
