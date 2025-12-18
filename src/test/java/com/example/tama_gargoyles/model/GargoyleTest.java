package com.example.tama_gargoyles.model;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;


public class GargoyleTest {
    @Test
        public void gargoyleHasUserId() {
        User user = new User("Test@Testemail.com");
        user.setId(1L);
        Gargoyle gargoyle = new Gargoyle(user);

        assertThat(gargoyle.getUser().getId(), is(1L));
        }

    @Test
        public void gargoyleCanStoreDefaultValues() {
        User user = new User("Test@Testemail.com");
        user.setId(1L);
        Gargoyle gargoyle = new Gargoyle(user);

        assertThat(gargoyle.getAge(), is(0));
        assertThat(gargoyle.getType(), is(Gargoyle.Type.CHILD));
        assertThat(gargoyle.getStatus(), is(Gargoyle.Status.ACTIVE));

        assertThat(gargoyle.getHunger(), is(100));
        assertThat(gargoyle.getHappiness(), is(100));
        assertThat(gargoyle.getHealth(), is(100));
        assertThat(gargoyle.getExperience(), is(0));

        assertThat(gargoyle.getStrength(), is(25));
        assertThat(gargoyle.getSpeed(), is(10));
        assertThat(gargoyle.getIntelligence(), is(10));

        assertThat(gargoyle.isPaused(), is(false));
        assertThat(gargoyle.getActiveMinutes(), is(0L));

        assertThat(gargoyle.getLastTickAt() != null, is(true));
    }
}
