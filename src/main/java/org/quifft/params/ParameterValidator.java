package org.quifft.params;

import org.quifft.output.BadParametersException;

/**
 * Validates {@link FFTParameters} prior to the computation of FFT and throws a {@link BadParametersException} if
 * any invalid parameters are found
 */
public class ParameterValidator {

    /**
     * Runs through checklist of parameter validations and throws exception if any issues are identified
     * @param params parameters of the FFT to be computed
     * @param isFFTStream true if is FFTStream, false if is FFTResult
     * @throws BadParametersException if there is an invalid parameter
     */
    public static void validateFFTParameters(FFTParameters params, boolean isFFTStream) {
        // window size must be > 0
        if(params.windowSize <= 0)
            throw new BadParametersException(String.format("Window size must be positive; " +
                    "was set to %d", params.windowSize));

        // window size must be a power of 2 if num points is not set
        if(params.numPoints == null && !isPow2(params.windowSize))
            throw new BadParametersException(String.format("If number of points is not set, window size must be a " +
                    "power of 2; was set to %1$d. \nIf you'd like to use a window of size %1$d, " +
                    "set numPoints to the next power of 2 greater than %1$d so the signal will " +
                    "be zero-padded up to that length.", params.windowSize));

        // window function cannot be null
        if(params.windowFunction == null)
            throw new BadParametersException("Window function cannot be null");

        // window overlap must be positive and less than 1
        if(params.windowOverlap < 0 || params.windowOverlap >= 1)
            throw new BadParametersException(String.format("Window overlap must be a positive value " +
                    "between 0 and 0.99; was set to %f", params.windowOverlap));

        // num points, if set, must be positive
        if(params.numPoints != null && params.numPoints < 0)
            throw new BadParametersException(String.format("Number of points in FFT must be positive; " +
                    "was set to %d", params.numPoints));

        // num points, if set, must be greater than or equal to window size
        if(params.numPoints != null && params.numPoints < params.windowSize)
            throw new BadParametersException(String.format("Number of points in FFT must be at least as large as " +
                    "window size; window size was %d but numPoints was only %d",
                    params.windowSize, params.numPoints));

        // num points, if set, must be a power of 2
        if(params.numPoints != null && !isPow2(params.numPoints))
            throw new BadParametersException(String.format("Number of points in FFT must be a power of two; " +
                    "was set to %d", params.numPoints));

        // normalization without dB scale can't be on for an FFTStream
        if(isFFTStream && !params.useDecibelScale && params.isNormalized)
            throw new BadParametersException("Normalization can't be used without also using dB scale for an FFTStream " +
                    "because it doesn't make any sense -- normalization relies on knowing the maximum amplitude across " +
                    "any frequency in the entire file, and FFTStream only knows the maximum frequency of one window " +
                    "at a time.  If you'd like to use normalization with an FFTStream, it's recommended that you " +
                    "implement this yourself");
    }

    private static boolean isPow2(int n) {
        return n > 1 && ((n & (n - 1)) == 0);
    }

}
