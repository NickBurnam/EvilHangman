import java.util.Scanner;
import java.util.Random;
import java.util.Arrays;
import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
/**
 * Evil Hangman
 *
 * Nicholas Burnam
 * version 1.0
 */
public class MainGame
{
    /**
     * Play game here
     */
    public static void main(String[] args)
    {
        Boolean keepPlaying = true;     // used in while loop to either keep playing or quit
        while( keepPlaying == true )
        {
            int wordsToChooseFrom = 127142; // initialized to total number of words in dictionary.txt
            int wordLength = -1;            // to store the player input for length of the word
            int numberOfGuesses = -1;       // to store the player input for number of alotted guesses
            Scanner keyboard;               // direct user input scanner

            String temp;                    // user input goes here first, then check
                                            // to see if its an integer or not
            char guess;
            Boolean runningTotal = true;
            Boolean isDigit = false;        // set to true if temp is a valid digit                                
            
            File file;                      // dictionary file to read
            ArrayList<String> wordList = new ArrayList<String>();

            System.out.println("Welcome to Evil Hangman!\n");

            //ask the user for wordLength
            System.out.println("Please enter the length of word for you to guess: ");
            while(isDigit == false)
            {
                try
                {
                    keyboard = new Scanner(System.in);
                    temp = keyboard.next();
                    wordLength = Integer.parseInt(temp);
                    if(wordLength < 2 || wordLength > 29) // these values were pre-calculated using
                    {                                     // getSmallestWord and getLargestWord
                        System.out.println("Invalid input, please enter new word length: ");
                    }
                    else
                    {
                        isDigit = true;
                    }
                }
                catch(NumberFormatException e) // catches when input is not an int
                {
                    System.out.println("Invalid input, please enter new word length: ");
                }
            }

            isDigit = false;  // reset to false for next input

            //ask user for numberOfGuesses
            System.out.println("Please enter amount of guesses: ");
            while(isDigit == false)
            {
                try
                {
                    keyboard = new Scanner(System.in);
                    temp = keyboard.next();
                    numberOfGuesses = Integer.parseInt(temp);
                    if(numberOfGuesses < 1)
                    {
                        System.out.println("Invalid input, please enter valid number of guesses: ");
                    }
                    else
                    {
                        isDigit = true;
                    }
                }
                catch(NumberFormatException e) // catches when input is not an int
                {
                    System.out.println("Invalid input, please enter valid number of guesses: ");
                }
            }
            System.out.println("Would you like to display the running total of possible words? (yes/no)");
            boolean validInput = false;
            while(validInput == false) 
            {
                keyboard = new Scanner(System.in);
                temp = keyboard.next().toLowerCase();
                if(temp.equals("yes"))
                {
                    runningTotal = true; 
                    validInput = true;
                }
                else if(temp.equals("no"))
                {
                    runningTotal = false;
                    validInput = true;
                }
                else
                {
                    System.out.println("Not a real answer, enter yes or no");
                    validInput = false;
                }
                System.out.println("\n");
            }

            try // initial file read to add words of wordLength to wordList
            {
                file = new File("dictionary.txt");
                Scanner s = new Scanner(file); 
                while (s.hasNext())
                {
                    // read in a word from dictionary
                    temp = s.nextLine();
                    if(temp.length() == wordLength)
                        wordList.add(temp);
                    else
                        continue;
                }
            } 
            catch (FileNotFoundException s) 
            { 
                System.out.println("Error File Not Found"); 
                s.printStackTrace();
            }   

             // main game loop to keep guessing letters or words
            //make a hangman constructor
            Hangman currentGame = new Hangman(wordList, wordLength, numberOfGuesses);
            while(!currentGame.gameOver())
            {
                // print targetPattern
                System.out.println("\n" +currentGame.getTargetPattern());
                System.out.println("Remaining guesses: " + currentGame.getRemainingGuesses());
                System.out.println("Letters already guessed: ");
                System.out.println(currentGame.printGuessedLetters());
                if(runningTotal)
                    System.out.println("Possible words to choose from: " + currentGame.currentOptionsSize());
                System.out.println("Please guess a single letter: ");
                keyboard = new Scanner(System.in);
                guess = keyboard.next().toLowerCase().charAt(0); // gets a guess from user
                if(currentGame.nextGuess(guess))
                {
                    System.out.println("Correct!");
                }
                else
                {
                    System.out.println("Wrong!");
                }

            }
            if (currentGame.playerVictory())
            {
                System.out.println("You're winner!!!");
                System.out.println("Final answer is: ");
                System.out.println(currentGame.getWinningWord());
            }
            else if(!currentGame.playerVictory())
            {
                System.out.println("You lose.");
                System.out.println("Final answer is: ");
                System.out.println(currentGame.getWinningWord());
            }
            System.out.println("Would you like to play again?");
            boolean valid = false;
            while(valid == false) // ask if user wants to keep playing
            {
                keyboard = new Scanner(System.in);
                temp = keyboard.next().toLowerCase();
                if(temp.equals("yes"))
                {
                    System.out.println("Starting new game...");
                    keepPlaying = true; 
                    valid = true;
                }
                else if(temp.equals("no"))
                {
                    System.out.println("Goodbye!!");
                    keepPlaying = false;
                    valid = true;
                }
                else
                {
                    System.out.println("Not a real answer, enter yes or no");
                    valid = false;
                }
                System.out.println("\n");
            }
            System.out.println("\n\n\n");
        }
    }

    // Other methods
    // public static boolean wordRevealed(String actualWord)
    // {
        // for(int k = 0; k < actualWord.length(); k++)
        // {
            // if(actualWord.charAt(k) == '-')
                // return false;
        // }
        // return true;
    // }

    public static int getSmallestWord(File dictionary) // used this to find the smallest word length
    {                                                  // to save on processing time
        String temp;
        int smallest = 3;
        try // initial file read to add words of wordLength to wordList
        {
            dictionary = new File("dictionary.txt");
            Scanner s = new Scanner(dictionary);                    
            while (s.hasNext())
            {
                // read in a word from dictionary
                temp = s.nextLine();
                if(temp.length() < smallest)
                    smallest = temp.length();
            }
        } 
        catch (FileNotFoundException s) 
        { 
            System.out.println("Error File Not Found"); 
            s.printStackTrace();
        }   
        return smallest;
    } // this method always will return a 2 for dictionary.txt

    public static int getLargestWord(File dictionary) // used this to find the largest word length
    {                                                 // to save on processing time
        String temp;
        int largest = 0;
        try // initial file read to add words of wordLength to wordList
        {
            dictionary = new File("dictionary.txt");
            Scanner s = new Scanner(dictionary);                    
            while (s.hasNext())
            {
                // read in a word from dictionary
                temp = s.nextLine();
                if(temp.length() > largest)
                    largest = temp.length();
            }
        } 
        catch (FileNotFoundException s) 
        { 
            System.out.println("Error File Not Found"); 
            s.printStackTrace();
        }   
        return largest;
    } // this method always will return a 29 for dictionary.txt
}
