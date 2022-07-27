/* Controller contains the state of the matrix and calls the respective algorithms
 * It will send the GUI the state when updated/asked 
 * The size and states of the matrix which are dynamically changable through the GUI and Main also populates the matrix during algorithm searching
*/

import java.awt.event.*;
import java.awt.event.MouseEvent;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputListener;

public class Controller {
    private Matrix matrix;
    private GUI gui;
    private Algorithms algo;

    private String currentAlgorithm;

    private int clickCellAction; //0 = add start, 1 = add end, 2 = add wall
    private int mousePressed = 0; //for dragging in grid   0 = none, 1 = left, 2 = right 
    private int lastCellVal; //saving last cell entered for dragging 
    private int lastCellHeldVal;

    //private boolean start, end; //tracks if a start and end cell is in grid
    private Point start, end;

    public Controller(GUI gui, Matrix matrix, Algorithms algo) {
        this.gui = gui;
        this.matrix = matrix;
        this.algo = algo;

        this.gui.setGrid(matrix);

        registerListeners();
        initDefaultValues();

        gui.addText(
                "Right click to clear cell, drag to quickly fill cell. \n Only Breadth-First Search is currently implemented."
                        + GUI.STRING_BREAK);
    }

    public void initDefaultValues() {
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
            System.out.println(newSize.getX() + " " + newSize.getY());
            if (newSize.getX() <= 0 || newSize.getY() <= 0)
                gui.displayErrorMessage("Invalid size");
            else if (newSize.equals(matrix.getSize())) {
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
                    gui.displayErrorMessage("Invalid Input");
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
                gui.displayErrorMessage("Add Start Toggle Button Error");
            }
        }
    }

    //sets the add cell setting to be End cell 
    class AddEndListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                clickCellAction = 1;
            } catch (Exception E) {
                gui.displayErrorMessage("Add End Toggle Button Error");
            }

        }
    }

    //sets the add cell setting to be walls 
    class AddWallListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                clickCellAction = 2;
            } catch (Exception E) {
                gui.displayErrorMessage("Add Wall Toggle Button Error");
            }
        }
    }

    //runs currently chosen algorithm 
    class RunButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                gui.clearExtrasGrid();
                matrix.updateMatrix(start, end, gui.getGridAsIntArr());
                algo.BFS(start, end, gui.getGrid());
            } catch (Exception E) {
                gui.displayErrorMessage("Run error");
            }
        }
    }

    //Action listener for cell grid -> adding cells, deleting cells, dragging add/delete cells
    class CellMouseListener implements MouseInputListener {

        //sets mouse pressed on mouse release 
        public void mouseReleased(MouseEvent e) {
            System.out.println("released");
            Cell curr = (Cell) e.getSource();

            //only save on mouse release if current cell is a start or end and the currently held cell is start or end
            //and mouse pressed is not right click  (clear)
            if (mousePressed != 2) {
                if (!curr.isStart() || !curr.isEnd()) {
                    if (lastCellHeldVal == 1 && start == null) {//if last cell held was a start / end
                        curr.setAsStart();
                        start = new Point(curr.getRow(), curr.getCol());
                    } else if (lastCellHeldVal == 2 && end == null) {
                        curr.setAsEnd();
                        end = new Point(curr.getRow(), curr.getCol());
                    }
                }
            }

            lastCellHeldVal = -10;
            lastCellVal = -10;
            mousePressed = 0;
        }

        //if mousePressed state is left or right click, then entering a cell should update the cells with the current cell type toggle
        public void mouseEntered(MouseEvent e) {
            System.out.println(" entered");
            Cell curr = (Cell) e.getSource();
            lastCellVal = curr.getValue();

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
            System.out.println("mouse exited");
            Cell curr = (Cell) e.getSource();
            System.out.println(curr.isStart());

            if (mousePressed == 1) {
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
            System.out.println("mouse pressed");
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
            System.out.println(lastCellHeldVal);

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
                gui.displayErrorMessage("Could not change to Octo direction setting");
            }
        }
    }

    class QuadDirectionButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                algo.setDirection(Algorithms.QUAD_DIRECTIONS);
            } catch (Exception E) {
                gui.displayErrorMessage("Could not change to Quad direction setting");
            }
        }
    }

    //------------------------helper methods----------------------------
    //updates the start and end global booleans to false if current cell is start or end and being overwritten
    private void updateStartEndWhenUpdating(Cell curr) {
        if (curr.isStart())
            start = null;
        if (curr.isEnd())
            end = null;
    }

    private void resetStartEnd() {
        start = null;
        end = null;
        System.out.println("cleared start and end");
    }

    private void clearStart() {
        start = null;
        System.out.println("cleared start");
    }

    private void clearEnd() {
        end = null;
        System.out.println("cleared end");
    }

    private void initializeDefaultSliderSpeed() {
        int[] times = gui.getInitialSliderValues();
        algo.setLayerTime(times[0]);
        algo.setCheckTime(times[1]);
        algo.setQueueTime(times[2]);
    }

}
