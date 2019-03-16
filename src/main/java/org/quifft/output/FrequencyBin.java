package org.quifft.output;

/**
 * A pair of numbers representing the amplitude of a certain frequency.
 * Frequency buckets are produced as a result of the Fourier transform.
 */
public class FrequencyBin {

    /**
     * The frequency in Hz representing the starting point of this frequency bin.
     */
    public final double frequency;

    /**
     * The amplitude of the signal at this frequency bin.
     * For normalized FFTs, this will be a value between 0 and 1.
     * For un-normalized FFTs, this value could be arbitrarily large.
     */
    public double amplitude;

    public FrequencyBin(double freq, double amp) {
        this.frequency = freq;
        this.amplitude = amp;
    }

}
