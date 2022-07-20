import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GUI extends JFrame {

    //JPanels contains all the JComponents
    private JPanel gridPane;
    private JPanel inputPane;

    //------JComponents -------
    //gridComponent
    private JButton[][] grid;

    //Input components
    private JTextField updateGridSizeField;
    private JButton updateGridSizeButton;
    private JTextArea currentAlgo;
    private JButton algo1;
    private JButton algo2;

    GUI() {
        setLookAndFeel();

        this.setLayout(new BorderLayout());

        gridPane = getGridPanel();
        this.add(gridPane, BorderLayout.CENTER);

        inputPane = getInputPanel();
        this.add(inputPane, BorderLayout.NORTH);

        //JFRAME
        this.setTitle("Pathfinding Visualizer");
        this.setLocation(500, 500); //to-do: set to center
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);
    }

    //try to set look and feel of components to OS look and feel
    public void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException ex) {
        }
    }

    //initializing and returning the grid panel 
    public JPanel getGridPanel() {
        gridPane = new JPanel(new GridLayout());
        gridPane.setPreferredSize(new Dimension(800, 600));
        gridPane.setBorder(BorderFactory.createLineBorder(Color.GREEN));
        return gridPane;
    }

    public JPanel getInputPanel() {
        //setting inputPanel
        inputPane = new JPanel(new GridBagLayout());
        inputPane.setPreferredSize(new Dimension(100, 100));
        inputPane.setBorder(BorderFactory.createLineBorder(Color.BLUE));

        //input for grid size
        JLabel gridSizeInputLabel = new JLabel("Enter grid size:");
        updateGridSizeField = new JTextField();
        updateGridSizeField.setColumns(5);
        updateGridSizeButton = new JButton("submit");

        //display current algo 
        currentAlgo = new JTextArea("Curr algo");
        //row of algos
        algo1 = new JButton("Algo 1");
        algo2 = new JButton("Algo 2");

        inputPane.add(gridSizeInputLabel);
        inputPane.add(updateGridSizeField);
        inputPane.add(updateGridSizeButton);

        inputPane.add(currentAlgo);
        inputPane.add(algo1);
        inputPane.add(algo2);

        return inputPane;
    }

    //input action listeners
    void addUpdateGridSizeActionListener(ActionListener listenerForUpdateGridSizeButton) {
        updateGridSizeButton.addActionListener(listenerForUpdateGridSizeButton);
    }

    public Point getGridSize() {
        //add input validation later
        String[] input = updateGridSizeField.getText().split(",");
        int xValue = Integer.parseInt(input[0]);
        int yValue = Integer.parseInt(input[1]);
        return new Point(xValue, yValue);
    }

    public void setGrid(Matrix matrix) {
        gridPane.setLayout(new GridLayout(matrix.getRowLength(), matrix.getColumnLength()));
        grid = new JButton[matrix.getRowLength()][matrix.getColumnLength()];
        for (int r = 0; r < matrix.getRowLength(); r++) {
            for (int c = 0; c < matrix.getColumnLength(); c++) {
                grid[r][c] = new JButton();
                gridPane.add(grid[r][c]);
            }
        }

    }

    public void updateGrid(Matrix matrix) {

    }

    public void displayErrorMessage(String errorMessage) {
        JOptionPane.showMessageDialog(this, errorMessage);
    }

    public static void main(String[] args) {
        new GUI();
    }

}