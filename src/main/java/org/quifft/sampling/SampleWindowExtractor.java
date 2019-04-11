package org.quifft.sampling;

import org.quifft.params.WindowFunction;
import org.quifft.params.WindowFunctionGenerator;

/**
 * Applies zero-padding and smoothing functions to extract sample windows from a longer waveform
 * @see org.quifft.params.FFTParameters
 * @see WindowFunction
 */
public class SampleWindowExtractor {

    // full-length waveform of original audio file
    private int[] wave;

    // true if signal is stereo (2 channels), false if mono
    private boolean isStereo;

    // size of window as defined by FFT parameters (excludes zero-padding)
    private int windowSize;

    // windowing function to be applied to input signal
    private WindowFunction windowFunction;

    // number of zeroes to be appended to windowed signal
    private int zeroPadLength;

    // delta sample (distance between start indices between consecutive windows)
    private int ds;

    /**
     * Constructs a SampleWindowExtractor to take windows from an input signal for use in FFTs
     * @param wave full-length waveform of original audio file
     * @param isStereo true if waveform is stereo, false if mono
     * @param windowSize size of window as defined by FFT parameters (excludes zero-padding)
     * @param windowFunction windowing function to be applied to input signal
     * @param windowOverlap window overlap percentage
     * @param zeroPadLength number of zeroes to be appended to windowed signal
     */
    public SampleWindowExtractor(int[] wave, boolean isStereo, int windowSize, WindowFunction windowFunction,
                                 double windowOverlap, int zeroPadLength) {
        this.wave = wave;
        this.isStereo = isStereo;
        this.windowSize = windowSize;
        this.windowFunction = windowFunction;
        this.zeroPadLength = zeroPadLength;

        this.ds = (int) Math.floor(windowSize * (1 - windowOverlap));
    }

    /**
     * Extracts the {@code i}th sampling window from a full-length waveform
     * <p>If is stereo signal, adjacent values will be averaged to produce mono samples</p>
     * @param i index of window to be extracted
     * @return a single window extracted from full-length audio waveform
     */
    public int[] extractWindow(int i) {
        // copy section of original waveform into sample array
        int[] window = new int[windowSize + zeroPadLength];

        int j = i * ds * (isStereo ? 2 : 1); // index into source waveform array
        int samplesCopied = 0; // count samples copied to terminate loop once window size has been reached

        while(samplesCopied < windowSize && j < wave.length) {
            if(isStereo) {
                window[samplesCopied++] = (int) Math.round((wave[j] + wave[j + 1]) / 2.0);
                j += 2;
            } else {
                window[samplesCopied++] = wave[j++];
            }
        }

        // apply windowing function to extracted sample
        applyWindowingFunction(window);

        return window;
    }

    /**
     * Applies zero-padding and the selected smoothing function to a given window; used with FFTStream
     * @param window sampling window to which smoothing function should be applied
     * @return sampling window with smoothing function applied
     */
    public int[] convertSamplesToWindow(int[] window) {
        int[] fullWindow = new int[windowSize + zeroPadLength];

        int j = 0;
        int samplesCopied = 0;

        while(samplesCopied < windowSize) {
            if(isStereo) {
                fullWindow[samplesCopied++] = (int) Math.round((window[j] + window[j + 1]) / 2.0);
                j += 2;
            } else {
                fullWindow[samplesCopied++] = window[j++];
            }
        }

        applyWindowingFunction(fullWindow);

        return fullWindow;
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
