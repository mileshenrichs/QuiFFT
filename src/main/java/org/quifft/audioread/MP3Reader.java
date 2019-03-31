package org.quifft.audioread;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Audio reader to extract waveform data from MP3 files
 * <p><strong>Depends on: </strong>MP3SPI, JLayer, and Tritonus</p>
 */
public class MP3Reader extends AudioReader {

    // Format of MP3 file, used to compute duration
    private AudioFileFormat audioFileFormat;

    /**
     * The construction of an MP3Reader opens an {@link AudioInputStream} for the .mp3 file.
     * @param audio .mp3 file to be read
     * @throws IOException if an I/O exception occurs when the input stream is initialized
     * @throws UnsupportedAudioFileException if the file is not a valid audio file
     */
    public MP3Reader(File audio) throws IOException, UnsupportedAudioFileException {
        this.audio = audio;
        audioFileFormat = AudioSystem.getAudioFileFormat(audio);
        getInputStream();
    }

    @Override
    public long getFileDurationMs() {
        Map<?, ?> properties = audioFileFormat.properties();
        Long microseconds = (Long) properties.get("duration");
        return (long) ((double) microseconds / 1000.0);
    }

    private void getInputStream() throws IOException, UnsupportedAudioFileException {
        getInputStreamAs8Bit();
    }

}
