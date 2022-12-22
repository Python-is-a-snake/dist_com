public class MatrixGenerator {
    public static double[][] generateMatrix(int rows, int cols){
        double[][] res = new double[rows][cols];
        for(int i = 0; i < rows; i++){
            for (int j = 0; j < cols; j++){
                res[i][j] = (int) (Math.random()%100);
            }
        }
        return res;
    }

    public static double[] matrixToArray(double[][] matrix){
        double[] arr = new double[matrix.length * matrix.length];
        for(int i = 0; i < matrix.length; i++){
            for(int j = 0; j < matrix.length; j++){
                arr[i*matrix.length+j] = matrix[i][j];
            }
        }
        return arr;
    }

    public static int[][] generateMatrixInt(int rows, int cols){
        int[][] res = new int[rows][cols];
        for(int i = 0; i < rows; i++){
            for (int j = 0; j < cols; j++){
                res[i][j] = (int) (Math.random()%100);
            }
        }
        return res;
    }

    public static int[] matrixToArrayInt(int[][] matrix){
        int[] arr = new int[matrix.length * matrix.length];
        for(int i = 0; i < matrix.length; i++){
            for(int j = 0; j < matrix.length; j++){
                arr[i*matrix.length+j] = matrix[i][j];
            }
        }
        return arr;
    }

    public static double[][] generateDummy (int rows, int cols, boolean transpose){
        var res = new double[rows][cols];
        for (int i=0; i<rows; i++)
            for (int j=0; j<cols; j++) {
                if(transpose) res[i][j] = j*10 + i;
                else res[i][j]= i*10 + j;
            }
        return res;
    }

    public static int[][] generateDummyInt (int rows, int cols, boolean transpose){
        var res = new int[rows][cols];
        for (int i=0; i<rows; i++)
            for (int j=0; j<cols; j++) {
                if(transpose) res[i][j] = j*10 + i;
                else res[i][j]= i*10 + j;
            }
        return res;
    }

}
