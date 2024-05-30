public class GameEvent {

    //there are better ways of doing event messaging, however given time
    //present time constraints, this was easiest to implement without
    //additional research time
    //todo:investigate replacing this with builder pattern

    GameEvent(EventType e) {
        eventType = e;
    }

    //original plan was to have some of these constructors
    //handle multiple event types...I suppose passing Objects
    //and casting could do that..
    GameEvent(EventType e, PlayerNumber pn, byte b) {
        eventType = e;
        //nothing else will ever GameEvent with these args (yet), but whatever
        switch(e) {
            case PLACE_PIECE:
                playerNumber = pn;
                column = b;
                break;
        }
        return;
    }
    GameEvent(EventType e, byte columns, byte rows, GameType gameType,
              short timerLength, String p1Name, PieceColor p1Color,
              String p2Name, PieceColor p2Color) {
        eventType = e;
        //nothing else will ever GameEvent with these args (yet), but whatever
        switch(e) {
            case CREATE_GAME:
                this.column = columns;
                this.row = rows;
                this.gameType = gameType;
                this.timerLength = timerLength;
                this.p1Name = p1Name;
                this.p1Color = p1Color;
                this.p2Name = p2Name;
                this.p2Color = p2Color;
                break;
        }
        return;
    }
    GameEvent(EventType e, short s) {
        eventType = e;
        //nothing else will ever GameEvent with these args (yet), but whatever
        switch(e) {
            case TIME:
                currentTime = s;
                break;
        }
        return;
    }
    GameEvent(EventType e, PlayerNumber pn) {
        eventType = e;
        //nothing else will ever GameEvent with these args (yet), but whatever
        switch(e) {
            case TURN:
                playerNumber = pn;
                break;
        }
        return;
    }
    GameEvent(EventType e, PieceColor c) {
        eventType = e;
    }

    GameEvent(EventType e, GameOutcome outcome, Player p1, Player p2) {
        eventType = e;
        //nothing else will ever GameEvent with these args (yet), but whatever
        //This might not be used yet. In the event of a total-sets-of-four
        //game however, a client may wish to display the sets that the loser
        //had as well.
        switch(e) {
            case GAME_OVER:
                this.player1 = p1;
                this.player2 = p2;
                this.gameOutcome = outcome;
                break;
        }
        return;
    }
    GameEvent(EventType e, PieceColor[][] slots) {
        eventType = e;
        //nothing else will ever GameEvent with these args (yet), but whatever
        switch(e) {
            case UPDATE_SLOTS:
                this.slots = slots;
//                this.slots = new Color[slots.length][];
//                for(int i = 0; i < slots.length; i++) {
//                    this.slots[i] = slots[i].clone();
//                }
                break;
        }
        return;
    }
    private EventType eventType;
    public EventType type(){ return eventType; }

    private byte setLength = 4;
    public byte getSetLength() { return setLength; }

    private GameType gameType;
    public GameType getGameType(){ return gameType; }

    private short timerLength = -1;
    public short getTimerLength(){ return timerLength;}

    private short currentTime;
    public short getCurrentTime() { return currentTime; };

    private Player player1 = null;
    public Player getPlayer1(){ return player1;}
    
    private Player player2 = null;
    public Player getPlayer2(){ return player2;}

    private GameOutcome gameOutcome = null;
    public GameOutcome getGameOutcome(){ return gameOutcome;}

    private PlayerNumber playerNumber = null;
    public PlayerNumber getPlayerNumber(){ return playerNumber;}

    private PieceColor[][] slots = null;
    public PieceColor[][] getSlots() { return slots; }

    private byte column = -1;
    public byte getColumn() { return column; }

    private byte row = -1;
    public byte getRow() { return row; }

    private String p1Name = null;
    public String getP1Name() { return p1Name; }

    private PieceColor p1Color = null;
    public PieceColor getP1Color() { return p1Color; }

    private String p2Name = null;
    public String getP2Name() { return p2Name; }

    private PieceColor p2Color = null;
    public PieceColor getP2Color() { return p2Color; }

    private PieceColor color = null;
    public PieceColor getColor() { return color; }
}
