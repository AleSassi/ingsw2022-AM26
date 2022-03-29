package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.TableManager;
import it.polimi.ingsw.model.student.Student;
import it.polimi.ingsw.model.student.StudentHost;

import java.util.*;

public class CharacterCardExtractor {

    private final List<Character> availableCharacters;
    private final Random randomizer;

    public CharacterCardExtractor() {
        this.randomizer = new Random();
        this.availableCharacters = new ArrayList<>(Arrays.asList(Character.values()));
    }

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
