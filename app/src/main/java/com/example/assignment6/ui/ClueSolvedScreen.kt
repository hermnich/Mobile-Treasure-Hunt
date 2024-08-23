/*  Assignment 6
    ClueSolvedScreen.kt

    Nicholas Herman / hermnich@oregonstate.edu
    CS 492 / Oregon State University
*/

package com.example.assignment6.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assignment6.R
import com.example.assignment6.data.DataSource
import com.example.assignment6.model.Clue
import com.example.assignment6.ui.components.Timer

@Composable
fun ClueSolvedScreen(
    time: Long,
    clue: Clue,
    onContinueClicked: (() -> Unit)
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(30.dp),
        modifier = Modifier
            .padding(16.dp)
    ) {
        Timer(time = time)
        Text(
            text = stringResource(clue.info),
            fontSize = 20.sp
        )
        Button(
            onClick = onContinueClicked,
            modifier = Modifier
                .width(150.dp)
                .height(60.dp)
        ) {
            Text(
                text = stringResource(R.string.cont),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
@Preview
fun PreviewClueSolvedScreen() {
    ClueSolvedScreen(
        time = 0,
        clue = DataSource.clues[1],
        onContinueClicked = {}
    )
}
