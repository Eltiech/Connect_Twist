import java.util.TreeSet;

public class Board {

    private byte cols;
    private byte rows;
    private byte setLength;
    private PieceColor[][] slots;

    public Board() {
        this(7, 6, 4);
    }

    //this just helps with java favoring ints for bare numbers
    public Board(int cols, int rows, int setLength) {
        this((byte)cols, (byte)rows, (byte)setLength);
    }

    public Board(byte cols, byte rows, byte setLength) {
        this.cols = cols;
        this.rows = rows;
        this.setLength = setLength;
        slots = new PieceColor[cols][rows];
        for (byte x = 0; x < cols; x++) {
            for (byte y = 0; y < rows; y++) {
                slots[x][y] = PieceColor.NONE;
            }
        }
    }
    public byte getRows() {
        return rows;
    }
    public byte getCols() {
        return cols;
    }

    private boolean full = false;
    public boolean isFull() {
        //avoid checking again if found full already
        if (full) {
            return true;
        }
        //check the top row, see if any are empty
        for (int x = 0; x < cols; x++) {
            if (slots[x][0] == PieceColor.NONE) {
                return false;
            }
        }
        full = true;
        return true;
    }

    public boolean addPiece(Player p, byte col) {
        //check each place along the column started at the bottom
        for (byte currRow = (byte)(rows - 1); currRow >= 0; currRow --) {
            if (slots[col][currRow] == PieceColor.NONE) {
                slots[col][currRow] = p.getColor();
                findAdjacent(p, new Coord(col,currRow));
                return true;
            }
        }
        return false;
    }
    public PieceColor getPiece(Coord c) {
        return slots[c.getCol()][c.getRow()];
    }

    public PieceColor[][] getSlots() {
        return slots;
    }

    private void findAdjacent(Player player, Coord c) {
        ComparableTreeSet<Coord> workingSet = new ComparableTreeSet<Coord>();
        workingSet.add(c);
        findAdjacentHelper(player, workingSet, c, c, Direction.NW);
        findAdjacentHelper(player, workingSet, c, c, Direction.W);
        findAdjacentHelper(player, workingSet, c, c, Direction.SW);
        findAdjacentHelper(player, workingSet, c, c, Direction.S);
    }
    private void findAdjacentHelper(Player player,
                                    ComparableTreeSet<Coord> workingSet, Coord current,
                                    Coord root, Direction dir) {
        Coord next = current.add(dir);
        PieceColor color = player.getColor();
        //System.out.println(String.format("FindAdjacentHelper: current: %s, root:%s, dir: %s, setLength: %d",current,root,dir,setLength));
        //Util.coordSetToString(workingSet);
        if (isInBounds(next) && getPiece(next) == color) {
            //copy it for recursion
            ComparableTreeSet<Coord> setCopy = new ComparableTreeSet<Coord>(workingSet);
            setCopy.add(next);
            //System.out.println("setCopy is " + Util.coordSetToString(setCopy));
            if (setCopy.size() == setLength) {
                player.getSets().add(setCopy);
                return;
            }
            findAdjacentHelper(player, setCopy, next, root, dir);
        }
        //now check the opposite directions if we haven't already
        switch (dir) {
            case NW:
                dir = Direction.SE;
                break;
            case W:
                dir = Direction.E;
                break;
            case SW:
                dir = Direction.NE;
                break;
            default:
                //either we're already coming from an opposite-check, or S
                //in which case there's no need to go N because a piece was
                //just added so nothing can be above it
                return;
        }
        //add the direction offset
        Coord opp = root.add(dir);
        if (isInBounds(opp) && getPiece(opp) == color) {
            //copy it for recursion
            ComparableTreeSet<Coord> setCopy = new ComparableTreeSet<Coord>(workingSet);
            setCopy.add(opp);
            //System.out.println("setCopy is " + Util.coordSetToString(setCopy));
            if (setCopy.size() == setLength) {
                player.getSets().add(setCopy);
                return;
            }
            findAdjacentHelper(player, setCopy, opp, root, dir);
        }
        return;
    }
    private boolean isInBounds(final Coord c) {
        return c.getCol() >= 0 &&
                c.getRow() >= 0 &&
                c.getCol() < cols &&
                c.getRow() < rows;
    }
}
