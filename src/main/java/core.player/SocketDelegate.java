/**
 * 
 */
package core.player;

import core.game.Move;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;
import java.util.StringTokenizer;


public class SocketDelegate extends Player implements Observer{
	private final String name;
	PrintStream out = null;
	BufferedReader reader = null;
	private volatile String moveStr = null;
	
	public SocketDelegate(String name,Socket socket) {
		super();
		this.name = name;
		try {
			out = new PrintStream(socket.getOutputStream());
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Connected to a Server");
	}
	
	/* (non-Javadoc)
	 * @see core.player.Player#isManual()
	 */
	@Override
	public boolean isManual() {
		return false;
	}

	/* (non-Javadoc)
	 * @see core.player.Player#name()
	 */
	@Override
	public String name() {
		// TODO Auto-generated method stub
		return name;
	}

	/* (non-Javadoc)
	 * @see core.player.Player#findMove(core.game.Move)
	 */
	@Override
	//发送playchess命令
//	public Move findMove(Move opponentMove) {
//		if (opponentMove != null) sendMove(opponentMove);
//		Move move = null;
//		String message = "";
////		while(moveStr == null){
//			try {
//				message = reader.readLine();
//				System.out.println(message);
//				StringTokenizer stringTokenizer = new StringTokenizer(message, "@");
//				String flag = stringTokenizer.nextToken(); //标识符
//				if(flag.equals("Move")){
//					this.moveStr=stringTokenizer.nextToken();
//				}
//				System.out.println("SocketDelegate 74行，我接受了Move："+moveStr);
//
//			} catch (Exception e) {
//				closeConnection();
//					System.exit(0);
//			}
//		move = Move.parseMove(moveStr);
//		moveStr = null;
//		return move;
//	}


	public Move findMove(Move opponentMove) {//发送playchess命令
		if (opponentMove != null) sendMove(opponentMove);

		Move move = null;
		while(moveStr == null){

		}
		move = Move.parseMove(moveStr);
		moveStr = null;
		return move;
	}


	public void sendMove(Move currentMove) {
		// TODO Auto-generated method stub
		out.println("Move@" + currentMove);
		out.flush();
		System.out.println("SocketDelegate 89行，我发送了Move："+currentMove);
	}

	public void sendEnd(String endReason){
		out.println("End@" + endReason);
		out.flush();
		System.out.println("SocketDelegate 95行，我发送了结束原因："+endReason);
	}





	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		StringTokenizer stringTokenizer = new StringTokenizer((String)arg, "@");
		String flag = stringTokenizer.nextToken();
		if(flag.equals("Move")){
			this.moveStr=stringTokenizer.nextToken();
		}
	}

	public synchronized boolean closeConnection() {
		try {
			// 释放资源
			if (reader != null) {
				reader.close();
			}
			if (out != null) {
				out.close();
			}
			return true;
		} catch (IOException e1) {
			e1.printStackTrace();
			return false;
		}
	}


}
