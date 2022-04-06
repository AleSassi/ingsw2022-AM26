package it.polimi.ingsw.exceptions;

public class EmptyCollectionException extends Exception {
    public EmptyCollectionException(){
        super();
    }
    public EmptyCollectionException(String s){
        super(s);
    }

}
