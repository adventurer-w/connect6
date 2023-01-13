package core.game;

import core.board.Board;
import core.board.PieceColor;
import core.game.timer.GameTimer;
import core.game.timer.TimerFactory;
import core.game.ui.BeautyGUI;
import core.game.ui.Configuration;
import core.game.ui.GameUI;
import core.game.ui.UiFactory;
import core.player.AI;
import core.player.Player;
import jagoclient.board.GoFrame;

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class Game extends Observable implements Observer, Runnable {
    private final Referee referee;
    private Thread me;
    int steps = 1;
    Move currMove = null;
    Player currPlayer = null;
    GameUI ui = null;
    String randomGameId;
    PrintWriter writer;

    public Game(Player black, Player white) {
        int timeLimit = Configuration.TIME_LIMIT;
        GameTimer blackTimer = TimerFactory.getTimer("Console", timeLimit);
        black.setTimer(blackTimer);
        GameTimer whiteTimer = TimerFactory.getTimer("Console", timeLimit);
        white.setTimer(whiteTimer);
        black.setColor(PieceColor.BLACK);
        white.setColor(PieceColor.WHITE);
        black.playGame(this);
        white.playGame(this);
        this.referee = new Referee(black, white);
        if (Configuration.GUI) {
            ui = UiFactory.getUi("GUI", this.referee.gameTitle());
            addObserver((Observer) ui);
        }
    }

    public Game(Player black, Player white, String randomGameId, PrintWriter writer) {

        this.randomGameId = randomGameId;
        this.writer = writer;

        int timeLimit = Configuration.TIME_LIMIT;
        GameTimer blackTimer = TimerFactory.getTimer("Console", timeLimit);
        black.setTimer(blackTimer);
        GameTimer whiteTimer = TimerFactory.getTimer("Console", timeLimit);
        white.setTimer(whiteTimer);
        black.setColor(PieceColor.BLACK);
        white.setColor(PieceColor.WHITE);
        black.playGame(this);
        white.playGame(this);
        this.referee = new Referee(black, white);
        if (Configuration.GUI) {
            ui = UiFactory.getUi("GUI", this.referee.gameTitle());
            addObserver((Observer) ui);
        }
    }

    public Thread start() {
        this.me = new Thread(this);
        this.me.start();
        return this.me;
    }

    public void run() {

        while (true) {

            GoFrame frame = null;
            if (ui instanceof BeautyGUI) {
                frame = ((BeautyGUI) ui).frame;
            }
            if (!frame.isVisible()) {
                if (this.writer != null) {
                    writer.println("CloseWindow@" + this.randomGameId);
                    writer.flush();
                }
                break;
            }

            Move move;
            if (Configuration.GUI) {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            currPlayer = this.referee.whoseMove();
            System.out.println("Game第97行，当前棋手是" + currPlayer);
            if (this.referee.gameOver()) {
                // 正常结束，分出胜负
                this.referee.endingGame("F", currPlayer, currMove);
                break;
            }
            if (steps > Configuration.MAX_STEP) {
                // 超出步数，判为平局
                this.referee.endingGame("M", currPlayer, currMove);
                break;
            }
            currPlayer.startTimer();
            try {
                move = currPlayer.findMove(currMove);
            } catch (Exception ex) {
                // 棋手出现异常情况
                this.referee.endingGame("E", currPlayer, null);
                break;
            }
            currPlayer.stopTimer();
            if (Thread.interrupted()) {
                break;
            }

            if (this.referee.legalMove(move)) {
                setChanged();
                notifyObservers(move);
            } else {
                System.out.println("非法走步：" + move);
                // 如果是非法走步（不反应到棋盘上）
                this.referee.endingGame("N", currPlayer, move);
                break;
                //非法走步告诉了服务器端
            }
            this.referee.recordMove(move);
            steps++;
            currMove = move;
        }
    }

    public void update(Observable arg0, Object arg1) {
        if (this.me != null)
            this.me.stop();
        this.referee.endingGame("T", null, null);
        //超时进行处理

    }

    /**
     * 将中断之前所走的move分别重新记录在player和refree中的棋盘上
     *
     * @param moves
     * @throws Exception
     */
    public void resumeGame(String[] moves, Class clientClass) {

        List<Move> moveLists = new ArrayList<>();
        Arrays.stream(moves).forEach((move) -> {
            moveLists.add(Move.parseMove(move));
        });

        try {
            /**
             * 通过反射获取到getBorad方法返回的board
             */
            AtomicReference<Move> lastMove = new AtomicReference<>();
            moveLists.stream().forEach((move) -> {
                // 通知更新棋盘
                setChanged();
                notifyObservers(move);
                currPlayer = referee.whoseMove();
                if (currPlayer instanceof AI) {
                    AI player = (AI) currPlayer;
                    if (lastMove.get() != null) player.resume(lastMove.get());
                    player.resume(lastMove.get());
                }
                referee.recordMove(move);
                currMove = move;
                lastMove.set(move);
                steps++;
            });

            if (currPlayer instanceof AI) {
                currMove = null;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}