import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HandleConnection extends Thread {

    Socket socket;

    public HandleConnection() {
        this.socket = null;
    }

    public HandleConnection(Socket socket) {
        this.socket = socket;
    }

    static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
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

            // TODO Auto-generated catch block

            e.printStackTrace();

        }
        String reHeaders = null;
        if (success) {
            reHeaders = "HTTP/1.1 200 OK\r\nContent-type = text/html\r\nConnection = " + keepAliveS + "\r\nServer = "
                    + serverName + "\r\nContent-Length = " + contentLength + "\r\nDate = " + date + "\r\n\r\n";
        } else {
            reHeaders = "HTTP/1.1 404 NOT FOUND\r\nContent-type = text/html\r\nConnection = " + keepAliveS
                    + "\r\nServer = " + serverName + "\r\nContent-Length = " + contentLength + "\r\nDate = " + date
                    + "\r\n\r\n";
        }
        return reHeaders;

    }

    public void run() {
        try {

            System.out.println("HandleConnection thread started");

            System.out.println("Proceesing client connection");
            while (true) {

                System.out.println("Proceesing client input");

                OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream()); // once connection is
                // established, grab output
                // stream
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // grab input
                // stream

                int c;
                String request = "";
				/*while ((c = in.read())!=-1) {
					request += (char)c;
				}*/

                String inputString="";

                while (!(inputString = in.readLine()).equals("")) {
                    request = request+inputString+"\r\n";
                }

                if(in.ready())
                    inputString=in.readLine();
//				inputString=in.readLine();
                if (request.startsWith("GET")) {
                    try {
                        String[] req = request.split(" ");
                        String filepath = "C:\\Users\\Aranyak Ghosh\\eclipse-java-workspace\\COE49407\\Server\\src";

                        File infile = null;
                        if (req[1].trim().equals("/")) {
                            infile = new File(filepath + "\\index.html");
                        } else {
                            infile = new File(filepath + req[1]);
                        }
                        if (infile.exists()) {
                            // Header for file
                            FileReader fin = new FileReader(infile);
                            out.flush();

                            BufferedReader read = new BufferedReader(fin);
                            // out.write("\r\n");
                            String filetext;
                            String file = "";
                            while ((filetext = read.readLine()) != null) {
                                // out.write(filetext + "\n");
                                file += filetext;
                                file += "\n";
                            }

                            out.write(createHeader(file, true));
                            out.write(file);
                            out.write("\r\n");
                            out.flush();
                        } else {

                            infile = new File(filepath + "\\nf.html");

                            FileReader fin = new FileReader(infile);
                            out.flush();

                            BufferedReader read = new BufferedReader(fin);

                            String filetext;
                            String file = "";
                            while ((filetext = read.readLine()) != null) {

                                file += filetext;
                                file += "\n";
                            }

                            out.write(createHeader(file, true));
                            out.write(file);
                            out.write("\r\n");
                            out.flush();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (request.startsWith("POST")) {
                    String filepath = "C:\\Users\\Aranyak Ghosh\\eclipse-java-workspace\\COE49407\\Server\\src";

                    //System.out.println(" post");
                    File response = new File(filepath + "\\formrec.html");
                    FileReader fin = new FileReader(response);


                    String name=inputString;
                    System.out.println(name);
                    BufferedReader read = new BufferedReader(fin);
                    String filetext;
                    String file = "";
                    while ((filetext = read.readLine()) != null) {
                        file += filetext;
                        file += "\n";
                        if (filetext.contains("<p>")) {
                            file += name;
                            file += "\n";
                        }
                    }

                    out.write(createHeader(file, true));
                    out.write(file);
                    out.write("\r\n");
                    out.flush();

                } else if (request.startsWith("OPTIONS")) {

                } else if (request.startsWith("HEAD")) {

                    try {
                        String[] req = request.split(" ");
                        String filepath = "C:\\Users\\profile\\workspace\\Server\\src";

                        File infile = null;
                        if (req[1].trim().equals("/")) {
                            infile = new File(filepath + "\\index.html");
                        } else {
                            infile = new File(filepath + req[1]);
                        }
                        if (infile.exists()) {

                            FileReader fin = new FileReader(infile);
                            out.flush();

                            BufferedReader read = new BufferedReader(fin);

                            String filetext;
                            String file = "";
                            while ((filetext = read.readLine()) != null) {

                                file += filetext;
                                file += "\n";
                            }

                            out.write(createHeader(file, true));
                            // out.write(file);
                            out.write("\r\n");
                            out.flush();
                        } else {
                            // Header for file doesn't exist
                            infile = new File(filepath + "\\nf.html");

                            FileReader fin = new FileReader(infile);
                            out.flush();

                            BufferedReader read = new BufferedReader(fin);
                            // out.write("\r\n");
                            String filetext;
                            String file = "";
                            while ((filetext = read.readLine()) != null) {
                                // out.write(filetext + "\n");
                                file += filetext;
                                file += "\n";
                            }

                            out.write(createHeader(file, true));
                            // out.write(file);
                            out.write("\r\n");
                            out.flush();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                socket.close();
                break;

            }
        } catch (IOException ex) {
            Logger.getLogger(PingServer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (!this.socket.isClosed())
                    this.socket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

}

public class HandleConnection extends Thread{

    private Socket socket;
    private static String logpath = "C://Users//Aranyak Ghosh//IdeaProjects//Module_1//Server//log.txt";
    private File logfile;

    public HandleConnection(){
        this.socket=null;
    }

    public HandleConnection(Socket socket){
        this.socket=socket;
        logfile=new File(logpath);
    }

    private String createHeader(String payload, boolean success){
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
        } else {
            reHeaders = "HTTP/1.1 404 NOT FOUND\r\nContent-type = text/html\r\nConnection = " + keepAliveS
                    + "\r\nServer = " + serverName + "\r\nContent-Length = " + contentLength + "\r\nDate = " + date
                    + "\r\n\r\n";
        }
        return reHeaders;
    }

    private String ReadFile(String path){
        String payload="";

        /*
        * Front End directory
        * */
        String directory="C://Users//Aranyak Ghosh//IdeaProjects//Module_1//front-end";

        /*
        * Filenames
        * */
        String homepage="//homepage.html";
        String login="//login.html";
        String signup="//signup.html";
        String fourofour="//fourofour.html";


        File infile=null;

        if(path.equals("/")){
            infile=new File(directory+homepage);
        }
        else if(path.equalsIgnoreCase("//login")){
            infile=new File(directory+login);
        }
        else if(path.equalsIgnoreCase("//signup")){
            infile=new File(directory+signup);
        }
        else{
            infile=new File(directory+fourofour);
        }



    }
}

