package core.game.ui;

import jagoclient.Global;

public class UiFactory {
	
	static {

    	Global.setApplet(false);
		Global.home(System.getProperty("user.home"));
		Global.readparameter(".go.cfg");
		Global.createfonts();
	}
	
	public static GameUI getUi(String type, String title) {
		return new BeautyGUI(title);
	}
}
