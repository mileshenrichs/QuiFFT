package org.quifft;

import org.junit.Test;
import org.quifft.output.BadParametersException;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

public class ParameterValidationTest {

    private static File audioFile = TestUtils.getAudioFile("600hz-tone-3secs-mono.wav");

    @Test(expected = BadParametersException.class)
    public void Window_Size_Is_Negative() throws IOException, UnsupportedAudioFileException {
        new QuiFFT(audioFile).windowSize(-1).fullFFT();
    }

    @Test(expected = BadParametersException.class)
    public void Num_Points_Not_Set_And_Window_Size_Not_Power_Of_Two() throws IOException, UnsupportedAudioFileException {
        new QuiFFT(audioFile).windowSize(8190).fullFFT();
    }

    @Test(expected = BadParametersException.class)
    public void Window_Function_Is_Null() throws IOException, UnsupportedAudioFileException {
        new QuiFFT(audioFile).windowFunction(null).fullFFT();
    }

    @Test(expected = BadParametersException.class)
    public void Window_Overlap_Is_Negative() throws IOException, UnsupportedAudioFileException {
        new QuiFFT(audioFile).windowOverlap(-0.50).fullFFT();
    }

    @Test(expected = BadParametersException.class)
    public void Window_Overlap_Equals_One() throws IOException, UnsupportedAudioFileException {
        new QuiFFT(audioFile).windowOverlap(1).fullFFT();
    }

    @Test(expected = BadParametersException.class)
    public void Window_Overlap_Is_Greater_Than_One() throws IOException, UnsupportedAudioFileException {
        new QuiFFT(audioFile).windowOverlap(2.5).fullFFT();
    }

    @Test(expected = BadParametersException.class)
    public void Num_Points_Is_Negative() throws IOException, UnsupportedAudioFileException {
        new QuiFFT(audioFile).numPoints(-2048).fullFFT();
    }

    @Test(expected = BadParametersException.class)
    public void Num_Points_Is_Less_Than_Window_Size() throws IOException, UnsupportedAudioFileException {
        new QuiFFT(audioFile).windowSize(8192).numPoints(4096).fullFFT();
    }

    @Test(expected = BadParametersException.class)
    public void Num_Points_Is_Not_A_Power_Of_Two() throws IOException, UnsupportedAudioFileException {
        new QuiFFT(audioFile).windowSize(512).numPoints(1023).fullFFT();
    }

    @Test(expected = BadParametersException.class)
    public void Using_FFT_Stream_And_Normalized_Output_Without_Decibel_Scale() throws IOException, UnsupportedAudioFileException {
        new QuiFFT(audioFile).normalized(true).dBScale(false).fftStream();
    }

}
