package it.polimi.ingsw.exceptions;

public class InvalidPlayerCountException extends Exception{
    public InvalidPlayerCountException(){
        super();
    }
    public InvalidPlayerCountException(String s){
        super(s);
    }
}
