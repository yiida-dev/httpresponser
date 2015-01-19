package yiida.httpresponser;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import yiida.httpresponser.net.HttpServer;
import yiida.httpresponser.net.HttpsServer;
import yiida.util.Arguments;

public class HttpResponser {

    private HttpServer server;
    private MenuItem menuItemRun;
    private MenuItem menuItemStop;
    TrayIcon trayIcon;

    private HttpResponser() {
        server = new HttpsServer(1, 34441);
        server.setResponseHandler(new UserInputResponseHandler());
    }

    private HttpResponser(HttpServer server) {
        this.server = server;
    }

    public static void main(String[] args) throws Exception {
        if(!SystemTray.isSupported()) {
            System.out.println("Sorry, this program run in tray only.");
            return;
        }
        HttpServer server = null;
        Arguments arguments = new Arguments(args);
        if(arguments.hasOption("https")) {
            server = new HttpsServer();
        } else {
            server = new HttpServer();
        }
        String portValue = arguments.getOptionValue("port");
        int port = 34441;
        if(portValue != null) {
            try {
                int p = Integer.valueOf(portValue);
                if(p > 0 && p <= 65535) port = p;
            } catch(NumberFormatException e) {
                e.printStackTrace();
            }
        }
        server.setPort(port);
        server.setResponseHandler(new UserInputResponseHandler());
        HttpResponser responser = new HttpResponser(server);
        responser.runInTray();
        System.out.println("main thread done.");
    }


    private void runInTray() {
        PopupMenu menu = new PopupMenu();
        menuItemRun = new MenuItem("Run", new MenuShortcut(KeyEvent.VK_R, false));
        menuItemRun.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                try {
                    server.run();
                } catch(IOException e) {
                    e.printStackTrace();
                    return;
                }
                menuItemRun.setEnabled(false);
                menuItemStop.setEnabled(true);
            }
        });
        menuItemStop = new MenuItem("Stop", new MenuShortcut(KeyEvent.VK_S, false));
        menuItemStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                server.stop();
                menuItemRun.setEnabled(true);
                menuItemStop.setEnabled(false);
            }
        });
        MenuItem menuItemClose = new MenuItem("Close", new MenuShortcut(KeyEvent.VK_C, false));
        menuItemClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                server.stop();
                SystemTray.getSystemTray().remove(trayIcon);

                System.exit(0);
            }
        });
        menuItemRun.setEnabled(true);
        menuItemStop.setEnabled(false);
        menu.add(menuItemRun);
        menu.add(menuItemStop);
        menu.add(menuItemClose);

        URL resource = getClass().getResource("someicon2.png");
        Image img = null;
        try {
            img = ImageIO.read(resource);
        } catch(IOException e) {
            throw new RuntimeException();
        }
        trayIcon = new TrayIcon(img, "HttpResponser", menu);
        try {
            SystemTray.getSystemTray().add(trayIcon);
        } catch(AWTException e) {
            throw new RuntimeException();
        }

    }
}
