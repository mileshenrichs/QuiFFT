package org.quifft.output;

import org.quifft.audioread.AudioReader;
import org.quifft.fft.FFTComputationWrapper;
import org.quifft.params.FFTParameters;
import org.quifft.params.WindowFunction;
import org.quifft.sampling.SampleWindowExtractor;

import java.util.Iterator;

/**
 * FFTStream computes an FFT on an audio file incrementally as opposed to all at once
 * <p>It exposes an Iterator interface for computing {@link FFTFrame}s one at a time.
 * This can be a useful alternative to {@link FFTResult} if your audio file is large or you are space-constrained.</p>
 */
public class FFTStream extends FFTOutputObject implements Iterator<FFTFrame> {

    // AudioReader from which samples can be extracted
    private AudioReader audioReader;

    // Counter for how many samples have been computed so far (how many times next() has been called)
    private int samplesTakenCount;

    /**
     * Checks whether another FFTFrame exists
     * @return true if another FFTFrame exists
     */
    public boolean hasNext() {
        return audioReader.hasNext();
    }

    /**
     * Gets next computed FFTFrame
     * @return next computed FFTFrame
     */
    public FFTFrame next() {
        int[] nextWindow = audioReader.next();
        boolean isStereo = audioReader.getAudioFormat().getChannels() == 2;
        int windowSize = fftParameters.windowSize;
        WindowFunction windowFunction = fftParameters.windowFunction;
        double overlap = fftParameters.windowOverlap;
        int zeroPadLength = fftParameters.zeroPadLength();

        double startTimeMs = samplesTakenCount * windowDurationMs * (1 - fftParameters.windowOverlap);
        float sampleRate = audioReader.getAudioFormat().getSampleRate();

        SampleWindowExtractor windowExtractor = new SampleWindowExtractor(nextWindow, isStereo, windowSize,
                windowFunction, overlap, zeroPadLength);
        nextWindow = windowExtractor.convertSamplesToWindow(nextWindow);

        samplesTakenCount++;

        FFTFrame nextFrame = FFTComputationWrapper.doFFT(nextWindow, startTimeMs,
                windowDurationMs, fileDurationMs, sampleRate, fftParameters);
        if(fftParameters.useDecibelScale) {
            FFTComputationWrapper.scaleLogarithmically(nextFrame);
        }

        return nextFrame;
    }

    @Override
    public void setMetadata(AudioReader reader, FFTParameters params) {
        super.setMetadata(reader, params);

        // capture AudioReader object after setting metadata
        audioReader = reader;
        audioReader.setFFTParameters(params);
    }
}
