package org.quifft.params;

/**
 * A config object containing the parameters of a Fourier transform
 * <p>These parameters are used while performing the FFT, and can be accessed
 * as an attribute of the result.</p>
 */
public class FFTParameters {

    /**
     * Number of samples taken from audio waveform for use in FFT
     * <p>If numPoints is not defined, this must be a power of 2.
     * If numPoints is defined to be greater than window size, the signal will be
     * padded with (numPoints - windowSize) zeroes.</p>
     */
    public int windowSize = 4096;

    /**
     * Window function to be used
     * <p>One of: rectangular, triangular, Bartlett, Hanning, Hamming, Blackman</p>
     */
    public WindowFunction windowFunction = WindowFunction.HANNING;

    /**
     * Percentage of overlap between adjacent sampled windows; must be between 0 and 1
     * <p>Large window overlap percentages can dramatically increase the size of the FFT result
     * because more FFT frames are calculated.  For example, if 75% overlap is used (windowOverlap = .75),
     * there will be 4 times as many FFT frames computed than there would be with no overlap.</p>
     */
    public double windowOverlap = 0.50;

    /**
     * Number of points in the N-point FFT
     * <p>If not defined, will default to the window size.
     * If defined, must be greater than window size and be a power of 2.</p>
     */
    public Integer numPoints = null;

    /**
     * If true, amplitude of frequency bins will be scaled logarithmically (decibels) instead of linearly
     * <p>The decibel scale describes the amplitude of a sound relative to some reference value.
     * In the case of digital audio, this reference value is the maximum possible amplitude value that can be
     * represented at a given bit depth.  Since each value will be compared to the maximum possible, most or all
     * of the dB readings will be less than 0.  No sound at all is typically represented by negative infinity,
     * but QuiFFT sets a floor of -100 dB to avoid infinite values.</p>
     * <p>Therefore, if a decibel scale is used, bin amplitudes will be in the range [-100.0, 0.0].</p>
     */
    public boolean useDecibelScale = true;

    /**
     * If true, all frequency bin amplitudes will be in the range from 0.00 to 1.00,
     * where 1.00 represents the maximum frequency amplitude amongst all amplitudes in the file
     * <p>If {@code useDecibelScale} is set to true, the value of {@code isNormalized} doesn't
     * matter because the decibel scale is normalized by definition.</p>
     */
    public boolean isNormalized = false;

    /**
     * Get zero padding length (# of zeroes that should be appended to input signal before taking FFT)
     * based on numPoints and windowSize parameters
     * @return zero padding length for FFT
     */
    public int zeroPadLength() {
        if(numPoints == null) {
            return 0;
        }

        return numPoints - windowSize;
    }

    /**
     * Get total length of window; returns windowSize by default, but will return numPoints of it is set
     * @return total length of sampling window (including zero-padding if applied)
     */
    public int totalWindowLength() {
        if(numPoints == null) {
            return windowSize;
        }

        return numPoints;
    }

}
