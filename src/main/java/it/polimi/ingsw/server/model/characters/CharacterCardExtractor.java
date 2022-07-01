package it.polimi.ingsw.server.model.characters;

import it.polimi.ingsw.server.exceptions.model.UnavailableCardException;

import java.util.*;
/**
 * This Interface represent the {@code CharacterCardExtractor}
 * @author Alessandro Sassi
 */
public class CharacterCardExtractor {
    /**
     * initialize {@code CharacterCardExtractor}
     */
    private final List<Character> availableCharacters;
    private final Random randomizer;
    /**
     * costruct the class with a new randomize and an empty list of (type {@link it.polimi.ingsw.server.model.characters.CharacterCard charactercard})
     */
    public CharacterCardExtractor() {
        this.randomizer = new Random();
        this.availableCharacters = new ArrayList<>(Arrays.asList(Character.values()));
    }

    /**
     * @throws UnavailableCardException when try to pick a {@link it.polimi.ingsw.server.model.characters.CharacterCard charactercard} but avaiblecard is empty
     * the class pick a (@code charactercard) and have different return according to the card pick
     * @return  character (type {@link it.polimi.ingsw.server.model.characters.CharacterCard character})
     * @return  character (type {@link it.polimi.ingsw.server.model.characters.StopCardActivatorCard)
     * @return  character (type {@link it.polimi.ingsw.server.model.characters.GenericModifierCard})
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
