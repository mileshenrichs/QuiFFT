package org.quifft.params;

/**
 * The type of window function to be applied to each section of waveform before computing its FFT
 */
public enum WindowFunction {

    /**
     * Extracts blocks of data from waveform without performing any transformation
     */
    RECTANGULAR("Rectangular"),

    /**
     * A triangular window
     */
    TRIANGULAR("Triangular"),

    /**
     * A Bartlett window (triangular window with zeroes on each end)
     */
    BARTLETT("Bartlett"),

    /**
     * A Hann (or Hanning) window
     */
    HANNING("Hanning"),

    /**
     * A Hamming window
     */
    HAMMING("Hamming"),

    /**
     * A Blackman window
     */
    BLACKMAN("Blackman");

    private final String name;

    WindowFunction(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
