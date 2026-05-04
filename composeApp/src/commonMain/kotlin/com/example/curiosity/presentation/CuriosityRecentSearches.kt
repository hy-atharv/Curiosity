package com.example.curiosity.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.example.curiosity.core.models.CurrentUserSessionDataViewModel
import com.example.curiosity.core.models.RenamePopUpData
import com.example.curiosity.core.navigation.RecentSearchScreen
import com.example.curiosity.theme.BlackBackgroundShade
import curiosity.composeapp.generated.resources.Res
import curiosity.composeapp.generated.resources.close_new
import curiosity.composeapp.generated.resources.curiosity_delete_search_icon
import curiosity.composeapp.generated.resources.curiosity_edit_name_icon
import curiosity.composeapp.generated.resources.curiosity_recentsearch_options
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

@Composable
fun RecentSearchItem(
    sessionDataViewModel: CurrentUserSessionDataViewModel,
    searchId: Int,
    drawerState: DrawerState,
    scope: CoroutineScope,
    navigator: NavHostController
){

    val searchHeading = sessionDataViewModel.searchDataMap.value[searchId]?.searchHeading ?: "Not Found"

    val recentSearchOptionsVisible by sessionDataViewModel.searchOptionsPopUpState.collectAsState()

    val currentOpenOptionsId by sessionDataViewModel.searchOptionsPopUpIdState.collectAsState()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.padding(vertical = 5.dp)
    ){
        Box(
            modifier = Modifier.widthIn(
                min = 200.dp, max = 200.dp
            ),
            contentAlignment = Alignment.CenterStart
        )
        {
            Text(
                text = searchHeading,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Normal,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.clickable{
                    scope.launch {
                        if (drawerState.isOpen){
                            drawerState.close()
                        }
                    }
                    navigator.navigate(RecentSearchScreen(searchId))
                }.pointerHoverIcon(PointerIcon.Hand)
            )
        }
        Icon(
            painter = painterResource(
                resource = when(recentSearchOptionsVisible && currentOpenOptionsId == searchId){
                    false -> Res.drawable.curiosity_recentsearch_options
                    true -> Res.drawable.close_new
                }
            ),
            tint = Color.Gray,
            contentDescription = searchHeading,
            modifier = Modifier.padding(start = 15.dp).size(20.dp)
                .clickable{
                    sessionDataViewModel.switchSearchOptionsPopUpId(searchId)
                    sessionDataViewModel.toggleSearchOptionsPopUp()
                }
        )

    }
    if (recentSearchOptionsVisible && currentOpenOptionsId == searchId){
        Box(
            modifier = Modifier.absoluteOffset(x = 102.dp)
                .drawBehind {

                    val tailWidth = 10.dp.toPx()
                    val tailHeight = 8.dp.toPx()

                    val path = Path().apply {
                        // Top-end triangle
                        moveTo(size.width - tailWidth, 0f)
                        lineTo(size.width, -tailHeight)
                        lineTo(size.width, 0f)
                        close()
                    }

                    drawPath(
                        path = path,
                        color = BlackBackgroundShade
                    )
                }
                .clip(RoundedCornerShape(
                    topStart = 15.dp,
                    bottomStart = 15.dp,
                    bottomEnd = 15.dp
                ))
                .background(color = BlackBackgroundShade)
                .zIndex(10f),
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.padding(top = 20.dp, bottom = 12.dp, start = 20.dp, end = 20.dp)
                        .clickable{
                            // LOGIC TO RENAME SEARCH
                            sessionDataViewModel.enableRenamePopUp(
                                data = RenamePopUpData(
                                    oldHeading = searchHeading,
                                    searchId = searchId
                                )
                            )
                        }
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.curiosity_edit_name_icon),
                        tint = Color.Gray,
                        contentDescription = "Edit Name",
                        modifier = Modifier.padding(end = 10.dp).size(20.dp)
                    )
                    Text(
                        text = "Rename",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                    )
                }
                HorizontalDivider(
                    thickness = 0.25.dp,
                    modifier = Modifier.padding(horizontal = 10.dp).widthIn(max = 100.dp),
                    color = Color.DarkGray
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.padding(top = 12.dp, bottom = 20.dp, start = 20.dp, end = 20.dp)
                        .clickable{
                            // LOGIC TO DELETE SEARCH
                            sessionDataViewModel.deleteSearchData(searchId)
                            sessionDataViewModel.toggleSearchOptionsPopUp()
                        }
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.curiosity_delete_search_icon),
                        tint = Color(0xFFED7F7D),
                        contentDescription = "Delete Search",
                        modifier = Modifier.padding(end = 10.dp).size(20.dp)
                    )
                    Text(
                        text = "Delete",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFED7F7D),
                    )
                }
            }
        }
    }
}


@Composable
fun RenamePopup(
    sessionDataViewModel: CurrentUserSessionDataViewModel,
){
    val keyboardController = LocalSoftwareKeyboardController.current

    val popUpData by sessionDataViewModel.renamePopUpData.collectAsState()
    val searchHeading = popUpData.oldHeading
    val searchId = popUpData.searchId

    val textFieldState = remember { mutableStateOf(TextFieldValue(searchHeading)) }

    val focusRequester = remember { FocusRequester() }


    Dialog(
        onDismissRequest = {
            sessionDataViewModel.disableRenamePopUp()
        },
    ){
        // AUTOFOCUS AND AUTO KEYBOARD OPEN
        LaunchedEffect(Unit){
            focusRequester.requestFocus()
            textFieldState.value = textFieldState.value.copy(
                selection = TextRange(0, searchHeading.length)
            )
        }

        Box(
            modifier = Modifier.padding(10.dp).imePadding().widthIn(max = 500.dp).clip(RoundedCornerShape(15.dp))
                .background(color = BlackBackgroundShade).zIndex(12f),
            contentAlignment = Alignment.Center
        ){
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = textFieldState.value,
                    onValueChange = {
                        textFieldState.value = it
                    },
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.White
                    ),
                    maxLines = 3,
                    shape = RoundedCornerShape(5.dp),
                    label = {
                        Text(
                            text = "New name",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Normal,
                            color = Color.White,
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done,
                        autoCorrectEnabled = true,
                        showKeyboardOnFocus = true
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            val newName = textFieldState.value.text.trim()
                            if (newName.isNotEmpty()){
                                sessionDataViewModel.renameSearchData(searchId, newName)
                                sessionDataViewModel.disableRenamePopUp()
                                sessionDataViewModel.toggleSearchOptionsPopUp()
                            }
                            keyboardController?.hide()
                        }
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = BlackBackgroundShade,
                        unfocusedContainerColor = BlackBackgroundShade,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White,
                        disabledBorderColor = Color.White,
                        errorBorderColor = Color.White,
                        errorContainerColor = BlackBackgroundShade,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        disabledTextColor = Color.White,
                        errorTextColor = Color.White,
                        cursorColor = Color.White,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White,
                    ),
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                        .focusRequester(focusRequester)
                        .onPreviewKeyEvent { event ->
                            if (
                                event.type == KeyEventType.KeyDown &&
                                (event.key == Key.Enter || event.key == Key.NumPadEnter) &&
                                !event.isShiftPressed
                            ) {
                                val newName = textFieldState.value.text.trim()
                                if (newName.isNotEmpty()){
                                    sessionDataViewModel.renameSearchData(searchId, newName)
                                    sessionDataViewModel.disableRenamePopUp()
                                    sessionDataViewModel.toggleSearchOptionsPopUp()
                                }
                                true
                            }
                            else{
                                false
                            }
                        }
                )
                Text(
                    text = "Rename",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.End).padding(end = 20.dp, bottom = 10.dp)
                        .clickable{
                            val newName = textFieldState.value.text.trim()
                            if (newName.isNotEmpty()){
                                sessionDataViewModel.renameSearchData(searchId, newName)
                                sessionDataViewModel.disableRenamePopUp()
                                sessionDataViewModel.toggleSearchOptionsPopUp()
                            }
                            keyboardController?.hide()
                        }
                )
            }
        }
    }
}


