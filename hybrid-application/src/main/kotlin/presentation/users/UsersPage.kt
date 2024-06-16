package presentation.users

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import presentation.other.components.BackButton
import presentation.other.components.SearchField
import presentation.other.components.UserButton
import presentation.other.extension.padding.ExtensionPadding
import presentation.users.store.Action
import presentation.users.store.Event
import presentation.users.viewmodel.UsersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsersPage(onEvent: (Event) -> Unit, usersViewModel: UsersViewModel, onAction: suspend (Action) -> Unit) {

    val state = usersViewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        onAction(Action.InitUsers)
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues = ExtensionPadding.mediumAsymmetricalContentPadding),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = ExtensionPadding.mediumHorizontalArrangement
            ) {
                BackButton(
                    onClick = { onEvent(Event.BackClicked) }
                )
                SearchField(onSearchClick = {
                    onEvent(Event.OnSearch(query = it))
                })
            }
        },
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = ExtensionPadding.scaffoldTopPadding)
                .padding(ExtensionPadding.mediumAsymmetricalContentPadding),
            verticalArrangement = ExtensionPadding.largeVerticalArrangementTop,
            horizontalAlignment = Alignment.Start
        ) {
            itemsIndexed(state.value.searchList) { index, item ->
                UserButton(
                    onClick = {
                        onEvent(Event.OnUserClick(userId = item.id))
                    },
                    text = "${item.surname} ${item.name} ${item.patronymic}"
                )
            }
        }
    }
}