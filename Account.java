import java.util.ArrayList;
import java.io.*;
public class Account {
    String userName;
    String password;
    int AccountClass; //Normal User, Moderator

    public Account(String userName, String password) {
        this.userName = userName;
        this.password = password;
        AccountClass = 0;
    }
}
