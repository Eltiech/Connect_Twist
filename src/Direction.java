public enum Direction {
    N(0,-1) {
        @Override
        public String toString() {return "N";}
    },
    NE(1,-1) {
        @Override
        public String toString() {return "NE";}
    },
    E(1,0) {
        @Override
        public String toString() {return "E";}
    },
    SE(1,1) {
        @Override
        public String toString() {return "SE";}
    },
    S(0,1) {
        @Override
        public String toString() {return "S";}
    },
    SW(-1,1) {
        @Override
        public String toString() {return "SW";}
    },
    W(-1,0) {
        @Override
        public String toString() {return "W";}
    },
    NW(-1,-1) {
        @Override
        public String toString() {return "NW";}
    };
    public final int x;
    public final int y;
    private Direction(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
