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
    int noOfClients;
    ArrayList<HandleConnection> handler = null;
    private static String logpath = "C://Users//Aranyak Ghosh//IdeaProjects//Module_1//Server//log.txt";

    public PingServer() {
        port = 0;
        noOfClients = 0;
    }

    public PingServer(int port, int noOfClients) {
        this.port = port;
        this.noOfClients = noOfClients;
        handler=new ArrayList<>();
    }

    public void run() {
        File logfile = new File(logpath);

        try {
            FileWriter logfilewriter = new FileWriter(logfile, true);

            logfilewriter.write(LocalDateTime.now().toString() + ": PingServer Thread launched.\n");

            ServerSocket server = new ServerSocket(port);

            logfilewriter.write(LocalDateTime.now().toString() + ": ServerSocket created on port: " + port + ".\n");

            Socket socket = server.accept();

            logfilewriter.write(LocalDateTime.now().toString() + ": Connection Accepted. Client INET address: " + socket.getInetAddress());

            logfilewriter.flush();
            logfilewriter.close();
            handler.add(new HandleConnection(socket));

        } catch (IOException ex) {
            Logger.getLogger(PingServer.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
    }
}
