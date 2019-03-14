import org.junit.jupiter.api.Test;
import org.quifft.QuiFFT;

import static org.junit.jupiter.api.Assertions.*;

class QuiFFTTest {

    @Test
    void testInitialization() {
        QuiFFT quiFFT = new QuiFFT();
        assertNotNull(quiFFT);
    }

}