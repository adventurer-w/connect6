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


public class SocketPlayer extends Player implements Observer {
    private final String name;
    PrintStream out = null;
    BufferedReader reader = null;
    private String color;//识别黑棋白棋
    private volatile String moveStr = null;
    private String type;// 服务器间棋手对战、棋局回放

    public SocketPlayer(String name, Socket socket, String color, String type) {
        super();
        this.name = name;
        this.color = color;
        this.type = type;
        try {
            out = new PrintStream(socket.getOutputStream());
            InputStreamReader isr = new InputStreamReader(socket.getInputStream());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /* (non-Javadoc)
     * @see core.player.Player#isManual()
     */
    @Override
    public boolean isManual() {
        // TODO Auto-generated method stub
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

    public Move findMove(Move opponentMove) throws Exception {
        Move move = null;
        while (moveStr == null) {

        }
        move = Move.parseMove(moveStr);
        moveStr = null;
        sendNextPlayer();
        return move;
    }


    public void sendNextPlayer() {
        out.println(type + "@" + this.color);
        out.flush();
    }

    @Override
    public void update(Observable o, Object arg) {
        StringTokenizer stringTokenizer = new StringTokenizer((String) arg, "@");
        String flag = stringTokenizer.nextToken();
        if (flag.equals(type)) {
            String colorStr = stringTokenizer.nextToken();
            if (colorStr.equals(this.color)) {
                this.moveStr = stringTokenizer.nextToken();
                System.out.println("SocketPlayer:" + this.moveStr);
            }
        }
    }
}
