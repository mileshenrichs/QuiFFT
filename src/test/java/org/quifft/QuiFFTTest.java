package org.quifft;

import org.junit.BeforeClass;
import org.junit.Test;
import org.quifft.output.FFTFrame;
import org.quifft.output.FFTResult;
import org.quifft.output.FrequencyBin;
import org.quifft.params.FFTParameters;
import org.quifft.params.WindowFunction;

import static org.junit.Assert.*;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

public class QuiFFTTest {

    private static File mono600Hz3SecsWav;
    private static File stereo600Hz3SecsWav;
    private static File stereo600Hz500MsWAV;
    private static File mono600Hz3SecsMP3;
    private static File mono500Hz3SecsWav;
    private static File stereo500Hz3SecsWav;
    private static File mono500Hz3SecsMP3;
    private static File stereo500Hz3SecsMP3;

    @BeforeClass
    public static void createQuiFFTResult() {
        mono600Hz3SecsWav = TestUtils.getAudioFile("600hz-tone-3secs-mono.wav");
        stereo600Hz3SecsWav = TestUtils.getAudioFile("600hz-tone-3secs-stereo.wav");
        stereo600Hz500MsWAV = TestUtils.getAudioFile("600hz-tone-500ms-stereo.wav");
        mono600Hz3SecsMP3 = TestUtils.getAudioFile("600hz-tone-3secs-mono.mp3");
        mono500Hz3SecsWav = TestUtils.getAudioFile("500hz-tone-3secs-mono.wav");
        stereo500Hz3SecsWav = TestUtils.getAudioFile("500hz-tone-3secs-stereo.wav");
        mono500Hz3SecsMP3 = TestUtils.getAudioFile("500hz-tone-3secs-mono.mp3");
        stereo500Hz3SecsMP3 = TestUtils.getAudioFile("500hz-tone-3secs-stereo.mp3");
    }

    @Test(expected = UnsupportedAudioFileException.class)
    public void Should_Throw_Exception_When_Passed_Non_Audio_File() throws IOException, UnsupportedAudioFileException {
        File audio = TestUtils.getAudioFile("text.txt");
        new QuiFFT(audio);
    }

    @Test(expected = UnsupportedAudioFileException.class)
    public void Should_Throw_Exception_When_Passed_File_With_No_Extension() throws IOException, UnsupportedAudioFileException {
        File noExtensionFile = TestUtils.getAudioFile("file-with-no-extension");
        new QuiFFT(noExtensionFile);
    }

    @Test
    public void Should_Set_And_Return_FFT_Parameters_Correctly() throws IOException, UnsupportedAudioFileException {
        QuiFFT quiFFT = new QuiFFT(mono600Hz3SecsWav).windowSize(512).windowFunction(WindowFunction.HANNING)
                .windowOverlap(0.25).numPoints(1024).dBScale(true).normalized(false);
        FFTParameters params = quiFFT.fullFFT().fftParameters;

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

    @Test(expected = Test.None.class)
    public void Should_Successfully_Initialize_Given_String_Pathname() throws IOException, UnsupportedAudioFileException {
        new QuiFFT("src/test/resources/600hz-tone-3secs-mono.wav");
    }

    @Test(expected = Test.None.class)
    public void Should_Successfully_Initialize_With_MP3_File() throws IOException, UnsupportedAudioFileException {
        new QuiFFT(mono600Hz3SecsMP3);
    }

    @Test(expected = Test.None.class)
    public void Should_Successfully_Complete_FFT_On_8_Bit_Audio() throws IOException, UnsupportedAudioFileException {
        File audio = TestUtils.getAudioFile("600hz-tone-3secs-mono-8bit.wav");
        new QuiFFT(audio).fullFFT();
    }

    @Test
    public void Should_Return_Amplitudes_Between_0_And_1_When_Normalized() throws IOException, UnsupportedAudioFileException {
        FFTResult result = new QuiFFT(stereo600Hz500MsWAV).dBScale(false).normalized(true).fullFFT();

        for(FFTFrame frame : result.fftFrames) {
            for(FrequencyBin bin : frame.bins) {
                assertTrue(bin.amplitude >= 0);
                assertTrue(bin.amplitude <= 1);
            }
        }
    }

    @Test
    public void Should_Keep_FFT_Result_Metadata_Constant_When_Zero_Padding() throws IOException, UnsupportedAudioFileException {
        final double EXPECTED_WINDOW_DURATION = 46.44;
        FFTResult noPaddingResult = new QuiFFT(mono600Hz3SecsWav).windowSize(2048).fullFFT();
        FFTResult withPaddingResult = new QuiFFT(mono600Hz3SecsWav).windowSize(2048).numPoints(4096).fullFFT();

        // test window duration
        assertEquals(EXPECTED_WINDOW_DURATION, noPaddingResult.fftFrames[0].frameEndMs, 0.01);
        assertEquals(EXPECTED_WINDOW_DURATION, withPaddingResult.fftFrames[0].frameEndMs, 0.01);

        // test frequency resolution
        assertEquals(21.5, noPaddingResult.frequencyResolution, 0.1);
        assertEquals(10.7, withPaddingResult.frequencyResolution, 0.1);
    }

    @Test
    public void Should_Not_Allow_Last_Frames_End_Times_To_Be_Greater_Than_Audio_Length() throws IOException, UnsupportedAudioFileException {
        // no overlap (only check last frame)
        FFTResult result = new QuiFFT(stereo600Hz3SecsWav).windowOverlap(0).fullFFT();
        assertEquals(result.fftFrames[result.fftFrames.length - 1].frameEndMs, result.fileDurationMs, 0.0001);

        // 50% overlap = 2x the frames (check the last 2 frames)
        FFTResult overlapResult = new QuiFFT(stereo600Hz3SecsWav).windowOverlap(0.50).fullFFT();
        assertEquals(overlapResult.fftFrames[overlapResult.fftFrames.length - 1].frameEndMs, result.fileDurationMs, 0.0001);
        assertEquals(overlapResult.fftFrames[overlapResult.fftFrames.length - 2].frameEndMs, result.fileDurationMs, 0.0001);
    }

    @Test
    public void Should_Call_ToString_On_Result_Without_Error() throws IOException, UnsupportedAudioFileException {
        assertNotNull(new QuiFFT(mono600Hz3SecsWav).fullFFT().toString());
        assertNotNull(new QuiFFT(mono600Hz3SecsWav).windowOverlap(0).numPoints(8192).fullFFT().toString());
    }

    @Test
    public void Should_Keep_WAV_Metadata_Equal_Whether_Stereo_Or_Mono() throws IOException, UnsupportedAudioFileException {
        FFTResult stereoResult = new QuiFFT(stereo600Hz3SecsWav).fullFFT();
        FFTResult monoResult = new QuiFFT(mono600Hz3SecsWav).fullFFT();

        assertEquals(stereoResult.fileDurationMs, monoResult.fileDurationMs);
        assertEquals(stereoResult.fftFrames.length, monoResult.fftFrames.length);
        assertEquals(stereoResult.fftFrames[0].frameEndMs, monoResult.fftFrames[0].frameEndMs, 0.001);
    }

    @Test
    public void Should_Keep_MP3_Metadata_Equal_Whether_Stereo_Or_Mono() throws IOException, UnsupportedAudioFileException {
        FFTResult stereoResult = new QuiFFT(stereo500Hz3SecsMP3).fullFFT();
        FFTResult monoResult = new QuiFFT(mono500Hz3SecsMP3).fullFFT();

        assertEquals(stereoResult.fileDurationMs, monoResult.fileDurationMs);
        assertEquals(stereoResult.fftFrames.length, monoResult.fftFrames.length);
        assertEquals(stereoResult.fftFrames[0].frameEndMs, monoResult.fftFrames[0].frameEndMs, 0.001);
    }

    @Test
    public void Should_Compute_Peak_At_500Hz_For_500Hz_Stereo_WAV_Signal() throws IOException, UnsupportedAudioFileException {
        FFTResult result = new QuiFFT(stereo500Hz3SecsWav).fullFFT();
        assertEquals(500, TestUtils.findMaxFrequencyBin(result.fftFrames[0]), result.frequencyResolution);
    }

    @Test
    public void Should_Compute_Peak_At_500Hz_For_500Hz_Mono_WAV_Signal() throws IOException, UnsupportedAudioFileException {
        FFTResult result = new QuiFFT(mono500Hz3SecsWav).fullFFT();
        assertEquals(500, TestUtils.findMaxFrequencyBin(result.fftFrames[0]), result.frequencyResolution);
    }

    @Test
    public void Should_Compute_Peak_At_500Hz_For_500Hz_Stereo_MP3_Signal() throws IOException, UnsupportedAudioFileException {
        FFTResult result = new QuiFFT(stereo500Hz3SecsMP3).fullFFT();
        assertEquals(500, TestUtils.findMaxFrequencyBin(result.fftFrames[0]), result.frequencyResolution);
    }

    @Test
    public void Should_Compute_Peak_At_500Hz_For_500Hz_Mono_MP3_Signal() throws IOException, UnsupportedAudioFileException {
        FFTResult result = new QuiFFT(mono500Hz3SecsMP3).fullFFT();
        assertEquals(500, TestUtils.findMaxFrequencyBin(result.fftFrames[0]), result.frequencyResolution);
    }

    @Test
    public void Should_Compute_Approx_Double_As_Many_Frames_With_50_Percent_Overlap() throws IOException, UnsupportedAudioFileException {
        FFTResult noOverlap = new QuiFFT(mono600Hz3SecsWav).windowOverlap(0).fullFFT();
        FFTResult overlap = new QuiFFT(mono600Hz3SecsWav).windowOverlap(0.50).fullFFT();

        assertTrue(Math.abs(noOverlap.fftFrames.length - (overlap.fftFrames.length / 2)) <= 1);
    }

    @Test
    public void Should_Compute_Approx_4_Times_As_Many_Frames_With_75_Percent_Overlap() throws IOException, UnsupportedAudioFileException {
        FFTResult noOverlap = new QuiFFT(mono600Hz3SecsWav).windowOverlap(0).fullFFT();
        FFTResult overlap = new QuiFFT(mono600Hz3SecsWav).windowOverlap(0.75).fullFFT();

        assertTrue(Math.abs(noOverlap.fftFrames.length - (overlap.fftFrames.length / 4)) <= 1);
    }

    @Test
    public void Should_Take_Half_As_Much_Time_Between_Windows_With_50_Percent_Overlap() throws IOException, UnsupportedAudioFileException {
        FFTResult noOverlap = new QuiFFT(mono600Hz3SecsWav).windowOverlap(0).fullFFT();
        FFTResult overlap = new QuiFFT(mono600Hz3SecsWav).windowOverlap(0.50).fullFFT();

        double noOverlapTime = noOverlap.fftFrames[1].frameStartMs - noOverlap.fftFrames[0].frameStartMs;
        double overlapTime = overlap.fftFrames[1].frameStartMs - overlap.fftFrames[0].frameStartMs;

        assertEquals(overlapTime, noOverlapTime / 2, 0.001);
    }

}