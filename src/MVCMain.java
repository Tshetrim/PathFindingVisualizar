public class MVCMain {

    public static void main(String[] args) {
        GUI theView = new GUI();
        Matrix theMatrixModel = new Matrix(new Point(50, 50));
        Controller theController = new Controller(theView, theMatrixModel);

    }

}

    

    

    

    