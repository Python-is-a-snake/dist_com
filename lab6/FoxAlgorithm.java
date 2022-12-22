import mpi.Cartcomm;
import mpi.MPI;

public class FoxAlgorithm {
    private static int procRank = 100;
    private static int gridSize;
    private static int[] gridCoords = new int[2];
    private static Cartcomm colComm;
    private static Cartcomm rowComm;

    public static void main(String[] args) {
        start(args);
    }
    private static void serialResultCalculation(double[] pAMatrix, double[] pBMatrix,
                                                double[] pCMatrix, int size) {
        for (int i=0; i<size; i++) {
            for (int j=0; j<size; j++)
            {
                for (int k=0; k<size; k++){
                    pCMatrix[i*size+j] += pAMatrix[i*size+k]*pBMatrix[k*size+j];
                }
            }
        }
    }
    private static void blockMultiplication(double[] pAblock, double[] pBblock,
                                            double[] pCblock, int size) {
        serialResultCalculation(pAblock,pBblock,pCblock, size);
    }
    private static void createGridCommunicators() {
        var DimSize = new int[2];
        var Periodic = new boolean[2];
        var Subdims = new boolean[2];
        DimSize[0] = gridSize;
        DimSize[1] = gridSize;
        Cartcomm gridComm = MPI.COMM_WORLD.Create_cart(DimSize, Periodic, true);
        gridCoords = gridComm.Coords(procRank);
        Subdims[1] = true;
        rowComm = gridComm.Sub(Subdims);
        Subdims[0] = true;
        Subdims[1] = false;
        colComm = gridComm.Sub(Subdims);
    }
    private static void processInitialization(Data data){
        if (procRank == 0) {
            data.size[0] = 1000; //10, 100, 1000 for test
            if(data.size[0] % gridSize != 0)
                System.out.println("Size must be proportional to grid size!");
        }
        MPI.COMM_WORLD.Bcast(data.size,0,1,MPI.INT, 0);
        data.blockSize = data.size[0]/ gridSize;
        data.pAblock = new double [data.blockSize *data.blockSize];
        data.pBblock = new double [data.blockSize *data.blockSize];
        data.pCblock = new double [data.blockSize *data.blockSize];
        data.pMatrixAblock = new double [data.blockSize *data.blockSize];
        for (int i = 0; i<data.blockSize *data.blockSize; i++) {
            data.pCblock[i] = 0;
        }
        data.pAMatrix = new double [data.size[0]*data.size[0]];
        data.pBMatrix = new double [data.size[0]*data.size[0]];
        data.pCMatrix = new double [data.size[0]*data.size[0]];
        if(procRank == 0){
            data.pAMatrix = MatrixGenerator.matrixToArray(
                    MatrixGenerator.generateDummy(data.size[0], data.size[0], false));
            data.pBMatrix = MatrixGenerator.matrixToArray(
                    MatrixGenerator.generateDummy(data.size[0], data.size[0], true));
        }
        System.out.println(data.size[0]);
    }
    private static void checkerboardMatrixScatter(double[] pMatrix, double[] pMatrixBlock,
                                                  int size, int blockSize) {
        var MatrixRow = new double [blockSize*size];
        if (gridCoords[1] == 0) {
            colComm.Scatter(pMatrix, 0, blockSize*size, MPI.DOUBLE, MatrixRow,
                    0, blockSize*size, MPI.DOUBLE, 0);
        }
        for (int i=0; i<blockSize; i++) {
            var tempSend = new double[size];
            var tempRecv = new double[blockSize];
            System.arraycopy(MatrixRow, i*size, tempSend , 0, size);
            rowComm.Scatter(tempSend, 0, blockSize, MPI.DOUBLE,
                    tempRecv, 0, blockSize, MPI.DOUBLE, 0);
            System.arraycopy(tempRecv, 0, pMatrixBlock , i*blockSize, blockSize);
        }
    }
    private static void dataDistribution(double[] pAMatrix, double[] pBMatrix, double[]
            pMatrixAblock, double[] pBblock, int size, int blockSize) {
        checkerboardMatrixScatter(pAMatrix, pMatrixAblock, size, blockSize);
        checkerboardMatrixScatter(pBMatrix, pBblock, size, blockSize);
    }
    private static void resultCollection(double[] pCMatrix, double[] pCblock, int size,
                                         int blockSize) {
        var pResultRow = new double [size*blockSize];
        for (int i=0; i<blockSize; i++) {
            var tempRecv = new double[size];
            var tempSend = new double[blockSize];
            System.arraycopy(pCblock, i*blockSize, tempSend , 0, blockSize);
            rowComm.Gather(tempSend, 0, blockSize, MPI.DOUBLE,
                    tempRecv, 0, blockSize, MPI.DOUBLE, 0);
            System.arraycopy(tempRecv, 0, pResultRow , i*size, size);
        }
        if (gridCoords[1] == 0) {
            colComm.Gather(pResultRow, 0,blockSize*size, MPI.DOUBLE,
                    pCMatrix, 0,blockSize*size, MPI.DOUBLE, 0);
        }
    }
    private static void aBlockCommunication(int iter, double[] pAblock, double[] pMatrixAblock,
                                            int blockSize) {
        int Pivot = (gridCoords[0] + iter) % gridSize;
        if (gridCoords[1] == Pivot) {
            if (blockSize * blockSize >= 0)
                System.arraycopy(pMatrixAblock, 0, pAblock, 0, blockSize * blockSize);
        }
        rowComm.Bcast(pAblock, 0, blockSize*blockSize, MPI.DOUBLE, Pivot);
    }
    private static void bBlockCommunication(double[] pBblock, int blockSize) {
        int NextProc = gridCoords[0] + 1;
        if ( gridCoords[0] == gridSize -1 ) NextProc = 0;
        int PrevProc = gridCoords[0] - 1;
        if ( gridCoords[0] == 0 ) PrevProc = gridSize -1;
        var status = colComm.Sendrecv_replace(pBblock, 0,blockSize*blockSize, MPI.DOUBLE,
                NextProc, 0,PrevProc, 0);
    }
    private static void parallelResultCalculation(double[] pAblock, double[] pMatrixAblock,
                                                  double[] pBblock, double[] pCblock, int blockSize) {
        for (int iter = 0; iter < gridSize; iter ++) {
            aBlockCommunication(iter, pAblock, pMatrixAblock, blockSize);
            blockMultiplication(pAblock, pBblock, pCblock, blockSize);
            bBlockCommunication(pBblock, blockSize);
        }
    }
    private static void start(String[] args){
        Data data = new Data();
        double startTime = 0,endTime = 0;
        MPI.Init(args);
        procRank = MPI.COMM_WORLD.Rank();
        int procNum = MPI.COMM_WORLD.Size();
        gridSize = (int) Math.sqrt(procNum);
        if (procNum != gridSize * gridSize) {
            if (procRank == 0) {
                System.out.println("Number of processes must be a perfect square!");
            }
        }
        else {
            if (procRank == 0) startTime = MPI.Wtime();
            createGridCommunicators();
            processInitialization(data);
            dataDistribution(data.pAMatrix, data.pBMatrix, data.pMatrixAblock, data.pBblock, data.size[0],
                    data.blockSize);
            parallelResultCalculation(data.pAblock, data.pMatrixAblock, data.pBblock,
                    data.pCblock, data.blockSize);
            resultCollection(data.pCMatrix, data.pCblock, data.size[0], data.blockSize);
        }
        if(procRank == 0){
            endTime = MPI.Wtime();
            System.out.println("Time: " +(endTime - startTime)+"s");
        }
        MPI.Finalize();
    }
    
}
class Data{
    public double[] pAMatrix;
    public double[] pBMatrix;
    public double[] pCMatrix;
    public double[] pAblock;
    public double[] pBblock;
    public double[] pCblock;
    public double[] pMatrixAblock;
    public int[] size = new int[1];
    public int blockSize;
}
