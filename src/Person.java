import java.io.Serializable;

public class Person implements Serializable {
    private String Nickname;
    private String name;
    private String password;

    public Person(String nickname, String name, String password){
        this.Nickname = nickname;
        this.name = name;
        this.password = password;

    }
    public String getAge() {
        return Nickname;
    }

    public String getName() {
        return name;
    }
    public String toString(){
        return Nickname + " " + name + " [" + password + "]";
    }

    public String getPassword() {
        return password;
    }
}
