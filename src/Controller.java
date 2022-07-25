/* Controller contains the state of the matrix and calls the respective algorithms
 * It will send the GUI the state when updated/asked 
 * The size and states of the matrix which are dynamically changable through the GUI and Main also populates the matrix during algorithm searching
*/

import java.awt.event.*;
import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputListener;

public class Controller {
    private Matrix matrix;
    private GUI gui;

    private String currentAlgorithm;

    private int clickCellAction; //0 = add start, 1 = add end, 2 = add wall
    private int mousePressed = 0; //for dragging in grid

    //private boolean start, end; //tracks if a start and end cell is in grid
    private Point start, end;

    public Controller(GUI gui, Matrix matrix) {
        this.gui = gui;
        this.matrix = matrix;

        this.clickCellAction = 2;
        registerListeners();

        run();
    }

    public void run() {
        this.gui.setGrid(matrix);
        registerCellMatrixListeners();
        this.gui.getGridSizeTextField().setText(matrix.getSize().getX() + "," + matrix.getSize().getY());
    }

    //adds action listeners to components in the GUI 
    public void registerListeners() {
        gui.addUpdateGridSizeActionListener(new UpdateGridSizeListener());
        gui.addStartButtonListener(new AddStartListener());
        gui.addEndButtonListener(new AddEndListener());
        gui.addWallButtonListener(new AddWallListener());
        gui.addRunButtonListener(new RunButtonListener());
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
            else {
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

    class AddStartListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                clickCellAction = 0;
            } catch (Exception E) {
                gui.displayErrorMessage("Add Start Toggle Button Error");
            }
        }
    }

    class AddEndListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                clickCellAction = 1;
            } catch (Exception E) {
                gui.displayErrorMessage("Add End Toggle Button Error");
            }

        }
    }

    class AddWallListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                clickCellAction = 2;
            } catch (Exception E) {
                gui.displayErrorMessage("Add Wall Toggle Button Error");
            }
        }
    }

    class RunButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                matrix.updateMatrix(start, end, gui.getGridAsIntArr());
                new Algorithms(gui).BFS(start, end, gui.getGrid());
            } catch (Exception E) {
                gui.displayErrorMessage("Run error");
            }
        }
    }

    class CellMouseListener implements MouseInputListener {

        //sets mouse pressed on mouse release 
        public void mouseReleased(MouseEvent e) {
            mousePressed = 0;
        }

        //if mousePressed state is left or right click, then entering a cell should update the cells with the current cell type toggle
        public void mouseEntered(MouseEvent e) {
            Cell curr = (Cell) e.getSource();

            if (mousePressed > 0) {
                updateStartEndWhenUpdating(curr); //important this is here because if not being dragged, current cell not be getting updated and thus, if current cell is start/end, should not be cleared
                if (mousePressed == 2) {
                    curr.clear();
                } else if (mousePressed == 1) {
                    if (clickCellAction == 0 && start == null) {
                        curr.setAsStart();
                        start = new Point(curr.getRow(), curr.getCol());
                        //start = new Point(curr.getX(), curr.getY());

                    } else if (clickCellAction == 1 && end == null) {
                        curr.setAsEnd();
                        end = new Point(curr.getRow(), curr.getCol());
                        //end = new Point(curr.getX(), curr.getY());

                    } else if (clickCellAction == 2) {
                        curr.setAsWall();
                    }
                }
            }
        }

        //only needed to be here because these are abstract methods of the MouseInputListener interface 
        public void mouseExited(MouseEvent e) {
            //do nothing
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
            Cell curr = (Cell) e.getSource();
            updateStartEndWhenUpdating(curr);

            if (e.getButton() == MouseEvent.BUTTON3) {
                curr.clear();
            } else if (e.getButton() == MouseEvent.BUTTON1) {
                if (clickCellAction == 0 && start == null) {
                    curr.setAsStart();
                    start = new Point(curr.getRow(), curr.getCol());
                    //start = new Point(curr.getX(), curr.getY());
                } else if (clickCellAction == 1 && end == null) {
                    curr.setAsEnd();
                    end = new Point(curr.getRow(), curr.getCol());
                    //end = new Point(curr.getX(), curr.getY());
                } else if (clickCellAction == 2) {
                    curr.setAsWall();
                }
            }

            //sets what button is currently held down for dragging tracking
            if (e.getButton() == MouseEvent.BUTTON3) {
                mousePressed = 2;
            } else if (e.getButton() == MouseEvent.BUTTON1) {
                mousePressed = 1;
            }
        }
    }

    //------------------------helper methods----------------------------
    //updates the start and end global booleans to false if current cell is start or end and being overwritten
    private void updateStartEndWhenUpdating(Cell curr) {
        int value = curr.getValue();
        if (value == 1) {
            start = null;
            //start = null;
        } else if (value == 2) {
            end = null;
            //end = null;
        }
    }

    private void resetStartEnd() {
        start = null;
        end = null;

        //start = null;
        //end = null;
    }

}
