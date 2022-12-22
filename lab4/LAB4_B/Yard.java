package LAB4_B;

public class Yard {
    private int width = 5;
    public int[][] yard = new int[width][width];

    Yard() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < width; j++) {
                yard[i][j] = 1;
            }
        }
    }
    public void ChangePlantStatus(int xPos, int yPos, int status) {
        if (xPos < 0 || xPos >= width || yPos < 0 || yPos >= width) {
            throw new NumberFormatException("WRONG FORMAT");
        }
        if (status != 0 && status != 1) {
            throw new NumberFormatException("WRONG FORMAT");
        }
        yard[xPos][yPos] = status;
    }

    public void prettyGardenOutput() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < width; j++) {
                System.out.print(yard[i][j] + "  ");
                if (j == width - 1) {
                    System.out.print("");
                }
            }
            System.out.println("");
        }
        System.out.println();
        for (int i = 0; i < width - 1; i++) {
            System.out.print("------------------------------");
        }
        System.out.println("-");
        System.out.println("");
    }
}
