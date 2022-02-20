/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package com.example.android.unscramble.ui.game

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.android.unscramble.R
import com.example.android.unscramble.databinding.GameFragmentBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Fragment where the game is played, contains the game logic.
 */
class GameFragment : Fragment() {
    //Associating the View Model to the Fragment
    private val viewModel: GameViewModel by viewModels()

    private var score = 0
    private var currentWordCount = 0
    private var currentScrambledWord = "test"


    // Binding object instance with access to the views in the game_fragment.xml layout
    private lateinit var binding: GameFragmentBinding

    // Create a ViewModel the first time the fragment is created.
    // If the fragment is re-created, it receives the same GameViewModel instance created by the
    // first fragment

    //Inflates the game_fragment layout XML using the binding object
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = GameFragmentBinding.inflate(inflater, container, false)
        //Log statement for when the fragment is created for the first time and also every time it is re-created for any events like configuration changes.
        Log.d("GameFragment", "GameFragment created/re-created!")
        return binding.root
        //Log statement to print the app data, word, score, and word count.
        Log.d("GameFragment", "Word: ${viewModel.currentScrambledWord} " +
                "Score: ${viewModel.score} WordCount: ${viewModel.currentWordCount}")
    }

    //function sets up the button click listeners and updates the UI.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup a click listener for the Submit and Skip buttons.
        binding.submit.setOnClickListener { onSubmitWord() }
        binding.skip.setOnClickListener { onSkipWord() }
        // Update the UI
        updateNextWordOnScreen()
        binding.score.text = getString(R.string.score, 0)
        binding.wordCount.text = getString(
                R.string.word_count, 0, MAX_NO_OF_WORDS)
    }

    /*
    * Checks the user's word, and updates the score accordingly.
    * Displays the next scrambled word.
    */

    //Click listener for the submit button
    // validate the user's guess by checking against the original word. If the word is correct, then go to the next word
    private fun onSubmitWord() {
        val playerWord = binding.textInputEditText.text.toString()

        if (viewModel.isUserWordCorrect(playerWord)){       //Logic for the error text field
            setErrorTextField(false)
            if (viewModel.nextWord()) {     //check on the return value of viewModel.nextWord() method.
                updateNextWordOnScreen()    //If true, another word is available, so update the scrambled word on screen using updateNextWordOnScreen()
            } else {
                showFinalScoreDialog()      //Otherwise the game is over, so display the alert dialog with the final score.
            }
        } else {
            setErrorTextField(true)
        }
    }

    /*
     * Skips the current word without changing the score.
     * Increases the word count.
     */

    //click listener for the Skip button, updates the UI similar to onSubmitWord() except the score.
    private fun onSkipWord() {
        if (viewModel.nextWord()) {
            setErrorTextField(false)
            updateNextWordOnScreen()
        } else {
            showFinalScoreDialog()
        }
    }

    /*
     * Gets a random word for the list of words and shuffles the letters in it.
     */
    private fun getNextScrambledWord(): String {
        val tempWord = allWordsList.random().toCharArray()
        tempWord.shuffle()
        return String(tempWord)
    }

    //Creates and shows an AlertDialog with the final score.
    private fun showFinalScoreDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.congratulations))      //Setting the title on the alert dialog, using a string resource from strings.xml.
            .setMessage(getString(R.string.you_scored, viewModel.score))    //Setting the message to show the final score, use the read only version of the score variable
            .setCancelable(false)   //Make your alert dialog not cancelable when the back key is pressed
            .setNegativeButton(getString(R.string.exit)) { _, _ ->
                exitGame()              //Exit game text button using setNegativeButton()
            }
            .setPositiveButton(getString(R.string.play_again)) { _, _ ->
                restartGame()           //Restart game text button using setPositiveButton()
            }
            .show()     //creates and displays the alert dialog
    }

    //Re-initializes the data in the ViewModel and updates the views with the new data, to restart the game.
    private fun restartGame() {
        viewModel.reinitializeData()
        setErrorTextField(false)
        updateNextWordOnScreen()
    }

    /*
     * Exits the game.
     */
    private fun exitGame() {
        activity?.finish()
    }

    //Will be called when the corresponding activity and fragment are destroyed
    override fun onDetach() {
        super.onDetach()
        Log.d("GameFragment", "GameFragment destroyed!")
    }

    /*
    * Sets and resets the text field error status.
    */
    //clears the text field content and resets the error status.
    private fun setErrorTextField(error: Boolean) {
        if (error) {
            binding.textField.isErrorEnabled = true
            binding.textField.error = getString(R.string.try_again)
        } else {
            binding.textField.isErrorEnabled = false
            binding.textInputEditText.text = null
        }
    }

    //Displays the next scrambled word on screen.
    private fun updateNextWordOnScreen() {
        binding.textViewUnscrambledWord.text = viewModel.currentScrambledWord
    }


}
