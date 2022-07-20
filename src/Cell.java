import java.awt.Color;

import javax.swing.JButton;
import java.awt.event.*;

import javax.swing.event.MouseInputListener;


public class Cell extends JButton {
    int col;
    int row;

    boolean start;
    boolean end;
    boolean wall;

    public Cell(int col, int row) {
        this.col = col;
        this.row = row;

        setBackground(Color.WHITE);
        setForeground(Color.BLACK);
    }

    public void setAsStart() {
        start = true;
        //this.setName("1");
        this.setText("Start");
        setBackground(Color.GREEN);
    }

    public void setAsEnd() {
        end = true;
        //this.setName("0");
        this.setText("End");
        setBackground(Color.BLUE);
    }

    public void setAsWall() {
        wall = true;
        this.setName("-1");
        //this.setText("Wall");
        setBackground(Color.BLACK);
    }

    public void clear() {
        start = false;
        end = false;
        wall = false;

        this.setName("");
        this.setText("");

        setBackground(Color.WHITE);
        setForeground(Color.BLACK);
    }


    public void addCellMouseListener(MouseInputListener listenerForCell) {
        this.addMouseListener(listenerForCell);
    }

}
