public class Point {
    private int x;
    private int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point(String str) {
        String[] arr = str.substring(1, str.length()).split(",");
        this.x = Integer.parseInt(arr[0]);
        this.y = Integer.parseInt(arr[1]);
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int compare(Point point) {
        int oX = point.x;
        int oY = point.y;

        if (x > oX && y > oY)
            return 1;
        else if (x < oX && y < oY)
            return -1;
        else if (x == oX && y == oY)
            return 0;
        else {
            double distance = Math.sqrt(y * y + x * x);
            double oDistance = Math.sqrt(oY * oY + oX * oX);
            if (distance > oDistance)
                return 1;
            else if (distance < oDistance)
                return -1;
            else
                return 1;
        }
    }

    public boolean equals(Point point) {
        return this.compare(point) == 0;
    }

    // for testing purposes since I'm not going to do full unit testing 
    public static void main(String[] args) {
        Point a = new Point(0, 4);
        Point b = new Point(4, 0);
        System.out.println(a.compare(b));
    }

    public String toString() {
        return ("(" + this.x + "," + this.y + ")");
    }
}
