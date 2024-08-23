/*  Assignment 6
    UiState.kt

    Nicholas Herman / hermnich@oregonstate.edu
    CS 492 / Oregon State University
*/

package com.example.assignment6.data

import com.example.assignment6.model.Clue

data class UiState(
    val clue: Clue = DataSource.clues.first(),
    val clueIndex: Int = 0,
    val hasLocationAccess: Boolean,
    val distance: Double? = null,
    val isLastClue: Boolean = false,
    val time: Long = 0,
    val timerPaused: Boolean = true
)