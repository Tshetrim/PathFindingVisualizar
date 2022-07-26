/* Algoritm class contains all the logic for the searching algoritms and helper methods
 * 
 */

import java.util.*;

import javax.swing.SwingWorker;

public class Algorithms {
    private GUI gui;
    private int layerTime = 100;
    private int checkTime = 50; //500
    private int queueTime = 50; //500

    Algorithms(GUI gui) {
        this.gui = gui;
    }

    int[][] directions = new int[][] { { 0, 1 }, { 1, 1 }, { 1, 0 }, { 1, -1 }, { 0, -1 }, { -1, -1 }, { -1, 0 },
            { -1, 1 } };

    public void BFS(Point start, Point end, Cell[][] arr) {

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {

                HashMap<String, Point> path = new HashMap<>();
                ArrayList<Point> shortestPath = new ArrayList<>();
                Queue<Cell> q = new LinkedList<>();
                path.put(start.toString(), start);
                q.offer(arr[start.getX()][start.getY()]);

                boolean endFound = false;
                int ringLength = 0;

                while (!q.isEmpty() && !endFound) {
                    ringLength = q.size();
                    // System.out.println("----------------------------new ring-------------------------");

                    //update gui
                    Thread.sleep(layerTime);
                    publish();

                    for (int i = 0; i < ringLength; i++) {
                        Cell current = q.poll();
                        Point currPos = new Point(current.getRow(), current.getCol());
                        // System.out.println("Current: " + currPos);
                        if (currPos.equals(end)) {
                            // System.out.println("end found, curr= " + currPos + " end=" + end);
                            endFound = true;
                            break;
                        }
                        if (!currPos.equals(start))
                            current.setAsSeen();

                        //update GUI
                        Thread.sleep(checkTime);
                        publish();

                        for (int[] direction : directions) {
                            int nextX = direction[0] + currPos.getX();
                            int nextY = direction[1] + currPos.getY();
                            Point nextPos = new Point(nextX, nextY);
                            if (validPointOnMatrix(arr, nextPos) && !path.containsKey(nextPos.toString())) {
                                path.put(nextPos.toString(), currPos);
                                // System.out.println(path);
                                q.offer(arr[nextPos.getX()][nextPos.getY()]);
                                // System.out.println("------------------------Queued: " + nextPos);
                                if (nextPos.equals(end))
                                    break;
                                arr[nextPos.getX()][nextPos.getY()].setAsQueued();

                                //update GUI
                                Thread.sleep(queueTime);
                                publish();
                            }

                        }
                    }
                }
                if (endFound) {
                    shortestPath = traceShortestPath(start, end, path);
                    // System.out.println(shortestPath);
                    boolean seeBestPath = true;
                    if (seeBestPath) {
                        for (Point p : shortestPath) {
                            if (!p.equals(end))
                                arr[p.getX()][p.getY()].setAsBest();
                        }
                    }

                } else {
                    gui.displayErrorMessage("Could not find a path");
                    // System.out.println("Could not find a path");
                }

                return null;
            }

            @Override
            // Can safely update the GUI here.
            protected void process(List<Void> chunks) {
                gui.validate();
                gui.repaint();
            }

        };

        worker.execute();
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
        // System.out.print(size + " vs " + point);

        if (point.getX() > size.getX() - 1 && point.getY() > size.getY() - 1) {
            // System.out.println("Point is out of grid size on both x and y coordinates");
            return false;
        }
        if (point.getX() < 0 && point.getY() < 0) {
            // System.out.println("Point is out of grid size on both x and y coordinates");
            return false;
        }
        if (point.getX() > size.getX() - 1 || point.getX() < 0) {
            // System.out.println("Point is out of grid size on x coordinate");
            return false;
        }
        if (point.getY() > size.getY() - 1 || point.getY() < 0) {
            // System.out.println("Point is out of grid size on y coordinate");
            return false;
        }
        if (arr[point.getX()][point.getY()].isWall()) {
            // System.out.println("Point is a wall, invalid");
            return false;
        }
        // System.out.println(" is valid");

        return true;
    }

}
