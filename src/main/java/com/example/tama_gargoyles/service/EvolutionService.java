package com.example.tama_gargoyles.service;

import com.example.tama_gargoyles.model.Gargoyle;

import java.util.Objects;

public class EvolutionService {

    private static final int GOOD_HAPPINESS = 60;
    private static final int AGE_TO_EVOLVE = 3;

    public boolean evolveGargoyle(Gargoyle g){

        Gargoyle.Type gType = g.getType();
        Integer age = g.getAge();

        if (age >= AGE_TO_EVOLVE && gType == Gargoyle.Type.CHILD){
            String currentType = g.getEvolutionType();
            if (Objects.equals(currentType, "None")){
                String evolutionType = calculateEvolutionType(g);
                g.setEvolutionType(evolutionType);

            }

            g.setType(calculateGargoyleType(g));
            return true;

        }

        return false;

    }

    public boolean canEvolve(Gargoyle g){
        Gargoyle.Type gType = g.getType();
        Integer age = g.getAge();

        if (age >= AGE_TO_EVOLVE && gType == Gargoyle.Type.CHILD){
            return true;
        }
        return false;
    }

    public String calculateEvolutionType(Gargoyle g){

        Integer intel = g.getIntelligence();
        Integer spd = g.getSpeed();
        Integer str = g.getStrength();

        if (intel > spd && intel > str) {
            return "Intelligence";
        }else if(spd > intel && spd > str){
            return "Speed";
        }else if(str > intel && str > spd){
            return "Strength";
        }else{
            // TODO: think of what happens when something doesnt have a dominant trait
            // e.g. what if strength and speed are the same?
            return "None"; //for now return none?
        }

    }

    public Gargoyle.Type calculateGargoyleType(Gargoyle g){

        if (g.getHappiness() >= GOOD_HAPPINESS){
            return Gargoyle.Type.GOOD;
        }else{
            return Gargoyle.Type.BAD;
        }

    }

}
