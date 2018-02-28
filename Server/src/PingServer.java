import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PingServer extends Thread {
    int port;
    ArrayList<HandleConnection> handler = null;
    private static String logpath = "log.txt";

    public PingServer() {
        port = 0;
    }

    public PingServer(int port) {
        this.port = port;
        handler = new ArrayList<>();
    }

    public void run() {
        File logfile = new File(logpath);

        try {
            FileWriter logfilewriter = new FileWriter(logfile, true);

            logfilewriter.write(LocalDateTime.now().toString() + ": PingServer Thread launched.\n");

            ServerSocket server = new ServerSocket(port);

            logfilewriter.write(LocalDateTime.now().toString() + ": ServerSocket created on port: " + port + ".\n");

            while (true) {
                Socket socket = server.accept();

                logfilewriter.write(LocalDateTime.now().toString() + ": Connection Accepted. Client INET address: " + socket.getInetAddress() + "\n");

                logfilewriter.flush();
                HandleConnection temp=new HandleConnection(socket);
                handler.add(temp);

                temp.start();

                for (HandleConnection h : handler
                        ) {
                    if (h.isAlive())
                        h.join();
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(PingServer.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
