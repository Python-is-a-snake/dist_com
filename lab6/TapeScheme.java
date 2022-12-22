import mpi.MPI;

public class TapeScheme {
    public static void main(String[] args) {
        int[] sizes = {10, 100, 1000};

        for (int matrixSize : sizes) {
            double[][]
                    matrix1 = new double[matrixSize][matrixSize],
                    matrix2 = new double[matrixSize][matrixSize];
            multiply(args, matrix1, matrix2);
        }
    }
  
    public static double[][] multiply(String[] args, double[][] matrix1, double[][] matrix2){
        if(matrix1.length == 0 || matrix2.length == 0 || matrix1[0].length != matrix2.length){
            throw new IllegalArgumentException("Matrices have incorrect sizes!");
        }
        double[][] res = new double[matrix1.length][matrix2[0].length],
                myRes = new double[matrix1.length][matrix2[0].length];
        double startTime = 0,endTime = 0;
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();
        if(rank == 0){
            matrix1 = MatrixGenerator.generateMatrix(matrix1.length,matrix1.length);
            matrix2 = MatrixGenerator.generateMatrix(matrix2.length,matrix2.length);
        }
        for(int i = 0; i < matrix1.length; i++){
            MPI.COMM_WORLD.Bcast(matrix1[i], 0, matrix1.length, MPI.DOUBLE, 0);
        }
        if(rank == 0) startTime = MPI.Wtime();
        for(int i = rank; i < myRes.length; i+=size){
            for(int j = 0; j < myRes[i].length; j++){
                myRes[i][j] = 0;
                for (int k = 0; k < matrix1[0].length; k++){
                    myRes[i][j] += matrix1[i][k] * matrix2[k][j];
                }
            }
        }
        for(int i = 0; i < myRes.length; i++){
            MPI.COMM_WORLD.Reduce(myRes[i],
                    0,
                    res[i],
                    0,
                    myRes[i].length,
                    MPI.DOUBLE,
                    MPI.SUM,
                    0);
        }
        if(rank == 0){
            endTime = MPI.Wtime();
            System.out.println("Time: " +(endTime - startTime)+"s");
        }
        MPI.Finalize();
        if(rank == 0) return res;
        else return null;
    }
}
