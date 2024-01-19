package gui;

import java.util.Observer;

public interface GameObserver extends Observer {
	/**
	 * Implements the move of the bot.
	 */
    void makeBotMove();
}