public class fab {
    public Person Fabm(String Nickname ,String name, String password){

        Person NotAVerifiedUser = new Person("dsdsd", "incorrectValues", "dfdff" );

        if(Nickname != "asdas"){
            NotAVerifiedUser  = new Person( Nickname,name,password);
        }
        return NotAVerifiedUser;
    }
}