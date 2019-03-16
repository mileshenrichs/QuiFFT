package org.quifft.output;

import java.util.Iterator;

/**
 * FFTStream computes an FFT on an audio file incrementally as opposed to all at once.
 * It exposes an Iterator interface for computing {@link FFTFrame}s one at a time.
 * This can be a useful alternative to {@link FFTResult} if your audio file is very large or you are space-constrained.
 */
public class FFTStream extends FFTOutputObject implements Iterator<FFTFrame> {

    /**
     * @return true if another FFTFrame exists
     */
    public boolean hasNext() {
        return true;
    }

    /**
     * @return next computed FFTFrame
     */
    public FFTFrame next() {
        return new FFTFrame(0, 0, new FrequencyBin[]{});
    }
}
