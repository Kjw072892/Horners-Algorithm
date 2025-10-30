import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;
import java.util.regex.Pattern;

public class Horners_Algorithm {

    /**
     * The regex for splitting the polynomial.
     */
    private static final Pattern TERM_SPLIT = Pattern.compile("[+]+|(?=-)");

    /**
     * The regex for checking if the string is a digit.
     */
    private static final Pattern CHECK_DIGIT = Pattern.compile("-?\\d+");

    /**
     * Horner's Algorithm for solving polynomials.
     *
     * @param coeffArr The array of the coefficients sorted from the smallest degree power to
     *                 largest.
     * @param x        The value of which to evaluate the polynomial.
     * @return The value of the polynomial given x.
     */
    private static BigInteger start(final int[] coeffArr, final int x) {
//        Arrays.sort(coeffArr);
        BigInteger p = BigInteger.valueOf(coeffArr[coeffArr.length - 1]);

        for (int i = coeffArr.length - 2; i > -1; i--) {
            p = BigInteger.valueOf(x).multiply(p).add(BigInteger.valueOf(coeffArr[i]));
        }

        return p;
    }

    /**
     * Takes a String polynomial and evaluates the equation given X.
     *
     * @param poly A polynomial of n-degree in terms of 'x'.
     * @param x    The value of which is used to evaluate with the polynomial.
     * @return The value of the evaluated polynomial.
     */
    public static BigInteger start(final String poly, final int x) {
        String[] arr = poly.split(TERM_SPLIT.pattern());
        int[] coefficient = new int[arr.length];
        int[] powerArr = new int[arr.length];
        boolean isInitialized = false;

        int index = coefficient.length - 1;

        for (final String eq : arr) {

            final char[] sub = eq.toCharArray();
            String[] arr2 = new String[sub.length];
            StringBuilder sb = new StringBuilder();

            int nullCounter = 0;
            
            // Counts the number of spaces in the sub string
            for (int i = 0; i < sub.length; i++) {
                if (sub[i] == ' ') {
                    nullCounter++;
                    continue;
                }

                arr2[i] = String.valueOf(sub[i]);
            }
            
            final String[] temp = Arrays.copyOf(arr2, arr2.length - nullCounter);
            
            int diff = 0;

            // Removes the spaces.
            for (int k = 0; k < arr2.length; k++) {
                if (arr2[k] == null) {
                    diff++;
                    continue;
                }
                temp[k - diff] = arr2[k];
            }

            arr2 = temp;
            StringBuilder power = new StringBuilder();
            boolean isPower = false;

            // Gets the exponent value
            for (final String str : arr2) {
                if (str.equals("^")) {
                    isPower = true;
                    continue;
                }

                if (isPower) {
                    power.append(str);
                }
            }

            // Initializes the arrays to the highest rank.
            if (!isInitialized) {

                int highestDegree = Integer.parseInt(power.toString()) + 1;
                powerArr = new int[highestDegree];
                coefficient = new int[highestDegree];
                index = coefficient.length - 1;
                isInitialized = true;
            }

            // Gets the coefficient.
            for (final String str : arr2) {
                if (str.equals("x") || str.equals("^")) {
                    break;
                }
                sb.append(str);
            }


            if (arr2[arr2.length - 1].equals("x")) {
                powerArr[index] = 1;

            } else {

                if (power.toString().matches(CHECK_DIGIT.pattern())) {

                    int powerInt = Integer.parseInt(power.toString());

                    // Appends a 0 to the missing coefficient
                    if (arr2.length > 1 && index < powerArr.length - 1 && Math.abs(powerArr[index + 1] - powerInt) > 1) {

                        powerArr[index] = 0;

                        coefficient[index] = 0;
                        index--;

                        powerArr[index] = powerInt;

                    } else {
                        powerArr[index] = Integer.parseInt(power.toString());

                    }
                } else {
                    powerArr[index] = 1;

                }
            }


            // Safety checks that the coefficient is an integer before parsing.
            if (sb.toString().matches(CHECK_DIGIT.pattern())) {
                
                int num = Integer.parseInt(sb.toString());
                
                coefficient[index] = num;
                
            } else {
                if (arr2[0].equals("-") && arr2[1].equals("x")) {
                    
                    coefficient[index] = -1;

                } else if (arr2[0].equals("x")) {
                    
                    coefficient[index] = 1;
                }
            }
            index--;

        }
        return start(coefficient, x);
    }

    /**
     * Stress testing the Horners algorithm.
     *
     * @param x The value of which to evaluate the polynomial by.
     */
    public static void stressTest(final int x) {
        stressTest(x, 5, 9);
    }

    /**
     * Stress testing the Horners algorithm.
     *
     * @param maxPowerDegree Highest rank for the polynomial.
     * @param maxCoefficient Largest value for the coefficient.
     */
    public static void stressTest(final int x, final int maxPowerDegree,
                                  final int maxCoefficient) {
        String[] operators = {"-", "+"};
        StringBuilder polynomial = new StringBuilder();
        int counter = 0;


        for (int i = 0; i < maxPowerDegree; i++) {
            int number = new Random().nextInt(maxCoefficient+2);
            int randOper = new Random().nextInt(2);
            int randSkip = new Random().nextInt(i, maxPowerDegree+2);

            if (i == 0) {
                polynomial.append(number);
                continue;
            } else if (i == 1) {
                polynomial = new StringBuilder().append(number).append("x")
                        .append(operators[randOper]).append(polynomial);
                continue;

            } else if (i == randSkip) {
                counter++;
                continue;
            }

            polynomial = new StringBuilder().append(number).append("x").append("^")
                    .append(i).append(" ")
                    .append(operators[randOper]).append(" ").append(polynomial);
        }

        System.out.printf("P(%d) = " + polynomial + "\nWhere x = %d\nSkipped: %d\n", x, x,
                        counter);

        BigInteger result = start(polynomial.toString(), x);

        char[] arr = result.toString().toCharArray();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < arr.length; i++) {
            if (i % 3 == 0 && i != 0) {
                sb = new StringBuilder().append(",").append(sb);
            }

            sb = new StringBuilder().append(arr[Math.abs(i - (arr.length - 1))]).append(sb);
        }
        System.out.println(sb);
    }

    public static void main(String[] args) {

        long startTime = System.nanoTime();
        BigInteger result = start("x^5 - x^4+ 5x^3  + x^2 - 5x + 3", 2);
//        stressTest(5, 1000, 10_000);
        long stopTime = System.nanoTime();

        System.out.println(result);
        double time = (double) (stopTime - startTime) / 1_000_000;
        System.out.println("Time: " + time + " ms");
    }
}
