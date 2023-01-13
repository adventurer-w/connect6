/**
 * 
 */
package entity;


public class GameInfo {
	private int gameId;
	private String whiteName;
	private String blackName;
	private String result;
	private String date;
	private String winerName;
	private int step;
	private String reason;
	
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public int getGameId() {
		return gameId;
	}
	public void setGameId(int gameId) {
		this.gameId = gameId;
	}
	public String getWhiteName() {
		return whiteName;
	}
	public void setWhiteName(String whiteName) {
		this.whiteName = whiteName;
	}
	public String getBlackName() {
		return blackName;
	}
	public void setBlackName(String blackName) {
		this.blackName = blackName;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getWinerName() {
		return winerName;
	}
	public void setWinerName(String winerName) {
		this.winerName = winerName;
	}
	public int getStep() {
		return step;
	}
	public void setStep(int step) {
		this.step = step;
	}
	
}
