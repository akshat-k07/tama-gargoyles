package com.example.tama_gargoyles.model;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class UserTest {

    @Test
    public void testConstructor(){

        String user_email = "test@email.com";
        User testUser = new User(user_email);

        assertEquals(user_email, testUser.getEmail());
        assertEquals("null", testUser.getUsername());
        assertEquals(0L, testUser.getBugs().longValue());
        assertEquals(0L, testUser.getRocks().longValue());
        assertEquals(0L, testUser.getMysteryFood().longValue());

    }

    @Test
    public void testLombokGetAndSet(){

        User testUser = new User();
        testUser.setBugs(2);
        testUser.setRocks(65);
        testUser.setMysteryFood(1);
        testUser.setEmail("test@email.com");

        assertEquals("test@email.com", testUser.getEmail());
        assertEquals(2L, testUser.getBugs().longValue());
        assertEquals(65L, testUser.getRocks().longValue());
        assertEquals(1L, testUser.getMysteryFood().longValue());

    }

    @Test
    public void testEquals(){

        User user1 = new User("test@email.com");
        User user2 = new User("test@email.com");

        assertEquals(user1, user2);

    }

}
