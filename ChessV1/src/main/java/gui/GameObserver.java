package gui;

import java.util.Observer;

public interface GameObserver extends Observer {
    void makeBotMove(boolean isWhite);
}