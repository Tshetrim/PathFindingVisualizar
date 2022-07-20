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

    String currentAlgorithm;
    int clickCellAction; //0 = add start, 1 = add end, 2 = add wall

    private int mousePressed = 0; //for dragging in grid

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
    }

    //adds action listeners to components in the GUI 
    public void registerListeners() {
        gui.addUpdateGridSizeActionListener(new UpdateGridSizeListener());
        gui.addStartButtonListener(new AddStartListener());
        gui.addEndButtonListener(new AddEndListener());
        gui.addWallButtonListener(new AddWallListener());
    }

    //adds action listeners to all cells n the grid -> must call after every setGrid(Matrix)
    public void registerCellMatrixListeners() {
        for (Cell[] row : this.gui.getGrid()) {
            for (Cell cell : row) {
                cell.addMouseListener(new CellMouseListener());
            }
        }
    }

    class UpdateGridSizeListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Point newSize = gui.getGridSize();
            System.out.println(newSize.x + " " + newSize.y);
            try {
                newSize = gui.getGridSize();
                matrix = new Matrix(newSize);
                gui.setGrid(matrix);
                registerCellMatrixListeners();
            } catch (Exception E) { //note add more/specific exception catches later including for Matrix end
                gui.displayErrorMessage("Invalid Input");
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


    
    class CellMouseListener implements MouseInputListener {

        public void mouseReleased(MouseEvent e) {
            mousePressed = 0;
        }

        public void mouseEntered(MouseEvent e) {
            Cell curr = (Cell) e.getSource();
            if (mousePressed > 0) {
                if (mousePressed == 2) {
                    curr.clear();
                } else if (mousePressed == 1) {
                    if (clickCellAction == 0) {
                        curr.setAsStart();
                    } else if (clickCellAction == 1) {
                        curr.setAsEnd();
                    } else {
                        curr.setAsWall();
                    }
                }
            }
        }

        public void mouseExited(MouseEvent e) {
        }

        public void mouseMoved(MouseEvent e) {
        }

        public void mouseDragged(MouseEvent e) {
        }

        public void mouseClicked(MouseEvent e) {
        }

        //add input validation later for if a start/end cell already exists 
        public void mousePressed(MouseEvent e) {
            Cell curr = (Cell) e.getSource();
            if (e.getButton() == MouseEvent.BUTTON3) {
                curr.clear();
            } else if (e.getButton() == MouseEvent.BUTTON1) {
                if (clickCellAction == 0) {
                    curr.setAsStart();
                } else if (clickCellAction == 1) {
                    curr.setAsEnd();
                } else {
                    curr.setAsWall();
                }
            }

            if (e.getButton() == MouseEvent.BUTTON3) {
                mousePressed = 2;
            } else if (e.getButton() == MouseEvent.BUTTON1) {
                mousePressed = 1;
            }
        }
    }

}
