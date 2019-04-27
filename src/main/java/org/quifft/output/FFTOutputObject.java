package org.quifft.output;

import org.quifft.audioread.AudioReader;
import org.quifft.params.FFTParameters;

import javax.sound.sampled.AudioFormat;
import java.text.DecimalFormat;

/**
 * Object representing the result of a Fourier transform; superclass of two result types {@link FFTResult} and {@link FFTStream}
 */
public abstract class FFTOutputObject {

    /**
     * Name of file for which the FFT operation was performed
     */
    public String fileName;

    /**
     * Duration of input audio file in milliseconds
     */
    public long fileDurationMs;

    /**
     * Frequency resolution of FFT result
     * <p>This is calculated by dividing the audio file's sampling rate by the number of points in the FFT.
     * A lower frequency resolution makes it easier to distinguish between frequencies that are close together.
     * By improving frequency resolution, however, some time resolution is lost because there are fewer
     * FFT calculations per unit of time (sampling windows are larger).</p>
     */
    public double frequencyResolution;

    /**
     * Length of each sampling window in milliseconds
     * <p>This is proportional to the length of each window in terms of number of samples.</p>
     */
    public double windowDurationMs;

    /**
     * The parameters used to compute this FFT
     */
    public FFTParameters fftParameters;

    // The sample rate of the audio
    private float audioSampleRate;

    /**
     * Sets metadata to be returned by an output object ({@link FFTResult} or {@link FFTStream})
     * @param reader AudioReader created for input file
     * @param params parameters for FFT
     */
    public void setMetadata(AudioReader reader, FFTParameters params) {
        this.fileName = reader.getFile().getName();

        this.fileDurationMs = reader.getFileDurationMs();

        AudioFormat format = reader.getAudioFormat();
        this.audioSampleRate = format.getSampleRate();
        this.frequencyResolution = format.getSampleRate() / params.totalWindowLength();

        double sampleLengthMs = 1 / format.getSampleRate() * 1000;
        this.windowDurationMs = sampleLengthMs * params.windowSize;

        this.fftParameters = params;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("== ").append(this instanceof FFTResult ? "FFTResult" : "FFTStream")
                .append(" ==========================\n");
        builder.append(String.format("File: %s\n", fileName));
        builder.append(String.format("Audio sample rate: %d\n", (long) audioSampleRate));
        builder.append(String.format("Frequency resolution: %.3f Hz\n", frequencyResolution));
        builder.append(String.format("Windowing function: %s\n", fftParameters.windowFunction.toString()));
        builder.append(String.format("Window duration: %.1f ms\n", windowDurationMs));
        builder.append("Window overlap: ");
        if(fftParameters.windowOverlap == 0) {
            builder.append("none\n");
        } else {
            DecimalFormat decimalFormat = new DecimalFormat("#.#");
            builder.append(String.format("%s", decimalFormat.format(fftParameters.windowOverlap * 100)))
                    .append("%\n");
        }
        builder.append(String.format("Number of points in FFT: %d", fftParameters.windowSize));
        if(fftParameters.numPoints != null) {
            builder.append(String.format(" window size + %d zero-padding = %d",
                    fftParameters.zeroPadLength(), fftParameters.numPoints));
        }
        builder.append(" points");

        builder.append("\n");
        return builder.toString();
    }
}
