package org.quifft.params;

/**
 * The type of window function to be used for extracting subsequences of audio waveform for FFTs
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
     * A Hann (or Hanning) window
     */
    HANNING,

    /**
     * A Hamming window
     */
    HAMMING
}
