/*  Assignment 6
    Clue.kt

    Nicholas Herman / hermnich@oregonstate.edu
    CS 492 / Oregon State University
*/

package com.example.assignment6.model

import androidx.annotation.StringRes

data class Clue(
    @StringRes val clue: Int,
    @StringRes val hint: Int,
    @StringRes val info: Int,
    val latitude: Double,
    val longitude: Double
)