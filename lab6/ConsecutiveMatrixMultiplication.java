public class ConsecutiveMatrixMultiplication {
    public static void main(String[] args) {
        int[] sizes = {10, 100, 1000};

        for (int matrixSize : sizes) {
            double[][]
                    matrix1 = MatrixGenerator.generateDummy(matrixSize, matrixSize, false),
                    matrix2 = MatrixGenerator.generateDummy(matrixSize, matrixSize, true);
            multiply(args, matrix1, matrix2);
            //TapeScheme.multiply(args, matrix1, matrix2);
            //FoxAlgorithm.start(args);
            CannonAlgorithm.calculate(args, matrixSize);
        }
    }
    public static double[][] multiply(String[] args, double[][] matrix1, double[][] matrix2){
        if(matrix1.length == 0 || matrix2.length == 0 || matrix1[0].length != matrix2.length){
            throw new IllegalArgumentException("Matrices have incorrect sizes!");
        }
        var res = new double[matrix1.length][matrix2[0].length];
        long startTime = System.currentTimeMillis();
        //sequential
        for (int i = 0; i < matrix1.length; i++){
            for (int j = 0; j < matrix2[0].length; j++){
                res[i][j] = 0;
                for (int k = 0; k < matrix1[0].length; k++){
                    res[i][j] += matrix1[i][k] * matrix2[k][j];
                }
            }
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Time: " +(endTime - startTime)/1000.0 + "s");
        return res;
    }
}
