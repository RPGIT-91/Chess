package gui;

import java.util.Observable;

public class BotSetting extends Observable{
	private boolean whiteBotEnabled;
    private boolean blackBotEnabled;
    
    private static int nextFrom;
    private static int nextTo;

    public BotSetting() {
        this.whiteBotEnabled = false;
        this.blackBotEnabled = false;
    }

    public boolean isWhiteBotEnabled() {
        return whiteBotEnabled;
    }

    public void setWhiteBotEnabled(boolean whiteBotEnabled) {
        this.whiteBotEnabled = whiteBotEnabled;
        setChanged();
        notifyObservers();
    }

    public boolean isBlackBotEnabled() {
        return blackBotEnabled;
    }

    public void setBlackBotEnabled(boolean blackBotEnabled) {
        this.blackBotEnabled = blackBotEnabled;
        setChanged();
        notifyObservers();
    }
    
	public static void setNextFrom(int next) {
		nextFrom = next;
	}
	
	public static int getNextFrom() {
		return nextFrom;
	}
	
	public static void setNextTo(int to) {
		nextTo = to;
	}
	
	public static int getNextTo() {
		return nextTo;
	}
	
}
