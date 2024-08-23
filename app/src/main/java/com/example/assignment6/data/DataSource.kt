/*  Assignment 6
    DataSource.kt

    Nicholas Herman / hermnich@oregonstate.edu
    CS 492 / Oregon State University
*/

package com.example.assignment6.data

import com.example.assignment6.R
import com.example.assignment6.model.Clue

object DataSource {
    const val lastClueIndex = 1

    val clues = listOf(
        Clue(
            clue = R.string.clue1,
            hint = R.string.hint1,
            info = R.string.info1,
            latitude = 41.5130,
            longitude = -81.5904
        ),
        Clue(
            clue = R.string.clue2,
            hint = R.string.hint2,
            info = R.string.info2,
            latitude = 41.4849,
            longitude = -81.7029
        )
    )
}