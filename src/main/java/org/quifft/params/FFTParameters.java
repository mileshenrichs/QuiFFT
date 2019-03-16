package org.quifft.params;

/**
 * A config object containing the parameters of a Fourier transform.
 * These parameters are used while performing the FFT, and can be accessed
 * as an attribute of the result.
 */
public class FFTParameters {

    /**
     * Number of samples taken from audio waveform for use in FFT.
     * If numPoints is not defined, this must be a power of 2.
     * If numPoints is defined to be greater than window size, the signal will be
     * padded with (numPoints - windowSize) zeroes.
     */
    public int windowSize = 4096;

    /**
     * Window function to be used.
     * One of: rectangular, triangular, Hanning, Hamming
     */
    public WindowFunction windowFunction = WindowFunction.RECTANGULAR;

    /**
     * Overlap between adjacent sampled windows.
     */
    public float windowOverlap = 0;

    /**
     * Number of points in the N-point FFT.
     * If not defined, will default to the window size.
     * If defined, must be greater than window size and be a power of 2.
     */
    public int numPoints = -1;

    /**
     * If true, amplitude of frequency bins will be scaled logarithmically instead of linearly.
     */
    public boolean isLogarithmic = false;

    /**
     * If true, all frequency bin amplitudes will be in the range from 0.00 to 1.00,
     * where 1.00 represents the maximum frequency amplitude amongst all amplitudes in the audio file.
     */
    public boolean isNormalized = true;

}
