public class ServerLaunch {

    public static void main(String []args)
    {
        int PORT = 8080;
        int NO_OF_CLIENTS = 10;
        PingServer ps = new PingServer(PORT, NO_OF_CLIENTS);
        ps.start();



    }
}


