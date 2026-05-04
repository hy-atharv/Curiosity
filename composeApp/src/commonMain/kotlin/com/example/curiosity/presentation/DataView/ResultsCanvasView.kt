package com.example.curiosity.presentation.DataView

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.curiosity.core.models.Fact
import com.example.curiosity.core.models.FactRelationship
import com.example.curiosity.core.models.FactRelationshipTypes
import com.example.curiosity.core.models.PageData
import com.example.curiosity.core.models.ResultData
import com.example.curiosity.core.models.ScreenSizeState
import com.example.curiosity.theme.ElectricBlue
import com.example.curiosity.theme.GoldenYellow
import com.example.curiosity.theme.VividMagenta
import curiosity.composeapp.generated.resources.Res
import curiosity.composeapp.generated.resources.back
import org.jetbrains.compose.resources.painterResource

@Composable
fun ResultsCanvasView(
    screenSizeState: ScreenSizeState,
    query: String,
    resultsData: ResultData,
    openSourcePage: (String) -> Unit,
    disableResultsCanvas: () -> Unit,
){
//    val resultsData = ResultData(
//        resultSummary = "While Dhruv Rathee denies direct political affiliation, a notable connection between him and the Aam Aadmi Party (AAP) stems from his video 'Is India Becoming a Dictatorship?'. This video, critical of the Modi government and discussing issues like the arrest of Arvind Kejriwal, was widely shared and promoted by AAP leaders, including Delhi Minister Atishi. This active endorsement by the AAP and other opposition parties has led critics to accuse Rathee of promoting an anti-BJP agenda and acting as a mouthpiece for the opposition, thus strengthening the perception of his alignment with AAP's narrative.",
//        resultPages = listOf(
//            PageData(
//                pageTitle = "Dhruv Rathee's 'Dictatorship' Video Shared By AAP, Opposition Leaders",
//                pageUrl = "https://www.ndtv.com/india-news/dhruv-rathees-dictatorship-video-shared-by-aap-opposition-leaders-5152862",
//                pageSummary = "YouTuber Dhruv Rathee posted a video titled 'Is India Becoming a Dictatorship?' which quickly gained significant viewership. The video discussed various issues including electoral bonds, the arrest of Arvind Kejriwal, and alleged misuse of central agencies, framing them as threats to Indian democracy. This video was widely shared by leaders of the Aam Aadmi Party (AAP) and other opposition parties, leading to accusations of Rathee being an AAP mouthpiece.",
//                pageDate = "2024-03-01",
//                pageCredibilityReason = "NDTV is a well-established and respected Indian news channel known for its journalistic standards and wide coverage of political events."
//            ),
//            PageData(
//                pageTitle = "Dhruv Rathee makes video on 'Is India becoming a dictatorship?', Delhi Minister Atishi shares it; gets over 10 million views",
//                pageUrl = "https://economictimes.indiatimes.com/news/india/dhruv-rathee-makes-video-on-is-india-becoming-a-dictatorship-delhi-minister-atishi-shares-it-gets-over-10-million-views/articleshow/108422452.cms",
//                pageSummary = "After Arvind Kejriwal's arrest, Dhruv Rathee released a video critical of the Modi government, titled 'Is India Becoming a Dictatorship?', which garnered immense attention. This video was widely promoted by the Aam Aadmi Party, including by Delhi Minister Atishi, who endorsed its content and urged people to watch it for understanding the 'truth' of India's situation.",
//                pageDate = "2024-03-12",
//                pageCredibilityReason = "The Economic Times is a leading Indian business newspaper, part of The Times Group, known for its extensive coverage of politics and economy."
//            ),
//            PageData(
//                pageTitle = "Who is Dhruv Rathee? All about the YouTuber who claims India is heading towards 'dictatorship'",
//                pageUrl = "https://timesofindia.indiatimes.com/web-stories/who-is-dhruv-rathee-all-about-the-youtuber-who-claims-india-is-heading-towards-dictatorship/photostory/108139501.cms",
//                pageSummary = "Dhruv Rathee's video on 'Is India becoming a Dictatorship?' triggered a significant debate, with critics accusing him of promoting an anti-BJP agenda and acting as a mouthpiece for the opposition, particularly the AAP.",
//                pageDate = "2024-03-01",
//                pageCredibilityReason = "The Times of India is one of the oldest and largest English-language newspapers in India, with a strong reputation for comprehensive news coverage."
//            )
//        ),
//        resultFacts = listOf(
//            Fact(
//                claim = "Dhruv Rathee produced a video titled 'Is India Becoming a Dictatorship?' discussing issues like electoral bonds and misuse of central agencies.",
//                evidence_urls = listOf(
//                    "https://www.ndtv.com/india-news/dhruv-rathees-dictatorship-video-shared-by-aap-opposition-leaders-5152862",
//                    "https://economictimes.indiatimes.com/news/india/dhruv-rathee-makes-video-on-is-india-becoming-a-dictatorship-delhi-minister-atishi-shares-it-gets-over-10-million-views/articleshow/108422452.cms"
//                )
//            ),
//            Fact(
//                claim = "The video gained significant viewership and discussed issues including the arrest of Arvind Kejriwal.",
//                evidence_urls = listOf(
//                    "https://www.ndtv.com/india-news/dhruv-rathees-dictatorship-video-shared-by-aap-opposition-leaders-5152862",
//                    "https://economictimes.indiatimes.com/news/india/dhruv-rathee-makes-video-on-is-india-becoming-a-dictatorship-delhi-minister-atishi-shares-it-gets-over-10-million-views/articleshow/108422452.cms"
//                )
//            ),
//            Fact(
//                claim = "The video was widely shared by leaders of the Aam Aadmi Party (AAP) and other opposition parties.",
//                evidence_urls = listOf("https://www.ndtv.com/india-news/dhruv-rathees-dictatorship-video-shared-by-aap-opposition-leaders-5152862")
//            ),
//            Fact(
//                claim = "Delhi Minister Atishi of the AAP actively promoted Dhruv Rathee's video, endorsing its content and urging viewership.",
//                evidence_urls = listOf("https://economictimes.indiatimes.com/news/india/dhruv-rathee-makes-video-on-is-india-becoming-a-dictatorship-delhi-minister-atishi-shares-it-gets-over-10-million-views/articleshow/108422452.cms")
//            )
//        ),
//        resultFactRelationships = listOf(
//            FactRelationship(
//                source_claim = "Dhruv Rathee produced a video titled 'Is India Becoming a Dictatorship?' discussing issues like electoral bonds and misuse of central agencies.",
//                target_claim = "The video was widely shared by leaders of the Aam Aadmi Party (AAP) and other opposition parties.",
//                connection_type = FactRelationshipTypes.CAUSES
//            ),
//            FactRelationship(
//                source_claim = "The video was widely shared by leaders of the Aam Aadmi Party (AAP) and other opposition parties.",
//                target_claim = "Delhi Minister Atishi of the AAP actively promoted Dhruv Rathee's video, endorsing its content and urging viewership.",
//                connection_type = FactRelationshipTypes.ELABORATES
//            ),
//            FactRelationship(
//                source_claim = "The frequent sharing of Dhruv Rathee's videos by AAP leaders fuels speculation about his political leanings and the narrative he presents.",
//                target_claim = "Critics have accused Dhruv Rathee of promoting an anti-BJP agenda and acting as a mouthpiece for the opposition, especially the AAP.",
//                connection_type = FactRelationshipTypes.SUPPORTS
//            ),
//            FactRelationship(
//                source_claim = "Dhruv Rathee produced a video titled 'Is India Becoming a Dictatorship?' discussing issues like electoral bonds and misuse of central agencies.",
//                target_claim = "The video gained significant viewership and discussed issues including the arrest of Arvind Kejriwal.",
//                connection_type = FactRelationshipTypes.ELABORATES
//            ),
//            FactRelationship(
//                source_claim = "This active promotion strengthened the perception of Rathee's alignment with AAP's narrative.",
//                target_claim = "Critics have accused Dhruv Rathee of promoting an anti-BJP agenda and acting as a mouthpiece for the opposition, especially the AAP.",
//                connection_type = FactRelationshipTypes.SUPPORTS
//            )
//        )
//    )

    // Tabs State
    var currentTab by remember {
        mutableStateOf<TabType>(TabType.RESULTS)
    }


    val colors = listOf<Color>(
        GoldenYellow,
        ElectricBlue,
        VividMagenta
    )

    val gradientBrush = remember {
        Brush.linearGradient(colors)
    }

    Box(
        modifier = Modifier.fillMaxSize(
            when(screenSizeState){
                ScreenSizeState.COMPACT -> 1f
                ScreenSizeState.MEDIUM -> 1f
                ScreenSizeState.EXPANDED -> 0.9f
            }
        ),
    ){
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            // TOP BAR
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 15.dp, bottom = 10.dp, start = 20.dp, end = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                // BACK
                Box(
                    modifier = Modifier.size(25.dp).clip(CircleShape)
                        .clickable{
                            // BACK TO QUERIES CANVAS
                            disableResultsCanvas()
                        },
                    contentAlignment = Alignment.Center
                ){
                    Icon(
                        painter = painterResource(Res.drawable.back),
                        tint = Color.White,
                        contentDescription = "Back to all queries",
                        modifier = Modifier.size(20.dp)
                    )
                }
                // TABS
                // Search Results
                Box(
                    modifier = Modifier.padding(start = 5.dp)
                        .clickable{
                            // OPEN SEARCH RESULTS
                            if (currentTab!= TabType.RESULTS){
                                currentTab = TabType.RESULTS
                            }
                        }
                        .pointerHoverIcon(PointerIcon.Hand),
                    contentAlignment = Alignment.Center
                ){
                    Text(
                        text = "Results",
                        style = if (currentTab==TabType.RESULTS){
                            MaterialTheme.typography.bodyLarge.copy(
                                brush = gradientBrush,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        else{
                            MaterialTheme.typography.bodyLarge.copy(
                                color = Color.Gray,
                                fontWeight = FontWeight.SemiBold
                            )
                        },
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(all = 5.dp)
                    )
                }
                // Facts
                Box(
                    modifier = Modifier
                        .clickable{
                            // OPEN FACTS
                            if (currentTab!= TabType.FACTS){
                                currentTab = TabType.FACTS
                            }
                        }
                        .pointerHoverIcon(PointerIcon.Hand),
                    contentAlignment = Alignment.Center
                ){
                    Text(
                        text = "Facts",
                        style = if (currentTab==TabType.FACTS){
                            MaterialTheme.typography.bodyLarge.copy(
                                brush = gradientBrush,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        else{
                            MaterialTheme.typography.bodyLarge.copy(
                                color = Color.Gray,
                                fontWeight = FontWeight.SemiBold
                            )
                        },
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(all = 5.dp)
                    )
                }
                // Knowledge Graph
                Box(
                    modifier = Modifier
                        .clickable{
                            // OPEN KNOWLEDGE GRAPH
                            if (currentTab!= TabType.KNOWLEDGE_GRAPH){
                                currentTab = TabType.KNOWLEDGE_GRAPH
                            }
                        }
                        .pointerHoverIcon(PointerIcon.Hand),
                    contentAlignment = Alignment.Center
                ){
                    Text(
                        text = "Knowledge Graph",
                        style = if (currentTab==TabType.KNOWLEDGE_GRAPH){
                            MaterialTheme.typography.bodyLarge.copy(
                                brush = gradientBrush,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        else{
                            MaterialTheme.typography.bodyLarge.copy(
                                color = Color.Gray,
                                fontWeight = FontWeight.SemiBold
                            )
                        },
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(all = 5.dp)
                    )
                }
                // EMPTY BOX TO MAKE ALIGNMENT BETTER FOR DESKTOP DISPLAY
                if (screenSizeState== ScreenSizeState.EXPANDED){
                    Box(
                        modifier = Modifier.size(25.dp).clip(CircleShape),
                    )
                }
            }
            // Results Tab View
            if (currentTab==TabType.RESULTS){
                androidx.compose.animation.AnimatedVisibility(
                    visible = currentTab==TabType.RESULTS,
                    enter = fadeIn(),
                    exit = fadeOut()
                ){
                    ResultsTabView(
                        query,
                        resultsData,
                        openSourcePage
                    )
                }
            }
            // Facts Tab View
            else if (currentTab==TabType.FACTS) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = currentTab==TabType.FACTS,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    FactsTabView(
                        query,
                        resultsData,
                        openSourcePage
                    )
                }
            }
            // Knowledge Graph Tab View
            else{
                androidx.compose.animation.AnimatedVisibility(
                    visible = currentTab==TabType.KNOWLEDGE_GRAPH,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    KnowledgeGraphTabView(
                        screenSizeState,
                        query,
                        resultsData
                    )
                }
            }
        }
    }
}



enum class TabType{
    RESULTS,
    FACTS,
    KNOWLEDGE_GRAPH
}