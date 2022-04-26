package it.polimi.ingsw.server.exceptions.model;

public class InvalidPlayerCountException extends Exception{
    public InvalidPlayerCountException(){
        super();
    }
    public InvalidPlayerCountException(String s){
        super(s);
    }
}
