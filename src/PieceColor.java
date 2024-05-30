import org.fusesource.jansi.AnsiConsole;
import org.fusesource.jansi.AnsiRenderer;

import static org.fusesource.jansi.Ansi.*;
import static org.fusesource.jansi.Ansi.Color.*;

public enum PieceColor {
    NONE {
        @Override
        public String toString() {
            if (AnsiConsole.isInstalled()) {
                return "Ｏ";//AnsiRenderer.render("⭕", "BLUE");
            } else {
                return "-";
            }

        }
    },
    RED {
        @Override
        public String toString() {
            if (AnsiConsole.isInstalled()) {
                return "\uD83D\uDD34";//AnsiRenderer.render("⭕", "BLUE");
            } else {
                return "R";
            }
        }
    },
    YELLOW {
        @Override
        public String toString() {
            if (AnsiConsole.isInstalled()) {
                return "\uD83D\uDFE1";//AnsiRenderer.render("⭕", "BLUE");
            } else {
                return "Y";
            }
        }
    }
}
