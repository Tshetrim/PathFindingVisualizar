import javax.swing.SwingUtilities;

public class MVCMain {

    public static void main(String[] args) {
        

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                GUI theView = new GUI();
                Matrix theMatrixModel = new Matrix(new Point(8, 8));
                Controller theController = new Controller(theView, theMatrixModel);
                
            }
        });

    }

}
