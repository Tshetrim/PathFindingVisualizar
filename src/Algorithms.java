/* Algoritm class contains all the logic for the searching algoritms and helper methods
 * 
 */

import java.util.*;

import javax.swing.SwingWorker;

public class Algorithms {
    private GUI gui;
    private AudioWorker audio;

    private int layerTime;
    private int checkTime; //500
    private int queueTime; //500
    private int[][] directions;


    Algorithms(GUI gui) {
        this.gui = gui;
    }

    Algorithms(GUI gui, AudioWorker audio) {
        this.gui = gui;
        this.audio = audio;
    }

    final static int[][] OCTO_DIRECTIONS = new int[][] { { 0, 1 }, { 1, 1 }, { 1, 0 }, { 1, -1 }, { 0, -1 }, { -1, -1 },
            { -1, 0 },
            { -1, 1 } };
    final static int[][] QUAD_DIRECTIONS = new int[][] { { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 } };

    private SwingWorker<Void, Void> worker;

    public void BFS(Point start, Point end, Cell[][] arr) {
        if (worker != null)
            worker.cancel(true);
        long startTime = System.nanoTime();

        worker = new SwingWorker<Void, Void>() {

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

                long endTime = System.nanoTime();
                long executionTime = endTime - startTime;
                double executionTimeInMillis = executionTime / 1000000;
                double executionTimeInSec = executionTimeInMillis / 1000;

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
                    int length = shortestPath.size();
                    String outputText = ("Shortest path length: " + length +
                            "\nThat took: " + executionTime + " nanoseconds" +
                            "\nThat took: " + executionTimeInMillis + " milliseconds" +
                            "\nThat took: " + executionTimeInSec + " seconds" +
                            "\nSaw " + path.size() + " cells"
                            + GUI.STRING_BREAK);
                    gui.addText(outputText);
                    // gui.displayMessage(outputText);

                } else {
                    String outputText = ("Unable to find path\n" +
                            "\nThat took: " + executionTime + " nanoseconds" +
                            "\nThat took: " + executionTimeInMillis + " milliseconds" +
                            "\nThat took: " + executionTimeInSec + " seconds" +
                            "\nSaw " + path.size() + " cells"
                            + GUI.STRING_BREAK);
                    gui.addText(outputText);
                    gui.displayMessage("Could not find a path");
                }

                return null;
            }

            @Override
            // Can safely update the GUI here.
            protected void process(List<Void> chunks) {
                audio.playLowPop();
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

    public void setLayerTime(int layerTime) {
        this.layerTime = layerTime;
    }

    public void setCheckTime(int checkTime) {
        this.checkTime = checkTime;
    }

    public void setQueueTime(int queueTime) {
        this.queueTime = queueTime;
    }

    public void setDirection(int[][] directions) {
        this.directions = directions;
    }

    public boolean workerInit(){
        return this.worker != null;
    }

    //stops the thread that is executing the algorithm
    public void stopWorker(){
        this.worker.cancel(true);
    }

}
