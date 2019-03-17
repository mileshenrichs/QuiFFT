package org.quifft.params;

/**
 * A config object containing the parameters of a Fourier transform.
 * These parameters are used while performing the FFT, and can be accessed
 * as an attribute of the result.
 */
public class FFTParameters {

    /**
     * Number of samples taken from audio waveform for use in FFT.
     * <p>If numPoints is not defined, this must be a power of 2.
     * If numPoints is defined to be greater than window size, the signal will be
     * padded with (numPoints - windowSize) zeroes.</p>
     */
    public int windowSize = 4096;

    /**
     * Window function to be used.
     * <p>One of: rectangular, triangular, Hanning, Hamming</p>
     */
    public WindowFunction windowFunction = WindowFunction.RECTANGULAR;

    /**
     * Percentage of overlap between adjacent sampled windows.  Must be between 0 and 1.
     * <p>Large window overlap percentages can dramatically increase the size of the FFT result
     * because more FFT frames are calculated.  For example, if 75% overlap is used (windowOverlap = .75),
     * there will be 4 times as many FFT frames computed than there would be with no overlap.</p>
     */
    public double windowOverlap = 0;

    /**
     * Number of points in the N-point FFT.
     * <p>If not defined, will default to the window size.
     * If defined, must be greater than window size and be a power of 2.</p>
     */
    public int numPoints = -1;

    /**
     * If true, amplitude of frequency bins will be scaled logarithmically instead of linearly.
     */
    public boolean isLogarithmic = false;

    /**
     * If true, all frequency bin amplitudes will be in the range from 0.00 to 1.00,
     * where 1.00 represents the maximum frequency amplitude amongst all amplitudes in the file.
     */
    public boolean isNormalized = true;

}
