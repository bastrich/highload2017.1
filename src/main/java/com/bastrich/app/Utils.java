package com.bastrich.app;

/**
 * @author bastrich on 19.08.2017.
 */
public class Utils {

    public static long parseLong(byte[] number) {
        long result = 0;
        int length = number.length;
        boolean minus = number[0] == '-';
        for (int i = minus ? 1 : 0; i < length; i++) {
            if (number[i] == '1') {
                result += tenPow(length - i - 1);
            } else if (number[i] == '2') {
                result += 2 * tenPow(length - i - 1);
            } else if (number[i] == '3') {
                result += 3 * tenPow(length - i - 1);
            } else if (number[i] == '4') {
                result += 4 * tenPow(length - i - 1);
            } else if (number[i] == '5') {
                result += 5 * tenPow(length - i - 1);
            } else if (number[i] == '6') {
                result += 6 * tenPow(length - i - 1);
            } else if (number[i] == '7') {
                result += 7 * tenPow(length - i - 1);
            } else if (number[i] == '8') {
                result += 8 * tenPow(length - i - 1);
            } else if (number[i] == '9') {
                result += 9 * tenPow(length - i - 1);
            } else if (number[i] != '0') {
                break;
            }
        }
        if (minus) {
            result *= -1;
        }
        return result;
    }

    public static int parseInt(byte[] number) {
        int result = 0;
        int length = number.length;
        for (int i = 0; i < length; i++) {
            if (number[i] == '1') {
                result += tenPow(length - i - 1);
            } else if (number[i] == '2') {
                result += 2 * tenPow(length - i - 1);
            } else if (number[i] == '3') {
                result += 3 * tenPow(length - i - 1);
            } else if (number[i] == '4') {
                result += 4 * tenPow(length - i - 1);
            } else if (number[i] == '5') {
                result += 5 * tenPow(length - i - 1);
            } else if (number[i] == '6') {
                result += 6 * tenPow(length - i - 1);
            } else if (number[i] == '7') {
                result += 7 * tenPow(length - i - 1);
            } else if (number[i] == '8') {
                result += 8 * tenPow(length - i - 1);
            } else if (number[i] == '9') {
                result += 9 * tenPow(length - i - 1);
            } else if (number[i] != '0') {
                break;
            }
        }
        return result;
    }

    private static long tenPow(int pow) {
        long res = 1;
        for (int i = 0; i < pow; i++) {
            res *= 10;
        }
        return res;
    }
}
