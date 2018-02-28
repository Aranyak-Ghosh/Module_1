import java.io.*;

public class UserInfo {
    private String username;
    private String password;
    private String email;

    public UserInfo(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public UserInfo() {
        this.username = null;
        this.password = null;
        this.email = null;
    }

    public String getUsername() {
        return username;
    }

    public void addUser() {
        String filepath = "C://Users//Aranyak Ghosh//IdeaProjects//Module_1//Server//info";
        File userfile = new File(filepath + "//User_Info.txt");
        File cred = new File(filepath + "//Credentials.txt");
        try {

            FileWriter credwriter = new FileWriter(cred, true);
            FileWriter userwriter = new FileWriter(userfile, true);

            credwriter.write(username + "\t" + password);
            userwriter.write(username + "\t" + email);

            credwriter.flush();
            userwriter.flush();

            credwriter.close();
            userwriter.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public boolean exist() {
        try {
            BufferedReader uin = new BufferedReader(new FileReader(new File("//info//User_Info.txt")));
            String s;
            while ((s = uin.readLine()) != null) {
                if (s.startsWith(this.username)||s.contains(this.email))
                    return true;
            }

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
        return false;
    }

    public static boolean authenticate(String username, String password){
        try {
            BufferedReader uin = new BufferedReader(new FileReader(new File("//info//Credential.txt")));
            String s;
            while((s=uin.readLine())!=null){
                String[] cred=s.split("\t",2);
                if(cred[0].equals(username)&&cred[1].equals(password))
                    return true;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
