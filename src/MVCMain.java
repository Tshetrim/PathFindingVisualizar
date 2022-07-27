import javax.swing.SwingUtilities;

public class MVCMain {

    public static void main(String[] args) {
        

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                GUI theView = new GUI();
                Matrix theMatrixModel = new Matrix(new Point(8, 8));
                AudioWorker audio = new AudioWorker();
                Algorithms algo = new Algorithms(theView, audio);
                Controller theController = new Controller(theView, theMatrixModel, algo, audio);
                
            }
        });

    }

}
