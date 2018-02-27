import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class HandleConnection extends Thread {

    private Socket socket;
    private static String logpath = "C://Users//Aranyak Ghosh//IdeaProjects//Module_1//Server//log.txt";
    private File logfile;
    private FileWriter logWriter;

    public HandleConnection() {
        this.socket = null;
    }

    public HandleConnection(Socket socket) {
        this.socket = socket;
        logfile = new File(logpath);
        try {
            logWriter = new FileWriter(logfile, true);
            logWriter.write(LocalDateTime.now().toString() + ": Connection Handler object created for client " + socket.getInetAddress());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private String createHeader(String payload, boolean success) {
        int contentLength = payload.length();

        String date = (new Date()).toString();

        String serverName = socket.getInetAddress().toString();

        boolean keepAlive;

        String keepAliveS = "closed";

        try {

            keepAlive = socket.getKeepAlive();

            if (keepAlive)
                keepAliveS = "keep-alive";

        } catch (SocketException e) {

            e.printStackTrace();

        }
        String reHeaders = null;
        if (success) {
            reHeaders = "HTTP/1.1 200 OK\r\nContent-type = text/html\r\nConnection = " + keepAliveS + "\r\nServer = "
                    + serverName + "\r\nContent-Length = " + contentLength + "\r\nDate = " + date + "\r\n\r\n";
            try {
                logWriter.write(LocalDateTime.now().toString() + ": HTTP 200 header created");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            reHeaders = "HTTP/1.1 404 NOT FOUND\r\nContent-type = text/html\r\nConnection = " + keepAliveS
                    + "\r\nServer = " + serverName + "\r\nContent-Length = " + contentLength + "\r\nDate = " + date
                    + "\r\n\r\n";
            try {
                logWriter.write(LocalDateTime.now().toString() + ": HTTP 404 header created");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return reHeaders;
    }

    private String ReadFile(String path) {
        String payload = "";

        /*
        * Front End directory
        * */
        String directory = "C://Users//Aranyak Ghosh//IdeaProjects//Module_1//front-end";

        /*
        * Filenames
        * */
        String homepage = "//homepage.html";
        String login = "//login.html";
        String signup = "//signup.html";
        String fourofour = "//fourofour.html";


        File infile = null;

        if (path.equals("/")) {
            infile = new File(directory + homepage);
        } else if (path.equalsIgnoreCase("//login")) {
            infile = new File(directory + login);
        } else if (path.equalsIgnoreCase("//signup")) {
            infile = new File(directory + signup);
        } else {
            infile = new File(directory + fourofour);
        }
        try {
            logWriter.write(LocalDateTime.now() + ": File requested: " + path);
            logWriter.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        try {
            FileReader in = new FileReader(infile);
            int c;
            while ((c = in.read()) != -1)
                payload += (char) c;
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return payload;
    }

    public void run() {
        try {
            logWriter.write(LocalDateTime.now().toString() + ": Handle Connection thread running");
            OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream());
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String request = "";

            String inputString = "";
            int length = 0;
            while (!(inputString = in.readLine()).equals("")) {
                request = request + inputString + "\r\n";
                if (inputString.contains("Content-Length:")) {
                    length = Integer.parseInt(inputString.substring(inputString.indexOf("Content-Length:") + 16, inputString.length()));
                }
            }

            String[] reqheader = request.split("\n");
            if (reqheader[0].startsWith("GET")) {
                String path = reqheader[0].split(" ")[1].trim();
                String payload = ReadFile(path);
                String resheader = createHeader(payload, !payload.contains("404"));

                logWriter.write(LocalDateTime.now().toString() + ": Get request received from client");
                /*
                * Handle cookies
                * */


                out.write(resheader + payload + "\r\n");
                out.flush();
            } else if (reqheader[0].startsWith("POST")) {
                String payload = "";
                for (int i = 0; i < length; i++) {
                    int c = in.read();
                    payload += (char) c;
                }

                ArrayList<String> fields = new ArrayList<String>(Arrays.asList(payload.split("&")));
                if (fields.size() > 2) {
                    //Register

                    for (String s : fields
                            ) {
                        String fieldname = s.split("=", 2)[0];
                        String fieldvalue = s.split("=", 2)[1];

                        
                    }
                    UserInfo user = new UserInfo();
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}

