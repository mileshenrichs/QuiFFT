package org.quifft.output;

import org.quifft.params.FFTParameters;

/**
 * Raised when {@link FFTParameters} set through QuiFFT method chaining are illogical, invalid, or otherwise incorrect
 * <p>This exception will be raised in the following cases: </p>
 * <ul>
 *     <li>{@code windowSize} is less than or equal to 0</li>
 *     <li>{@code numPoints} is not set and {@code windowSize} is not a power of 2</li>
 *     <li>{@code windowFunction} is null</li>
 *     <li>{@code windowOverlap} is negative</li>
 *     <li>{@code windowOverlap} is 1.00 or greater</li>
 *     <li>{@code numPoints} is negative</li>
 *     <li>{@code numPoints} is set to be less than {@code windowSize}</li>
 *     <li>{@code numPoints} is set to a value that is not a power of 2</li>
 *     <li>{@code useDecibelScale} is set to false and {@code isNormalzed} is set to true when using an {@link FFTStream}</li>
 *  </ul>
 * @see FFTParameters
 */
public class BadParametersException extends RuntimeException {

    /**
     * Constructs a new {@link BadParametersException} with an explanation of which parameter is invalid
     * @param msg explanation of which FFT parameter is invalid or illogical
     */
    public BadParametersException(String msg) {
        super(msg);
    }

}
