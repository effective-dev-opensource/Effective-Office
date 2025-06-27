package band.effective.office.elevator.ui.profile.editProfile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import band.effective.office.elevator.ExtendedThemeColors
import band.effective.office.elevator.MainRes
import band.effective.office.elevator.components.EffectiveButton
import band.effective.office.elevator.components.LoadingIndicator
import band.effective.office.elevator.components.OutlinedTextColorsSetup
import band.effective.office.elevator.components.TitlePage
import band.effective.office.elevator.components.UserInfoTextField
import band.effective.office.elevator.ui.main.SnackBarErrorMessage
import band.effective.office.elevator.ui.models.PhoneMaskTransformation
import band.effective.office.elevator.ui.models.UserDataTextFieldType
import band.effective.office.elevator.ui.models.getAllUserDataEditProfile
import band.effective.office.elevator.ui.models.validator.UserInfoValidator
import band.effective.office.elevator.ui.profile.editProfile.store.ProfileEditStore
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.delay

@Composable
fun ProfileEditScreen(component: ProfileEditComponent) {
    val user by component.user.collectAsState()

    when {
        user.isData -> {
            ProfileEditView(
                user = user,
                component = component
            )
        }

        user.isLoading -> {
            LoadingIndicator()
        }

        else -> {}
    }

}

@Composable
fun ProfileEditView(user: ProfileEditStore.State, component: ProfileEditComponent) {

    var errorMessage by remember { mutableStateOf(MainRes.strings.something_went_wrong) }
    var isErrorMessageVisible by remember { mutableStateOf(false) }

    LaunchedEffect(component) {
        component.label.collect { label ->
            when (label) {
                is ProfileEditStore.Label.ReturnedInProfile -> component.onOutput(
                    ProfileEditComponent.Output.NavigationBack
                )

                is ProfileEditStore.Label.SavedChange -> component.onOutput(ProfileEditComponent.Output.NavigationBack)
                is ProfileEditStore.Label.Error -> {
                    errorMessage = label.name
                    isErrorMessageVisible = true
                    delay(3000)
                    isErrorMessageVisible = false
                }
            }
        }
    }
    ProfileEditScreenContent(
        isErrorMessageVisible = isErrorMessageVisible,
        errorMessage = errorMessage,
        isErrorTelegram = user.isErrorTelegram,
        isErrorPost = user.isErrorPost,
        isErrorName = user.isErrorName,
        isErrorPhone = user.isErrorPhone,
        userName = user.user.userName,
        post = user.user.post,
        telegram = user.user.telegram,
        phoneNumber = user.user.phoneNumber,
        onReturnToProfile = { component.onEvent(ProfileEditStore.Intent.BackInProfileClicked) },
        onSaveChange = { userName, post, phoneNumber, telegram ->
            component.onEvent(
                ProfileEditStore.Intent.SaveChangeClicked(
                    userName = userName,
                    post = post,
                    telegram = telegram,
                    phoneNumber = phoneNumber
                )
            )
        }
    )
}

@Composable
private fun ProfileEditScreenContent(
    isErrorPhone: Boolean,
    userName: String,
    post: String,
    telegram: String,
    phoneNumber: String,
    onReturnToProfile: () -> Unit,
    onSaveChange: (userName: String, post: String, phoneNumber: String, telegram: String) -> Unit,
    isErrorName: Boolean,
    isErrorPost: Boolean,
    isErrorTelegram: Boolean,
    errorMessage: StringResource,
    isErrorMessageVisible: Boolean
) {
    val phone = if (phoneNumber.length > UserInfoValidator.phoneNumberSize)
        phoneNumber.substring(
            startIndex = phoneNumber.length % UserInfoValidator.phoneNumberSize,
        )
    else
        phoneNumber

    val userNameText = rememberSaveable { mutableStateOf(userName) }
    val phoneNumberText = rememberSaveable { mutableStateOf(phone) }
    val postText = rememberSaveable { mutableStateOf(post) }
    val telegramText = rememberSaveable { mutableStateOf(telegram) }

    Box {
        Column(
            modifier = Modifier.fillMaxSize().background(ExtendedThemeColors.colors.whiteColor)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            ProfileEditHeader(onReturnToProfile)
            LazyColumn(modifier = Modifier.padding(top = 28.dp)) {
                items(getAllUserDataEditProfile()) { item ->
                    when (item) {
                        UserDataTextFieldType.Phone -> {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                stringResource(item.title),
                                modifier = Modifier.padding(bottom = 8.dp),
                                style = MaterialTheme.typography.body1,
                                color = ExtendedThemeColors.colors.blackColor
                            )
                            UserInfoTextField(
                                item = item,
                                text = phoneNumberText.value,
                                error = isErrorPhone,
                                visualTransformation = PhoneMaskTransformation,
                            onValueChange = { phoneNumberText.value = it },
                            filterInput = { newText ->
                                newText
                                    .filter { it.isDigit() }
                                    .take(10)}
                            )
                        }

                        UserDataTextFieldType.Person -> {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                stringResource(item.title),
                                modifier = Modifier.padding(bottom = 8.dp),
                                style = MaterialTheme.typography.body1,
                                color = ExtendedThemeColors.colors.blackColor
                            )
                            UserInfoTextField(
                                item = item,
                                text = userNameText.value,
                                error = isErrorName,
                                onValueChange = { userNameText.value = it }
                            )
                        }

                        UserDataTextFieldType.Post -> {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                stringResource(item.title),
                                modifier = Modifier.padding(bottom = 8.dp),
                                style = MaterialTheme.typography.body1,
                                color = ExtendedThemeColors.colors.blackColor
                            )
                            UserInfoTextField(
                                item = item,
                                text = postText.value,
                                error = isErrorPost,
                                onValueChange = { postText.value = it }
                            )
                        }

                        UserDataTextFieldType.Telegram -> {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                stringResource(item.title),
                                modifier = Modifier.padding(bottom = 8.dp),
                                style = MaterialTheme.typography.body1,
                                color = ExtendedThemeColors.colors.blackColor
                            )
                            UserInfoTextField(
                                item = item,
                                text = telegramText.value,
                                error = isErrorTelegram,
                                onValueChange = { telegramText.value = it }
                            )
                        }
                    }
                }
                item {
                    EffectiveButton(
                        onClick = {
                            onSaveChange(
                                userNameText.value,
                                postText.value,
                                phoneNumberText.value,
                                telegramText.value
                            )
                        },
                        buttonText = stringResource(MainRes.strings.save),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp, top = 32.dp)
                    )
                }
            }
        }
        SnackBarErrorMessage(
            message = stringResource(errorMessage),
            isVisible = isErrorMessageVisible,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
fun ProfileEditHeader(onReturnToProfile: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(top = 40.dp)
    ) {
        IconButton(onClick = onReturnToProfile) {
            Icon(
                painter = painterResource(MainRes.images.back_button),
                contentDescription = null,
                tint = ExtendedThemeColors.colors.blackColor
            )
        }
        TitlePage(
            title = stringResource(MainRes.strings.profile_data),
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}
