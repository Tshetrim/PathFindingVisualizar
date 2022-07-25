
/*
    Matrix class represents the board/field, it will contain all the objects in the board/field including:
    *Starting Point = 1
    *Ending Point = 2
    *Obstacles  = -1
    *Empty = 0
*/
public class Matrix {
    private int[][] matrix;
    private Point size;
    private Point start;
    private Point end;

    private final int START = 1, END = 2, WALL = -1, EMPTY = 0;
    //constructors

    //constructor with just input size
    public Matrix(Point inputSize) {
        this.size = new Point(inputSize.getX(), inputSize.getY());
        this.matrix = new int[this.size.getX()][this.size.getY()];
        //this.start = null;
        //this.end = null;
    }

    //constructor with input matrix, a start point, and end point
    public Matrix(int[][] arr) {
        if (isSquare(arr)) {
            this.size = new Point(arr.length, arr[0].length);
            this.matrix = new int[arr.length][arr[0].length];
            for (int r = 0; r < arr.length; r++) {
                for (int c = 0; c < arr[r].length; c++) {
                    if (arr[r][c] == this.START) {
                        start = new Point(r, c);
                    }
                    if (arr[r][c] == this.END) {
                        end = new Point(r, c);
                    }

                    if (arr[r][c] < 0 || arr[r][c] > 2) {
                        throw new IllegalArgumentException(
                                "Invalid input array, values must be 0,1, or 2 with a single 0 and 1");
                    } else {
                        this.matrix[r][c] = arr[r][c];
                    }
                }
            }
        } else {
            throw new IllegalArgumentException("Must be a square matrix input");
        }
    }

    //--------helper methods--------

    public boolean isSquare(int[][] arr) {
        if (arr == null)
            return false;
        int length = arr[0].length;
        for (int[] row : arr) {
            if (row.length != length)
                return false;
        }
        return true;
    }

    //checks if the input point is inside the matrix
    public boolean validPointOnMatrix(Point point) {
        if (point.getX() > size.getX() && point.getY() > size.getY()) {
            System.out.println("Point is out of grid size on both x and y coordinates");
            return false;
        } else if (point.getX() > size.getX()) {
            System.out.println("Point is out of grid size on x coordinate");
            return false;
        } else if (point.getY() > size.getY()) {
            System.out.println("Point is out of grid size on y coordinate");
        }
        return true;
    }

    //updateMatrix() updates the matrix to an input int[][] of equal dimensions
    public void updateMatrix(Point start, Point end, int[][] input) {
        if (input.length != matrix.length || input[0].length != matrix[0].length) {
            System.out.println("Trying to update Matrix: Error Invalid size");
        } else {
            this.matrix = input;
            this.start = start;
            this.end = end;
            System.out.println("Updated Matrix");
            //System.out.println(Arrays.deepToString(matrix));
        }
    }

    public int getRowLength() {
        return this.size.getX();
    }

    public int getColumnLength() {
        return this.size.getY();
    }

    public int get(int row, int col) {
        if (validPointOnMatrix(new Point(row, col)))
            return matrix[row][col];
        else
            return -10;

    }

    //return the Point that represents the size of the matrix
    public Point getSize() {
        return size;
    }

    //returns the int[][] that represents the matrix
    public int[][] getInnerMatrix() {
        return this.matrix;
    }
}
