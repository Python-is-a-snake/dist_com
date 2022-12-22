import mpi.Cartcomm;
import mpi.MPI;

import java.util.*;

public class CannonAlgorithm {

    public static void main(String[] args) {
        calculate(args, 10);
    }
    private static int[] gridCoords = new int[2];

    private static Cartcomm ColComm;
    private static Cartcomm RowComm;
    private static void matrixScatter(int[] matrix, int[] matrixBlock, int matrixSize, int blockSize) {
        double[] matrixRow = new double[blockSize * matrixSize];
        if (gridCoords[1] == 0)
            ColComm.Scatter(matrix, 0, blockSize * matrixSize, MPI.INT, matrixRow, 0, blockSize * matrixSize, MPI.INT, 0);
        for (int i = 0; i < blockSize; i++) {
            double[] subRow = Arrays.copyOfRange(matrixRow, i * matrixSize, matrixRow.length);
            double[] subRowRes = new double[blockSize];

            RowComm.Scatter(subRow, 0, blockSize, MPI.INT, subRowRes, 0, blockSize, MPI.INT, 0);
            System.arraycopy(subRowRes, 0, matrixBlock, i * blockSize, blockSize);
        }
    }

    public static void calculate(String[] args, int matrixSize) {
        MPI.Init(args);

        int procRank = MPI.COMM_WORLD.Rank();
        int procNum = MPI.COMM_WORLD.Size();
        int gridSize = (int) Math.sqrt(procNum);

        if (procNum != gridSize * gridSize) {
            if (procRank == 0)
                System.out.println("PROCS");
            MPI.Finalize();
            return;
        }

        Cartcomm gridComm;

        int blockSize = matrixSize / gridSize;

        var matrixA = MatrixGenerator.generateDummyInt(matrixSize, matrixSize, false);
        var matrixB = MatrixGenerator.generateDummyInt(matrixSize, matrixSize, false);
        var matrixC = MatrixGenerator.generateDummyInt(matrixSize, matrixSize, false);

        int[] ABlock = new int[blockSize * blockSize];
        int[] BBlock = new int[blockSize * blockSize];
        int[] CBlock = new int[blockSize * blockSize];

        long startTime = 0L;

        boolean[] subdims = new boolean[2];

        gridComm = MPI.COMM_WORLD.Create_cart(new int[]{gridSize, gridSize}, new boolean[]{false, false}, true);

        gridCoords = gridComm.Coords(procRank);

        subdims[1] = true;
        RowComm = gridComm.Sub(subdims);

        subdims[0] = true;
        subdims[1] = false;
        ColComm = gridComm.Sub(subdims);

        matrixScatter(MatrixGenerator.matrixToArrayInt(matrixA), ABlock, matrixSize, blockSize);
        matrixScatter(MatrixGenerator.matrixToArrayInt(matrixB), BBlock, matrixSize, blockSize);

        if (gridCoords[0] != 0) {
            int nextProc = gridCoords[1] - gridCoords[0];
            if (nextProc < 0)
                nextProc += gridSize;
            RowComm.Sendrecv_replace(ABlock, 0, blockSize * blockSize, MPI.INT, nextProc, 0, MPI.ANY_SOURCE, 0);
        }

        if (gridCoords[1] != 0) {
            int nextProc = gridCoords[0] - gridCoords[1];
            if (nextProc < 0) nextProc += gridSize;
            ColComm.Sendrecv_replace(BBlock, 0, blockSize * blockSize, MPI.INT, nextProc, 1, MPI.ANY_SOURCE, 1);
        }

        MPI.COMM_WORLD.Barrier();

        for (int i = 0; i < blockSize; i++)
            for (int j = 0; j < blockSize; j++)
                for (int k = 0; k < blockSize; k++)
                    CBlock[i * blockSize + j] += ABlock[i * blockSize + k] * BBlock[k * blockSize + j];

        for (int iter = 0; iter < gridSize - 1; iter++) {
            int nextProc = gridCoords[1] - 1;
            if (nextProc < 0)
                nextProc += gridSize;
            RowComm.Sendrecv_replace(ABlock, 0, blockSize, MPI.INT, nextProc, 0, MPI.ANY_SOURCE, 0);

            nextProc = gridCoords[0] - 1;
            if (nextProc < 0)
                nextProc += gridSize;

            ColComm.Sendrecv_replace(BBlock, 0, blockSize, MPI.INT, nextProc, 1, MPI.ANY_SOURCE, 1);

            for (int i = 0; i < blockSize; i++)
                for (int j = 0; j < blockSize; j++)
                    for (int k = 0; k < blockSize; k++)
                        CBlock[i * blockSize + j] += ABlock[i * blockSize + k] * BBlock[k * blockSize + j];
        }

        double[] resultRow = new double[matrixSize * blockSize];
        for (int i = 0; i < blockSize; i++) {
            int[] subRow = Arrays.copyOfRange(CBlock, i * blockSize, CBlock.length);
            int[] subRowRes = new int[gridSize * blockSize];

            RowComm.Gather(subRow, 0, blockSize, MPI.INT, subRowRes, 0, blockSize, MPI.INT, 0);
            System.arraycopy(subRowRes, 0, resultRow, i * matrixSize, gridSize * blockSize);
        }

        if (gridCoords[1] == 0)
            ColComm.Gather(resultRow, 0, blockSize * matrixSize, MPI.INT, MatrixGenerator.matrixToArrayInt(matrixC), 0, blockSize * matrixSize, MPI.INT, 0);

        if (procRank == 0) {
            System.out.print("4) matrixSize = " + matrixSize + ", ");
            System.out.println(System.currentTimeMillis() - startTime + " ms\n");
        }
        MPI.Finalize();
    }
}
