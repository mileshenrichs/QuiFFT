package org.quifft.params;

/**
 * The type of window function to be applied to each section of waveform before computing its FFT
 */
public enum WindowFunction {

    /**
     * Extracts blocks of data from waveform without performing any transformation
     */
    RECTANGULAR,

    /**
     * A triangular window
     */
    TRIANGULAR,

    /**
     * A Bartlett window (triangular window with zeroes on each end)
     */
    BARTLETT,

    /**
     * A Hann (or Hanning) window
     */
    HANNING,

    /**
     * A Hamming window
     */
    HAMMING,

    /**
     * A Blackman window
     */
    BLACKMAN
}
