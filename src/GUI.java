import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.event.ChangeListener;
import javax.swing.text.*;

import com.formdev.flatlaf.*;

import java.awt.*;
import java.awt.event.*;

public class GUI extends JFrame {

    //JPanels contains all the JComponents
    private JPanel gridPane;
    private JPanel inputPane;
    private JPanel rightInputPane;

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

    //speed sliders 
    private JSlider layerTimeSlider;
    private JSlider checkTimeSlider;
    private JSlider queueTimeSlider;

    private JLabel layerTimeLabel;
    private JLabel checkTimeLabel;
    private JLabel queueTimeLabel;

    //speed sliders constraints 
    private final int LAYER_TIME_MAX = 3000;
    private final int LAYER_TIME_MIN = 0;
    private final int LAYER_TIME_INIT = 200;

    private final int CHECK_TIME_MAX = 2000;
    private final int CHECK_TIME_MIN = 0;
    private final int CHECK_TIME_INIT = 50;

    private final int QUEUE_TIME_MAX = 2000;
    private final int QUEUE_TIME_MIN = 0;
    private final int QUEUE_TIME_INIT = 50;

    //Direction movement option buttons
    private ButtonGroup directionButtonGroup;
    private JRadioButton omniDirectionButton;
    private JRadioButton quadDirectionButton;

    //output component
    private JTextPane textArea;
    private JScrollPane textAreaScrollPane; // -> textArea goes inside textAreaScrollPane

    static final String STRING_BREAK = ("\n----------------------------------------------\n");

    GUI() {

        setLookAndFeel(4);
        this.setLayout(new BorderLayout());

        gridPane = getGridPanel();
        this.add(gridPane, BorderLayout.CENTER);

        inputPane = getInputPanel();
        this.add(inputPane, BorderLayout.NORTH);

        rightInputPane = getTimeSliderPanel();
        this.add(rightInputPane, BorderLayout.EAST);

        //JFRAME
        //this.setUndecorated(true);
        this.setTitle("Pathfinding Visualizer");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setLocationRelativeTo(null); //to-do: set to center
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
        } else if (val == 3) {
            try {
                UIManager.setLookAndFeel(new FlatLightLaf());
            } catch (Exception ex) {
                System.err.println("Failed to initialize LaF");
            }
        } else if (val == 4) {
            try {
                UIManager.setLookAndFeel(new FlatDarkLaf());
            } catch (Exception ex) {
                System.err.println("Failed to initialize LaF");
            }
        } else if (val == 5) {
            try {
                UIManager.setLookAndFeel(new FlatDarculaLaf());
            } catch (Exception ex) {
                System.err.println("Failed to initialize LaF");
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

        //adding components to panel
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

    public JPanel getTimeSliderPanel() {
        rightInputPane = new JPanel();
        rightInputPane.setLayout(new BoxLayout(rightInputPane, BoxLayout.Y_AXIS));

        //JSliders
        layerTimeSlider = new JSlider(JSlider.HORIZONTAL, LAYER_TIME_MIN, LAYER_TIME_MAX, LAYER_TIME_INIT);
        checkTimeSlider = new JSlider(JSlider.HORIZONTAL, CHECK_TIME_MIN, CHECK_TIME_MAX, CHECK_TIME_INIT);
        queueTimeSlider = new JSlider(JSlider.HORIZONTAL, QUEUE_TIME_MIN, QUEUE_TIME_MAX, QUEUE_TIME_INIT);

        layerTimeSlider.setMajorTickSpacing(1000);
        checkTimeSlider.setMajorTickSpacing(1000);
        queueTimeSlider.setMajorTickSpacing(1000);

        layerTimeSlider.setPaintLabels(true);
        checkTimeSlider.setPaintLabels(true);
        queueTimeSlider.setPaintLabels(true);

        layerTimeSlider.setPaintTicks(true);
        checkTimeSlider.setPaintTicks(true);
        queueTimeSlider.setPaintTicks(true);

        layerTimeLabel = new JLabel("Slowdown speed of starting new layer");
        checkTimeLabel = new JLabel("Slowdown speed of checking cell");
        queueTimeLabel = new JLabel("Slowdown speed of queuing cell");

        //Direction buttons 
        directionButtonGroup = new ButtonGroup();
        omniDirectionButton = new JRadioButton("Search in Eight directions ");
        quadDirectionButton = new JRadioButton("Search in Four directions ");
        directionButtonGroup.add(omniDirectionButton);
        directionButtonGroup.add(quadDirectionButton);
        omniDirectionButton.setSelected(true);

        //JText Area
        textArea = new JTextPane();
        //setting text pane 
        StyledDocument documentStyle = textArea.getStyledDocument();
        SimpleAttributeSet centerAttribute = new SimpleAttributeSet();
        StyleConstants.setAlignment(centerAttribute, StyleConstants.ALIGN_CENTER);
        documentStyle.setParagraphAttributes(0, documentStyle.getLength(), centerAttribute, false);

        textAreaScrollPane = new JScrollPane(textArea);
        textAreaScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        //adding components to panel
        //Note Box is a container, it is not needed but helps to align within a BoxLayout

        //Adding time slider components to a vertical box
        Box timeSliderBox = Box.createVerticalBox();
        timeSliderBox.add(layerTimeLabel);
        timeSliderBox.add(layerTimeSlider);

        timeSliderBox.add(checkTimeLabel);
        timeSliderBox.add(checkTimeSlider);

        timeSliderBox.add(queueTimeLabel);
        timeSliderBox.add(queueTimeSlider);

        //Adding direction button components to a horizontal box 
        Box directionButtonsBox = Box.createVerticalBox();
        directionButtonsBox.add(omniDirectionButton);
        directionButtonsBox.add(quadDirectionButton);

        //adding the boxes to the panel 
        rightInputPane.add(timeSliderBox);
        rightInputPane.add(directionButtonsBox);

        Component margin = Box.createVerticalStrut(10);
        //Adding output textfield
        rightInputPane.add(margin);
        rightInputPane.add(textAreaScrollPane);

        return rightInputPane;
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

    void addLayerTimeListener(ChangeListener listenerForLayerTimeSlider) {
        layerTimeSlider.addChangeListener(listenerForLayerTimeSlider);
    }

    void addCheckTimeListener(ChangeListener listenerForCheckTimeSlider) {
        checkTimeSlider.addChangeListener(listenerForCheckTimeSlider);
    }

    void addQueueTimeListener(ChangeListener listenerForQueueTimeSlider) {
        queueTimeSlider.addChangeListener(listenerForQueueTimeSlider);
    }

    void addOmniDirectionButtonListener(ActionListener listenerForOmniDirectionButton) {
        omniDirectionButton.addActionListener(listenerForOmniDirectionButton);
    }

    void addQuadDirectionButtonListener(ActionListener listenerForQuadDirectionButton) {
        quadDirectionButton.addActionListener(listenerForQuadDirectionButton);
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
        this.validate();
        this.repaint();
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
        JOptionPane.showMessageDialog(this, errorMessage, "", JOptionPane.PLAIN_MESSAGE);
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

    //0 = layer, 1 = check, 2 = queue initial times 
    public int[] getInitialSliderValues() {
        return new int[] { LAYER_TIME_INIT, CHECK_TIME_INIT, QUEUE_TIME_INIT };
    }

    public void addText(String text) {
        try {
            String prev = textArea.getText();
            textArea.setText(prev + text);

        } catch (Exception e) {
            displayErrorMessage("Unable to write text to Text pane");
        }
    }

}