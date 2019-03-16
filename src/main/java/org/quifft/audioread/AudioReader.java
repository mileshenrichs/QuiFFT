package org.quifft.audioread;

import javax.sound.sampled.AudioFormat;
import java.io.File;

/**
 * Interface for reading audio files into int[] waveforms
 * <p>In a waveform array, each value represents a sample of the sound wave at discrete time steps.</p>
 */
public interface AudioReader {

    /**
     * Obtains waveform for entirety of audio file
     * @return waveform for entirety of audio file
     */
    int[] getWaveform();

    /**
     * Get the audio file being used by this AudioReader
     * @return the audio file being used by this AudioReader
     */
    File getFile();

    /**
     * Get duration of audio file being read in milliseconds
     * @return duration of audio file in milliseconds
     */
    long getFileDurationMs();

    /**
     * Get {@link AudioFormat} of audio file
     * @return format of audio file
     */
    AudioFormat getAudioFormat();

}
