/*  Assignment 6
    ClueScreen.kt

    Nicholas Herman / hermnich@oregonstate.edu
    CS 492 / Oregon State University
*/

package com.example.assignment6.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import kotlinx.coroutines.delay

@Composable
fun ClueScreen(
    time: Long,
    clue: Clue,
    distance: Double?,
    onFoundClicked: (() -> Unit),
    onQuitClicked: (() -> Unit),
    onTimerUpdate: (() -> Unit),
    onFound: (() -> Unit),
    onNotFound: (() -> Unit)
) {

    val openDialog = remember { mutableStateOf(false) }

    val showHint = remember { mutableStateOf(false) }

    when {
        openDialog.value -> {
            WrongLocationDialog(
                onDismissRequest = { openDialog.value = false },
                onConfirmation = {
                    openDialog.value = false
                }
            )
        }
    }

    LaunchedEffect(onTimerUpdate) {
        delay(1000)
        onTimerUpdate()
        if (distance != null) {
            if (distance <= 0.05) {
                onFound()
            } else {
                openDialog.value = true
                onNotFound()
            }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(30.dp),
        modifier = Modifier
            .padding(16.dp)
    ) {
        Timer(time = time)
        Text(
            text = "${stringResource(R.string.clue)} ${stringResource(clue.clue)}",
            fontSize = 20.sp
        )
        when {
            showHint.value -> {
                Text(
                    text = "${stringResource(R.string.hint)} ${stringResource(clue.hint)}",
                    fontSize = 20.sp
                )
            }
            else -> {
                Button(
                    onClick = { showHint.value = true},
                    modifier = Modifier
                        .width(150.dp)
                        .height(60.dp)
                ) {
                    Text(
                        text = stringResource(R.string.need_a_hint),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ){
            Button(
                onClick = onFoundClicked,
                modifier = Modifier
                    .width(150.dp)
                    .height(60.dp)
            ) {
                Text(
                    text = stringResource(R.string.found_it),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Button(
                onClick = onQuitClicked,
                modifier = Modifier
                    .width(150.dp)
                    .height(60.dp)
            ) {
                Text(
                    text = stringResource(R.string.quit),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
@Preview
fun PreviewClueScreen() {
    ClueScreen(
        clue = DataSource.clues.first(),
        time = 12345,
        distance = 0.0,
        onFoundClicked = {},
        onQuitClicked = {},
        onTimerUpdate = {},
        onFound = {},
        onNotFound = {}
    )
}

@Composable
fun WrongLocationDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit
) {
    AlertDialog(
        title = {
            Text(text = stringResource(R.string.location_incorrect))
        },
        text = {
            Text(text = stringResource(R.string.please_try_again))
        },
        onDismissRequest = { onDismissRequest() },
        confirmButton = {
            TextButton(
                onClick = { onConfirmation() }
            ) {
                Text(stringResource(R.string.ok))
            }
        }
    )
}

@Preview
@Composable
fun PreviewWrongLocationDialog() {
    WrongLocationDialog(
        onDismissRequest = {},
        onConfirmation = {}
    )
}