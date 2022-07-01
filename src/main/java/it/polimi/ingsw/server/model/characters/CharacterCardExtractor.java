package it.polimi.ingsw.server.model.characters;

import it.polimi.ingsw.server.exceptions.model.UnavailableCardException;

import java.util.*;
/**
 * The class used to randomly extract Character Cards for a match
 * @author Alessandro Sassi
 */
public class CharacterCardExtractor {
    
    private final List<Character> availableCharacters;
    private final Random randomizer;
    
    /**
     * Creates a new extractor object by initializing the internal list of cards
     */
    public CharacterCardExtractor() {
        this.randomizer = new Random();
        this.availableCharacters = new ArrayList<>(Arrays.asList(Character.values()));
    }

    /**
     * Picks a random card from the list of available cards, and removes it from such list so that future calls will never pick the same card again
     * @return a randomly chosen and initialized character card
     * @throws UnavailableCardException if the list of available cards is empty
     */
    public CharacterCard pickRandom() throws UnavailableCardException {
        if (availableCharacters.isEmpty()) throw new UnavailableCardException();
        int characterIndex = randomizer.nextInt(0, availableCharacters.size());
        Character randomCharacter = availableCharacters.remove(characterIndex);
        switch (randomCharacter) {
            case Abbot, Circus, Musician, Queen, Thief -> {
                return new StudentHostingCard(randomCharacter);
            }
            case Herbalist -> {
                return new StopCardActivatorCard(randomCharacter);
            }
            case Magician, Mushroom, CheeseMan, Ambassador, Centaurus, Swordsman -> {
                return new GenericModifierCard(randomCharacter);
            }
        }
        //Will never be executed!
        throw new UnavailableCardException();
    }
}
