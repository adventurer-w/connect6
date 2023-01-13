package core.game.ui;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Configuration {
    public static final int TIME_LIMIT ;
    public static final boolean GUI ;
    public static final int MAX_STEP;
    public static final String username;
    public static final String password;
    public static final String order;
    public static final String myplayer;
    public static final String otherplayer;
    public static final String hostIp;
    public static final int port;


    public Configuration() {
    }
    public void setInfo(String order,String myplayer,String otherplayer){
        Properties pps = new Properties();
        try {
            FileInputStream fis = new FileInputStream("file.properties");
            FileOutputStream fos = new FileOutputStream("file.properties");
            pps.setProperty("Order",order);
            pps.setProperty("MyPlayer",myplayer);
            pps.setProperty("OtherPlayer",otherplayer);

            pps.store(fos,null);



        }catch (IOException E){
            E.printStackTrace();
        }

    }

    static {
        Properties pps = new Properties();

        try {
            //pps.load(new FileInputStream("/file.properties"));
            pps.load(Configuration.class.getClassLoader().getResourceAsStream("file.properties"));
        } catch (IOException var2) {
            var2.printStackTrace();
        }

        TIME_LIMIT = Integer.parseInt(pps.getProperty("TimeLimit"));
        GUI = Boolean.parseBoolean(pps.getProperty("GUI"));
        MAX_STEP = Integer.parseInt(pps.getProperty("MaxStep"));
        username = pps.getProperty("username");
        password = pps.getProperty("password");
        order = pps.getProperty("Order");
        myplayer = pps.getProperty("MyPlayer");
        otherplayer = pps.getProperty("OtherPlayer");
        hostIp = pps.getProperty("HostIP");
        port = Integer.parseInt(pps.getProperty("Port"));

    }
}
