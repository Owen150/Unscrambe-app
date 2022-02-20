package com.example.android.unscramble.ui.game

import android.util.Log
import androidx.lifecycle.ViewModel

//View Model is an abstract class so we need to extend the class to use in our app
class GameViewModel : ViewModel(){
    //The data variables with backing fields for outside class/public access
    private var _score = 0
    val score: Int
        get() = _score

    private var _currentWordCount = 0
    val currentWordCount: Int
        get() = _currentWordCount


    private lateinit var _currentScrambledWord: String
    val currentScrambledWord : String
        get() = _currentScrambledWord

    //Holds a list of words you use in the game, to avoid repetitions.
    private var wordsList: MutableList<String> = mutableListOf()

    //To hold the word the player is trying to unscramble
    private lateinit var currentWord: String

    init {
        Log.d("GameFragment", "GameViewModel created!")
        getNextWord()
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("GameFragment", "GameViewModel destroyed!")
    }

    private fun getNextWord(){
        //Get a random word from the allWordsList and assign it to currentWord
        currentWord = allWordsList.random()
        //convert the currentWord string to an array of characters
        val tempWord = currentWord.toCharArray()
        //To scramble the word, shuffle characters in this array using the Kotlin method, shuffle
        tempWord.shuffle()
        //To continue executing the loop until the scrambled word is not the same as the original word
        while (String(tempWord).equals(currentWord, false)) {
            tempWord.shuffle()
        }
        //if-else block to check if a word has been used already
        if (wordsList.contains(currentWord)) {      //If the wordsList contains currentWord, call getNextWord()
            getNextWord()
        } else {
            _currentScrambledWord = String(tempWord)    //If not, update the value of _currentScrambledWord with the newly scrambled word,
            ++_currentWordCount     // increase the word count,
            wordsList.add(currentWord)  //and add the new word to the wordsList.
        }
    }
    //Re-initializes the game data to restart the game
    fun reinitializeData() {
        _score = 0
        _currentWordCount = 0
        wordsList.clear()
        getNextWord()
    }
    //Increases the game score if the player's word is correct.
    private fun increaseScore() {
        _score += SCORE_INCREASE        //Increase the score variable by SCORE_INCREASE.
    }

    //Returns a Boolean and takes a String, the player's word, as a parameter.
    fun isUserWordCorrect(playerWord: String): Boolean {
        if (playerWord.equals(currentWord, true)) {
            increaseScore()
            return true
        }
        return false
    }
    /*
    * Returns true if the current word count is less than MAX_NO_OF_WORDS.
    * Updates the next word.
    */
    fun nextWord(): Boolean {
        return if (_currentWordCount < MAX_NO_OF_WORDS) {
            getNextWord()
            true
        } else false
    }
}