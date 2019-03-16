package org.quifft.output;

/**
 * The result of an FFT being computed for a single sampling window of an audio file.
 * An {@link FFTResult} for a full audio file will contain an array of FFTFrames.
 */
public class FFTFrame {

    /**
     * Start time in milliseconds from the original audio file for the sampling window used to compute this frame
     */
    public long frameStartMs;

    /**
     * End time in milliseconds from the original audio file for the sampling window used to compute this frame
     */
    public long frameEndMs;

    /**
     * An array of frequency bins.
     * In a discrete Fourier transform, each bin represents a range of frequencies in Hz.
     * A {@link FrequencyBin} contains the amplitude of this range from the original sound wave.
     */
    public FrequencyBin[] bins;

    public FFTFrame(long startMs, long endMs, FrequencyBin[] bins) {
        this.frameStartMs = startMs;
        this.frameEndMs = endMs;
        this.bins = bins;
    }

}