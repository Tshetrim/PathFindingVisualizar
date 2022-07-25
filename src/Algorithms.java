/* Algoritm class contains all the logic for the searching algoritms and helper methods
 * 
 */

import java.util.*;

public class Algorithms {
    GUI gui;

    Algorithms(GUI gui) {
        this.gui = gui;
    }

    private HashMap<String, Point> path = new HashMap<>();
    private ArrayList<Point> shortestPath = new ArrayList<>();
    int[][] directions = new int[][] { { 0, 1 }, { 1, 1 }, { 1, 0 }, { 1, -1 }, { 0, -1 }, { -1, -1 }, { -1, 0 },
            { -1, 1 } };

    public void BFS(Point start, Point end, Cell[][] arr) {
        Queue<Cell> q = new LinkedList<>();
        path.put(start.toString(), start);
        q.offer(arr[start.getX()][start.getY()]);

        boolean endFound = false;
        int ringLength = 0;

        while (!q.isEmpty() && !endFound) {
            ringLength = q.size();
            System.out.println("----------------------------new ring-------------------------");
            // try {
            //     gui.revalidate();
            //     gui.repaint();
            //     Thread.sleep(1000);

            // } catch (InterruptedException e) {
            //     e.printStackTrace();
            // }

            for (int i = 0; i < ringLength; i++) {
                Cell current = q.poll();
                Point currPos = new Point(current.getRow(), current.getCol());
                System.out.println("Current: " + currPos);
                if (currPos.equals(end)) {
                    System.out.println("end found, curr= " + currPos + " end=" + end);

                    endFound = true;
                    break;
                }
                if (!currPos.equals(start))
                    current.setAsSeen();

                for (int[] direction : directions) {
                    int nextX = direction[0] + currPos.getX();
                    int nextY = direction[1] + currPos.getY();
                    Point nextPos = new Point(nextX, nextY);
                    if (validPointOnMatrix(arr, nextPos) && !path.containsKey(nextPos.toString())) {
                        path.put(nextPos.toString(), currPos);
                        System.out.println(path);
                        q.offer(arr[nextPos.getX()][nextPos.getY()]);
                        System.out.println("------------------------Queued: " + nextPos);
                        if (nextPos.equals(end))
                            break;
                        arr[nextPos.getX()][nextPos.getY()].setAsQueued();
                    }

                }
            }
        }
        if (endFound) {
            shortestPath = traceShortestPath(start, end, path);
            System.out.println(shortestPath);
            boolean seeBestPath = true;
            if (seeBestPath) {
                for (Point p : shortestPath) {
                    if (!p.equals(end))
                        arr[p.getX()][p.getY()].setAsBest();
                }
            }

        } else {
            gui.displayErrorMessage("Could not find a path");
            System.out.println("Could not find a path");
        }
    }

    public ArrayList<Point> traceShortestPath(Point start, Point end, HashMap<String, Point> path) {
        Point curr = end;
        ArrayList<Point> list = new ArrayList<>();
        while (!curr.equals(start)) {
            list.add(curr);
            curr = path.get(curr.toString());
        }
        Collections.reverse(list);
        return list;
    }

    public boolean validPointOnMatrix(Cell[][] arr, Point point) {
        Point size = new Point(arr.length, arr[0].length);
        System.out.print(size + " vs " + point);

        if (point.getX() > size.getX() - 1 && point.getY() > size.getY() - 1) {
            System.out.println("Point is out of grid size on both x and y coordinates");
            return false;
        }
        if (point.getX() < 0 && point.getY() < 0) {
            System.out.println("Point is out of grid size on both x and y coordinates");
            return false;
        }
        if (point.getX() > size.getX() - 1 || point.getX() < 0) {
            System.out.println("Point is out of grid size on x coordinate");
            return false;
        }
        if (point.getY() > size.getY() - 1 || point.getY() < 0) {
            System.out.println("Point is out of grid size on y coordinate");
            return false;
        }
        if (arr[point.getX()][point.getY()].isWall()) {
            System.out.println("Point is a wall, invalid");
            return false;
        }
        System.out.println(" is valid");

        return true;
    }

}
