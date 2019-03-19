package org.quifft.audioread;

import com.google.common.io.ByteStreams;
import org.quifft.output.FFTStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Iterator;

/**
 * Reads audio files into {@code int[]} waveforms
 * <p>In a waveform array, each value represents a sample of the sound wave at discrete time steps.</p>
 */
public abstract class AudioReader implements Iterator<byte[]> {

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
        byte[] bytes = getBytes();
        int frameSize = inputStream.getFormat().getFrameSize();

        int n = bytes.length / frameSize;
        int[] wave = new int[n];
        int b = 0; // index into bytes list

        for(int i = 0; i < n; i++) {
            ByteBuffer bb = ByteBuffer.allocate(5);
            bb.order(inputStream.getFormat().isBigEndian() ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
            for(int j = 0; j < frameSize; j++) {
                bb.put(bytes[b++]);
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

    // todo: see if I can make getBytes() have the right length for MP3 files
    /**
     * Extracts audio bytes from input stream with help of Guava's {@link ByteStreams} class
     * @return all bytes present in audio file
     */
    private byte[] getBytes() {
        try {
            return ByteStreams.toByteArray(inputStream);
        } catch (IOException e) {
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

        return new byte[0];
    }

    /**
     * Only used for {@link FFTStream} output type -- indicates whether another window of samples exists
     * @return true if another window of samples exists in audio stream
     */
    @Override
    public boolean hasNext() {
        return true;
    }

    /**
     * Only used for {@link FFTStream} output type -- returns next window of samples from audio waveform
     * @return next window of samples from audio waveform
     */
    @Override
    public byte[] next() {
        // todo: use below code to implement byte[] iterator from input stream
//        try {
//            AudioFormat audioFormat = inputStream.getFormat();
//            int bytesPerFrame = audioFormat.getFrameSize();
//            if (bytesPerFrame == AudioSystem.NOT_SPECIFIED) {
//                bytesPerFrame = 1;
//            }
//
//            // Set an arbitrary buffer size of 1024 frames.
//            byte[] audioBytes = new byte[1024 * bytesPerFrame];
//            List<Byte> fullSongBytes = new ArrayList<>();
//            int numBytes;
//            try {
//                while ((numBytes = inputStream.read(audioBytes)) != -1) {
//                    totalBytesRead += numBytes;
//                    for(byte audioByte : audioBytes) {
//                        fullSongBytes.add(audioByte);
//                    }
//                }
//
//                // once all bytes are read into list, copy into array that reflects actual # of bytes in file
//                byte[] actualBytes = new byte[totalBytesRead];
//                for(int i = 0; i < totalBytesRead; i++) {
//                    actualBytes[i] = fullSongBytes.get(i);
//                }
//
//                return actualBytes;
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if(inputStream != null) {
//                try {
//                    inputStream.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }

        return new byte[0];
    }

}
