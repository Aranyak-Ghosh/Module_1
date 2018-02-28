public class ServerLaunch {

    public static void main(String []args)
    {
        int PORT = 8080;
        PingServer ps = new PingServer(PORT);
        ps.start();



    }
}


