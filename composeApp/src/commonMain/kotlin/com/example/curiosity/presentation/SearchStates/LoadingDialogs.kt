package com.example.curiosity.presentation.SearchStates


import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun BoxScope.IndexingDialog(){
    Text(
        text = "Searching the index...",
        style = MaterialTheme.typography.bodyLarge.copy(
            color = Color.Gray,
            fontWeight = FontWeight.SemiBold
        ),
        textAlign = TextAlign.Start,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 15.dp).align(Alignment.TopStart)
    )
}

@Composable
fun BoxScope.CrawlingDialog(){
    Text(
        text = "Crawling the web...",
        style = MaterialTheme.typography.bodyLarge.copy(
            color = Color.Gray,
            fontWeight = FontWeight.SemiBold
        ),
        textAlign = TextAlign.Start,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 15.dp).align(Alignment.TopStart)
    )
}

@Composable
fun BoxScope.ErrorDialog(
    errorMessage: String
){
    Text(
        text = errorMessage,
        style = MaterialTheme.typography.bodyLarge.copy(
            color = Color(0xFFED7F7D),
            fontWeight = FontWeight.SemiBold
        ),
        textAlign = TextAlign.Start,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 15.dp).align(Alignment.TopStart)
    )
}