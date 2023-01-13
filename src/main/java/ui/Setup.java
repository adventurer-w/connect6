package ui;

import core.game.Game;
import core.game.timer.GameTimer;
import core.game.ui.Configuration;
import core.player.Player;
import core.player.SocketDelegate;
import entity.GameInfo;
import entity.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * 1.输入显示登录棋手的完整类名
 * 2.选择本地或远程棋手进行对弈
 * (1)选择本地棋手，需要输入另一棋手的完整类名
 * (2)选择服务端棋手，则连接服务器
 */
public class Setup extends JFrame {
    // 我方棋手
    public static JTextField weClass;
    //对方棋手
    private JTextField otherClass;
    private JRadioButton localButton;
    private JRadioButton serverButton;
    private JRadioButton first;
    private JRadioButton later;
    private boolean isFirst;
    private Socket socket;
    private SocketDelegate serverPlayer;
    private String seletPlayer;//单选框选择的棋手

    // 列表框
    private JComboBox<String> chessTypeC, playerList;
    private String hostIp;
    private int port = 6666;
    private ArrayList<GameInfo> games;
    User user = new User();
    private BufferedReader reader;
    private PrintWriter writer;
    Player weplayer = null;


    public Setup() {
        // 设置登录窗口标题
        this.setTitle("棋手设置");
        // 去掉窗口的装饰（边框）
        // this.setUndecorated(true);
        // 采用指定的窗口装饰风格
        this.getRootPane().setWindowDecorationStyle(JRootPane.NONE);
        //窗体组件初始化
        init();
        //this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // 设置布局为绝对定位
        this.setLayout(null);
        this.setSize(355, 400);
        int screen_width = Toolkit.getDefaultToolkit().getScreenSize().width;
        int screen_height = Toolkit.getDefaultToolkit().getScreenSize().height;
        this.setLocation((screen_width - this.getWidth()) / 2, (screen_height - this.getHeight()) / 2);
        // 窗体大小不能改变
        this.setResizable(false);
        // 居中显示
        this.setLocationRelativeTo(null);
        // 窗体显示
        this.setVisible(true);
        //不获取焦点
        this.setFocusable(false);
        // this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    /**
     * 窗体组件初始化
     */
    public void init() {
        Container container = this.getContentPane();

        // 小容器
        JLabel chessType;
        JLabel we;
        JLabel other;
        JLabel title;
        JLabel example;

        chessType = new JLabel();
        chessType.setBounds(55, 10, 80, 30);
        chessType.setText("棋     类ࣺ");

        chessTypeC = new JComboBox<String>();
        chessTypeC.addItem("六子棋");
        chessTypeC.addItem("五子棋");
        chessTypeC.setBounds(120, 15, 70, 20);

        we = new JLabel();
        we.setBounds(55, 50, 80, 30);
        we.setText("我方棋手:");

        // 我方类名输入框
        weClass = new JTextField();
        weClass.setBounds(120, 50, 150, 30);

        first = new JRadioButton("先手");
        first.setBounds(120, 90, 70, 30);
        later = new JRadioButton("后手");
        later.setBounds(200, 90, 70, 30);
        //选择先手后手
        ButtonGroup group1 = new ButtonGroup();
        group1.add(first);
        group1.add(later);
        first.setSelected(true);

        other = new JLabel();
        other.setBounds(55, 140, 80, 30);
        other.setText("对方棋手:");

        localButton = new JRadioButton("Local");
        localButton.setBounds(120, 140, 70, 30);
        serverButton = new JRadioButton("Server");
        serverButton.setBounds(200, 140, 70, 30);

        ButtonGroup group2 = new ButtonGroup();
        group2.add(localButton);
        group2.add(serverButton);
        localButton.setSelected(true);

        //选择local事件
        localButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                otherClass.enable();
                otherClass.setBackground(new Color(255, 255, 255));
                playerList.setVisible(false);
            }
        });

        //选择server事件
        serverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                playerList.setVisible(true);
                // otherClass.disable();
                otherClass.setVisible(false);
                //otherClass.setBackground(new Color(205,201,201));
                try {
                    AllMes allmes = login();
                    String[] allplayer = allmes.allplayer;
                    socket = allmes.socket;
                    reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    for (int i = 1; i < allplayer.length; i++) {
                        //System.out.println(allplayer[i]);
                        playerList.addItem(allplayer[i]);
                    }
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });

        // 对方棋手类名输入框，从文件中读取
        otherClass = new JTextField();
        otherClass.setBounds(120, 180, 150, 30);

        playerList = new JComboBox<String>();
        playerList.setBounds(120, 220, 150, 25);
        playerList.setVisible(false);
        //选择棋手
        playerList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                seletPlayer = (String) playerList.getSelectedItem();
                otherClass.setText(seletPlayer);
            }
        });

        example = new JLabel("(输入格式如:baseline.player.AI)");
        example.setBounds(55, 255, 300, 30);
        example.setForeground(new Color(178, 48, 96));

        // 小按钮
        JButton confirm = new JButton("确认");
        // 设置字体和颜色和手形指针
        confirm.setFont(new Font("宋体", Font.PLAIN, 12));
        confirm.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        confirm.setBounds(80, 305, 60, 25);


        // 给确认按钮添加
        confirm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                weplayer = getPlayer(weClass);
                if (localButton.isSelected()) {
                    Player other = getPlayer(otherClass);
                    startGame(weplayer, other);
                }
                //getFrame().dispose(); 此处注释掉，防止确认后页面关闭
            }

            private void startGame(Player we, Player other) {
                int timeLimit = 30000;
                //我方的计时器
                GameTimer weTimer = new GameTimer(timeLimit);
                we.setTimer(weTimer);
                //地方的计时器
                GameTimer otherTimer = new GameTimer(timeLimit);
                other.setTimer(otherTimer);
                Game game = null;
                if (first.isSelected()) {
                    game = new Game(we, other);
                } else {
                    game = new Game(other, we);
                }
                game.start();
                // getFrame().setPlayerInfo();
            }
        });

        JButton cancel = new JButton("取消");
        // 设置字体颜色和手形指针
        cancel.setFont(new Font("宋体", Font.PLAIN, 12));
        cancel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cancel.setBounds(200, 305, 60, 25);

        // 给取消按钮添加
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (writer == null) {
                    getFrame().dispose();
                } else {
                    closeConnection();
                    getFrame().dispose();
                }
            }
        });

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (writer == null) {
                    getFrame().dispose();
                } else {
                    closeConnection();

                    System.exit(0);// 退出程序
                }

            }
        });


        container.add(chessType);
        container.add(chessTypeC);
        container.add(we);
        container.add(weClass);
        container.add(first);
        container.add(later);
        container.add(other);
        container.add(otherClass);
        container.add(confirm);
        container.add(cancel);
        container.add(localButton);
        container.add(serverButton);
        container.add(example);
        container.add(playerList);

        getPlayerInfo();
    }

    public Setup getFrame() {
        return this;
    }

    public static void main(String[] args) {
        new Setup();
    }

    class AllMes {
        private User user;
        private Socket socket;
        private String[] allplayer;
        private ArrayList<GameInfo> games;

        public Socket getSocket() {
            return socket;
        }

        public void setSocket(Socket socket) {
            this.socket = socket;
        }

        public String[] getAllplayer() {
            return allplayer;
        }

        public void setAllplayer(String[] allplayer) {
            this.allplayer = allplayer;
        }

        public ArrayList<GameInfo> getGames() {
            return games;
        }

        public void setGames(ArrayList<GameInfo> games) {
            this.games = games;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }
    }

    //连接服务器
    private Socket connectServer() {
        getUserInfo();
        getIPInfo();
        // 连接服务器
        try {
            System.out.println("hostIp=" + hostIp + "  port" + port);
            System.out.println(user.getUsername() + "@" + user.getPassword());

            Socket socket = new Socket(hostIp, port);
            System.out.println("socket"+socket);
            System.out.println(socket.hashCode() + "这里是connectServer连接服务器的socket");
            writer = new PrintWriter(socket.getOutputStream());
            // 发送客户端用户基本信息（用户名和ip地址）
            writer.println(user.getUsername() + "@" + user.getPassword() + "@" + socket.getLocalAddress().toString());
            writer.flush();
            // 开启接收消息的线程
            return socket;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "与端口号" + port + "    IP地址为" + hostIp + "   的服务器连接失败!" + "\r\n", "错误", JOptionPane.ERROR_MESSAGE);
            //e.printStackTrace();
            return null;
        }
    }

    /**
     * 登录获取棋手列表，用于在单选框显示
     */
    private AllMes login() {
        AllMes allmes = new AllMes();
        socket = connectServer();
        String[] allplayer = null;
        if (socket != null) {
            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter writer = new PrintWriter(socket.getOutputStream());
                games = new ArrayList<GameInfo>();
                String[] info = null;
                while (true) { //接收从服务端传来的棋局列表
                    info = reader.readLine().split("@");
                    if (info[0].equals("GameInfo")) {//GameInfo@gameId@blackName@whiteName@winerName@step@date@reason
                        GameInfo gameInfo = new GameInfo();
                        gameInfo.setGameId(Integer.parseInt(info[1]));
                        gameInfo.setBlackName(info[2]);
                        gameInfo.setWhiteName(info[3]);
                        gameInfo.setWinerName(info[4]);
                        gameInfo.setStep(Integer.parseInt(info[5]));
                        gameInfo.setDate(info[6]);
                        gameInfo.setReason(info[7]);
                        games.add(gameInfo);
                    } else {
                        break;
                    }
                }
                //此处时说明该用户已经登录，拒绝再次登录，进行弹窗处理
                if (info[0].equals("HADUSER")) {
                    JOptionPane.showMessageDialog(null, "该用户已登录，请勿重复登陆", "提示", JOptionPane.DEFAULT_OPTION);
                    closeConnection();
                    getFrame().dispose();
                } else {
                    if (info[0].equals("success")) {
                        user.setPlayerName(weClass.getText().trim());
                        user.setName(info[1]);
                        allplayer = reader.readLine().split("@");
                        String[] newAllPlayer = Arrays.copyOfRange(allplayer, 1, allplayer.length);
                        JOptionPane.showMessageDialog(null, "登录成功", "恭喜", JOptionPane.DEFAULT_OPTION);
                        isFirst = first.isSelected() ? true : false;
                        new HomePage(user, socket, newAllPlayer, games, isFirst, reader, writer);
                        getFrame().dispose();
                    } else {
                        JOptionPane.showMessageDialog(null, "用户名与密码不匹配", "错误", JOptionPane.ERROR_MESSAGE);
                        closeConnection();
                        getFrame().dispose();
                    }
                }
                allmes.user = user;
                allmes.socket = socket;
                allmes.games = games;
                allmes.allplayer = allplayer;
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return allmes;
    }

    private Player getPlayer(JTextField playerClassName) {
        if (playerClassName.getText().equals("")) {
            playerClassName.setBackground(new Color(250, 128, 114));
        } else {
            playerClassName.setBackground(new Color(255, 255, 255));
            try {
                return (Player) Class.forName(playerClassName.getText().trim()).newInstance();
            } catch (InstantiationException ex) {
                ex.printStackTrace();
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    private void getPlayerInfo() {
        String order = Configuration.order;
        if (order != null) {
            if (order.trim().equals("later")) {
                later.setSelected(true);
            }
            String myPlayer = Configuration.myplayer;
            if (myPlayer != null) {
                weClass.setText(myPlayer.trim());
                String otherPlayer = Configuration.otherplayer;
                if (otherPlayer != null) {
                    otherClass.setText(otherPlayer.trim());
                }
            }
        }
    }

    /**
     * 读取ip配置文件
     */
    private void getIPInfo() {
        this.hostIp = Configuration.hostIp;
        this.port = Configuration.port;
    }

    /**
     * 读取用户名和密码
     */
    private void getUserInfo() {
        user.setUsername(Configuration.username);
        user.setPassword(Configuration.password);
    }

    /**
     * 客户端主动关闭连接
     */
    public synchronized boolean closeConnection() {
        try {
            //发送断开连接命令给服务器
            writer.println("CLOSE");
            writer.flush();
            // 释放资源
            if (reader != null) {
                reader.close();
            }
            if (writer != null) {
                writer.close();
            }
            if (socket != null) {
                socket.close();
            }
            return true;
        } catch (IOException e1) {
            e1.printStackTrace();
            return false;
        }
    }
}
