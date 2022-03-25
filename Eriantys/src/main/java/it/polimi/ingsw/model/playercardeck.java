package it.polimi.ingsw.model;
import java.util.*;


public class playercardeck {
    private ArrayList<AssistantCard> Assistantlist=new ArrayList<AssistantCard>();

    /**
     *return the top card
     */
public AssistantCard topCard(){
    return Assistantlist.get(0);
}
    /**
     *return on top the card
     */
public void AddCardOnTop(AssistantCard maghetto){
    Assistantlist.add(0, maghetto );
        }





}
