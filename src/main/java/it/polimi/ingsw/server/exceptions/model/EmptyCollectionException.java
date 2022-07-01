package it.polimi.ingsw.server.exceptions.model;
/**
 this class rapresent an exception that is thrown when we player try to take an element from empty collection
 @Author Alessandro Sassi
 */
public class EmptyCollectionException extends Exception {

    public EmptyCollectionException(){
        super();
    }
    public EmptyCollectionException(String s){
        super(s);
    }

}
