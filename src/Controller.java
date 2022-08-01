/* Controller contains the state of the matrix and calls the respective algorithms
 * It will send the GUI the state when updated/asked 
 * The size and states of the matrix which are dynamically changable through the GUI and Main also populates the matrix during algorithm searching
*/

import java.awt.Color;
import java.awt.event.*;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JSlider;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputListener;

public class Controller {
    private Matrix matrix;
    private GUI gui;
    private Algorithms algo;
    private AudioWorker audio;

    private String currentAlgorithm;

    private int clickCellAction; //0 = add start, 1 = add end, 2 = add wall
    private int mousePressed = 0; //for dragging in grid   0 = none, 1 = left, 2 = right 
    private int lastCellVal; //saving last cell entered for dragging 
    private int lastCellHeldVal;

    //private boolean start, end; //tracks if a start and end cell is in grid
    private Point start, end;

    public Controller(GUI gui, Matrix matrix, Algorithms algo, AudioWorker audio) {
        this.gui = gui;
        this.matrix = matrix;
        this.algo = algo;
        this.audio = audio;

        this.gui.setGrid(matrix);

        registerListeners();
        initDefaultValues();
    }

    public void initDefaultValues() {

        gui.addText(getStartingText());
        gui.displayMessage(getStartingText());
        //default values init
        resetStartEnd();
        this.clickCellAction = 2;
        this.gui.getGridSizeTextField().setText(matrix.getSize().getX() + "," + matrix.getSize().getY());

        initializeDefaultSliderSpeed();
        algo.setDirection(Algorithms.OCTO_DIRECTIONS);
    }

    //adds action listeners to components in the GUI 
    public void registerListeners() {
        gui.addUpdateGridSizeActionListener(new UpdateGridSizeListener());
        gui.addStartButtonListener(new AddStartListener());
        gui.addEndButtonListener(new AddEndListener());
        gui.addWallButtonListener(new AddWallListener());
        gui.addRunButtonListener(new RunButtonListener());
        gui.addLayerTimeListener(new LayerTimeSliderListener());
        gui.addCheckTimeListener(new CheckTimeSliderListener());
        gui.addQueueTimeListener(new QueueTimeSliderListener());
        gui.addOmniDirectionButtonListener(new OmniDirectionButtonListener());
        gui.addQuadDirectionButtonListener(new QuadDirectionButtonListener());
        gui.addToggleDarkLightModeListener(new DarkLightModeButtonListener());
        gui.addToggleSoundListener(new ToggleSoundButtonListener());

        registerCellMatrixListeners();
    }

    //adds action listeners to all cells n the grid -> must call after every setGrid(Matrix)
    public void registerCellMatrixListeners() {
        for (Cell[] row : this.gui.getGrid()) {
            for (Cell cell : row) {
                cell.addMouseListener(new CellMouseListener());
            }
        }
    }

    //--------------------------defining action listeners----------------------------------

    //updates the size of the grid and resets the matrix to be empty
    class UpdateGridSizeListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Point newSize = gui.getGridSize();
            //System.out.println(newSize.getX() + " " + newSize.getY());
            if (newSize.getX() <= 0 || newSize.getY() <= 0)
                gui.displayMessage("Invalid size");
            else if (newSize.equals(matrix.getSize())) {
                //stops running algo if there is any
                if (algo.workerInit())
                    algo.stopWorker();
                //clears board 
                gui.clearGrid();
                resetStartEnd();
            } else {
                try {
                    newSize = gui.getGridSize();
                    matrix = new Matrix(newSize);
                    gui.setGrid(matrix);
                    registerCellMatrixListeners();
                    resetStartEnd();
                } catch (Exception E) { //note add more/specific exception catches later including for Matrix end
                    gui.displayMessage("Invalid Input");
                }
            }
        }
    }

    //sets the add cell setting to be Start cell 
    class AddStartListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                clickCellAction = 0;
            } catch (Exception E) {
                gui.displayMessage("Add Start Toggle Button Error");
            }
        }
    }

    //sets the add cell setting to be End cell 
    class AddEndListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                clickCellAction = 1;
            } catch (Exception E) {
                gui.displayMessage("Add End Toggle Button Error");
            }

        }
    }

    //sets the add cell setting to be walls 
    class AddWallListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                clickCellAction = 2;
            } catch (Exception E) {
                gui.displayMessage("Add Wall Toggle Button Error");
            }
        }
    }

    //runs currently chosen algorithm 
    class RunButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                runAlgo();
            } catch (Exception E) {
                gui.displayMessage("Run error");
            }
        }
    }

    //toggles between dark/light laf 
    class DarkLightModeButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                JButton curr = (JButton) e.getSource();
                String text = curr.getText();

                if (text.equals("Light Mode ☀")) {
                    gui.setLightMode();
                    curr.setText("Dark Mode ☽");
                } else {
                    gui.setDarkMode();
                    curr.setText("Light Mode ☀");
                }

            } catch (Exception E) {
                gui.displayMessage("Run error");
            }
        }
    }

    class ToggleSoundButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                JButton curr = (JButton) e.getSource();
                String text = curr.getText();

                if (text.equals("🔊")) {
                    audio.setSoundOff();
                    curr.setText("🔇");
                    curr.setBackground(Color.lightGray);
                } else {
                    audio.setSoundOn();
                    curr.setText("🔊");
                    curr.setBackground(UIManager.getColor("JButton.background"));
                }

            } catch (Exception E) {
                gui.displayMessage("Run error");
            }
        }
    }

    //Action listener for cell grid -> adding cells, deleting cells, dragging add/delete cells
    class CellMouseListener implements MouseInputListener {

        //sets mouse pressed on mouse release 
        public void mouseReleased(MouseEvent e) {
            //System.out.println("released");
            Cell curr = (Cell) e.getSource();
            audio.playSound("pop_low.wav");

            // System.out.println("on release " + lastCellVal);

            //only save on mouse release if current cell is a start or end and the currently held cell is start or end
            //and mouse pressed is not right click  (clear)
            if (mousePressed != 2) {
                if (!curr.isStart() || !curr.isEnd()) {
                    if (lastCellHeldVal == 1 && start == null) {//if last cell held was a start / end
                        curr.setAsStart();
                        start = new Point(curr.getRow(), curr.getCol());
                        System.out.println("released + " + lastCellVal);
                        if (lastCellVal == 2) {
                            clearEnd();
                            System.out.println("start set to null");
                        }
                    } else if (lastCellHeldVal == 2 && end == null) {
                        curr.setAsEnd();
                        end = new Point(curr.getRow(), curr.getCol());
                        if (lastCellVal == 1)
                            clearEnd();
                    }
                }
            }

            if (lastCellHeldVal == 1 || lastCellHeldVal == 2) {
                if (start != null && end != null) {
                    gui.clearExtrasGrid();
                    runAlgo();
                }
            }

            lastCellHeldVal = -10;
            lastCellVal = -10;
            mousePressed = 0;
        }

        //if mousePressed state is left or right click, then entering a cell should update the cells with the current cell type toggle
        public void mouseEntered(MouseEvent e) {
            //System.out.println(" entered");

            //playKeyClick(1);
            if (mousePressed == 1 && clickCellAction == 2)
                audio.playSound("pop_low.wav");
            else
                audio.playSound("pop_high.wav");

            Cell curr = (Cell) e.getSource();
            lastCellVal = curr.getValue();
            // System.out.println(lastCellVal);

            if (mousePressed == 2) {
                if (curr.isStart())
                    clearStart();
                else if (curr.isEnd())
                    clearEnd();
                curr.clear();
            } else if (mousePressed == 1) {
                if (clickCellAction == 0 && start == null) {
                    curr.setAsStart();
                    start = new Point(curr.getRow(), curr.getCol());
                } else if (clickCellAction == 1 && end == null) {
                    curr.setAsEnd();
                    end = new Point(curr.getRow(), curr.getCol());
                } else if (clickCellAction == 2) {
                    if (curr.isStart())
                        clearStart();
                    else if (curr.isEnd())
                        clearEnd();
                    curr.setAsWall();
                }
            }

        }

        //only needed to be here because these are abstract methods of the MouseInputListener interface 
        public void mouseExited(MouseEvent e) {
            //System.out.println("mouse exited");
            Cell curr = (Cell) e.getSource();

            if (mousePressed == 1 && clickCellAction != 2) {
                if (curr.isStart() && clickCellAction == 0) {
                    if (lastCellHeldVal == 1 || start == null) {
                        curr.clear();
                        clearStart();
                    }
                } else if (curr.isEnd() && clickCellAction == 1) {
                    if (lastCellHeldVal == 2 || end == null) {
                        curr.clear();
                        clearEnd();
                    }
                }

                if (!(lastCellVal == 1 || lastCellVal == 2))
                    curr.setByValue(lastCellVal);
                if (lastCellHeldVal == 1 && lastCellVal == 2)
                    curr.setByValue(lastCellVal);
                else if (lastCellHeldVal == 2 && lastCellVal == 1)
                    curr.setByValue(lastCellVal);

            }
        }

        public void mouseMoved(MouseEvent e) {
            //do nothing
        }

        public void mouseDragged(MouseEvent e) {
            //do nothing
        }

        public void mouseClicked(MouseEvent e) {
            //do nothing
        }

        //add input validation later for if a start/end cell already exists 
        public void mousePressed(MouseEvent e) {
            //System.out.println("mouse pressed");
            Cell curr = (Cell) e.getSource();

            //if right click (clear)
            if (e.getButton() == MouseEvent.BUTTON3) {
                if (curr.isStart())
                    clearStart();
                else if (curr.isEnd())
                    clearEnd();
                curr.clear();
            }
            //if left click 
            else if (e.getButton() == MouseEvent.BUTTON1) {
                //for dragging
                if (curr.isStart()) { //include && clickCellAction!=2 if want walls to overwrite start/end 
                    clickCellAction = 0;
                } else if (curr.isEnd()) {
                    clickCellAction = 1;
                }
                //left click if current cell is not start or end 
                else {
                    //if add start && end not on board
                    if (clickCellAction == 0 && start == null) {
                        curr.setAsStart();
                        start = new Point(curr.getRow(), curr.getCol());
                    }
                    //if add end && end not on board
                    else if (clickCellAction == 1 && end == null) {
                        curr.setAsEnd();
                        end = new Point(curr.getRow(), curr.getCol());
                    }
                    //if add toggle set on wall 
                    else if (clickCellAction == 2) {
                        curr.setAsWall();
                    }
                }
            }

            lastCellHeldVal = curr.getValue();

            //sets what button is currently held down for dragging tracking
            if (e.getButton() == MouseEvent.BUTTON3) {
                mousePressed = 2;
            } else if (e.getButton() == MouseEvent.BUTTON1) {
                mousePressed = 1;
            }
        }
    }

    //add listeners to slider to update slider time values
    class LayerTimeSliderListener implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent e) {
            JSlider source = (JSlider) e.getSource();
            if (!source.getValueIsAdjusting()) {
                int layerTime = (int) source.getValue();
                algo.setLayerTime(layerTime);
            }
        }
    }

    class CheckTimeSliderListener implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent e) {
            JSlider source = (JSlider) e.getSource();
            if (!source.getValueIsAdjusting()) {
                int checkTime = (int) source.getValue();
                algo.setCheckTime(checkTime);
            }
        }
    }

    class QueueTimeSliderListener implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent e) {
            JSlider source = (JSlider) e.getSource();
            if (!source.getValueIsAdjusting()) {
                int queueTime = (int) source.getValue();
                algo.setQueueTime(queueTime);
            }
        }
    }

    //add listeners to movement direction buttons 
    class OmniDirectionButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                algo.setDirection(Algorithms.OCTO_DIRECTIONS);
            } catch (Exception E) {
                gui.displayMessage("Could not change to Octo direction setting");
            }
        }
    }

    class QuadDirectionButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                algo.setDirection(Algorithms.QUAD_DIRECTIONS);
            } catch (Exception E) {
                gui.displayMessage("Could not change to Quad direction setting");
            }
        }
    }

    //------------------------helper methods----------------------------

    private void resetStartEnd() {
        start = null;
        end = null;
        //System.out.println("cleared start and end");
    }

    private void clearStart() {
        start = null;
        //System.out.println("cleared start");
    }

    private void clearEnd() {
        end = null;
        //System.out.println("cleared end");
    }

    private void initializeDefaultSliderSpeed() {
        int[] times = gui.getInitialSliderValues();
        algo.setLayerTime(times[0]);
        algo.setCheckTime(times[1]);
        algo.setQueueTime(times[2]);
    }

    private String getStartingText() {
        return ("Welcome, to the pathfinding visualizer!\n\n"
                + "Controls:"
                + GUI.STRING_BREAK +
                "Left click to add cells on the grid\n" +
                "You can drag and move the start and end cell\n" +
                "Hold and drag to add walls to quickly\n" +
                "Right click to clear cell\n Hold and drag to quickly clear cells.\n" +
                "You can change all the settings in real time \n" +
                "Experiment to see the effects!\n" +
                "To change grid size, input in the format r,c and update grid size\n" +
                "To clear the grid, click the same update grid button\n" +
                "I hope you enjoy! - Tshetrim (Tim)"
                + GUI.STRING_BREAK +
                "Also note comparing times between algorithms are\nonly accurate when speed timers are all at 0\n"
                + "https://github.com/Tshetrim/PathFindingVisualizar"
                + GUI.STRING_BREAK);
    }

    public void runAlgo() {
        String currAlgo = gui.getCurrentAlgo();

        gui.clearExtrasGrid();
        matrix.updateMatrix(start, end, gui.getGridAsIntArr());

        //Depth-First Search
        if (currAlgo.equals(GUI.ALGO_CHOICES[0])) {
            algo.BFS(start, end, gui.getGrid());
            //A* Pathfinding 
        } else if (currAlgo.equals(GUI.ALGO_CHOICES[1])) {
            algo.AStarPathfinding(start, end, gui.getGrid());
        } else if (currAlgo.equals(GUI.ALGO_CHOICES[2])) {
            algo.DFS(start, end, gui.getGrid());
        }
    }

}
