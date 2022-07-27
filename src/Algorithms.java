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

                //end of algo 
                long endTime = System.nanoTime();
                long executionTime = endTime - startTime;
                int pathSize = path.size();

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
                    String outputText = getEndMessage(executionTime, length, pathSize, true);
                    gui.addText(outputText);
                    // gui.displayMessage(outputText);

                } else {
                    String outputText = getEndMessage(executionTime, -1, pathSize, false);
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

    //------------------------A* Algo----------------------------------
    public void AStarPathfinding(Point start, Point end, Cell[][] arr) {
        if (worker != null)
            worker.cancel(true);
        long startTime = System.nanoTime();

        worker = new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {
                //the calcualtions 
                HashMap<String, Point> path = new HashMap<>();
                ArrayList<Point> shortestPath = new ArrayList<>();
                PriorityQueue<Cell> q = new PriorityQueue<>(new Comparator<Cell>() {

                    @Override
                    public int compare(Cell a, Cell b) {
                        int[] aCosts = a.getCosts();
                        int[] bCosts = b.getCosts();
                        int aFCost = aCosts[2];
                        int bFCost = bCosts[2];

                        if (aCosts != null && bCosts != null) {
                            if (aFCost > bFCost)
                                return 1;
                            else if (bFCost > aFCost)
                                return -1;
                            else {
                                int aGCost = aCosts[0];
                                int bGCost = bCosts[0];
                                int aHCost = aCosts[1];
                                int bHCost = bCosts[1];
                                if (aHCost > bHCost)
                                    return 1;
                                else if (bHCost > aHCost)
                                    return -1;
                                else {
                                    if (aGCost > bGCost)
                                        return 1;
                                    else if (bGCost > aGCost)
                                        return -1;
                                    else {
                                        return 0;
                                    }
                                }
                            }
                        } else {
                            if (aCosts != null && bCosts == null)
                                return 1;
                            else if (aCosts == null && bCosts != null)
                                return -1;
                            else
                                return 0;
                        }

                    }

                });

                path.put(start.toString(), start);
                q.offer(arr[start.getX()][start.getY()]);

                boolean endFound = false;
                int ringLength = 0;

                int multiplier = 10;

                while (!q.isEmpty() && !endFound) {
                    ringLength = q.size();

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
                        if (!currPos.equals(start)) {
                            current.setAsSeen();
                        }

                        //update GUI
                        Thread.sleep(checkTime);
                        publish();

                        List<Cell> currentLayer = new ArrayList<Cell>();

                        for (int[] direction : directions) {
                            int nextX = direction[0] + currPos.getX();
                            int nextY = direction[1] + currPos.getY();
                            Point nextPos = new Point(nextX, nextY);
                            if (validPointOnMatrix(arr, nextPos) && !path.containsKey(nextPos.toString())) {
                                path.put(nextPos.toString(), currPos);

                                int gCost = getDistance(nextPos, start, multiplier);
                                int hCost = getDistance(nextPos, end, multiplier);
                                int fCost = gCost + hCost;
                                currentLayer.add(arr[nextPos.getX()][nextPos.getY()]);
                                //q.offer(arr[nextPos.getX()][nextPos.getY()]);
                                arr[nextPos.getX()][nextPos.getY()].setCosts(gCost, hCost, fCost);

                                if (!nextPos.equals(end)) {
                                    arr[nextPos.getX()][nextPos.getY()].setAsQueuedTextCost();
                                } else {
                                    break;
                                }
                                //update GUI
                                Thread.sleep(queueTime);
                                publish();
                            }
                        }
                        for (Cell c : currentLayer) {
                            q.offer(c);
                        }
                        currentLayer.clear();
                    }
                }

                long endTime = System.nanoTime();
                long executionTime = endTime - startTime;
                int pathSize = path.size();

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
                    String outputText = getEndMessage(executionTime, length, pathSize, true);
                    gui.addText(outputText);
                    // gui.displayMessage(outputText);

                } else {
                    String outputText = getEndMessage(executionTime, -1, pathSize, false);
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

    public boolean workerInit() {
        return this.worker != null;
    }

    //stops the thread that is executing the algorithm
    public void stopWorker() {
        this.worker.cancel(true);
    }

    public int getDistance(Cell a, Cell b, int multiplier) {
        int aX = a.getRow() * multiplier;
        int aY = a.getCol() * multiplier;
        int bX = b.getRow() * multiplier;
        int bY = b.getCol() * multiplier;
        double distance = Math.sqrt((bX - aX) * (bX - aX) + (bY - aY) * (bY - aY));
        return (int) distance;
    }

    public int getDistance(Point a, Point b, int multiplier) {
        int aX = a.getX() * multiplier;
        int aY = a.getY() * multiplier;
        int bX = b.getX() * multiplier;
        int bY = b.getY() * multiplier;
        double distance = Math.sqrt((bX - aX) * (bX - aX) + (bY - aY) * (bY - aY));
        return (int) distance;
    }

    private String getEndMessage(long executionTime, int length, int cellsSeen, boolean foundEnd) { //in Nanoseconds 
        double executionTimeInMillis = executionTime / 1000000;
        double executionTimeInSec = executionTimeInMillis / 1000;
        if (foundEnd) {
            return ("Shortest path length: " + length +
                    "\nThat took: " + executionTime + " nanoseconds" +
                    "\nThat took: " + executionTimeInMillis + " milliseconds" +
                    "\nThat took: " + executionTimeInSec + " seconds" +
                    "\nSaw " + cellsSeen + " cells"
                    + GUI.STRING_BREAK);
        } else {
            return ("Unable to find path\n" +
                    "\nThat took: " + executionTime + " nanoseconds" +
                    "\nThat took: " + executionTimeInMillis + " milliseconds" +
                    "\nThat took: " + executionTimeInSec + " seconds" +
                    "\nSaw " + cellsSeen + " cells"
                    + GUI.STRING_BREAK);
        }
    }

}
