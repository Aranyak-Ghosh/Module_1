import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.Buffer;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class HandleConnection extends Thread {

    private Socket socket;
    private static String logpath = "log.txt";
    private File logfile;
    private FileWriter logWriter;
    private UserInfo user;

    public HandleConnection() {
        this.socket = null;
    }

    public HandleConnection(Socket socket) {
        this.socket = socket;
        logfile = new File(logpath);
        user=new UserInfo("AK","","");
        try {
            logWriter = new FileWriter(logfile, true);
            logWriter.write(LocalDateTime.now().toString() + ": Connection Handler object created for client " + socket.getInetAddress()+"\n");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private String createHeader(String payload, boolean success, boolean cookie) {
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
                    + serverName + "\r\nContent-Length = " + contentLength + "\r\nDate = " + date + "\r\n";
            try {
                logWriter.write(LocalDateTime.now().toString() + ": HTTP 200 header created\n");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            reHeaders = "HTTP/1.1 404 NOT FOUND\r\nContent-type = text/html\r\nConnection = " + keepAliveS
                    + "\r\nServer = " + serverName + "\r\nContent-Length = " + contentLength + "\r\nDate = " + date
                    + "\r\n";
            try {
                logWriter.write(LocalDateTime.now().toString() + ": HTTP 404 header created\n");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        if(cookie)
        {
            reHeaders+="Set-Cookie: Date="+LocalDateTime.now().toString();
            OffsetDateTime onemonthFromNow = OffsetDateTime.now(ZoneOffset.UTC).plus(Duration.ofHours(1));
            String cookieExpires = DateTimeFormatter.RFC_1123_DATE_TIME.format(onemonthFromNow);
            reHeaders+=" "+cookieExpires+"\r\n";
            try{
                BufferedReader read=new BufferedReader(new FileReader(new File("/info/Cookies.txt")));
                String s;
                String cookie_list="";
                FileWriter cookiewrite=new FileWriter(new File("/info/Cookies.txt"));
                while((s=read.readLine())!=null)
                {
                    if(s.contains(user.getUsername()))
                        s=user.getUsername()+"\t"+LocalDateTime.now().toString();
                    cookie_list+=s;
                    cookie_list+="\n";
                }
                cookiewrite.write(cookie_list);
            }catch(FileNotFoundException ex){
                ex.printStackTrace();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        reHeaders+="\r\n";
        return reHeaders;
    }

    private String ReadFile(String path) {
        String payload = "";

        /*
        * Front End directory
        * */
        String rel_directory="..//front-end";   //Relative Directory

        /*
        * Filenames
        * */
        String homepage = "//homepage.html";
        String login = "//login.html";
        String signup = "//signup1.html";
        String fourofour = "//fourofour.html";
        String refresh="//refresh.html";

        File infile = null;

        if (path.equals("/")) {
            infile = new File(rel_directory + login);
        } else if (path.equalsIgnoreCase("/login")) {
            infile = new File(rel_directory + login);
        } else if (path.equalsIgnoreCase("/signup")) {
            infile = new File(rel_directory + signup);
        } else if(path.equalsIgnoreCase("/favicon.ico")){
            //DO NOTHING

        } else if(path.startsWith("refresh")){
            infile=new File(rel_directory+refresh);
        } else {
            infile = new File(rel_directory + fourofour);
        }
        try {
            logWriter.write(LocalDateTime.now() + ": File requested: " + path+"\n");
            logWriter.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        try {
            if(infile!=null) {
                BufferedReader in = new BufferedReader(new FileReader(infile));

                if (infile.getName().startsWith("refresh")) {
                    String s;
                    String redirectto = path.split("\t")[1];
                    while ((s = in.readLine()) != null) {
                        payload += s;
                        if (s.contains("<head>"))
                            payload += "<meta http-equiv=\"refresh\" content=\"0; url=" + socket.getLocalSocketAddress() + "/" + redirectto + "\"/>";
                        payload+="\r\n";
                    }
                } else {
                    String s;
                    while ((s = in.readLine()) != null) {
                        if (s.contains("span") && s.contains("username"))
                            s = s.replace("<span id='username'></span>!",  user.getUsername());
                        if (s.contains("localhost"))
                            s = s.replace("localhost:8080", socket.getLocalSocketAddress().toString());
                        payload += s;
                        payload+= "\r\n";
                    }
                }
            }

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return payload;
    }

    public void run() {
        try {
            logWriter.write(LocalDateTime.now().toString() + ": Handle Connection thread running\n");
            OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream());
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String request = "";
            String cookie=null;
            String inputString = "";
            int length = 0;
            while (!(inputString = in.readLine()).equals("")) {
                request = request + inputString + "\r\n";
                if (inputString.contains("Content-Length:")) {
                    length = Integer.parseInt(inputString.substring(inputString.indexOf("Content-Length:") + 16, inputString.length()));
                }
               if(inputString.contains("Cookie"))
                    cookie=inputString.split("=")[1];
            }

            String[] reqheader = request.split("\n");
            if (reqheader[0].startsWith("GET")) {
                String path = reqheader[0].split(" ")[1].trim();

                if(path.equals("/")&&cookie!=null){
                    BufferedReader bin=new BufferedReader(new FileReader(new File("info/Cookies.txt")));
                    String s;
                    while((s=bin.readLine())!=null)
                        if(s.contains(cookie))
                            user=new UserInfo(s.split("/t")[0],"not-required","not-required");
                        else
                            path="/login";

                }
                String payload = ReadFile(path);
                String resheader = createHeader(payload, !payload.contains("404"),false);

                logWriter.write(LocalDateTime.now().toString() + ": Get request received from client\n");
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
                    logWriter.write(LocalDateTime.now()+": Register request received\n");
                    String username=null, password=null, email=null;
                    for (String s : fields
                            ) {
                        String fieldname = s.split("=", 2)[0];
                        String fieldvalue = s.split("=", 2)[1];

                        if(fieldname.equalsIgnoreCase("username"))
                            username=fieldvalue;
                        else if(fieldname.equalsIgnoreCase("password"))
                            password=fieldvalue;
                        else
                            email=fieldvalue;
                    }
                    user = new UserInfo(username,password,email);
                    if(!user.exist()){
                        user.addUser();
                        logWriter.write(LocalDateTime.now().toString()+": User created");

                        String response=ReadFile("refresh\tlogin");
                        String resheader=createHeader(payload,true,false);
                        out.write(resheader+response);
                    }
                    else{
                        logWriter.write(LocalDateTime.now().toString()+": User already exists.");
                        //TODO: Send error message, User already exists

                    }
                }
                else{
                    logWriter.write(LocalDateTime.now().toString()+": Login request received");
                    String username=null;
                    String password=null;
                    for (String s : fields
                            ) {
                        String fieldname = s.split("=", 2)[0];
                        String fieldvalue = s.split("=", 2)[1];

                        if(fieldname.equalsIgnoreCase("username"))
                            username=fieldvalue;
                        else
                            password=fieldvalue;

                    }
                    if(UserInfo.authenticate(username,password)){
                        user=new UserInfo(username,password,"not_required");
                        String response=ReadFile("refresh\thomepage");
                        String head=createHeader(response,true,true);
                        out.write(head+response);
                        logWriter.write(LocalDateTime.now().toString()+": User Authenticated");
                    }
                    else{
                        logWriter.write(LocalDateTime.now().toString()+": User Authentication rejected");
                        //TODO: Send error message
                        //TODO: invalid username or password
                    }

                }
            }
            this.socket.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}

