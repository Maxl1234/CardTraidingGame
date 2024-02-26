package test.logic.models;


import com.example.app.mtcg.entity.Card;
import com.example.app.mtcg.entity.Deck;
import com.example.app.mtcg.entity.User;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserTests {

    @Test
    public void testEvaluateDeck() {
        User user = new User("TestUser", "password");

        Card c1 = new Card("Card1", "Card1", "Card1", 30, "Card1");
        Card c2 = new Card("Card2", "Card2", "Card2", 20, "Card2");
        Card c3 = new Card("Card5", "Card5", "Card5", 2, "Card5");
        Card c4 = new Card("Card6", "Card6", "Card6", 3, "Card6");
        Card c5 = new Card("Card3", "Card3", "Card3", 40, "Card3");
        Card c6 = new Card("Card7", "Card7", "Card7", 5, "Card7");
        Card c7 = new Card("Card4", "Card4", "Card4", 25, "Card4");

        Vector<Card> cardStack = new Vector<>();
        cardStack.add(c1);
        cardStack.add(c2);
        cardStack.add(c3);
        cardStack.add(c4);
        cardStack.add(c5);
        cardStack.add(c6);
        cardStack.add(c7);

        user.setUserCards(cardStack);

        user.updateDeck();


        Vector<Card> expectedDeck = new Vector<>();
        expectedDeck.add(c1);
        expectedDeck.add(c2);
        expectedDeck.add(c6);
        expectedDeck.add(c7);

        Vector<Card> userDeck = user.getUserDeck().getDeck();

        expectedDeck.sort(Comparator.comparing(Card::getDamage));
        userDeck.sort(Comparator.comparing(Card::getDamage));

        assertEquals(expectedDeck, userDeck);
    }

    @Test
    public void testAddCardToStack() {
        User user = new User("TestUser", "password");

        Card cardToAdd = new Card("NewCard", "NewCard", "NewCard", 15, "NewCard");
        Vector<Card> cards = new Vector<>();
        cards.add(cardToAdd);

        user.addCards(cards);

        // After adding a card to the stack, the cardStack should contain the added card
        Vector<Card> expectedStack = new Vector<>();
        expectedStack.add(cardToAdd);

        Vector<Card> userCardStack = user.getUserCards();

        assertEquals(expectedStack, userCardStack);
    }



    @Test
    public void testAddCardToDeck() {
        User user = new User("TestUser", "password");

        Card cardToAdd = new Card("NewCard", "NewCard", "NewCard", 15, "NewCard");

        user.getUserDeck().addCard(cardToAdd);

        // After adding a card to the deck, the user's cardDeck should contain the added card
        Vector<Card> expectedDeck = new Vector<>();
        expectedDeck.add(cardToAdd);

        Vector<Card> userCardDeck = user.getUserDeck().getDeck();

        assertEquals(expectedDeck, userCardDeck);
    }

    @Test
    public void testRemoveCardFromDeck() {
        User user = new User("TestUser", "password");

        Card cardToRemove = new Card("CardToRemove", "CardToRemove", "CardToRemove", 10, "CardToRemove");

        Vector<Card> cardDeck = new Vector<>();
        cardDeck.add(cardToRemove);

        Deck userDeck = new Deck();
        userDeck.setDeck(cardDeck);

        user.setUserDeck(userDeck);

        user.getUserDeck().deleteCard(cardToRemove);

        // After removing a card from the deck, the user's cardDeck should be empty
        Vector<Card> expectedDeck = new Vector<>();

        Vector<Card> userCardDeck = user.getUserDeck().getDeck();

        assertEquals(expectedDeck, userCardDeck);
    }
}