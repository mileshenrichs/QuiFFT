package org.quifft.audioread;

import javax.sound.sampled.AudioFormat;
import java.io.File;

/**
 * Interface for reading audio files into int[] waveforms.
 * In a waveform array, each value represents a sample of the sound wave at discrete time steps.
 */
public interface AudioReader {

    int[] getWaveform();

    File getFile();

    long getFileDurationMs();

    AudioFormat getAudioFormat();

}
