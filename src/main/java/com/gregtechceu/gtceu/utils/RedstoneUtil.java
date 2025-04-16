package com.gregtechceu.gtceu.utils;

import java.math.BigInteger;

public class RedstoneUtil {

    /**
     * Compares a value against a min and max, with an option to invert the logic
     *
     * @param value      value to be compared
     * @param maxValue   the max that the value can be
     * @param minValue   the min that the value can be
     * @param isInverted whether to invert the logic of this method
     * @return an int from 0 (value <= min) to 15 (value >= max) normally, with a ratio when the value is between min
     *         and max
     */
    public static int computeRedstoneBetweenValues(Number value, Number maxValue, Number minValue, boolean isInverted) {
        if (value instanceof BigInteger || maxValue instanceof BigInteger || minValue instanceof BigInteger) {
            var bigValue = MathUtils.numberToBigInteger(value);
            var bigMaxValue = MathUtils.numberToBigInteger(maxValue);
            var bigMinValue = MathUtils.numberToBigInteger(minValue);

            if (bigValue.compareTo(bigMaxValue) >= 0) return isInverted ? 0 : 15;
            else if (bigValue.compareTo(bigMinValue) <= 0) return isInverted ? 15 : 0;

            float ratio;
            if (!isInverted) ratio = 15 * MathUtils.ratio(bigValue.subtract(bigMinValue), bigMaxValue.subtract(bigMinValue));
            else ratio = 15 * MathUtils.ratio(bigMaxValue.subtract(bigValue), bigMaxValue.subtract(bigMinValue));

            return Math.round(ratio);
        } else {
            var floatValue = value.floatValue();
            var floatMaxValue = maxValue.floatValue();
            var floatMinValue = minValue.floatValue();

            if (floatValue >= floatMaxValue) return isInverted ? 0 : 15;
            else if (floatValue <= floatMinValue) return isInverted ? 15 : 0;

            float ratio;
            if (!isInverted) ratio = 15 * (floatValue - floatMinValue) / (floatMaxValue - floatMinValue);
            else ratio = 15 * (floatMaxValue - floatValue) / (floatMaxValue - floatMinValue);

            return Math.round(ratio);
        }
    }

    /**
     * Compares a value against a min and max, with an option to invert the logic. Has latching functionality.
     *
     * @param value    value to be compared
     * @param maxValue the max that the value can be
     * @param minValue the min that the value can be
     * @param output   the output value the function modifies
     * @return returns the modified output value
     */
    public static int computeLatchedRedstoneBetweenValues(Number value, Number maxValue, Number minValue, boolean isInverted, int output) {
        if (value instanceof BigInteger || maxValue instanceof BigInteger || minValue instanceof BigInteger) {
            var bigValue = MathUtils.numberToBigInteger(value);
            var bigMaxValue = MathUtils.numberToBigInteger(maxValue);
            var bigMinValue = MathUtils.numberToBigInteger(minValue);

            if (bigValue.compareTo(bigMaxValue) >= 0) output = !isInverted ? 0 : 15; // value above maxValue should normally be 0, otherwise 15
            else if (bigValue.compareTo(bigMinValue) <= 0) output = !isInverted ? 15 : 0; // value below minValue should normally be 15, otherwise 0
        } else {
            var floatValue = value.floatValue();
            var floatMaxValue = maxValue.floatValue();
            var floatMinValue = minValue.floatValue();

            if (floatValue >= floatMaxValue) output = !isInverted ? 0 : 15; // value above maxValue should normally be 0, otherwise 15
            else if (floatValue <= floatMinValue) output = !isInverted ? 15 : 0; // value below minValue should normally be 15, otherwise 0
        }
        return output;
    }

    /**
     * Compares a current against max, with an option to invert the logic.
     *
     * @param current    value to be compared
     * @param max        the max that value can be
     * @param isInverted whether to invert the logic of this method
     * @return value 0 to 15; A value of <em>at least</em> 1 is returned if current > 0
     * @throws ArithmeticException when max is 0
     */
    public static int computeRedstoneValue(Number current, Number max, boolean isInverted) throws ArithmeticException {
        int output;
        if (current instanceof BigInteger || max instanceof BigInteger) {
            var bigCurrent = MathUtils.numberToBigInteger(current);
            var bigMax = MathUtils.numberToBigInteger(max);
            var isNotEmpty = bigCurrent.compareTo(BigInteger.ZERO) > 0;
            output = (int) (14f * MathUtils.ratio(bigCurrent, bigMax)) + (isNotEmpty ? 1 : 0);
        } else {
            var floatCurrent = current.floatValue();
            var floatMax = max.floatValue();
            output = (int) (14f * floatCurrent / floatMax) + (floatCurrent > 0 ? 1 : 0);
        }
        return isInverted ? 15 - output: output;
    }
}
