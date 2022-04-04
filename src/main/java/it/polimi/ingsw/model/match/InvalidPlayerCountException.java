package it.polimi.ingsw.model.match;

public class InvalidPlayerCountException extends Exception{
    public InvalidPlayerCountException(){
        super();
    }
    public InvalidPlayerCountException(String s){
        super(s);
    }
}
