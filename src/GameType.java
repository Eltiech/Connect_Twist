public enum GameType {
    FIRST_TO_SET {
        @Override public String toString() {
            return "First to make a set";
        }
    },
    SET_TOTAL {
        @Override public String toString() {
            return "Total set count";
        }
    }
}
