package org.quifft.audioread;

import org.quifft.output.FFTStream;
import org.quifft.params.FFTParameters;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Reads audio files into {@code int[]} waveforms
 * <p>In a waveform array, each value represents a sample of the sound wave at discrete time steps.</p>
 */
public abstract class AudioReader implements Iterator<int[]> {

    /**
     * Audio file being read
     */
    File audio;

    /**
     * Input stream for audio file
     */
    AudioInputStream inputStream;

    // Keep count of how many frames have been read (how many times FFTStream's next() has been called)
    private int framesReadCount = 0;

    // Indicates whether or not all bytes in the input stream have been read yet (for FFTStream's hasNext() method)
    private boolean areMoreBytesToRead = true;

    // Keep count of how many samples there are in the full-length waveform as bytes are incrementally read
    private int waveLength;

    // The number of FFT frames that should be extractable; not known until entire input stream has been read
    private int numExpectedFrames;

    // Buffer used to store bytes as they are requested by FFTStream and replace them when they're no longer needed
    private int[] sampleBuffer;

    // FFT Parameters only used by FFTStream
    private FFTParameters fftParameters;

    /**
     * Decodes audio reader's input stream to a target format with bit depth of 16
     * <p>This is used when the input file is an 8-bit WAV or an MP3.</p>
     * @throws IOException if an I/O exception occurs when the input stream is initialized
     * @throws UnsupportedAudioFileException if the file is not a valid audio file or has bit depth greater than 16
     */
    void getInputStreamAs8Bit() throws IOException, UnsupportedAudioFileException {
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

    /**
     * Obtains waveform for entirety of audio file
     * @return waveform for entirety of audio file
     */
    public int[] getWaveform() {
        byte[] bytes = getBytes();
        return convertBytesToSamples(bytes);
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
     * Extracts all bytes from an audio file
     * @return all bytes present in audio file
     */
    private byte[] getBytes() {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int numBytesRead;
            byte[] bytes = new byte[16384];

            while ((numBytesRead = inputStream.read(bytes, 0, bytes.length)) != -1) {
                buffer.write(bytes, 0, numBytesRead);
            }

            return buffer.toByteArray();
        } catch(IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    /**
     * Allows {@link FFTStream} to share FFT parameters with AudioReader
     * @param parameters FFT parameters, which provide details needed to extract windows
     */
    public void setFFTParameters(FFTParameters parameters) {
        fftParameters = parameters;
    }

    /**
     * Only used for {@link FFTStream} output type -- indicates whether another window of samples exists
     * @return true if another window of samples exists in audio stream
     */
    @Override
    public boolean hasNext() {
        return areMoreBytesToRead || framesReadCount < numExpectedFrames;
    }

    /**
     * Only used for {@link FFTStream} output type -- returns next window of samples from audio waveform
     * @return next window of samples from audio waveform
     * @throws NoSuchElementException if next() is called when hasNext() is false
     */
    @Override
    public int[] next() {
        if(!hasNext()) throw new NoSuchElementException();

        boolean isStereo = getAudioFormat().getChannels() == 2;
        int windowSize = fftParameters.windowSize * (isStereo ? 2 : 1);
        double windowOverlap = fftParameters.windowOverlap;
        byte[] newBytes;

        try {
            int numBytesRead; // number of bytes actually read from input stream

            // if first window taken, simply copy window size worth of samples into buffer array
            if(sampleBuffer == null) {
                newBytes = new byte[windowSize * 2]; // 16-bit audio = 2 bytes per sample
                numBytesRead = readBytesToFillArray(newBytes);

                sampleBuffer = convertBytesToSamples(newBytes);
            } else {
                // if previous samples exist in buffer, copy them into next buffer and append newly read bytes
                int samplesToKeep = (int) Math.round(windowSize * windowOverlap);
                int prevSamplesCopyStartIndex = windowSize - samplesToKeep;
                int numMoreBytesToRead = (windowSize - samplesToKeep) * 2;

                // copy overlapped samples into new buffer
                int[] newSampleBuffer = new int[windowSize];
                System.arraycopy(sampleBuffer, prevSamplesCopyStartIndex, newSampleBuffer, 0, samplesToKeep);

                // read new bytes (if there are any)
                if(areMoreBytesToRead) {
                    newBytes = new byte[numMoreBytesToRead];
                    numBytesRead = readBytesToFillArray(newBytes);

                    int[] newSamples = convertBytesToSamples(newBytes);
                    System.arraycopy(newSamples, 0, newSampleBuffer, samplesToKeep, newSamples.length);
                } else {
                    newBytes = new byte[0];
                    numBytesRead = 0;
                }

                sampleBuffer = newSampleBuffer;
            }

            // accumulate length of wave as bytes are read
            waveLength += numBytesRead / 2;

            // whenever fewer bytes are read than can fit in newBytes, it means we've reached the end of
            // the input stream.  at this point, we can compute the number of expected FFT frames
            if(areMoreBytesToRead && numBytesRead < newBytes.length) {
                areMoreBytesToRead = false;

                // now that we know the length of the entire wave, we can compute how many frames there should be
                int lengthOfWave = waveLength / (isStereo ? 2 : 1);
                double frameOverlapMultiplier = 1 / (1 - windowOverlap);
                numExpectedFrames = (int) Math.ceil(((double) lengthOfWave / fftParameters.windowSize) * frameOverlapMultiplier);
            }
        } catch (IOException e) {
            System.err.println("An IOException occurred while reading next bytes from input stream " +
                    "as a result of an fftStream.next() call");
            return new int[windowSize];
        }

        framesReadCount++;
        return sampleBuffer;
    }

    /**
     * Converts a byte array consisting of 16-bit audio into a list of samples half as long
     * (each sample represented by 2 bytes)
     * @param bytes byte array to be converted to samples
     * @return an int[] representing the samples present in the input byte array
     */
    private int[] convertBytesToSamples(byte[] bytes) {
        final int BYTES_PER_SAMPLE = 2;
        int[] samples = new int[bytes.length / BYTES_PER_SAMPLE];

        int b = 0;
        for(int i = 0; i < samples.length; i++) {
            ByteBuffer bb = ByteBuffer.allocate(2);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            for(int j = 0; j < BYTES_PER_SAMPLE; j++) {
                bb.put(bytes[b++]);
            }

            samples[i] = bb.getShort(0);
        }

        return samples;
    }

    /**
     * Reads from the input stream until enough bytes have been read to fill given byte array
     * This method acts as a wrapper for the inputStream.read() method because it doesn't guarantee that it'll
     * read enough bytes to fill the array.
     * @param b byte array to fill with read bytes
     * @return number of bytes actually read
     */
    private int readBytesToFillArray(byte[] b) throws IOException {
        int numBytesRead = 0;
        int lastBytesRead = 0;

        while(numBytesRead < b.length && lastBytesRead != -1) {
            lastBytesRead = inputStream.read(b, numBytesRead, b.length - numBytesRead);

            if(lastBytesRead != -1) {
                numBytesRead += lastBytesRead;
            }
        }

        return numBytesRead;
    }

}
