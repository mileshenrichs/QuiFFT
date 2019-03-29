package org.quifft;

import org.quifft.output.FFTFrame;
import org.quifft.output.FFTResult;
import org.quifft.output.FrequencyBin;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestUtils {

    /**
     * Retrieves audio file from /test/resources directory
     * @param fileName name of file in test resources directory
     * @return audio file specified by fileName parameter
     */
    static File getAudioFile(String fileName) {
        Path currentPath = Paths.get(System.getProperty("user.dir"));
        Path filePath = Paths.get(currentPath.toString(), "src", "test", "resources", fileName);
        return filePath.toFile();
    }

    static double findMaxFrequencyBin(FFTFrame fftFrame) {
        double maxAmplitude = -100;
        double maxFrequencyBin = 0;
        for(FrequencyBin bin : fftFrame.bins) {
            if(bin.amplitude > maxAmplitude) {
                maxAmplitude = bin.amplitude;
                maxFrequencyBin = bin.frequency;
            }
        }

        return maxFrequencyBin;
    }

}
