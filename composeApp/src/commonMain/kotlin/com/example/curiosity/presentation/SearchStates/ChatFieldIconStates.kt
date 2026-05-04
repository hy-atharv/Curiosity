package com.example.curiosity.presentation.SearchStates

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.unit.dp
import curiosity.composeapp.generated.resources.Res
import curiosity.composeapp.generated.resources.curiosity_search_active_icon
import curiosity.composeapp.generated.resources.curiosity_search_inactive_icon
import org.jetbrains.compose.resources.vectorResource

@Composable
fun ActiveTextFieldMessageSendIcon(
    keyboardController: SoftwareKeyboardController?,
    onMessageQuerySent: (String) -> Unit,
    messageQuery: String,
    messageFieldState: TextFieldState
){
    Box(
        modifier = Modifier.padding(
            start = 20.dp,
            top = 5.dp,
            bottom = 5.dp,
            end = 5.dp
        )
            .size(36.dp)
            .clip(CircleShape)
            .background(color = Color.White)
            .clickable{
                keyboardController?.hide()
                // SEND QUERY LOGIC
                onMessageQuerySent(messageQuery)
                messageFieldState.clearText()
            }
            .pointerHoverIcon(PointerIcon.Default),
        contentAlignment = Alignment.Center
    ){
        Icon(
            imageVector = vectorResource(Res.drawable.curiosity_search_active_icon),
            contentDescription = "Send Icon",
            tint = Color.Unspecified,
            modifier = Modifier.padding(4.dp).fillMaxSize()
        )
    }
}

@Composable
fun InactiveTextFieldMessageSendIcon(){
    Box(
        modifier = Modifier.padding(
            start = 20.dp,
            top = 5.dp,
            bottom = 5.dp,
            end = 5.dp
        )
            .size(36.dp)
            .clip(CircleShape)
            .background(color = Color.Black),
        contentAlignment = Alignment.Center
    ){
        Icon(
            imageVector = vectorResource(Res.drawable.curiosity_search_inactive_icon),
            contentDescription = "Send Icon",
            tint = Color.Unspecified,
            modifier = Modifier.padding(4.dp).fillMaxSize()
        )
    }
}