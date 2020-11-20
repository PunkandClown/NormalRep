import java.util.Date;

public class Message {

    private String Name;
    private String Data;
    private String TextMess;

    public Message(String Name, String data, String TextMess){
        this.Name = Name;
        this.Data = data;
        this.TextMess = TextMess;
    }

    public String toString(){
        return  "{\n" + "\"" + "Name" + "\": " + "\"" + Name + "\"" + ",\n" + "\"" + "Data" + "\": " + "\"" + Data + "\"" + ",\n" + "\"" + "TextMessage"+ "\": " + "\"" + TextMess + "\"\n";
    }
}
