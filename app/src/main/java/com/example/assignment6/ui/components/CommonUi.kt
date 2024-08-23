/*  Assignment 6
    CommonUi.kt

    Nicholas Herman / hermnich@oregonstate.edu
    CS 492 / Oregon State University
*/

package com.example.assignment6.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Timer(
    time: Long,
    modifier: Modifier = Modifier
) {
    val seconds = time % 60
    val secondDisp = when {
        seconds < 10 -> "0$seconds"
        else -> "$seconds"
    }
    val minutes = (time - seconds) / 60
    val minuteDisp = when {
        (minutes % 60) < 10 -> "0${minutes % 60}"
        else -> "${minutes % 60}"
    }
    val hourDisp = (minutes - (minutes % 60)) / 60

    Card (
        modifier = modifier
            .width(100.dp)
            .height(50.dp)
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = modifier
                .fillMaxSize()
        ){
            Text(
                text = when (hourDisp) {
                    0L -> "$minuteDisp:$secondDisp"
                    else -> "$hourDisp:$minuteDisp:$secondDisp"
                },
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


@Preview
@Composable
fun PreviewTimer() {
    Timer(time = 4599)
}