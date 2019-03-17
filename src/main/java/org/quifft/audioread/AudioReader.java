package org.quifft.audioread;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads audio files into {@code int[]} waveforms
 * <p>In a waveform array, each value represents a sample of the sound wave at discrete time steps.</p>
 */
public abstract class AudioReader {

    /**
     * Audio file being read
     */
    File audio;

    /**
     * Input stream for audio file
     */
    AudioInputStream inputStream;

    /**
     * Obtains waveform for entirety of audio file
     * @return waveform for entirety of audio file
     */
    public int[] getWaveform() {
        List<Byte> bytes = getBytes();
        int frameSize = inputStream.getFormat().getFrameSize();

        int n = bytes.size() / frameSize;
        int[] wave = new int[n];
        int b = 0; // index into bytes list

        for(int i = 0; i < n; i++) {
            ByteBuffer bb = ByteBuffer.allocate(5);
            bb.order(inputStream.getFormat().isBigEndian() ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
            for(int j = 0; j < frameSize; j++) {
                bb.put(bytes.get(b++));
            }

            wave[i] = bb.getShort(0);
        }

        return wave;
    }

    /**
     * Get the audio file being used by this AudioReader
     * @return the audio file being used by this AudioReader
     */
    public File getFile() {
        return audio;
    }

    /**
     * Get duration of audio file being read in milliseconds
     * @return duration of audio file in milliseconds
     */
    public abstract long getFileDurationMs();

    /**
     * Get {@link AudioFormat} of audio file
     * @return format of audio file
     */
    public AudioFormat getAudioFormat() {
        return inputStream.getFormat();
    }

    /**
     * Extracts audio bytes from input stream
     * @return all bytes present in audio file
     */
    private List<Byte> getBytes() {
        try {
            AudioFormat audioFormat = inputStream.getFormat();
            int bytesPerFrame = audioFormat.getFrameSize();
            if (bytesPerFrame == AudioSystem.NOT_SPECIFIED) {
                bytesPerFrame = 1;
            }

            // Set an arbitrary buffer size of 1024 frames.
            int numBytes = 1024 * bytesPerFrame;
            byte[] audioBytes = new byte[numBytes];
            List<Byte> fullSongBytes = new ArrayList<>();
            try {
                while (inputStream.read(audioBytes) != -1) {
                    for(byte audioByte : audioBytes) {
                        fullSongBytes.add(audioByte);
                    }
                }

                return fullSongBytes;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return new ArrayList<>();
    }

}
