import java.util.TreeSet;

public class Player {
    public Player(PieceColor c, PlayerNumber num) {
        this("anonymous", c, num);
    }
    public Player(String name, PieceColor c, PlayerNumber num) {
        this.color = c;
        this.name = name;
        this.playerNumber = num;
        sets = new TreeSet<ComparableTreeSet<Coord>>();
    }
    public PieceColor getColor() {
        return color;
    }
    public String getName() {
        return name;
    }
    public PlayerNumber getPlayerNumber() {
        return playerNumber;
    }


    private PieceColor color;
    private String name;
    private PlayerNumber playerNumber;

    private TreeSet<ComparableTreeSet<Coord>> sets;
    public TreeSet<ComparableTreeSet<Coord>> getSets() {
        return sets;
    }
    //byte getMove();
}
//abstract class Player {
//    public Player(Color c) {
//        color = c;
//        name = "Anonymous";
//    }
//    public Player(Color c, String name) {
//        color = c;
//        this.name = name;
//    }
//    public Color getColor() {
//        return color;
//    }
//    public String getName() {
//        return name;
//    }
//    private Color color;
//    private String name;
//    private TreeSet<TreeSet<Coord>> sets;
//    public TreeSet<TreeSet<Coord>> getSets() {
//        return sets;
//    }
//    abstract byte getMove();
//}
