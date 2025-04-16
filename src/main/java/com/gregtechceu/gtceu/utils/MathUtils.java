package com.gregtechceu.gtceu.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

public class MathUtils {

    public static float ratio(Number a, Number b) {
        if (a instanceof BigInteger || b instanceof BigInteger) {
            return new BigDecimal(numberToBigInteger(a)).divide(new BigDecimal(numberToBigInteger(b)), MathContext.DECIMAL32).floatValue();
        } else  {
            return a.floatValue() / b.floatValue();
        }
    }

    public static int compare(Number a, Number b) {
        if (a instanceof BigInteger || b instanceof BigInteger) {
            var bigA = numberToBigInteger(a);
            var bigB = numberToBigInteger(b);
            return bigA.compareTo(bigB);
        } else {
            var floatA = a.floatValue();
            var floatB = b.floatValue();
            return Float.compare(floatA, floatB);
        }
    }

    public static BigInteger numberToBigInteger(Number number) {
        if (number instanceof BigInteger bigInt)
            return bigInt;
        else if (number instanceof Long || number instanceof Integer || number instanceof Short || number instanceof Byte)
            return BigInteger.valueOf(number.longValue());
        else
            return new BigInteger(number.toString());
    }

}
