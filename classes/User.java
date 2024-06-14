package classes;

import interfaces.IUserChat;

public class User implements IUserChat{
    private String name;

    public User(String name){
        this.name = name;
    }
    
    @Override
    public void deliverMsg(String senderName, String msg){

    }
}
