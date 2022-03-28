package it.polimi.ingsw.model;

public class CardDeckBuilder {
    private static CardDeckBuilder instance=null; // riferimento all' istanza


    public static CardDeckBuilder getInstance() {
        // Crea l'oggetto solo se NON esiste:
        if (instance == null) {
            instance = new CardDeckBuilder();
        }
        return instance;}

    public static AvaibleCardsDeck buildDeck(){
        AvaibleCardsDeck deck= new AvaibleCardsDeck();
        for( AssistantCard d : AssistantCard.values() ) {

        }
        return deck;
    }



}



