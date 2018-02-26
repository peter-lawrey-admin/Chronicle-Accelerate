package cash.xcl.api.dto;

import static java.lang.Double.isInfinite;
import static java.lang.Double.isNaN;

public enum Validators {
    ;

    public static <T> T notNull(T obj) {
        if (obj == null) {
            throw new NullPointerException();
        }
        return obj;
    }

    public static String notNullOrEmpty(String text) {
        if (text == null) {
            throw new NullPointerException();
        }
        if (text.trim().length() == 0) {
            throw new IllegalArgumentException();
        }
        return text;
    }

    public static <T> T notNull(T obj, String message) {
        if (obj == null) {
            throw new NullPointerException(message);
        }
        return obj;
    }

    public static double notNaN(double value) {
        if (isNaN(value)) {
            throw new IllegalArgumentException();
        }
        return value;
    }

    public static double notNaN(double value, String message) {
        if (isNaN(value)) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    public static double notInfinite(double value) {
        if (isInfinite(value)) {
            throw new IllegalArgumentException();
        }
        return value;
    }

    public static double validNumber(double value) {
        return notInfinite(notNaN(value));
    }

    public static double strictPositive(double value) {
        if (notNaN(value) <= 0) {
            throw new IllegalArgumentException();
        }
        return value;
    }

    public static long strictPositive(long value) {
        if (value <= 0) {
            throw new IllegalArgumentException();
        }
        return value;
    }

    public static double lessThan(double value, double bound) {
        if (notNaN(value) >= bound) {
            throw new IllegalArgumentException();
        }
        return value;
    }

    public static double positive(double value) {
        if (notNaN(value) < 0) {
            throw new IllegalArgumentException();
        }
        return value;
    }

    public static long positive(long value) {
        if (value < 0) {
            throw new IllegalArgumentException();
        }
        return value;
    }
}
