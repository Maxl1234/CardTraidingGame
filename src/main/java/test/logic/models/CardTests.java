package test.logic.models;

import com.example.app.mtcg.entity.Card;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CardTests {

    @Test
    public void testGetElementFromName() {
        Card card = new Card();
        card.setName("Water Elemental");
        card.setTypeAndElem();
        assertEquals("Water", card.getElement());

        card.setName("Fire Mage");
        card.setTypeAndElem();
        assertEquals("Fire", card.getElement());

        card.setName("Golem");
        card.setTypeAndElem();
        assertEquals("Normal", card.getElement());


    }



    @Test
    public void testGetTypeFromName() {
        Card card = new Card();
        card.setName("Fireball Spell");
        card.setTypeAndElem();
        assertEquals("Spell", card.getType());

        card.setName("Knight");
        card.setTypeAndElem();
        assertEquals("Monster", card.getType());

        card.setName("God");
        card.setTypeAndElem();
        assertEquals("God", card.getType());
    }
}
