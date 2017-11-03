import java.io.BufferedReader;
import java.io.FileReader;

public class Strassen {

    static final int STRASSEN_MULT = 0;
    static final int STANDARD_MULT = 1;
    static final int CROSSOVER = 64;

    static int MULT_MODE = STRASSEN_MULT;

    static boolean DEBUGGING = false;
    static boolean CROSSOVER_TEST = false;

    /*
        Formula:
        [[ A B ]     [[ E F ]      [[AE + BG   AF + BH]
         [ C D ]]  *  [ G H ]]  =   [CE + DG   CF + DH]]
     */

    public static int[][] strassenWithCrossover(int[][] X, int[][] Y, int crossover) {
        int[][] ret = new int[X.length][X.length];
        if (X.length <= crossover) {
            ret = standardMult(X, Y);
            return ret;
        }

        int n = X.length;

        int[][] A = getSubMatrix(X, 0, 0);
        int[][] D = getSubMatrix(X, n / 2, n / 2);

        int[][] E = getSubMatrix(Y, 0, 0);
        int[][] H = getSubMatrix(Y, n / 2, n / 2);

        int[][] P1 = strassenWithCrossover(A, subtract(Y, 0, n / 2, Y, n / 2, n / 2), crossover);
        int[][] P2 = strassenWithCrossover(add(X, 0, 0, X, 0, n / 2), H, crossover);
        int[][] P3 = strassenWithCrossover(add(X, n / 2, 0, X, n / 2, n / 2), E, crossover);
        int[][] P4 = strassenWithCrossover(D, subtract(Y, n / 2, 0, Y, 0, 0), crossover);
        int[][] P5 = strassenWithCrossover(add(X, 0, 0, X, n / 2, n / 2), add(Y, 0, 0, Y, n / 2, n / 2), crossover);
        int[][] P6 = strassenWithCrossover(subtract(X, 0, n / 2, X, n / 2, n / 2), add(Y, n / 2, 0, Y, n / 2, n / 2), crossover);
        int[][] P7 = strassenWithCrossover(subtract(X, 0, 0, X, n / 2, 0), add(Y, 0, 0, Y, 0, n / 2), crossover);

        int[][] AE_plus_BG = subtract(add(P5, P4), subtract(P2, P6));
        int[][] AF_plus_BH = add(P1, P2);
        int[][] CE_plus_DG = add(P3, P4);
        int[][] CF_plus_DH = subtract(add(P5, P1), add(P3, P7));

        assignSubMatrix(ret, 0, 0, AE_plus_BG);
        assignSubMatrix(ret, 0, n / 2, AF_plus_BH);
        assignSubMatrix(ret, n / 2, 0, CE_plus_DG);
        assignSubMatrix(ret, n / 2, n / 2, CF_plus_DH);

        return ret;

    }

    private static int[][] getSubMatrix(int[][] matrix, int rowStart, int colStart) {

        int[][] ret = new int[matrix.length / 2][matrix.length / 2];
        int i = rowStart;
        for (int row = 0; row < matrix.length / 2; row++) {
            int j = colStart;
            for (int col = 0; col < (matrix.length / 2); col++) {
                ret[row][col] = matrix[i][j];
                j++;
            }
            i++;
        }
        return ret;
    }

    private static void assignSubMatrix(int[][] matrix, int rowStart, int colStart, int[][] sub) {

        int i = rowStart;
        int j;
        for (int row = 0; row < matrix.length / 2; row++) {
            j = colStart;
            for (int col = 0; col < matrix.length / 2; col++) {
                matrix[i][j] = sub[row][col];
                j++;
            }
            i++;
        }
    }

    private static int[][] add(int[][] X, int[][] Y) {

        int[][] ret = new int[X.length][X.length];
        for (int row = 0; row < ret.length; row++) {
            for (int col = 0; col < ret.length; col++) {
                ret[row][col] = X[row][col] + Y[row][col];
            }
        }

        return ret;
    }

    private static int[][] add(int[][] X, int X_row_start, int X_col_start, int[][] Y, int Y_row_start, int Y_col_start) {

        int length = X.length / 2;
        int[][] ret = new int[length][length];
        for (int row = 0; row < length; row++) {
            for (int col = 0; col < length; col++) {
                ret[row][col] = X[X_row_start + row][X_col_start + col] + Y[Y_row_start + row][Y_col_start + col];
            }
        }

        return ret;
    }

    private static int[][] subtract(int[][] X, int[][] Y) {

        int[][] ret = new int[X.length][X.length];
        for (int row = 0; row < ret.length; row++) {
            for (int col = 0; col < ret.length; col++) {
                ret[row][col] = X[row][col] - Y[row][col];
            }
        }

        return ret;

    }

    private static int[][] subtract(int[][] X, int X_row_start, int X_col_start, int[][] Y, int Y_row_start, int Y_col_start) {

        int length = X.length / 2;
        int[][] ret = new int[length][length];
        for (int row = 0; row < length; row++) {
            for (int col = 0; col < length; col++) {
                ret[row][col] = X[X_row_start + row][X_col_start + col] - Y[Y_row_start + row][Y_col_start + col];
            }
        }

        return ret;

    }

    public static void main(String[] args) {

        if (args.length != 3) {
            System.out.println("Usage: ./strassen 0 dimension inputfile");
            System.exit(1);
        }

        int flag = Integer.parseInt(args[0]);
        int dimension = Integer.parseInt(args[1]);
        String inputfile = new String(args[2]);

        if (flag == 1) {
            DEBUGGING = true;
        } else if (flag == 2) {
            CROSSOVER_TEST = true;
        }

        Strassen me = new Strassen();

        if (CROSSOVER_TEST) {
            for (int i = 1 << 7; i < 1 << 16; i *= 2) {
                me.run(i, inputfile, MULT_MODE);
            }

        } else {
            me.run(dimension, inputfile, MULT_MODE);
        }

    }


    public void run(int dimension, String inputfile, int mode) {

        long startTime;

        int[][] X = new int[dimension][dimension];
        int[][] Y = new int[dimension][dimension];

        int[] elements = {0, 1, 2, 0, 2, 1, 1, 0, 2, 1, 2, 0, 2, 1, 0, 2, 0, 1};
        int pos = 0;

        try {
            BufferedReader br = new BufferedReader(new FileReader(inputfile));

            for (int i = 0; i < dimension; i++) {
                for (int j = 0; j < dimension; j++) {
                    if (CROSSOVER_TEST) {
                        X[i][j] = elements[pos++];
                        pos %= elements.length;

                    } else {
                        X[i][j] = Integer.parseInt(br.readLine());

                    }
                }
            }
            for (int k = 0; k < dimension; k++) {
                for (int l = 0; l < dimension; l++) {
                    if (CROSSOVER_TEST) {
                        Y[k][l] = elements[pos++];
                        pos %= elements.length;

                    } else {
                        Y[k][l] = Integer.parseInt(br.readLine());

                    }
                }
            }

            br.close();

        } catch (Exception e) {
            System.err.println("Caught Exception: " + e.getMessage());
        }

        if (DEBUGGING) {
            System.out.println("\n##### Reading Matrices X and Y from file ######\n");
            printMatrix(X,"X");
            printMatrix(Y,"Y");
        }

        if (mode == STANDARD_MULT) {
            int[][] Z = standardMult(X, Y);

            if (DEBUGGING) {
                System.out.println("Standard Product");
                printMatrix(Z, "Z");
            }

        } else if (mode == STRASSEN_MULT && CROSSOVER_TEST) {

            for (int crossover = 2; crossover <= dimension; crossover *= 2) {
                startTime = System.currentTimeMillis();

                int[][] paddedX = pad(X);
                int[][] paddedY = pad(Y);

                int[][] Z = strassenWithCrossover(paddedX, paddedY, crossover);

                printTimes("Strassen Product", startTime, dimension, crossover);
            }

        } else if (mode == STRASSEN_MULT) {
            startTime = System.currentTimeMillis();

            int[][] paddedX = pad(X);
            int[][] paddedY = pad(Y);

            int[][] Z = strassenWithCrossover(paddedX, paddedY, CROSSOVER);

            int[][] ZTrimmed = trim(Z, dimension);

            if (DEBUGGING) {
                printTimes("Strassen Product", startTime, dimension, CROSSOVER);
                printMatrix(ZTrimmed, "Z");

            } else {
                printDiagonal(ZTrimmed);
            }
        }


    }

    private static void printTimes(String mode, long startTime, int dimension, int crossover) {
        System.out.println(mode + " Crossover = " + crossover);
        long time = System.currentTimeMillis() - startTime;
        System.out.printf("Finished Matrix Multiplication of %d dimensions in %d milliseconds, or %.2f minutes\n", dimension, time, ((double) time) / 60 / 1000);
        System.out.println();

    }

    private static int[][] pad(int[][] matrix) {

        int newDim = nextPowerOf2(matrix.length);
        if (newDim == matrix.length)
            return matrix;
        int[][] ret = new int[newDim][newDim];

        for (int row = 0; row < matrix.length; row++) {
            for (int col = 0; col < matrix.length; col++) {
                ret[row][col] = matrix[row][col];
            }
        }
        return ret;

    }

    private static int[][] trim(int[][] matrix, int dim) {

        int[][] ret = new int[dim][dim];
        for (int row = 0; row < dim; row++) {
            for (int col = 0; col < dim; col++) {
                ret[row][col] = matrix[row][col];
            }
        }

        return ret;
    }

    private static int nextPowerOf2(int length) {
        int exponent = (int) (Math.log(length) / Math.log(2));
        int reconstructed = (int) Math.pow(2, exponent);
        if (length != reconstructed) {
            return (int) Math.pow(2, exponent + 1);
        }
        return length;
    }

    // Standard Matrix Multiplication
    public static int[][] standardMult(int[][] A, int[][] B) {

        int dim = B.length;
        int[][] C = new int[B.length][B.length];
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                for (int k = 0; k < dim; k++) {
                    if (DEBUGGING) {
                        System.out.println(C[i][k] + " += " + A[i][k] + " * " + B[k][j]);
                    }

                    C[i][j] += A[i][k] * B[k][j];
                }
            }
        }
        return C;
    }

    // Prints complete matrix
    public static void printMatrix(int[][] A) {
        int dim = A.length;

        for (int i = 0; i < dim; i++) {
            System.out.print(" [ ");

            for (int j = 0; j < dim; j++) {
                System.out.print(A[i][j] + " ");
            }
            System.out.println("]");
        }
        System.out.println();
    }

    // Prints complete matrix
    public static void printMatrix(int[][] A, String name) {
        System.out.println("Printing matrix " + name);
        printMatrix(A);
    }

    // Prints the list of values on the diagonal entries
    public static void printDiagonal(int[][] A) {
        for (int i = 0; i < A.length; i++) {
            System.out.println(A[i][i]);
        }
    }

}
