/* Controller contains the state of the matrix and calls the respective algorithms
 * It will send the GUI the state when updated/asked 
 * The size and states of the matrix which are dynamically changable through the GUI and Main also populates the matrix during algorithm searching
*/

import java.awt.event.*;

public class Controller {
    private Matrix matrix;
    private GUI gui;

    String currentAlgorithm;

    public Controller(GUI gui, Matrix matrix) {
        this.gui = gui;
        this.matrix = matrix;

        this.gui.addUpdateGridSizeActionListener(new UpdateGridSizeListener());
        this.gui.setGrid(matrix);

        run();
    }

    public void run() {

    }

    class UpdateGridSizeListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Point newSize;
            try {
                newSize = gui.getGridSize();
                matrix.updateSize(newSize);
            } catch (Exception E) { //note add more/specific exception catches later including for Matrix end
                gui.displayErrorMessage("Invalid Input");
            }

        }
    }

}
