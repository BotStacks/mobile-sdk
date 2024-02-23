/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sdk.navigation.ui

// TODO: Do we need this screen?

//import android.Manifest
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.lazy.rememberLazyListState
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import ai.botstacks.sdk.state.BotStacksChatStore
//import ai.botstacks.sdk.state.User
//import ai.botstacks.sdk.ui.BotStacks.colorScheme
//import ai.botstacks.sdk.ui.BotStacks.fonts
//import ai.botstacks.sdk.ui.components.Header
//import ai.botstacks.sdk.ui.components.PagerList
//import ai.botstacks.sdk.ui.components.Text
//import ai.botstacks.sdk.ui.components.UserRow
//import ai.botstacks.sdk.ui.components.radius
//import androidx.compose.foundation.layout.fillMaxWidth
//
//@Composable
//fun ContactsView(scrollToTop: Int, openProfile: (User) -> Unit) {
//    val onPermission = { havePermission: Boolean ->
//        if (havePermission) {
//            BotStacksChatStore.current.contacts.syncContacts()
//        }
//        BotStacksChatStore.current.contacts.requestContacts = false
//    }
//    val permissionLauncher = rememberLauncherForActivityResult(
//        ActivityResultContracts.RequestPermission()
//    ) {
//        onPermission(it)
//    }
//    val listState = rememberLazyListState()
//    Column {
//        Header(title = "My Contacts")
//        PagerList(
//            pager = BotStacksChatStore.current.contacts,
//            scrollToTop = scrollToTop.toString(),
//            header = {
//                if (BotStacksChatStore.current.contacts.requestContacts) {
//                    Box(modifier = Modifier.padding(16.dp)) {
//                        Column(
//                            modifier =
//                            Modifier
//                                .radius(15.dp)
//                                .background(colorScheme.header)
//                                .padding(16.dp),
//                            verticalArrangement = Arrangement.spacedBy(2.dp)
//                        ) {
//                            Text(
//                                text = "Find your friends",
//                                fontStyle = fonts.h3,
//                                color = colorScheme.onHeader
//                            )
//                            Text(
//                                "Sync your contacts to easily find people you know. Your contacts will only be to help you connect with friends.",
//                                fontStyle = fonts.body1.copy(weight = FontWeight.Bold),
//                                color = colorScheme.onHeader
//                            )
//                            Row(modifier = Modifier
//                                .radius(13.dp)
//                                .background(colorScheme.background)
//                                .clickable {
//                                    permissionLauncher.launch(Manifest.permission.READ_CONTACTS)
//                                }
//                                .padding(20.dp, 0.dp)
//                            ) {
//                                Text(
//                                    text = "Sync",
//                                    fontStyle = fonts.body1.copy(weight = FontWeight.Bold),
//                                    color = colorScheme.onHeader
//                                )
//                            }
//                        }
//                    }
//                }
//            },
//        ) {
//            UserRow(
//                modifier = Modifier.fillMaxWidth(),
//                user = it
//            ) {
//                openProfile(it)
//            }
//        }
//    }
//    LaunchedEffect(key1 = scrollToTop, block = {
//        if (scrollToTop > 0) {
//            listState.animateScrollToItem(0)
//        }
//    })
//}