package org.quifft.audioread;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

/**
 * Audio reader to extract waveform data from PCM-formatted files (WAV and AIFF)
 */
public class PCMReader extends AudioReader {

    /**
     * The construction of a PCMReader opens an {@link AudioInputStream} for the .wav or .aiff file.
     * @param audio .wav or .aiff file to be read
     * @throws IOException if an I/O exception occurs when the input stream is initialized
     * @throws UnsupportedAudioFileException if the file is not a valid audio file
     */
    public PCMReader(File audio) throws IOException, UnsupportedAudioFileException {
        this.audio = audio;
        getInputStream();
    }

    @Override
    public long getFileDurationMs() {
        AudioFormat format = inputStream.getFormat();
        long audioFileLength = audio.length();
        int frameSize = format.getFrameSize();
        float frameRate = format.getFrameRate();
        return (long) Math.ceil((audioFileLength / (frameSize * frameRate)) * 1000);
    }

    private void getInputStream() throws IOException, UnsupportedAudioFileException {
        inputStream = AudioSystem.getAudioInputStream(audio);

        // convert 8-bit audio into 16-bit
        if(inputStream.getFormat().getSampleSizeInBits() == 8) {
            getInputStreamAs8Bit();
        }
    }
}
