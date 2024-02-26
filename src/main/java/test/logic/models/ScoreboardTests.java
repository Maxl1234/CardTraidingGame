package test.logic.models;

import com.example.app.mtcg.entity.Scoreboard;
import com.example.app.mtcg.entity.User;
import org.junit.Test;

import java.util.Vector;

import static org.junit.Assert.*;

public class ScoreboardTests {

    @Test
    public void testSortUserScoresByScore() {
        // Create a scoreboard with unsorted user scores
        Vector<User> userScores = new Vector<>();
        User user1 = new User("User1", "password1");
        user1.setScore(100);
        User user2 = new User("User2", "password2");
        user2.setScore(80);
        User user3 = new User("User3", "password3");
        user3.setScore(120);
        userScores.add(user1);
        userScores.add(user2);
        userScores.add(user3);

        Scoreboard scoreboard = new Scoreboard(userScores);
        scoreboard.sortUserScoresByScore();

        // Check if the users are sorted by score in descending order
        Vector<User> sortedScores = scoreboard.getUserScores();
        assertEquals(user3, sortedScores.get(0));
        assertEquals(user1, sortedScores.get(1));
        assertEquals(user2, sortedScores.get(2));
    }

    @Test
    public void testAddUserToEmptyScoreboard() {
        // Create an empty scoreboard and add a user
        Scoreboard scoreboard = new Scoreboard(new Vector<>());
        User user = new User("NewUser", "newPassword");
        user.setScore(90);
        scoreboard.addUser(user);

        // Check if the user is added to the scoreboard
        Vector<User> userScores = scoreboard.getUserScores();
        assertEquals(1, userScores.size());
        assertEquals(user, userScores.get(0));
    }

    @Test
    public void testAddUserToNonEmptyScoreboard() {
        // Create a scoreboard with existing users
        Vector<User> userScores = new Vector<>();
        User user1 = new User("User1", "password1");
        user1.setScore(100);
        userScores.add(user1);

        Scoreboard scoreboard = new Scoreboard(userScores);

        // Add a new user with a higher score
        User user2 = new User("User2", "password2");
        user2.setScore(120);
        scoreboard.addUser(user2);
        scoreboard.sortUserScoresByScore();

        // Check if the new user is added and the scoreboard is sorted
        Vector<User> updatedScores = scoreboard.getUserScores();
        assertEquals(2, updatedScores.size());
        assertEquals(user2, updatedScores.get(0));
        assertEquals(user1, updatedScores.get(1));
    }

    @Test
    public void testShowScores() {
        // Create a scoreboard with user scores
        Vector<User> userScores = new Vector<>();
        User user1 = new User("User1", "password1");
        user1.setScore(100);
        User user2 = new User("User2", "password2");
        user2.setScore(80);
        User user3 = new User("User3", "password3");
        user3.setScore(120);
        userScores.add(user1);
        userScores.add(user2);
        userScores.add(user3);

        Scoreboard scoreboard = new Scoreboard(userScores);
        scoreboard.sortUserScoresByScore();

        // Check if the showScores method returns the expected output
        String expectedOutput = "Username: User3 Score: 120\nUsername: User1 Score: 100\nUsername: User2 Score: 80\n";
        System.out.println(scoreboard.showScores());
        assertEquals(expectedOutput, scoreboard.showScores());
    }
}