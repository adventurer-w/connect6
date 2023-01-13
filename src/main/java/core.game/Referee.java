package core.game;


import core.board.Board;
import core.board.PieceColor;
import core.player.Player;
import core.player.SocketDelegate;

import javax.swing.*;
import java.awt.*;


public class Referee {
    private final Board _board;
    private final Player _black;
    private final Player _white;
    private PieceColor _whoseMove;
    private String endReason;
    private int steps = 0;

    public Referee(Player black, Player white) {
        this._board = new Board();
        this._board.clear();
        this._black = black;
        this._white = white;
    }


    public Player whoseMove() {
        return (this._board.whoseMove() == PieceColor.WHITE) ? this._white : this._black;
    }


    public boolean gameOver() {
        return this._board.gameOver();
    }


    public void endingGame(String endReason, Player currPlayer, Move currMove) {
        this._black.stopTimer();
        this._white.stopTimer();
        if (currPlayer!=null && currPlayer instanceof SocketDelegate) {
            ((SocketDelegate) currPlayer).sendMove(currMove);
        }
        String gameResult = "";
        switch(endReason.hashCode()) {
            case 69:
                if (endReason.equals("E")) {
                    gameResult = currPlayer.name() + ":Chess player internal error";
                    if (currPlayer!=null && currPlayer instanceof Player) {
                        currPlayer = this.whoseMove();
                        System.out.println("第52行，当前棋手是"+currPlayer);
                        ((SocketDelegate) currPlayer).sendEnd("client fail");
                    }
                }
                break;
            case 70:
                if (endReason.equals("F")) {
                    gameResult = "success:" + this.getWinner();
                }
                break;
            case 77:
                if (endReason.equals("M")) {
                    gameResult = ":it ends in a draw";
                }
                break;
            case 78:
                if (endReason.equals("N")) {
                    gameResult = currPlayer.name() + ":Illegal walking";
                }
                break;
            case 84:
                if (endReason.equals("T")) {
                    gameResult = currPlayer.name() + ":overtime";
                }
                break;
        }

        //不是正常结束的时候要给服务器发消息





        JOptionPane.showMessageDialog((Component)null, gameResult, "对战结束", -1);
        this.endReason = gameResult;
        this.recordGame();
    }


    public boolean legalMove(Move move) {
        return this._board.legalMove(move);
    }


    public void recordMove(Move move) {
        this._board.makeMove(move);
        this.steps++;
    }


    private void recordGame() {
        GameResult result = new GameResult(this._black.name(), this._white.name(), getWinner(), this.steps,
                this.endReason);
        this._black.addGameResult(result);
        this._white.addGameResult(result);
        System.out.println(result.toString());
    }

    private String getWinner() {
        if ("M".equalsIgnoreCase(this.endReason)) {
            return "NONE";
        }
        return (this._board.whoseMove() == PieceColor.WHITE) ? this._black.name() : this._white.name();
    }

    public String gameTitle() {
        return this._black.name() + " vs " + this._white.name();
    }



}