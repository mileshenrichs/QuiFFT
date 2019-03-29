package org.quifft.fft;

import org.quifft.output.FFTFrame;
import org.quifft.output.FrequencyBin;
import org.quifft.params.FFTParameters;

/**
 * Uses Princeton FFT Implementation to compute {@link FFTFrame}s
 * @see InplaceFFT
 */
public class FFTComputationWrapper {

    /**
     * Computes an FFT for a windowed time domain signal
     * @param wave sampled values from audio waveform
     * @param startTimeMs timestamp in the original audio file at which this sample window begins
     * @param windowDurationMs duration of sample window in milliseconds
     * @param fileDurationMs duration of entire audio file in milliseconds
     * @param audioSampleRate sample rate of audio file
     * @param fftParameters parameters used for this FFT
     * @return a single FFTFrame that is the result of an FFT being computed on wave with given parameters
     */
    public static FFTFrame doFFT(int[] wave, double startTimeMs, double windowDurationMs, double fileDurationMs,
                                 float audioSampleRate, FFTParameters fftParameters) {
        // get complex FFT values
        Complex[] complexWave = Complex.convertIntToComplex(wave);
        InplaceFFT.fft(complexWave); // wave becomes FFT result

        // compute frequency increment for bins
        double frequencyAxisIncrement = audioSampleRate / (double) wave.length;

        // copy first half of FFT results into a list of frequency bins
        // (FFT is symmetrical so any information after the halfway point is redundant)
        FrequencyBin[] bins = new FrequencyBin[complexWave.length / 2];
        for(int i = 0; i < bins.length; i++) {
            double scaledBinAmplitude = 2 * complexWave[i].abs() / fftParameters.totalWindowLength();
            bins[i] = new FrequencyBin(i * frequencyAxisIncrement, scaledBinAmplitude);
        }

        double endMs = Math.min(fileDurationMs, startTimeMs + windowDurationMs); // last window(s) will probably be partial
        return new FFTFrame(startTimeMs, endMs, bins);
    }

    /**
     * Converts bin amplitude contents of FFT frames to a decibel (dB) scale
     * @param fftFrames collection of FFT frames for which amplitudes should be scaled logarithmically
     */
    public static void scaleLogarithmically(FFTFrame[] fftFrames) {
        for(FFTFrame frame : fftFrames) {
            scaleLogarithmically(frame);
        }
    }

    /**
     * Converts bin amplitudes contents of a single FFT frame to a decibel (dB) scale
     * @param fftFrame single FFT frame for which amplitudes should be scaled logarithmically
     */
    public static void scaleLogarithmically(FFTFrame fftFrame) {
        // dB is a measure that compares an intensity (amplitude) to some reference intensity.
        // This reference intensity should be the maximum possible intensity for any sample in the entire signal.
        // For 16-bit signed audio, this intensity is 32768.
        final int MAX_INTENSITY = 32768;

        for(FrequencyBin bin : fftFrame.bins) {
            bin.amplitude = 10 * Math.log10(bin.amplitude / MAX_INTENSITY);

            // establish -100 dB floor (avoid infinitely negative values)
            bin.amplitude = Math.max(bin.amplitude, -100);
        }
    }

}
