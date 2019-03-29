package org.quifft;

import org.quifft.audioread.AudioReader;
import org.quifft.audioread.AudioReaderFactory;
import org.quifft.fft.FFTComputationWrapper;
import org.quifft.output.*;
import org.quifft.params.FFTParameters;
import org.quifft.params.ParameterValidator;
import org.quifft.params.WindowFunction;
import org.quifft.sampling.SampleWindowExtractor;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

/**
 * Class used by the client to compute an FFT for an audio file
 */
public class QuiFFT {

    // parameters for FFT operation (i.e. window size, normalization, etc)
    private FFTParameters fftParameters = new FFTParameters();

    // audio reader for input file
    private AudioReader audioReader;

    /**
     * Constructs a QuiFFT instance with an audio file
     * @param inputFile reference to audio file for which FFT will be performed
     * @throws IOException if an I/O exception occurs when the input stream is initialized
     * @throws UnsupportedAudioFileException if the file is not a valid audio file or has bit depth greater than 16
     */
    public QuiFFT(File inputFile) throws IOException, UnsupportedAudioFileException {
        this.audioReader = AudioReaderFactory.audioReaderFor(inputFile);
    }

    /**
     * Constructs a QuiFFT instance with a String file name
     * @param fileName name of audio file for which FFT will be performed
     * @throws IOException if an I/O exception occurs when the input stream is initialized
     * @throws UnsupportedAudioFileException if the file is not a valid audio file or has bit depth greater than 16
     */
    public QuiFFT(String fileName) throws IOException, UnsupportedAudioFileException {
        this(new File(fileName));
    }

    /**
     * Set window size (number of samples per FFT)
     * <p>If numPoints parameter is not defined, this must be a power of 2.</p>
     * @param windowSize number of samples from audio file for which each FFT should be performed
     * @return current QuiFFT object with window size parameter set
     */
    public QuiFFT windowSize(int windowSize) {
        fftParameters.windowSize = windowSize;
        return this;
    }

    /**
     * Get window size parameter for FFT
     * @return window size parameter for FFT
     */
    public int windowSize() {
        return fftParameters.windowSize;
    }

    /**
     * Set window function to be used for obtaining sequence of samples to be used for each FFT frame
     * @param windowFunction type of window function to be used
     * @return current QuiFFT object with window function parameter set
     */
    public QuiFFT windowFunction(WindowFunction windowFunction) {
        fftParameters.windowFunction = windowFunction;
        return this;
    }

    /**
     * Get window function parameter for FFT
     * @return window function parameter for FFT
     */
    public WindowFunction windowFunction() {
        return fftParameters.windowFunction;
    }

    /**
     * Set percentage by which adjacent windows should be overlapped
     * @param overlapPercentage value between 0 and 1 representing window overlap percentage
     * @return current QuiFFT object with window overlap percentage parameter set
     */
    public QuiFFT windowOverlap(double overlapPercentage) {
        fftParameters.windowOverlap = overlapPercentage;
        return this;
    }

    /**
     * Get window overlap percentage parameter for FFT
     * @return window overlap percentage parameter for FFT
     */
    public double windowOverlap() {
        return fftParameters.windowOverlap;
    }

    /**
     * Set number of points for FFT
     * <p>If this is not explicitly defined, the number of points will be equal to the window size.
     * If defined, must be greater than or equal to window size AND must be a power of 2.
     * Each signal window will be zero-padded to reach a length equal to numPoints.</p>
     * @param numPoints the number of points for the N-point FFT
     * @return current QuiFFT object with number of points parameter set
     */
    public QuiFFT numPoints(int numPoints) {
        fftParameters.numPoints = numPoints;
        return this;
    }

    /**
     * Get number of points parameter for FFT
     * @return number of points parameter for FFT
     */
    public int numPoints() {
        return fftParameters.numPoints;
    }

    /**
     * Set whether the amplitudes of each frequency bin should be scaled logarithmically (decibels) instead of linearly
     * @param useDecibelScale true if amplitudes should be dB scale, false for linear scale
     * @return current QuiFFT object with amplitude scaling parameter set
     */
    public QuiFFT dBScale(boolean useDecibelScale) {
        fftParameters.useDecibelScale = useDecibelScale;
        return this;
    }

    /**
     * Get amplitude scaling parameter for FFT
     * @return true if amplitudes will be logarithmically scaled (decibels), false if they'll be on a linear scale
     */
    public boolean dBScale() {
        return fftParameters.useDecibelScale;
    }

    /**
     * Set option for whether FFT amplitudes should be normalized (scaled to range from 0 to 1)
     * @param shouldBeNormalized true if FFT results should be normalized
     * @return current QuiFFT object with normalization parameter set
     */
    public QuiFFT normalized(boolean shouldBeNormalized) {
        fftParameters.isNormalized = shouldBeNormalized;
        return this;
    }

    /**
     * Get normalization parameter for FFT
     * @return true if FFT amplitudes will be normalized
     */
    public boolean normalized() {
        return fftParameters.isNormalized;
    }

    /**
     * Performs an FFT for the entirety of the audio file
     * @return an FFT result containing metadata of this FFT and an array of all {@link FFTFrame}s computed
     * @throws BadParametersException if there are any invalid FFT parameters set
     */
    public FFTResult fullFFT() {
        ParameterValidator.validateFFTParameters(fftParameters, false);

        FFTResult fftResult = new FFTResult();
        fftResult.setMetadata(audioReader, fftParameters);

        boolean isStereo = audioReader.getAudioFormat().getChannels() == 2;
        float sampleRate = audioReader.getAudioFormat().getSampleRate();
        int[] wave = audioReader.getWaveform();

        int lengthOfWave = wave.length / (isStereo ? 2 : 1);
        double frameOverlapMultiplier = 1 / (1 - fftParameters.windowOverlap);
        int numFrames = (int) Math.ceil(((double) lengthOfWave / fftParameters.windowSize) * frameOverlapMultiplier);
        FFTFrame[] fftFrames = new FFTFrame[numFrames];

        SampleWindowExtractor windowExtractor = new SampleWindowExtractor(wave, isStereo, fftParameters.windowSize,
                fftParameters.windowFunction, fftParameters.windowOverlap, fftParameters.zeroPadLength());
        double currentAudioTimeMs = 0;
        for(int i = 0; i < fftFrames.length; i++) {
            // sampleWindow is input to FFT -- may be zero-padded if numPoints > windowSize
            int[] sampleWindow = windowExtractor.extractWindow(i);

            // compute next current FFT frame
            fftFrames[i] = FFTComputationWrapper.doFFT(sampleWindow, currentAudioTimeMs, fftResult.windowDurationMs,
                    fftResult.fileDurationMs, sampleRate, fftParameters);

            // adjust current audio time
            currentAudioTimeMs += fftResult.windowDurationMs * (1 - fftParameters.windowOverlap);
        }

        if(fftParameters.useDecibelScale) {
            FFTComputationWrapper.scaleLogarithmically(fftFrames);
        } else if(fftParameters.isNormalized) {
            double maxAmplitude = findMaxAmplitude(fftFrames);
            normalizeFFTResult(fftFrames, maxAmplitude);
        }

        fftResult.fftFrames = fftFrames;
        return fftResult;
    }

    /**
     * Creates an FFTStream which can be used as an iterator to compute FFT frames one by one
     * @return an FFTStream which can be used as an iterator to compute FFT frames one by one
     * @throws BadParametersException if there are any invalid FFT parameters set
     */
    public FFTStream fftStream() {
        ParameterValidator.validateFFTParameters(fftParameters, true);

        FFTStream fftStream = new FFTStream();
        fftStream.setMetadata(audioReader, fftParameters);

        return fftStream;
    }

    /**
     * Normalizes each bin amplitude by dividing all amplitudes by the max amplitude
     * @param fftFrames array of frames obtained by an FFT operation
     */
    private void normalizeFFTResult(FFTFrame[] fftFrames, double maxAmp) {
        for(FFTFrame frame : fftFrames) {
            for(FrequencyBin bin : frame.bins) {
                bin.amplitude /= maxAmp;
            }
        }
    }

    /**
     * Returns the maximum amplitude of any frequency bin in any frame in an array of FFTFrames
     * @param fftFrames an array of FFTFrames obtained by an FFT operation
     * @return the maximum amplitude found in the array of FFTFrames
     */
    private static double findMaxAmplitude(FFTFrame[] fftFrames) {
        double maxAmp = 0;
        for(FFTFrame frame : fftFrames) {
            for(FrequencyBin bin : frame.bins) {
                maxAmp = Math.max(maxAmp, bin.amplitude);
            }
        }
        return maxAmp;
    }

}
