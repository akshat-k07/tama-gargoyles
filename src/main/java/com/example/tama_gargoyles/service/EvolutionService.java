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

    public String getLifeStage(Gargoyle g) {
        int age = g.getAge();

        if (age < 1) return "egg";
        if (age < 2) return "baby";
        if (age < 3) return "child";
        return "adult";
    }


    public String getGargoyleImagePath(Gargoyle g){

        if (g.getAge() < 1){
            return "/images/gargoyle/egg.png";
        }else if(g.getAge() < 2){
            return "/images/gargoyle/baby.png";
        }else if(g.getAge() < 3){
            return "/images/gargoyle/child-without-base.png";
        }else if(g.getType() == Gargoyle.Type.BAD && Objects.equals(g.getEvolutionType(), "Strength")){
            return "/images/gargoyle/Bad-Strength-The-Breaker.png";
        }else if(g.getType() == Gargoyle.Type.GOOD && Objects.equals(g.getEvolutionType(), "Strength")){
            return "/images/gargoyle/Good-Strength-The-Sentinel.png";
        }else if(g.getType() == Gargoyle.Type.BAD && Objects.equals(g.getEvolutionType(), "Intelligence")){
            return "/images/gargoyle/Bad-Brains-The-Architect.png";
        }else if(g.getType() == Gargoyle.Type.GOOD && Objects.equals(g.getEvolutionType(), "Intelligence")){
            return "/images/gargoyle/Good-Brains-The-Sage.png";
        }else if(g.getType() == Gargoyle.Type.BAD && Objects.equals(g.getEvolutionType(), "Speed")){
            return "/images/gargoyle/Bad-Speed-The-Feral-Flash.png";
        }else if(g.getType() == Gargoyle.Type.GOOD && Objects.equals(g.getEvolutionType(), "Speed")){
            return "/images/gargoyle/Good-Speed-The-Skyrunner.png";
        }else{
            return "/images/gargoyle/happy-neutral-adult.png";
        }

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
