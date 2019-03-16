package org.quifft.output;

/**
 * The result of an FFT computed over entirety of audio file
 */
public class FFTResult extends FFTOutputObject {

    /**
     * Array containing all FFTFrames computed for entirety of audio file
     */
    public FFTFrame[] fftFrames;

}
