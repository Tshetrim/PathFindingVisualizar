/*
    GridModel class extends the Matrix, and represents the board/field, it will contain all the objects in the board/field including:
    *Starting Point = 1
    *Ending Point = 2
    *Obstacles  = -1

    It also contains and uses the Pathfinding Algorithms themselves
*/
public class GridModel extends Matrix {
    /*
     *      int[][] matrix;
            Point size;
            Point start, end;
            ArrayList<Point> walls;
     */

    //constructors
    GridModel(Point inputSize){
        super(inputSize);
    }

    GridModel(Point inputSize, Point start, Point end, Point[] walls){
        super(inputSize, start, end, walls);
    }

    GridModel(int[][] inputMatrix, Point start, Point end){
        super(inputMatrix, start, end);
    }

    //finds the minimum steps to get to an ending position
    public int minimumSteps(){
        return 10;
    }
}
