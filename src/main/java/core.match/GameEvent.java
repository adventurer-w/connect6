package core.match;

import core.game.Game;
import core.game.GameResult;
import core.player.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class GameEvent {
    private final String name;
    private final ArrayList<Player> players;
    private final ArrayList<Match> matches;

    public String getName() {
        return this.name;
    }

    public GameEvent(String name) {
        this.players = new ArrayList<>();
        this.matches = new ArrayList<>();
        this.name = name;
    }

    /**
     * 为该项赛事添加参赛棋手
     * @param player 参赛棋手
     */
    public void addPlayer(Player player) {
        this.players.add(player);
    }

    public ArrayList<Player> getPlayers() {
        return this.players;
    }

    /**
     * 获取赛事的所有对局
     * @return 赛事的所有对局
     */
    private ArrayList<Game> getGames() {
        ArrayList<Game> games = new ArrayList<>();

        Iterator<Match> itrMatch = this.matches.iterator();
        while (itrMatch.hasNext()) {
            games.addAll(itrMatch.next().getGames());
        }

        return games;
    }

    /**
     * 按照单循环为所有的参赛棋手安排比赛。每场比赛(Match)进行gameNumbers个对局(Game)
     * @param gameNumbers   每场比赛的对局数
     */
    public void arrangeMatches(int gameNumbers) {
        this.matches.clear();
        int size = this.players.size();
        for (int i = 0; i < size - 1; i++) {
            for (int j = i + 1; j < size; j++)
                this.matches.add(new Match(gameNumbers, this.players.get(i), this.players.get(j)));
        }
    }

    /**
     * host和其他所有棋手的比赛
     * @param host
     * @param gameNumbers
     * @return
     * @throws CloneNotSupportedException
     */
    public ArrayList<Game> hostGames(Player host, int gameNumbers) throws CloneNotSupportedException {
        ArrayList<Game> games = new ArrayList<>();
        for (Player player : this.players) {
            if (!player.equals(host))
                for (int i = 0; i < gameNumbers; i++)
                    games.add(new Game((Player) host.clone(), player));
        }
        return games;
    }

    public void runSingleThread() {
        runSingleThread(getGames());
    }

    public void runSingleThread(ArrayList<Game> games) {
        for (Game game : games)
            game.run();
    }

    /**
     * 多线程模拟赛事的所有比赛
     * @param games
     */
    public void runMultiThread(ArrayList<Game> games) {
        ArrayList<Thread> gameThreads = new ArrayList<>();
        for (Game game : games) {
            gameThreads.add(game.start());
        }
        for (Thread thread : gameThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }
    }

    public void runMultiThread() {
        runMultiThread(getGames());
    }

    /**
     * 显示本次赛事的所有棋手的比赛结果
     */
    public void showResults() {
        Collections.sort(this.players);
        for (Player player : this.players) {
            System.out.print(player.name() + "(" + player.name() + "), ");
        }
        System.out.println();
        for (Player player : this.players) {
            System.out.println();
            System.out.println(player.name() + "(" + player.name() + ")");
            for (GameResult result : player.gameResults())
                System.out.println(result);
        }
    }

    /**
     * 显示本次赛事的每个棋手的比赛结果统计
     */
    public void showStatistics() {
        int size = this.players.size();
        System.out.println("Scores of players.");
        //成绩榜
        for (int i = 0; i < size; i++){
            System.out.println("\t" + this.players.get(i).name() + ": " + this.players.get(i).scores());
        }

        /**
         * 棋手之间的对局结果统计
         * 例如： Game Statistics (G03 vs G04)
         * 	         win: 304, lose: 173, draw: 23
         */
        for (int i = 0; i < size - 1; i++) {
            for (int j = i + 1; j < size; j++){
                this.players.get(i).showGameStatistics(this.players.get(j).name());
            }
        }
    }

//    public static void main(String[] args) {
//        GameEvent event = new GameEvent("Report01");
//        event.addPlayer(new g02.AI());
//        event.addPlayer(new g04.AI());
//        event.arrangeMatches(500);
//        event.runMultiThread();
//        event.showResults();
//        event.showStatistics();
//    }
}
