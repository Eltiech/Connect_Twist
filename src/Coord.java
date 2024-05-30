import org.fusesource.jansi.AnsiConsole;

public class Coord implements Comparable<Coord>{
    public Coord(byte x, byte y) {
        this.x = x;
        this.y = y;
    }
    private byte x;
    private byte y;
    public byte getCol() {
        return x;
    }
    public byte getRow() {
        return y;
    }
    public Coord add(final Coord c) {
        return new Coord((byte)(this.x + c.x), (byte)(this.y + c.y));
    }
    public Coord add(Direction d) {
        return new Coord((byte)(this.x + d.x), (byte)(this.y + d.y));
    }


    @Override
    public int compareTo(final Coord c) {
        if (this.x < c.x ) {
            return -1;
        } else if (this.x > c.x) {
            return 1;
        } else {
            if (this.y < c.y ) {
                return -1;
            } else if (this.y > c.y) {
                return 1;
            }
        }
        return 0;
    }

    @Override
    public String toString() {
        return "<"+x+","+y+">";
    }
}
