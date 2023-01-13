package ui;

import core.game.Game;
import core.player.OnlinePlayer;
import core.player.Player;
import ui.HomePage.MessageThread;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public class OnlineBattle extends JFrame {

    // 小容器
    private JLabel opponent,me;
    private JComboBox<String> opponentPlayer;
    // 小按钮
    private JButton cancel;
    private JButton confirm;

    private JRadioButton first;
    private JRadioButton later;

    private String[] onlinePlayerList;
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private Player clientPlayer;

    private MessageThread mt;

    public OnlineBattle(String[] onlineplayer, Socket socket, MessageThread mt, Player clientPlayer, PrintWriter writer, BufferedReader reader) {
        this.onlinePlayerList = onlineplayer;
        this.socket = socket;
        this.mt = mt;
        this.clientPlayer = clientPlayer;
        this.writer = writer;
        this.reader = reader;

        // 设置登录窗口标题
        this.setTitle("在线棋手对战");
        // 去掉窗口的装饰(边框)
        // this.setUndecorated(true);
        // 采用指定的窗口装饰风格
        this.getRootPane().setWindowDecorationStyle(JRootPane.NONE);
        // 窗体组件初始化
        init();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // 设置布局为绝对定位
        this.setLayout(null);
        this.setSize(355, 340);
        int screen_width = Toolkit.getDefaultToolkit().getScreenSize().width;
        int screen_height = Toolkit.getDefaultToolkit().getScreenSize().height;
        this.setLocation((screen_width - this.getWidth()) / 2, (screen_height - this.getHeight()) / 2);
        // 窗体大小不能改变
        this.setResizable(false);
        // 居中显示
        this.setLocationRelativeTo(null);
        // 窗体显示
        this.setVisible(true);
        // 不获取焦点
        this.setFocusable(false);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    /**
     * 窗体组件初始化
     */
    public void init() {
        Container container = this.getContentPane();

        me = new JLabel();
        me.setBounds(55, 60, 80, 30);
        me.setText("我方棋手：");
        first = new JRadioButton("先手");
        first.setBounds(120, 60, 70, 30);
        later = new JRadioButton("后手");
        later.setBounds(200, 60, 70, 30);
        //选择先手后手
        ButtonGroup group1 = new ButtonGroup();
        group1.add(first);
        group1.add(later);
        first.setSelected(true);

        opponent = new JLabel();
        opponent.setBounds(55, 150, 80, 30);
        opponent.setText("对方棋手：");
        if(onlinePlayerList.length == 0){
            opponentPlayer = new JComboBox<>(new String[]{"当前无在线用户"});
            opponentPlayer.setBounds(130, 150, 120, 30);
            opponentPlayer.setFocusable(false);
            cancel = new JButton("取消");
            // 设置字体和颜色和手形指针
            cancel.setFont(new Font("宋体", Font.PLAIN, 12));
            cancel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            cancel.setBounds(150, 250, 60, 25);
            // 给按钮添加
            cancel.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    getFrame().dispose();
                }
            });
            container.add(first);
            container.add(later);
            container.add(cancel);
            container.add(opponent);
            container.add(me);
            container.add(opponentPlayer);
        }else{
            opponentPlayer = new JComboBox<>(onlinePlayerList);
            opponentPlayer.setBounds(130, 150, 120, 30);
            confirm = new JButton("确认");
            // 设置字体和颜色和手形指针
            confirm.setFont(new Font("宋体", Font.PLAIN, 12));
            confirm.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            confirm.setBounds(80, 250, 60, 25);
            // 给按钮添加
            confirm.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    //将选中的用户发送给服务端
                    String blackPlayerName;
                    String whitePlayerName;
                    if (first.isSelected()) {
                        blackPlayerName = clientPlayer.name();
                        whitePlayerName = (String) opponentPlayer.getSelectedItem();
                        // 向服务端发信号
                        //应该发送选中的用户名和选中用户的先后手情况
                        writer.println("ONLINEGAME@" + "white" + "@" + whitePlayerName + "@" + blackPlayerName);
                        writer.flush();
                        // 开启服务端下棋监听
                        //SocketPlayer white = new SocketPlayer(whitePlayerName, socket, "white", "ONLINEGAME");
                        OnlinePlayer white = new OnlinePlayer(whitePlayerName, socket);
                        mt.addObserver(white);
                        Game game = new Game(clientPlayer, white);
                        game.start();
                    } else {
                        blackPlayerName = (String) opponentPlayer.getSelectedItem();
                        whitePlayerName = clientPlayer.name();
                        // 向服务端发信号 开始下棋
                        writer.println("ONLINEGAME@" + "black" + "@" + blackPlayerName + "@" + whitePlayerName);
                        writer.flush();
                        // 开启服务端下棋监听
                        OnlinePlayer black = new OnlinePlayer(blackPlayerName, socket);
                        mt.addObserver(black);
                        Game game = new Game(black, clientPlayer);
                        game.start();
                    }
                    System.out.println("本次在线对战选择的黑白棋手分别是：" + blackPlayerName + "@" + whitePlayerName);
                    getFrame().dispose();//关掉选择棋手的页面
                }
            });

            cancel = new JButton("取消");
            // 设置字体和颜色和手形指针
            cancel.setFont(new Font("宋体", Font.PLAIN, 12));
            cancel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            cancel.setBounds(200, 250, 60, 25);
            // 给按钮添加
            cancel.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    getFrame().dispose();
                }
            });
            // 所有组件用容器装载
            container.add(first);
            container.add(later);
            container.add(confirm);
            container.add(cancel);
            container.add(opponent);
            container.add(me);
            container.add(opponentPlayer);
        }


    }


    public OnlineBattle getFrame() {
        return this;
    }
}
