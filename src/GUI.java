import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;

import java.awt.*;
import java.awt.event.*;

public class GUI extends JFrame {

    //JPanels contains all the JComponents
    private JPanel gridPane;
    private JPanel inputPane;

    //------JComponents -------
    //gridComponent
    private Cell[][] grid;

    //Input components
    private JTextField updateGridSizeField;
    private JButton updateGridSizeButton;
    private JTextArea currentAlgo;
    private JButton algo1;
    private JButton algo2;
    private JButton runButton;

    private JButton addStartButton;
    private JButton addEndButton;
    private JButton addWallButton;

    GUI() {

        setLookAndFeel(0);
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
    public void setLookAndFeel(int val) {
        if (val == 1) {
            try {
                for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception e) {
                System.out.println("Could not set to Nimbus Look and Feel");
            }
        } else if (val == 2) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                    | UnsupportedLookAndFeelException ex) {
            }
        }
    }

    //initializing and returning the grid panel 
    public JPanel getGridPanel() {
        gridPane = new JPanel(new GridLayout());
        gridPane.setPreferredSize(new Dimension(800, 700));
        // gridPane.setBorder(BorderFactory.createLineBorder(Color.GREEN));
        return gridPane;
    }

    public JPanel getInputPanel() {
        //setting inputPanel
        inputPane = new JPanel(new FlowLayout(FlowLayout.CENTER));
        // inputPane.setPreferredSize(new Dimension(100, 100));
        // inputPane.setBorder(BorderFactory.createLineBorder(Color.BLUE));

        //input for grid size
        JLabel gridSizeInputLabel = new JLabel("Enter grid size:");
        updateGridSizeField = new JTextField();
        updateGridSizeField.setColumns(5);
        updateGridSizeButton = new JButton("submit/clear");

        //display current algo 
        currentAlgo = new JTextArea("Curr algo");

        //row of algos
        algo1 = new JButton("Breadth-First-Search");
        algo2 = new JButton("A* Path Finding");

        //grid input buttons
        addStartButton = new JButton("Add Start");
        addEndButton = new JButton("Add End");
        addWallButton = new JButton("Add Wall");

        //run button
        runButton = new JButton("Run");

        inputPane.add(gridSizeInputLabel);
        inputPane.add(updateGridSizeField);
        inputPane.add(updateGridSizeButton);

        inputPane.add(currentAlgo);
        inputPane.add(algo1);
        inputPane.add(algo2);

        inputPane.add(addStartButton);
        inputPane.add(addEndButton);
        inputPane.add(addWallButton);

        inputPane.add(runButton);
        return inputPane;
    }

    //input action listeners
    void addUpdateGridSizeActionListener(ActionListener listenerForUpdateGridSizeButton) {
        updateGridSizeButton.addActionListener(listenerForUpdateGridSizeButton);
    }

    void addStartButtonListener(ActionListener listenerForAddStartButton) {
        addStartButton.addActionListener(listenerForAddStartButton);
    }

    void addEndButtonListener(ActionListener listenerForAddEndButton) {
        addEndButton.addActionListener(listenerForAddEndButton);
    }

    void addWallButtonListener(ActionListener listenerForAddWallButton) {
        addWallButton.addActionListener(listenerForAddWallButton);
    }

    void addRunButtonListener(ActionListener listenerForRunButton) {
        runButton.addActionListener(listenerForRunButton);
    }

    public Point getGridSize() {
        //add input validation later
        String[] input = updateGridSizeField.getText().split(",");
        int xValue = Integer.parseInt(input[0]);
        int yValue = Integer.parseInt(input[1]);
        return new Point(xValue, yValue);
    }

    public void setGrid(Matrix matrix) {
        GridLayout layout = new GridLayout(matrix.getRowLength(), matrix.getColumnLength());
        layout.setHgap(0);
        layout.setVgap(0);
        gridPane.setLayout(layout);

        grid = new Cell[matrix.getRowLength()][matrix.getColumnLength()];

        gridPane.removeAll();
        for (int r = 0; r < matrix.getRowLength(); r++) {
            for (int c = 0; c < matrix.getColumnLength(); c++) {
                grid[r][c] = new Cell(r, c);
                if (matrix.get(r, c) == 1) {
                    grid[r][c].setAsStart();
                }
                gridPane.add(grid[r][c]);
            }
        }
        this.pack();
    }

    public void clearGrid() {
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[0].length; c++) {
                grid[r][c].clear();
            }
        }
    }

    //for running again in the same grid size
    public void clearExtrasGrid() {
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[0].length; c++) {
                Cell curr = grid[r][c];
                if (!(curr.isWall() || curr.isStart() || curr.isEnd()))
                    curr.clear();
            }
        }
    }

    public void displayErrorMessage(String errorMessage) {
        JOptionPane.showMessageDialog(this, errorMessage);
    }

    public static void main(String[] args) {
        new GUI();
    }

    public Cell[][] getGrid() {
        return this.grid;
    }

    public JTextField getGridSizeTextField() {
        return this.updateGridSizeField;
    }

    //returns the JFrame[][] grid as an int[][]
    public int[][] getGridAsIntArr() {
        int[][] output = new int[grid.length][grid[0].length];
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[0].length; c++) {
                output[r][c] = grid[r][c].getValue();
            }
        }
        return output;
    }

}