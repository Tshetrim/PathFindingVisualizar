import java.awt.Color;

import javax.swing.JButton;
import javax.swing.event.MouseInputListener;

public class Cell extends JButton {
    private int col;
    private int row;

    private int value; //0=clear, 1 = start, 2=end, -1= wall //3 searched //4 queued to be searched

    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
        this.value = 0;

        setBackground(Color.WHITE);
        setForeground(Color.BLACK);
    }

    public void setAsStart() {
        value = 1;
        setText("Start");
        setBackground(Color.GREEN);
    }

    public void setAsEnd() {
        value = 2;
        setText("End");
        setBackground(Color.RED);
    }

    public void setAsWall() {
        value = -1;
        //setText("Wall");
        setBackground(Color.BLACK);
    }

    public void setAsSeen() {
        value = 3;
        setText("Checked");
        setBackground(Color.YELLOW);
    }

    public void setAsQueued() {
        value = 4;
        setText("Queued");
        setBackground(Color.ORANGE);
    }

    public void setAsBest() {
        value = 5;
        setBackground(Color.BLUE);
    }

    public void clear() {
        value = 0;
        setText("");

        setBackground(Color.WHITE);
        setForeground(Color.BLACK);
    }

    public void addCellMouseListener(MouseInputListener listenerForCell) {
        this.addMouseListener(listenerForCell);
    }

    public int getValue() {
        return this.value;
    }

    //do not override with getX and getY because they have exisiting funcitonality that will get broken
    public int getRow() {
        return this.row;
    }

    public int getCol() {
        return this.col;
    }

    public boolean isWall() {
        return this.value == -1;
    }

    public boolean isSeen() {
        return this.value == 3;
    }

    public boolean isStart() {
        return this.value == 1;
    }

    public boolean isEnd() {
        return this.value == 2;
    }

}
