package ui;

import org.apache.commons.io.IOUtils;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.Socket;
import java.util.Properties;

public class UploadJar extends JFrame {

    private String hostIp = null;
    private int port = 6668;

    private JTextField className;//AI名字（Jar包名字）
    private JTextField AIName;//AI全类名
    private JTextField fileAddress;//选择的文件名称
    private JLabel example;//示例文字
    private String filepath = null;//文件地址ַ
    File[] arrfiles = null;//发送的文件

    private String username;

    public UploadJar(String username) {
        this.username  = username;
        this.setTitle("上传文件");
        init();
        // 设置布局为绝对定位
        this.setLayout(null);
        this.setSize(360, 340);
        int screen_width = Toolkit.getDefaultToolkit().getScreenSize().width;
        int screen_height = Toolkit.getDefaultToolkit().getScreenSize().height;
        this.setLocation((screen_width - this.getWidth()) / 2, (screen_height - this.getHeight()) / 2);
        // 窗体大小不能改变
        this.setResizable(false);
        // 居中显示
        this.setLocationRelativeTo(null);
        // 设置窗体可见
        this.setVisible(true);
        //close的方式，隐藏并释放窗体
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    /**
     * 窗体组件初始化
     */
    public void init() {
        Container container = this.getContentPane();

        /**
         * 上传文件
         */
        fileAddress = new JTextField("请选择您的文件");
        fileAddress.setBounds(40, 40, 180, 30);
        fileAddress.setEnabled(false);
        JButton fileChoose = new JButton("选择");
        fileChoose.setFont(new Font("宋体", Font.PLAIN, 12));
        fileChoose.setBounds(240, 40, 80, 25);

        /**
         * 自动识别AI名字
         */
        JLabel CName = new JLabel();
        CName.setBounds(40, 100, 130, 30);
        CName.setText("jar包名(自动识别)：");
        className = new JTextField();
        className.setEnabled(false);
        className.setBounds(170, 100, 150, 30);

        /**
         * 输入AI全类名
         */
        JLabel AName = new JLabel();
        AName.setBounds(40, 165, 130, 30);
        AName.setText("请输入AI全类名：");
        AIName = new JTextField("如:baseline.player.AI");
        AIName.setBounds(170, 167, 150, 30);


        example = new JLabel();
        example.setBounds(55, 205, 300, 30);
        example.setForeground(new Color(178, 48, 96));

        JButton confirm = new JButton("确定");
        confirm.setFont(new Font("宋体", Font.PLAIN, 12));
        confirm.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        confirm.setBounds(30, 250, 140, 25);
        JButton cancel = new JButton("取消");
        cancel.setFont(new Font("宋体", Font.PLAIN, 12));
        cancel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cancel.setBounds(190, 250, 140, 25);


        container.add(CName);
        container.add(className);
        container.add(AName);
        container.add(AIName);
        container.add(fileAddress);
        container.add(fileChoose);
        container.add(example);
        container.add(confirm);
        container.add(cancel);


        // 给取消按钮添加事件
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getFrame().dispose();
            }
        });

        // 给选择文件按钮添加事件
        fileChoose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 设置选择器
                JFileChooser chooser = new JFileChooser();
                // 设置多选
                chooser.setMultiSelectionEnabled(true);
                //过滤文件类型
                FileNameExtensionFilter filter = new FileNameExtensionFilter("jar",
                        "jar", "txt");
                chooser.setFileFilter(filter);
                //是否打开文件选择框
                int returnVal = chooser.showOpenDialog(confirm);
                if (returnVal == JFileChooser.APPROVE_OPTION) {   //如果符合文件类型
                    filepath = chooser.getSelectedFile().getAbsolutePath();  //获取绝对路径
                    arrfiles = chooser.getSelectedFiles();    //得到选择的文件
                    if (arrfiles == null || arrfiles.length == 0) {
                        return;
                    }
                    fileAddress.setText(filepath);
                    String jarFullName = chooser.getSelectedFile().getName();
                    String jarName = jarFullName.substring(0, jarFullName.lastIndexOf('.'));
                    className.setText(jarName);
                }
            }
        });

        // 给提交按钮添加点击事件
        confirm.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                try {

                    eventOnImport(new JButton());
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("5555");
                }
            }
        });

    }

    private void eventOnImport(JButton confirm) throws IOException {
        if (className.getText().equals("") || AIName.getText().equals("") || fileAddress.getText().equals("")) {
            example.setText("请检查所有内容填写完毕后再提交");
            //className.setBackground(new Color(250,128,114));
        } else {
            getIPInfo();
            //建立socket服务
            Socket fileSocket = new Socket(hostIp, port);//
            System.out.println(port + "端口已连接成功");
            OutputStream socketOutputStream = fileSocket.getOutputStream();
            PrintWriter writer = new PrintWriter(socketOutputStream);
            // 发送用户输入的基本信息（jar包名字、AI类名、上传地址）
            writer.println(this.username +"@"+className.getText() + "@" + AIName.getText() + "@" + fileAddress.getText());
            writer.flush();

            //接收服务端信息
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileSocket.getInputStream()));
            String SeverMes = null;

            if (arrfiles != null) {

                FileInputStream input = null;
                try {
                    for (File f : arrfiles) {
                        input = new FileInputStream(f);
                        IOUtils.copy(input, socketOutputStream);
                        fileSocket.shutdownOutput();
                    }
                    SeverMes = reader.readLine();// 接收客户端消息
                    if (SeverMes.equals("UPLOADOK")) {
                        System.out.println(SeverMes);
                        JOptionPane.showMessageDialog(null, "上传成功!", "提示",
                                JOptionPane.INFORMATION_MESSAGE);
                        getFrame().dispose();
                        input.close();
                        fileSocket.close();
                    }
                } catch (FileNotFoundException e1) {
                    JOptionPane.showMessageDialog(null, "上传失败!", "提示",
                            JOptionPane.ERROR_MESSAGE);
                    e1.printStackTrace();
                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(null, "上传失败!", "提示",
                            JOptionPane.ERROR_MESSAGE);
                    e1.printStackTrace();
                }
            }
        }
    }

    public UploadJar getFrame() {
        return this;
    }


    //读取ip设置文件
    private void getIPInfo() throws IOException {
        InputStream in = null;
        try {
            in = this.getClass().getClassLoader().getResourceAsStream("file.properties");
            Properties properties = new Properties();
            properties.load(in);
            this.hostIp = properties.getProperty("HostIP");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            in.close();
        }
    }


}



