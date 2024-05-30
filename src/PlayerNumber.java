import org.fusesource.jansi.AnsiConsole;

public enum PlayerNumber {
    PLAYER_1{
        @Override
        public String toString() {
            return "Player 1";
        }
    },
    PLAYER_2{
        @Override
        public String toString() {
            return "Player 2";
        }
    }
}
