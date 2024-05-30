import org.fusesource.jansi.AnsiConsole;

import java.util.TreeSet;

public class Util {
    public static String coordSetToString(ComparableTreeSet<Coord> set) {
        StringBuilder str = new StringBuilder();
        str.append('{');
        for (Coord c: set) {
            str.append(String.format(" <%d,%d> "), c.getCol(), c.getRow());
        }
        str.append('}');
        return str.toString();
    }
    public static String coordSetSetToString(TreeSet<ComparableTreeSet<Coord>> set) {
        StringBuilder str = new StringBuilder();
        for (ComparableTreeSet<Coord> tc: set) {
            str.append(coordSetToString(tc));
            str.append('\n');
        }
        return str.toString();
    }
    public static String printSlots(PieceColor[][] slots, boolean fancy) {
        StringBuilder str = new StringBuilder();
        int rows = slots[0].length;
        int cols = slots.length;
        if (fancy) {
            AnsiConsole.systemInstall();
            for (byte y = 0; y < rows; y++) {
                str.append(y);
                str.append('║');
                for (byte x = 0; x < cols; x++) {
                    str.append(slots[x][y].toString());
                }
                str.append("║\n");
            }
            str.append(' ');
            str.append('╚');
            for (byte x = 0; x < cols*2; x++) {
                str.append('═');
            }
            str.append("╝\n");
            str.append(' ');
            str.append(' ');
            for (byte x = 0; x < cols; x++) {
                str.append(x);
                str.append(" ");
            };
            AnsiConsole.systemUninstall();
            return str.toString();

        } else {
            for (byte y = 0; y < rows; y++) {
                for (byte x = 0; x < cols; x++) {
                    str.append(slots[x][y]);
                }
                str.append('\n');
            }
            return str.toString();
        }
    }
}
