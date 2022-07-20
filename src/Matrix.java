/*
    Matrix class represents the board/field, it will contain all the objects in the board/field including:
    *Starting Point = 1
    *Ending Point = 2
    *Obstacles  = -1
*/

import java.util.*;

public class Matrix {
    int[][] matrix;
    Point size;
    Point start, end;
    //current Point? 
    ArrayList<Point> walls;

    //constructors

    //constructor with just input size
    public Matrix(Point inputSize) {
        this.size = new Point(inputSize.x, inputSize.y);
        this.matrix = new int[this.size.x][this.size.y];
    }

    //constructor with input size, a start point,end point, and an array of wall locations
    public Matrix(Point inputSize, Point start, Point end, Point[] walls) {
        this.size = new Point(inputSize.x, inputSize.y);
        this.matrix = new int[this.size.x][this.size.y];

        for (Point wall : walls) {
            if (validPointOnMatrix(wall)) {
                this.walls.add(wall);
            }
        }
        updateMatrixWalls();

        //to do: check if starting/ending can concide with a wall - likely not
        if (validPointOnMatrix(start)) {
            this.start = start;
        }

        if (validPointOnMatrix(end)) {
            this.end = end;
        }
    }

    //constructor with input matrix, a start point, and end point
    public Matrix(int[][] inputMatrix, Point start, Point end) {
        this.size = new Point(inputMatrix.length, inputMatrix[0].length);
        this.matrix = inputMatrix;

        if (validPointOnMatrix(start)) {
            this.start = start;
        }

        if (validPointOnMatrix(end)) {
            this.end = end;
        }
    }

    //helper methods

    //checks if the input point is inside the matrix
    public boolean validPointOnMatrix(Point point) {
        if (point.x > size.x && point.y > size.y) {
            System.out.println("Point is out of grid size on both x and y coordinates");
            return false;
        } else if (point.x > size.x) {
            System.out.println("Point is out of grid size on x coordinate");
            return false;
        } else if (point.y > size.y) {
            System.out.println("Point is out of grid size on y coordinate");
        }
        return true;
    }

    public void updateMatrixWalls() {
        for (Point wall : walls) {
            if (validPointOnMatrix(wall)) {
                matrix[wall.x][wall.y] = 1;
            }
        }
    }

    //updateSize() updates the size of the matrix 
    // public void updateSize(Point inputSize) {
    //     int[][] toBe = new int[inputSize.x][inputSize.y];

    //     int row = Math.max(inputSize.x, this.size.x);
    //     int col = Math.max(inputSize.y, this.size.y);

    //     for (int r = 0; r < row; r++) {
    //         for (int c = 0; c < col; c++) {
    //             toBe[r][c] = matrix[r][c];
    //         }
    //     }
    //     this.matrix = toBe;
    //     this.size = inputSize;

    // }

    public int getRowLength() {
        return this.size.x;
    }

    public int getColumnLength() {
        return this.size.y;
    }

    public int get(int row, int col) {
        if (validPointOnMatrix(new Point(row, col)))
            return matrix[row][col];
        else
            return -10;

    }
}
