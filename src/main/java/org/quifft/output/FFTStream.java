package org.quifft.output;

import java.util.Iterator;

/**
 * FFTStream computes an FFT on an audio file incrementally as opposed to all at once
 * <p>It exposes an Iterator interface for computing {@link FFTFrame}s one at a time.
 * This can be a useful alternative to {@link FFTResult} if your audio file is large or you are space-constrained.</p>
 */
public class FFTStream extends FFTOutputObject implements Iterator<FFTFrame> {

    /**
     * Checks whether another FFTFrame exists
     * @return true if another FFTFrame exists
     */
    public boolean hasNext() {
        return true;
    }

    /**
     * Gets next computed FFTFrame
     * @return next computed FFTFrame
     */
    public FFTFrame next() {
        return new FFTFrame(0, 0, new FrequencyBin[]{});
    }
}
