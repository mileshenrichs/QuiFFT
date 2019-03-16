package org.quifft.util;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import java.io.File;

/**
 * Utility class for computing durations of audio
 */
public class DurationUtil {

    /**
     * Computes duration of an audio file in milliseconds
     * @param inputStream input stream to audio file
     * @param audio audio file
     * @return duration of audio file in milliseconds
     */
    public static long getFileDurationMs(AudioInputStream inputStream, File audio) {
        AudioFormat format = inputStream.getFormat();
        long audioFileLength = audio.length();
        int frameSize = format.getFrameSize();
        float frameRate = format.getFrameRate();
        return Math.round((audioFileLength / (frameSize * frameRate)) * 1000);
    }

}
