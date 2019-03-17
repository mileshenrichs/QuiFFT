package org.quifft.audioread;

import org.tritonus.share.sampled.file.TAudioFileFormat;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Audio reader to extract waveform data from MP3 files
 * <p><strong>Depends on: </strong>MP3SPI, JLayer, and Tritonus</p>
 */
public class MP3Reader extends AudioReader {

    /**
     * The construction of a PCMReader opens an {@link AudioInputStream} for the .wav file.
     * @param audio .wav file to be read
     * @throws IOException if an I/O exception occurs when the input stream is initialized
     * @throws UnsupportedAudioFileException if the file is not a valid audio file
     */
    public MP3Reader(File audio) throws IOException, UnsupportedAudioFileException {
        this.audio = audio;
        getInputStream();

        System.out.println("New MP3Reader created with audio format: " + inputStream.getFormat().toString());
    }

    @Override
    public long getFileDurationMs() {
        AudioFileFormat fileFormat;
        try {
            fileFormat = AudioSystem.getAudioFileFormat(audio);
        } catch (UnsupportedAudioFileException | IOException e) {
            return 0L;
        }

        if (fileFormat instanceof TAudioFileFormat) {
            Map<?, ?> properties = fileFormat.properties();
            Long microseconds = (Long) properties.get("duration");
            return (long) ((double) microseconds / 1000.0);
        }

        return 0L;
    }

    private void getInputStream() throws IOException, UnsupportedAudioFileException {
        AudioInputStream in = AudioSystem.getAudioInputStream(audio);
        AudioFormat baseFormat = in.getFormat();
        AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                baseFormat.getSampleRate(),
                16,
                baseFormat.getChannels(),
                baseFormat.getChannels() * 2,
                baseFormat.getSampleRate(),
                false);
        this.inputStream = AudioSystem.getAudioInputStream(decodedFormat, in);
    }

}
