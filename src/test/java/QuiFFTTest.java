import org.junit.BeforeClass;
import org.junit.Test;
import org.quifft.QuiFFT;
import org.quifft.output.FFTResult;
import org.quifft.params.FFTParameters;
import org.quifft.params.WindowFunction;

import static org.junit.Assert.*;
import static testutil.TestUtils.*;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

public class QuiFFTTest {

    private static FFTResult quiFFTResult;

    @BeforeClass
    public static void createQuiFFTResult() {
        FFTResult result = null;

        try {
            File audio = getAudioFile("600hz-tone-3secs.wav");
            QuiFFT quiFFT = new QuiFFT(audio).windowSize(8192).windowFunction(WindowFunction.HANNING)
                    .windowOverlap(0.25).numPoints(1024).dBScale(true).normalized(false);
            result = quiFFT.fullFFT();

//            System.out.println("\n");
//            doFFTForSong("flexing-on-purpose-mono.wav");
//            doFFTForSong("lies-mono.wav");
//            doFFTForSong("moon-love-mono.wav");
//            doFFTForSong("noeyeinteam-mono.wav");
//            doFFTForSong("something-new-mono.wav");
//            doFFTForSong("wilson-place-mono.wav");
        } catch (Exception e) {
            e.printStackTrace();
        }

        quiFFTResult = result;
    }

    private static void doFFTForSong(String songFileName) throws IOException, UnsupportedAudioFileException {
        System.out.println(songFileName);
        FFTResult result = new QuiFFT(getAudioFile(songFileName)).windowSize(8192).dBScale(true).normalized(false).fullFFT();
        System.out.println();
    }

    @Test(expected = UnsupportedAudioFileException.class)
    public void Should_Throw_Exception_When_Passed_Non_Audio_File() throws IOException, UnsupportedAudioFileException {
        File audio = getAudioFile("text.txt");
        new QuiFFT(audio);
    }

    @Test
    public void Should_Set_And_Return_FFT_Parameters_Correctly() {
        FFTParameters params = quiFFTResult.fftParameters;

        assertEquals(8192, params.windowSize);
        assertEquals(WindowFunction.HANNING, params.windowFunction);
        assertEquals(0.25, params.windowOverlap, 0);
        assertEquals(1024, params.numPoints);
        assertTrue(params.useDecibelScale);
        assertFalse(params.isNormalized);
    }

    @Test(expected = Test.None.class)
    public void Should_Successfully_Initialize_With_MP3_File() throws IOException, UnsupportedAudioFileException {
        new QuiFFT(getAudioFile("600hz-tone-3secs.mp3"));
    }

}