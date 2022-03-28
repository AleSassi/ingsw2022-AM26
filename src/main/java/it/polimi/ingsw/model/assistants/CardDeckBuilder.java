package it.polimi.ingsw.model.assistants;

public class CardDeckBuilder {
    /**
     *unique instance of class
     */
    private static CardDeckBuilder instance = null;

    // /**
    //     *invisible constructor
    //     */
    private CardDeckBuilder() {}
    /**
     *create a instance of class if it doesn't exist
     */
    public static CardDeckBuilder getInstance() {
        if (instance == null) {
            instance = new CardDeckBuilder();
        }
        return instance;
    }
    /**
     *create a start card deck
     */
   public  AvailableCardsDeck buildDeck() {
       AvailableCardsDeck deck=new AvailableCardsDeck();

       return deck;

   }



}
