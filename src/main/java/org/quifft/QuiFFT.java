package org.quifft;

import org.quifft.audioread.AudioReader;
import org.quifft.audioread.AudioReaderFactory;
import org.quifft.fft.Complex;
import org.quifft.fft.FFT;
import org.quifft.output.FFTFrame;
import org.quifft.output.FFTResult;
import org.quifft.output.FrequencyBin;
import org.quifft.params.FFTParameters;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

public class QuiFFT {

    // parameters for FFT operation (i.e. window size, normalization, etc)
    private FFTParameters fftParameters = new FFTParameters();

    // audio reader for input file
    private AudioReader audioReader;

    public QuiFFT(File inputFile) throws IOException, UnsupportedAudioFileException {
        this.audioReader = AudioReaderFactory.audioReaderFor(inputFile);
    }

    public QuiFFT(String fileName) throws IOException, UnsupportedAudioFileException {
        this(new File(fileName));
    }

    public QuiFFT windowSize(int windowSize) {
        fftParameters.windowSize = windowSize;
        return this;
    }

    public QuiFFT normalized() {
        fftParameters.isNormalized = true;
        return this;
    }

    public FFTResult fullFFT() {
        FFTResult fftResult = new FFTResult();
        fftResult.setMetadata(audioReader, fftParameters);

        int[] wave = audioReader.getWaveform();
//        FFTFrame[] fftFrames = new FFTFrame[(wave.length / fftParameters.windowSize) + 1];
        FFTFrame[] fftFrames = new FFTFrame[wave.length / fftParameters.windowSize];

        int i = 0;
        long currentAudioTimeMs = 0;
        // todo: fix this loop so the last partial window (wave.length % windowSize) is captured
        for(int s = fftParameters.windowSize; s < wave.length; s += fftParameters.windowSize) {
            int[] sampleWindow = new int[fftParameters.windowSize];
            System.arraycopy(wave, s - fftParameters.windowSize, sampleWindow, 0, fftParameters.windowSize);

            fftFrames[i++] = doFFT(sampleWindow, currentAudioTimeMs, fftResult.windowDurationMs);
            currentAudioTimeMs += fftResult.windowDurationMs;
        }

        if(fftParameters.isNormalized) {
            normalizeFFTResult(fftFrames);
        }

        fftResult.fftFrames = fftFrames;
        return fftResult;
    }

    private FFTFrame doFFT(int[] wave, long startTimeMs, long windowDurationMs) {
        // get complex FFT values
        Complex[] complexWave = Complex.convertIntToComplex(wave);
        Complex[] complexFFT = FFT.fft(complexWave);

        // compute frequency increment for bins
        double frequencyAxisIncrement = audioReader.getAudioFormat().getSampleRate() / (double) wave.length;

        // copy first half of FFT results into a list of frequency bins
        // (FFT is symmetrical so any information after the halfway point is redundant)
        FrequencyBin[] bins = new FrequencyBin[complexFFT.length / 2];
        for(int i = 0; i < bins.length; i++) {
            bins[i] = new FrequencyBin(i * frequencyAxisIncrement, complexFFT[i].abs());
        }

        return new FFTFrame(startTimeMs, startTimeMs + windowDurationMs, bins);
    }

    private void normalizeFFTResult(FFTFrame[] fftFrames) {
        double maxAmp = findMaxAmplitude(fftFrames);
        for(FFTFrame frame : fftFrames) {
            for(FrequencyBin bin : frame.bins) {
                bin.amplitude /= maxAmp;
            }
        }
    }

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
