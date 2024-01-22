package ai.botstacks.sample

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ai.botstacks.sdk.BotStacksChat
import ai.botstacks.sdk.ui.views.Spinner

@Composable
fun Splash(openLogin: () -> Unit, openChat: () -> Unit, content: @Composable (() -> Unit)? = null) {
    if (content == null) {
        LaunchedEffect(key1 = BotStacksChat.shared.loaded, block = {
            if (BotStacksChat.shared.loaded) {
                if (BotStacksChat.shared.isUserLoggedIn) {
                    openChat()
                } else {
                    openLogin()
                }
            }
        })
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.inappchat_icon),
                contentDescription = "InAppChat",
                modifier = Modifier
                    .width(120.dp)
                    .height(120.dp)
            )
            Icon(
                painter = painterResource(id = R.drawable.inappchat_text),
                contentDescription = "InAppChat",
                tint = Color.White,
                modifier = Modifier.width(225.dp)
            )
            Text("Simple and elegant chat services", fontSize = 20.sp, color = Color.White)
            if (!BotStacksChat.shared.loaded || BotStacksChat.shared.loggingIn)
                Spinner()
        }
        content?.invoke()
    }
}

@Preview(
    name = "Dark mode",
    group = "UI mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    showBackground = true,
    backgroundColor = 0xFF000000
)
@Composable
fun SplashPreview() {
    MaterialTheme {
        Splash(openChat = {}, openLogin = {})
    }
}


@Composable
fun InAppChatLogo() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(15.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Image(
            painter = painterResource(id = R.drawable.inappchat_icon),
            contentDescription = "InAppChat",
            modifier = Modifier
                .width(120.dp)
                .height(120.dp)
        )
        Icon(
            painter = painterResource(id = R.drawable.inappchat_text),
            contentDescription = "InAppChat",
            tint = Color.White,
            modifier = Modifier.width(225.dp)
        )
        Text("Simple and elegant chat services", fontSize = 20.sp, color = Color.White)
        if (!BotStacksChat.shared.loaded || BotStacksChat.shared.loggingIn)
            Spinner()
    }
}

@Composable
fun InAppChatHeader() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(15.dp),
    ) {
        Image(
            painter = painterResource(id = R.drawable.inappchat_icon),
            contentDescription = "InAppChat",
            modifier = Modifier
                .width(36.dp)
                .height(36.dp)
        )
        Icon(
            painter = painterResource(id = R.drawable.inappchat_text),
            contentDescription = "InAppChat",
            tint = Color.White,
            modifier = Modifier.height(36.dp)
        )
    }
}