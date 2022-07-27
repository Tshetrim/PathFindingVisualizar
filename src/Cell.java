import java.awt.Color;
import javax.swing.JButton;
import javax.swing.border.LineBorder;
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
        //setBorder(new LineBorder(Color.BLACK));

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
        setText("Path");
        setBackground(Color.BLUE);
    }

    public void clear() {
        value = 0;
        setText("");

        setBackground(Color.WHITE);
        setForeground(Color.BLACK);
    }

    //cells can be current and other states simutaneously 
    public void setAsCurrent() {
        setBorderPainted(false);
    }

    public void setByValue(int val) {
        if (val == 1)
            this.setAsStart();
        else if (val == 2)
            this.setAsEnd();
        else if (val == -1)
            this.setAsWall();
        else if (val == 3)
            this.setAsSeen();
        else if (val == 4)
            this.setAsQueued();
        else if (val == 5)
            this.setAsBest();
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
