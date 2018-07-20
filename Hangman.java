import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
public class Hangman 
{
    private int guesses;
    private int wordLength;
    private ArrayList<String> currentOptions;
    private ArrayList<Character> lettersGuessed;
    private String pattern; // the current word family
    private String winningWord;
    

    public Hangman(ArrayList<String> wordList, int wordLength, int numberOfGuesses)  // constructor
    {
        this.pattern = "";
        this.wordLength = wordLength;
        for(int k = 0; k < wordLength; k++)  //initializes pattern to dashes
        {
            this.pattern += "-";
        }
        this.currentOptions = wordList;
        this.lettersGuessed = new ArrayList<Character>();
        this.guesses = numberOfGuesses;
    }

    public ArrayList<String> getCurrentOptions() //returns the arraylist for currentOptions
    {
        return this.currentOptions;
    }
    
    public int currentOptionsSize() // returns amount of possible options
    {
        return currentOptions.size();
    }

    public int getRemainingGuesses() //returns amount of guesses left
    {
        return this.guesses;
    }

    public ArrayList<Character> guesses() //returns the arraylist for guessed letters
    {
        return this.lettersGuessed;
    }

    public String getTargetPattern()  //returns whatever the word is ex: "--a-o--"
    {
        if(this.currentOptions.isEmpty()) 
        {
            throw new IllegalStateException();
        }
        return this.pattern;
    }

    public boolean gameOver() // returns true if out of guesses, if pattern is a full word, or if currentOptions is empty
    {
        return guesses == 0 || !pattern.contains("-") || currentOptions.isEmpty();
    }

    public boolean playerVictory() // yes or no if the player wins
    {
        if (!gameOver()) 
        {
            return false; // cannot win as long as the game is not over
        } 
        else 
        {
            return !pattern.contains("-"); // false if the pattern still has unguessed letters
        }
    }

    public String printGuessedLetters() 
    {
        return lettersGuessed.toString(); // nicely prints out the guessed letters on screen
    }

    private void addWordChoice(String key, String word, HashMap<String, ArrayList<String>> wordFamilyMap) 
    {
        if (wordFamilyMap.get(key) == null) // if the hashMap doesn't already contain our key
        {
            wordFamilyMap.put(key, new ArrayList<>()); // add the key along with an empty arrayList
        }
        wordFamilyMap.get(key).add(word); // regardless, for use inside a loop, this will add one word at a time to 
        // each word family's array List
    }

    public String getWinningWord() 
    {
        if (gameOver())
        {
            return winningWord; // at game over this will be called to show the last word
        } 
        else
        {
            return null; // as long as the game isn't over the winning word will stay secret
        }
    }

    private int getTotalUniqueCharacters(ArrayList<String> words) // this class calculates how many guesses the user will need to make for a given list of words
    {
        int result = 0;

        if (words == null) // zero if there are no words in list
            return 0;

        for (String word:words)
        {
            char[] currentWord = word.toCharArray();
            HashSet<Character> uniqueCharacters = new HashSet<>(); // advantage of hashset is that it there cannot be duplicate elements
            for (char a : currentWord) 
            {
                if (!lettersGuessed.contains(a)) // as long as the char has not been guessed
                {
                    uniqueCharacters.add(a);
                }
            }
            result += uniqueCharacters.size(); // since we are only interested in the amount of unique chars, the .size() will give just that
        }
        return result;
    }
    
    // main chunk of class
    public boolean nextGuess(char guess) 
    {
        String referencePattern = pattern; // calls in the class variable pattern for use with this turn of play
        boolean correctGuess = false; // initializes a value that will eventually be returned
        HashMap<String, ArrayList<String>> wordChoices = new HashMap<>(); // works as a stand in to a dictionary data type. we will fill it with  
        //    word families as keys and the corresponding words that belong to each family

        if (gameOver()) // if the game is finished then the rest of the method is not necessary so return playerVictory() to see if they won
        {
            System.out.println("Game Over");
            return playerVictory();
        }

        for (String currentWord : currentOptions) // iterates over each word in the class arrayList currentOptions
        {
            char[] key = new char[wordLength]; // char array to easily crate the current key with the class variable for word length

            for (int k = 0; k < wordLength; k++) // loops until all letters in wordLength are fulfilled
            {
                if (currentWord.charAt(k) == guess) // if the char at the specified index is the same as the player guess
                {
                    key[k] = guess; // set the current char of key to the players guess
                } 
                else 
                {
                    key[k] = pattern.charAt(k); // otherwise substitute the current char of key to whatever the class variable pattern has at the given index 
                    //      (could be "-" or a previous guess)
                }
            }

            String currentKey = new String(key); // when the char array key is filled, make a new string out of it

            addWordChoice(currentKey, currentWord, wordChoices); // utilizes the addWordChoice() method defined earlier
        }

        // once the guess is processed above, if that was the last guess(would still be 1 because guesses-- has not been called)
        if (guesses == 1) 
        {
            if (wordChoices.keySet().contains(pattern)) // check to see if our set of word families contains the class variable pattern
            {
                currentOptions = new ArrayList<>(wordChoices.get(pattern)); // update currentOption to the words associated with the pattern
                guesses--; // makes guesses zero
                winningWord = wordChoices.get(pattern).get(0); // chooses the first word in the pattern's word family as the winning word
                lettersGuessed.add(guess); // add the guess to our lettersGuessed arraylist
                return false; // incorrect guess so return false
            }
        }

        // as long as guesses > 1 do this

        for (String currentKey : wordChoices.keySet()) // iterates over the different word families
        {
            if (!wordChoices.keySet().contains(referencePattern)) // if our referencePattern is not equivalent to any of the new word families
            {
                referencePattern = currentKey; // set our referencePattern to the currentKey for use in the next if statement
            }
            if (getTotalUniqueCharacters(wordChoices.get(currentKey)) > getTotalUniqueCharacters(wordChoices.get(referencePattern))) // compares the number of unique chars for both word families
            {
                referencePattern = currentKey; // reference pattern, after iterating through all word families should end as the word family with the greatest number of unique 
                                              //      chars making it the best option to use against the player
            }
        }

        // with all of the processing complete, its time to set the variables
        if (wordChoices.keySet().contains(referencePattern)) // as long our reference pattern is one of the word families, do this
        {
            currentOptions = new ArrayList<>(wordChoices.get(referencePattern)); // this will alter currentOptions to reflect the new word family associated with our referncePattern
            correctGuess = !referencePattern.equals(pattern);       // if the referencePattern was unchanged after giong through the above processing, this means the guess was wrong

            if (!correctGuess) // as long as the guess was incorrect, we have to subtract 1 from guesses
                guesses--;

            pattern = referencePattern;                     // set class variable pattern to our optimized referencePattern
            winningWord = wordChoices.get(pattern).get(0); // chooses a new winning word that is a member of the new pattern/word family
            lettersGuessed.add(guess);                    // add the guess to lettersGuessed
            return correctGuess;                         // return T/F depending on if the guess was correct or not
        } 
        else // otherwise there was some kind of anomoly and things are empty do this (shouldnt happen)
        {
            currentOptions = new ArrayList<>(); // set currentOptions to blank
            lettersGuessed.add(guess);         // add the guess to lettersGuessed
            return false;                     // incorrect guess so return false
        }
    }
}