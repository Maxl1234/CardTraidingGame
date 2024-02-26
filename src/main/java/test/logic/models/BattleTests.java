package test.logic.models;

import com.example.app.mtcg.entity.Battle;
import com.example.app.mtcg.entity.Card;
import com.example.app.mtcg.entity.Deck;
import com.example.app.mtcg.entity.User;
import org.junit.Test;

import java.util.Vector;

import static org.junit.Assert.*;

public class BattleTests {

    // ... Vorherige Tests ...

    @Test
    public void testBattleWithSameDeckStrengthAndRounds() {
        // Create players with similar deck strength
        User playerA = new User("PlayerA", "passwordA");
        User playerB = new User("PlayerB", "passwordB");

        Deck deckE = new Deck();
        deckE.addCard(new Card("CardE1", "Water", "Monster", 30, "IdE1"));
        deckE.addCard(new Card("CardE2", "Fire", "Monster", 30, "IdE2"));
        deckE.addCard(new Card("CardE3", "Normal", "Monster", 30, "IdE3"));
        deckE.addCard(new Card("CardE4", "Water", "Monster", 30, "IdE4"));

        Deck deckF = new Deck();
        deckF.addCard(new Card("CardF1", "Fire", "Monster", 30, "IdF1"));
        deckF.addCard(new Card("CardF2", "Normal", "Monster", 30, "IdF2"));
        deckF.addCard(new Card("CardF3", "Water", "Monster", 30, "IdF3"));
        deckF.addCard(new Card("CardF4", "Fire", "Monster", 30, "IdF4"));

        playerA.setUserDeck(deckE);
        playerB.setUserDeck(deckF);

        Battle battle = new Battle(playerA, playerB);
        String result = battle.startBattle();

        assertTrue(result.contains("Todays fight was a draw"));
    }

    @Test
    public void testBattleWithOnePlayerNoDeck() {
        // Create a scenario where one player has no deck
        User playerA = new User("PlayerA", "passwordA");
        User playerB = new User("PlayerB", "passwordB");

        Battle battle = new Battle(playerA, playerB);
        battle.startBattle();

        assertNotNull(battle.startBattle());
    }


    @Test
    public void testBattleWithNoPlayers() {
        // Create a battle with no players
        Battle battle = new Battle(null, null);

        assertEquals("One user missing",battle.startBattle());
    }

    @Test
    public void testBattleWithOnePlayer() {
        User playerA = new User("PlayerA", "passwordA");

        Battle battle = new Battle(playerA, null);


        assertEquals("One user missing",battle.startBattle());
    }
    @Test
    public void testGetDamage_SameElement() {
        Battle battle = new Battle(null, null);
        Card cardA = new Card("CardA", "Water", "Monster", 20, "IdA");
        Card cardB = new Card("CardB", "Water", "Monster", 30, "IdB");

        double result = battle.getDamage(cardA, cardB);
        assertEquals(20, (int)result);
    }

    @Test
    public void testGetDamage_Weakness() {
        Battle battle = new Battle(null, null);
        Card cardA = new Card("CardA", "Water", "Monster", 20, "IdA");
        Card cardB = new Card("CardB", "Fire", "Spell", 30, "IdB");

        double result = battle.getDamage(cardA, cardB);
        assertEquals(40,(int) result);
    }

    @Test
    public void testGetDamage_NormalEffectiveness() {
        User userA = new User(),userB = new User();
        Battle battle = new Battle(userA, userB);
        Card cardA = new Card("CardA", "Water", "Spell", 20, "IdA");
        Card cardB = new Card("CardB", "Normal", "Monster", 30, "IdB");

        double result = battle.getDamage(cardA, cardB);
        assertEquals(10,(int) result);
    }

    @Test
    public void testGetSpecial_NoSpecial() {
        Battle battle = new Battle(null, null);
        Card cardA = new Card("CardA", "Water", "Monster", 20, "IdA");
        Card cardB = new Card("CardB", "Normal", "Monster", 30, "IdB");

        int result = battle.getSpecial(cardA, cardB);
        assertEquals(0, result);
    }
    @Test
    public void testStartBattle_PlayerAWins() {
        User playerA = new User("PlayerA", "passwordA");
        User playerB = new User("PlayerB", "passwordB");

        Deck deckA = new Deck();
        deckA.addCard(new Card("CardA1", "Water", "Monster", 50, "IdA1"));
        deckA.addCard(new Card("CardA2", "Fire", "Monster", 30, "IdA2"));
        deckA.addCard(new Card("CardA3", "Normal", "Monster", 40, "IdA3"));
        deckA.addCard(new Card("CardA4", "Water", "Monster", 20, "IdA4"));

        Deck deckB = new Deck();
        deckB.addCard(new Card("CardB1", "Fire", "Monster", 30, "IdB1"));
        deckB.addCard(new Card("CardB2", "Normal", "Monster", 20, "IdB2"));
        deckB.addCard(new Card("CardB3", "Water", "Monster", 40, "IdB3"));
        deckB.addCard(new Card("CardB4", "Fire", "Monster", 50, "IdB4"));

        playerA.setUserDeck(deckA);
        playerB.setUserDeck(deckB);

        Battle battle = new Battle(playerA, playerB);
        String result = battle.startBattle();

        assertTrue(result.contains("Player A won"));
    }

    @Test
    public void testStartBattle_PlayerBWins() {
        User playerA = new User("PlayerA", "passwordA");
        User playerB = new User("PlayerB", "passwordB");

        Deck deckA = new Deck();
        deckA.addCard(new Card("CardA1", "Water", "Monster", 30, "IdA1"));
        deckA.addCard(new Card("CardA2", "Fire", "Monster", 20, "IdA2"));
        deckA.addCard(new Card("CardA3", "Normal", "Monster", 40, "IdA3"));
        deckA.addCard(new Card("CardA4", "Water", "Monster", 50, "IdA4"));

        Deck deckB = new Deck();
        deckB.addCard(new Card("CardB1", "Fire", "Monster", 50, "IdB1"));
        deckB.addCard(new Card("CardB2", "Normal", "Monster", 40, "IdB2"));
        deckB.addCard(new Card("CardB3", "Water", "Monster", 30, "IdB3"));
        deckB.addCard(new Card("CardB4", "Fire", "Monster", 20, "IdB4"));

        playerA.setUserDeck(deckA);
        playerB.setUserDeck(deckB);

        Battle battle = new Battle(playerA, playerB);
        String result = battle.startBattle();

        assertTrue(result.contains("Player B won"));
    }

    @Test
    public void testStartBattle_PlayerBWinsWithGods() {
        User playerA = new User("PlayerA", "passwordA");
        User playerB = new User("PlayerB", "passwordB");

        Deck deckA = new Deck();
        deckA.addCard(new Card("CardA1", "Water", "Monster", 30, "IdA1"));
        deckA.addCard(new Card("CardA2", "Fire", "Monster", 20, "IdA2"));
        deckA.addCard(new Card("CardA3", "Normal", "Monster", 40, "IdA3"));
        deckA.addCard(new Card("CardA4", "Water", "Monster", 50, "IdA4"));

        Deck deckB = new Deck();
        deckB.addCard(new Card("God", "Fire", "Monster", 1, "IdB1"));
        deckB.addCard(new Card("God", "Normal", "Monster", 1, "IdB2"));
        deckB.addCard(new Card("God", "Water", "Monster", 1, "IdB3"));
        deckB.addCard(new Card("God", "Fire", "Monster", 1, "IdB4"));

        playerA.setUserDeck(deckA);
        playerB.setUserDeck(deckB);

        Battle battle = new Battle(playerA, playerB);
        String result = battle.startBattle();

        assertTrue(result.contains("Player B won"));
    }


}
