package com.example.tama_gargoyles.controller;

import java.util.Scanner;
import java.util.Random;

public class BattleModeController {

    public static void main(String[] args) {

        int userChoice, computerChoice;
        int slam = 0, sneak_attack = 1, dash = 2;

        int userScore = 0;
        int computerScore = 0;

        Scanner input = new Scanner(System.in);
        Random rnd = new Random();

        System.out.println("=== Battle Mode ===");
        System.out.println("First to 3 wins!");

        // Game loop
        while (userScore < 3 && computerScore < 3) {

            System.out.println("\nEnter your choice (0=slam, 1=sneak_attack, 2=dash)");
            userChoice = input.nextInt();

            while (userChoice > 2 || userChoice < 0) {
                System.out.println("Give a number between 0 and 2");
                userChoice = input.nextInt();
            }

            // User choice
            if (userChoice == slam)
                System.out.println("User chose slam attack");
            else if (userChoice == sneak_attack)
                System.out.println("User chose sneak attack");
            else
                System.out.println("User chose dash attack");

            // Computer choice
            computerChoice = rnd.nextInt(3);

            if (computerChoice == slam)
                System.out.println("Computer chose slam attack");
            else if (computerChoice == sneak_attack)
                System.out.println("Computer chose sneak attack");
            else
                System.out.println("Computer chose dash attack");

            // Draw
            if (userChoice == computerChoice) {
                System.out.println("It's a draw!");
                continue;
            }

            // Results
            if (computerChoice == slam) {
                if (userChoice == sneak_attack) {
                    System.out.println("User wins this round!");
                    userScore++;
                } else {
                    System.out.println("Computer wins this round!");
                    computerScore++;
                }
            } else if (computerChoice == sneak_attack) {
                if (userChoice == slam) {
                    System.out.println("Computer wins this round!");
                    computerScore++;
                } else {
                    System.out.println("User wins this round!");
                    userScore++;
                }
            } else { // computer = dash
                if (userChoice == slam) {
                    System.out.println("User wins this round!");
                    userScore++;
                } else {
                    System.out.println("Computer wins this round!");
                    computerScore++;
                }
            }

            // Display scores
            System.out.println("Score → User: " + userScore + " | Computer: " + computerScore);
        }

        // Final winner
        if (userScore == 3)
            System.out.println("\n🎉 USER WINS THE GAME! 🎉");
        else
            System.out.println("\n💻 COMPUTER WINS THE GAME 💻");

        input.close();
    }
}
