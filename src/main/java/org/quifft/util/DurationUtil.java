package org.quifft.util;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import java.io.File;

public class DurationUtil {

    public static long getFileDurationMs(AudioInputStream inputStream, File audio) {
        AudioFormat format = inputStream.getFormat();
        long audioFileLength = audio.length();
        int frameSize = format.getFrameSize();
        float frameRate = format.getFrameRate();
        return Math.round((audioFileLength / (frameSize * frameRate)) * 1000);
    }

}
