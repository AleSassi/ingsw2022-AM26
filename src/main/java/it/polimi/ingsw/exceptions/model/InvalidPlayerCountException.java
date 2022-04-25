package it.polimi.ingsw.exceptions.model;

public class InvalidPlayerCountException extends Exception{
    public InvalidPlayerCountException(){
        super();
    }
    public InvalidPlayerCountException(String s){
        super(s);
    }
}
