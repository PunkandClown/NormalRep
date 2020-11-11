import java.util.*;

public class ChatPage {
    public static String page(Map hashMap){
    StringBuilder SB =  new StringBuilder();
        System.out.println(hashMap.size());

    SB.append("<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" +
            "<head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <title>Title</title>\n" +
            "    <style type=\"text/css\">\n" +
            "   .par { \n" +
            "    border: 2px dashed black; \n"+
            "    border-color: #AFEEEE; \n"+
            "    padding: 5px; \n"+
            "    margin: down; \n"+
            "    font-size: 90%; /* Размер шрифта */\n" +
            "    font-family: Verdana, Arial, Helvetica, sans-serif; /* Семейство шрифта */\n" +
            "    color: #336; /* Цвет текста */\n" +
            "   }\n" +
                    "   .text { \n" +
                    "    border: 2px solid black; \n"+
                    "    border-color: #AFEEEE; \n"+
                    "    margin: 3%; \n"+
                    "    width: 200px \n;" +
                    "    height: 300px; \n"+
                    "    font-family: Verdana, Arial, Helvetica, sans-serif;\n" +
                    "    color: #336;\n" +
                    "   }\n" +
            "  </style>"+
            "</head>\n" +
            "<body>\n" +
            "\n" +
            "\n"+
            "<div class=\"text\">");
    for(int i = 0; i < hashMap.size(); i++){
        String ParseMessage = hashMap.get(i).toString();
        String Str = ParseMessage.substring(ParseMessage.indexOf('{') +1, ParseMessage.lastIndexOf('}')).trim();
        StringTokenizer st = new StringTokenizer(Str, ",");
        String Nick = st.nextToken().trim();
        String Date = st.nextToken();
        String textmess = st.nextToken();
        StringTokenizer DateToken = new StringTokenizer(Date, " ");
        String DayToken = DateToken.nextToken();
        String DayToken2 = DateToken.nextToken();
        String DayToken3 = DateToken.nextToken();
        String DayToken4 = DateToken.nextToken();
        SB.append("<div class=\"par\">" + "<p>Имя Пользователя: "+ Nick + "</p>" + "<p> Время: "+ DayToken4 + "</p>" +"<p class=\"par\"> Текст сообщения: "+ textmess + "</p>" + "</div>\n");
    }
    SB.append(
            "</div\n>" +
                    "<textarea cols=\"40\" rows=\"10\" id = 'TA'></textarea>\n" +
                    "<button onclick=\"start()\">Отправить</button>\n" +
            "</body>\n" +
            "    <script>\n" +
            "\n" +
            "            let xhr = new XMLHttpRequest();\n" +
            "\n" +
            "            function start () {\n" +
            "                let TA = document.getElementById('TA').value;\n" +
            "                console.log(TA);\n" +
            "\n" +
            "                xhr.open('POST', 'http://10.1.0.48:1337/main', true);\n" +
            "           if (TA.value == ' ') {" +
            "           alert('Поле пустое!')" + "} \n else {" +
            "                xhr.send(TA);\n" +
            "              location.reload(); } \n" +
            "           \n }"   +
            "\n" +
            "\n" +
            "\n" +
            "    </script>\n" +
            "</html>");


             return SB.toString();
    }
}
