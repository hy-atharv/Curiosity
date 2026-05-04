package com.example.curiosity.presentation.DataView

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.curiosity.core.models.ResultData
import com.example.curiosity.theme.ElectricBlue
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsTabView(
    query: String,
    resultsData: ResultData,
    openSourcePage: (String) -> Unit
){
    val overviewAnswer = resultsData.resultSummary
    val results = resultsData.resultPages

    val expandedResultsIndices = remember {
        mutableStateListOf<Int>()
    }

    LazyColumn(
        contentPadding = PaddingValues(top = 10.dp, bottom = 90.dp, start = 25.dp, end = 25.dp),
    ) {
        // Query
        item {
            Text(
                text = query,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                ),
                color = Color.LightGray,
                textAlign = TextAlign.Start,
                maxLines = 3,
                modifier = Modifier.padding(bottom = 20.dp)
            )
        }
        // Summary
        item {
            Column(
                modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "At a Glance",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.LightGray,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                Text(
                    text = overviewAnswer,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    textAlign = TextAlign.Start
                )
            }
        }
        // Results
        item {
            Text(
                text = "Sources",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color.LightGray,
                textAlign = TextAlign.Start,
            )
        }
        items(results.size){ index ->
            val tooltipState = rememberTooltipState()
            val scope = rememberCoroutineScope()
            Box(
                modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)
            ){
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    // Domain
                    Text(
                        text = results[index].pageUrl.substringAfter("//").substringBefore("/").removePrefix("www."),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = Color.White,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.padding(bottom = 7.dp)
                    )
                    // Title
                    Text(
                        text = results[index].pageTitle,
                        style = MaterialTheme.typography.bodyLarge,
                        color = ElectricBlue,
                        textAlign = TextAlign.Start,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(bottom = 7.dp)
                            .clickable{
                                // OPEN PAGE
                                openSourcePage(results[index].pageUrl)
                            }
                            .pointerHoverIcon(PointerIcon.Hand)
                    )
                    // Page Summary
                    Text(
                        text = results[index].pageSummary,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.LightGray,
                        textAlign = TextAlign.Start,
                        maxLines = if (expandedResultsIndices.contains(index)){
                            Int.MAX_VALUE
                        }
                        else{
                            3
                        },
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(bottom = 7.dp)
                    )
                    // See Full Summary
                    Text(
                        text = if (expandedResultsIndices.contains(index)){
                            "See less"
                        }
                        else{
                            "See full summary" //"Expand Summary"
                        },
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            textDecoration = TextDecoration.Underline
                        ),
                        color = Color.LightGray,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.padding(bottom = 7.dp)
                            .clickable {
                                // Toggle expanded state
                                if (expandedResultsIndices.contains(index)){
                                    expandedResultsIndices.remove(index)
                                }
                                else{
                                    expandedResultsIndices.add(index)
                                }
                            }
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Page Date
                        Text(
                            text = formatDate(results[index].pageDate),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            textAlign = TextAlign.Start,
                        )
                        TooltipBox(
                            positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                                TooltipAnchorPosition.Above,
                                spacingBetweenTooltipAndAnchor = 2.dp
                            ),
                            tooltip = { PlainTooltip(
                                containerColor = Color.Black
                            ) {
                                Text(
                                    results[index].pageCredibilityReason,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.LightGray,
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 5.dp)
                                )
                            } },
                            state = tooltipState
                        ){
                            // Domain Credibility i button
                            Box(
                                modifier = Modifier.size(15.dp).clip(CircleShape)
                                    .border(color = Color.LightGray, width = 0.75.dp, shape = CircleShape)
                                    .clickable{
                                        // Show/Hide Domain Credibility
                                        scope.launch {
                                            tooltipState.show()
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ){
                                Text(
                                    text = "i",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = Color.LightGray,
                                    textAlign = TextAlign.Start,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

fun formatDate(dateString: String): String {
    return try {
        val date = LocalDate.parse(dateString)

        val monthName = when (date.month.number) {
            1 -> "January"; 2 -> "February"; 3 -> "March"
            4 -> "April"; 5 -> "May"; 6 -> "June"
            7 -> "July"; 8 -> "August"; 9 -> "September"
            10 -> "October"; 11 -> "November"; 12 -> "December"
            else -> ""
        }

        "${date.dayOfMonth} $monthName, ${date.year}"
    } catch (e: Exception) {
        dateString
    }
}