import org.junit.Test;
import org.quifft.QuiFFT;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;


public class QuiFFTTest {

    @Test(expected = UnsupportedAudioFileException.class)
    public void Should_Throw_Exception_When_Passed_Non_Audio_File() throws IOException, UnsupportedAudioFileException {
        File audio = getAudioFile("text.txt");
        QuiFFT quiFFT = new QuiFFT(audio);
    }

    private File getAudioFile(String fileName) {
        Path currentPath = Paths.get(System.getProperty("user.dir"));
        Path filePath = Paths.get(currentPath.toString(), "src", "test", "resources", fileName);
        return filePath.toFile();
    }

}