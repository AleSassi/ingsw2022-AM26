package it.polimi.ingsw.server.exceptions.model;

public class EmptyCollectionException extends Exception {
    public EmptyCollectionException(){
        super();
    }
    public EmptyCollectionException(String s){
        super(s);
    }

}
