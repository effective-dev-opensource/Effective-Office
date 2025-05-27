package band.effective.office.elevator.ui.authorization.authorization_google

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import band.effective.office.elevator.EffectiveTheme
import band.effective.office.elevator.MainRes
import band.effective.office.elevator.components.EffectiveGradient
import band.effective.office.elevator.components.buttons.GoogleSignInButton
import band.effective.office.elevator.expects.showToast
import band.effective.office.elevator.ui.authorization.authorization_google.store.AuthorizationGoogleStore
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun AuthorizationGoogleScreen(component: AuthorizationGoogleComponent) {

    LaunchedEffect(component) {
        component.label.collect { label ->
            when (label) {
                is AuthorizationGoogleStore.Label.AuthorizationFailure -> showToast(label.message)
                is AuthorizationGoogleStore.Label.AuthorizationSuccess -> {
                    component.onOutput(
                        AuthorizationGoogleComponent.Output.OpenAuthorizationPhoneScreen(label.user)
                    )
                }
            }
        }
    }
    val state by component.state.collectAsState()

    AuthorizationGoogleScreenContent(
        isAuthInProgress = state.isAuthInProgress,
        onEvent = component::onEvent,
    )
}


@Composable
private fun AuthorizationGoogleScreenContent(
    isAuthInProgress: Boolean,
    onEvent: (AuthorizationGoogleStore.Intent) -> Unit,
) {
    EffectiveGradient()
    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            color = EffectiveTheme.colors.background.primary.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .border(
                            width = 1.dp,
                            shape = RoundedCornerShape(20.dp),
                            color = EffectiveTheme.colors.stroke.primary
                        )
                        .padding(20.dp)
                ) {
                    Image(
                        painter = painterResource(MainRes.images.ic_logo),
                        contentDescription = null,
                        modifier = Modifier.size(90.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(MainRes.strings.company_title_name),
                    color = EffectiveTheme.colors.text.primary,
                    style = EffectiveTheme.typography.xlMedium
                )
                Text(
                    text = stringResource(MainRes.strings.company_subtitle_name),
                    color = EffectiveTheme.colors.text.primary,
                    style = EffectiveTheme.typography.sMedium
                )
            }
            GoogleSignInButton(
                modifier = Modifier,
                isEnabled = !isAuthInProgress,
                onClick = { onEvent(AuthorizationGoogleStore.Intent.SignInButtonClicked) }
            )
        }
    }
}

