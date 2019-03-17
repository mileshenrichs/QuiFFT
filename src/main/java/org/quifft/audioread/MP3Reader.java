package org.quifft.audioread;

import org.quifft.util.DurationUtil;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

/**
 * Audio reader to extract waveform data from MP3 files
 */
public class MP3Reader implements AudioReader {

    // Audio file being read
    private File audio;

    // Input stream of audio file
    private AudioInputStream inputStream;

    /**
     * The construction of a PCMReader opens an {@link AudioInputStream} for the .wav file.
     * @param audio .wav file to be read
     * @throws IOException if an I/O exception occurs when the input stream is initialized
     * @throws UnsupportedAudioFileException if the file is not a valid audio file
     */
    public MP3Reader(File audio) throws IOException, UnsupportedAudioFileException {
        this.audio = audio;
        getInputStream();

        System.out.println("New PCMReader created with audio format: " + inputStream.getFormat().toString());
    }

    @Override
    public int[] getWaveform() {
        return new int[0];
    }

    @Override
    public File getFile() {
        return audio;
    }

    @Override
    public long getFileDurationMs() {
        return DurationUtil.getFileDurationMs(inputStream, audio);
    }

    @Override
    public AudioFormat getAudioFormat() {
        return inputStream.getFormat();
    }

    private void getInputStream() throws IOException, UnsupportedAudioFileException {
        this.inputStream = AudioSystem.getAudioInputStream(audio);
    }

}
