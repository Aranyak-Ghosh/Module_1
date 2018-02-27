import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

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
}
