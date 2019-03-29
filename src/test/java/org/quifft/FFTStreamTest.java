package org.quifft;

import org.junit.BeforeClass;
import org.junit.Test;
import org.quifft.output.FFTFrame;
import org.quifft.output.FFTResult;
import org.quifft.output.FFTStream;
import org.quifft.params.FFTParameters;
import org.quifft.params.WindowFunction;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;

public class FFTStreamTest {

    private static File mono600Hz3SecsWav;
    private static File stereo600Hz500MsWAV;
    private static File mono500Hz3SecsWav;
    private static File stereo500Hz3SecsWav;
    private static File mono500Hz3SecsMP3;
    private static File stereo500Hz3SecsMP3;

    @BeforeClass
    public static void createQuiFFTResult() {
        mono600Hz3SecsWav = TestUtils.getAudioFile("600hz-tone-3secs-mono.wav");
        stereo600Hz500MsWAV = TestUtils.getAudioFile("600hz-tone-500ms-stereo.wav");
        stereo600Hz500MsWAV = TestUtils.getAudioFile("600hz-tone-500ms-stereo.wav");
        mono500Hz3SecsWav = TestUtils.getAudioFile("500hz-tone-3secs-mono.wav");
        stereo500Hz3SecsWav = TestUtils.getAudioFile("500hz-tone-3secs-stereo.wav");
        mono500Hz3SecsMP3 = TestUtils.getAudioFile("500hz-tone-3secs-mono.mp3");
        stereo500Hz3SecsMP3 = TestUtils.getAudioFile("500hz-tone-3secs-stereo.mp3");
    }

    @Test(expected = NoSuchElementException.class)
    public void Should_Throw_Exception_If_Next_Called_When_No_More_Samples_Remain() throws IOException, UnsupportedAudioFileException {
        FFTStream fftStream = new QuiFFT(stereo600Hz500MsWAV).fftStream();
        for(int i = 0; i < 100; i++) {
            fftStream.next();
        }
    }

    @Test
    public void Should_Call_ToString_On_Stream_Without_Error() throws IOException, UnsupportedAudioFileException {
        assertNotNull(new QuiFFT(mono600Hz3SecsWav).fftStream().toString());
    }

    @Test
    public void Should_Set_And_Return_FFT_Parameters_Correctly_FFTStream() throws IOException, UnsupportedAudioFileException {
        QuiFFT quiFFT = new QuiFFT(mono600Hz3SecsWav).windowSize(512).windowFunction(WindowFunction.HANNING)
                .windowOverlap(0.25).numPoints(1024).dBScale(true).normalized(false);
        FFTParameters params = quiFFT.fftStream().fftParameters;

        // test FFT params object
        assertEquals(512, params.windowSize);
        assertEquals(WindowFunction.HANNING, params.windowFunction);
        assertEquals(0.25, params.windowOverlap, 0);
        assertEquals(1024, (int) params.numPoints);
        assertTrue(params.useDecibelScale);
        assertFalse(params.isNormalized);

        // test QuiFFT accessor methods
        assertEquals(512, quiFFT.windowSize());
        assertEquals(WindowFunction.HANNING, quiFFT.windowFunction());
        assertEquals(0.25, quiFFT.windowOverlap(), 0);
        assertEquals(1024, quiFFT.numPoints());
        assertTrue(quiFFT.dBScale());
        assertFalse(quiFFT.normalized());
    }

    @Test
    public void Should_Keep_FFT_Result_Metadata_Constant_When_Zero_Padding_FFTStream() throws IOException, UnsupportedAudioFileException {
        final double EXPECTED_WINDOW_DURATION = 46.44;
        FFTStream noPaddingResult = new QuiFFT(mono600Hz3SecsWav).windowSize(2048).fftStream();
        FFTStream withPaddingResult = new QuiFFT(mono600Hz3SecsWav).windowSize(2048).numPoints(4096).fftStream();

        // test window duration
        FFTFrame noPaddingFrame = noPaddingResult.next();
        FFTFrame withPaddingFrame = withPaddingResult.next();
        assertEquals(EXPECTED_WINDOW_DURATION, noPaddingFrame.frameEndMs, 0.01);
        assertEquals(EXPECTED_WINDOW_DURATION, withPaddingFrame.frameEndMs, 0.01);

        // test frequency resolution
        assertEquals(21.5, noPaddingResult.frequencyResolution, 0.1);
        assertEquals(10.7, withPaddingResult.frequencyResolution, 0.1);
    }

    @Test
    public void Should_Compute_Same_Number_Of_Frames_As_Full_FFT() throws IOException, UnsupportedAudioFileException {
        FFTStream stream = new QuiFFT(stereo600Hz500MsWAV).windowSize(8192).fftStream();
        FFTResult full = new QuiFFT(stereo600Hz500MsWAV).windowSize(8192).fullFFT();

        int streamedFFTFramesCount = 0;
        while(stream.hasNext()) {
            stream.next();
            streamedFFTFramesCount++;
        }

        assertEquals(full.fftFrames.length, streamedFFTFramesCount);
    }

    @Test
    public void Should_Compute_The_Same_FFT_Output_As_Full_FFT() throws IOException, UnsupportedAudioFileException {
        FFTStream stream = new QuiFFT(stereo600Hz500MsWAV).windowSize(8192).fftStream();
        FFTResult full = new QuiFFT(stereo600Hz500MsWAV).windowSize(8192).fullFFT();

        for(int i = 0; i < full.fftFrames.length; i++) {
            FFTFrame streamFrame = stream.next();
            for(int j = 0; j < full.fftFrames[i].bins.length; j++) {
                assertEquals(full.fftFrames[i].bins[j].amplitude, streamFrame.bins[j].amplitude, 0.01);
            }
        }
    }

    @Test
    public void Should_Have_Same_Frame_Start_And_End_Times_As_Full_FFT() throws IOException, UnsupportedAudioFileException {
        FFTStream stream = new QuiFFT(stereo600Hz500MsWAV).fftStream();
        FFTResult full = new QuiFFT(stereo600Hz500MsWAV).fullFFT();

        for(int i = 0; i < 3; i++) {
            FFTFrame streamFrame = stream.next();
            assertEquals(full.fftFrames[i].frameStartMs, streamFrame.frameStartMs, 0.001);
            assertEquals(full.fftFrames[i].frameEndMs, streamFrame.frameEndMs, 0.001);
        }
    }

    @Test
    public void Should_Compute_Peak_At_500Hz_For_500Hz_Stereo_WAV_Signal_FFTStream() throws IOException, UnsupportedAudioFileException {
        FFTStream stream = new QuiFFT(stereo500Hz3SecsWav).dBScale(true).fftStream();

        // call next() a few times so any issues with window overlap will be caught
        for(int i = 0; i < 5; i++) {
            stream.next();
        }

        assertEquals(500, TestUtils.findMaxFrequencyBin(stream.next()), stream.frequencyResolution);
    }

    @Test
    public void Should_Compute_Peak_At_500Hz_For_500Hz_Mono_WAV_Signal_FFTStream() throws IOException, UnsupportedAudioFileException {
        FFTStream stream = new QuiFFT(mono500Hz3SecsWav).dBScale(true).fftStream();

        // call next() a few times so any issues with window overlap will be caught
        for(int i = 0; i < 5; i++) {
            stream.next();
        }

        assertEquals(500, TestUtils.findMaxFrequencyBin(stream.next()), stream.frequencyResolution);
    }

    @Test
    public void Should_Compute_Peak_At_500Hz_For_500Hz_Stereo_MP3_Signal_FFTStream() throws IOException, UnsupportedAudioFileException {
        FFTStream stream = new QuiFFT(stereo500Hz3SecsMP3).dBScale(true).fftStream();

        // call next() a few times so any issues with window overlap will be caught
        for(int i = 0; i < 5; i++) {
            stream.next();
        }

        assertEquals(500, TestUtils.findMaxFrequencyBin(stream.next()), stream.frequencyResolution);
    }

    @Test
    public void Should_Compute_Peak_At_500Hz_For_500Hz_Mono_MP3_Signal_FFTStream() throws IOException, UnsupportedAudioFileException {
        FFTStream stream = new QuiFFT(mono500Hz3SecsMP3).dBScale(true).fftStream();

        // call next() a few times so any issues with window overlap will be caught
        for(int i = 0; i < 5; i++) {
            stream.next();
        }

        assertEquals(500, TestUtils.findMaxFrequencyBin(stream.next()), stream.frequencyResolution);
    }

    @Test
    public void Should_Compute_Approx_Double_As_Many_Frames_With_50_Percent_Overlap_FFTStream() throws IOException, UnsupportedAudioFileException {
        FFTStream noOverlap = new QuiFFT(mono600Hz3SecsWav).windowOverlap(0).fftStream();
        FFTStream overlap = new QuiFFT(mono600Hz3SecsWav).windowOverlap(0.50).fftStream();

        int noOverlapFramesCount = 0;
        int overlapFramesCount = 0;
        while(noOverlap.hasNext()) {
            noOverlap.next();
            noOverlapFramesCount++;
        }
        while(overlap.hasNext()) {
            overlap.next();
            overlapFramesCount++;
        }

        assertTrue(Math.abs(noOverlapFramesCount - (overlapFramesCount / 2)) <= 1);
    }

    @Test
    public void Should_Compute_Approx_4_Times_As_Many_Frames_With_75_Percent_Overlap() throws IOException, UnsupportedAudioFileException {
        FFTStream noOverlap = new QuiFFT(mono600Hz3SecsWav).windowOverlap(0).fftStream();
        FFTStream overlap = new QuiFFT(mono600Hz3SecsWav).windowOverlap(0.75).fftStream();

        int noOverlapFramesCount = 0;
        int overlapFramesCount = 0;
        while(noOverlap.hasNext()) {
            noOverlap.next();
            noOverlapFramesCount++;
        }
        while(overlap.hasNext()) {
            overlap.next();
            overlapFramesCount++;
        }

        assertTrue(Math.abs(noOverlapFramesCount - (overlapFramesCount / 4)) <= 1);
    }

    @Test
    public void Should_Compute_Same_Frame_Start_Times_As_Full_FFT() throws IOException, UnsupportedAudioFileException {
        FFTFrame[] fullFFTFrames = new QuiFFT(mono600Hz3SecsWav).windowOverlap(0.25).fullFFT().fftFrames;
        FFTStream fftStream = new QuiFFT(mono600Hz3SecsWav).windowOverlap(0.25).fftStream();
        for(int i = 0; i < 3; i++) {
            assertEquals(fullFFTFrames[i].frameStartMs, fftStream.next().frameStartMs, 0.0001);
        }
    }

    @Test
    public void Should_Take_Half_As_Much_Time_Between_Windows_With_50_Percent_Overlap_FFTStream() throws IOException, UnsupportedAudioFileException {
        FFTStream noOverlap = new QuiFFT(mono600Hz3SecsWav).windowOverlap(0).fftStream();
        FFTStream overlap = new QuiFFT(mono600Hz3SecsWav).windowOverlap(0.50).fftStream();

        FFTFrame noOverlap1 = noOverlap.next();
        FFTFrame noOverlap2 = noOverlap.next();
        FFTFrame overlap1 = overlap.next();
        FFTFrame overlap2 = overlap.next();

        double noOverlapTime = noOverlap2.frameStartMs - noOverlap1.frameStartMs;
        double overlapTime = overlap2.frameStartMs - overlap1.frameStartMs;

        assertEquals(overlapTime, noOverlapTime / 2, 0.001);
    }

}
