package it.polimi.ingsw.exceptions.model;

public class EmptyCollectionException extends Exception {
    public EmptyCollectionException(){
        super();
    }
    public EmptyCollectionException(String s){
        super(s);
    }

}
