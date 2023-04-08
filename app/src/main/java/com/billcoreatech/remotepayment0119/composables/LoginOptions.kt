package com.billcoreatech.remotepayment0119.composables

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.billcoreatech.remotepayment0119.R
import com.billcoreatech.remotepayment0119.ui.theme.Shapes
import com.billcoreatech.remotepayment0119.ui.theme.softBlue
import com.billcoreatech.remotepayment0119.ui.theme.typography
import com.billcoreatech.remotepayment0119.ui.theme.white850
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph

@RootNavGraph(start = true) // sets this as the start destination of the default nav graph
@Destination
@Composable
fun LoginOptions(
    doForGotPassword:(emailId : MutableState<String>) -> Unit,
    doLogin:(emailId: MutableState<String>, password: MutableState<String>) -> Unit,
    doRegisterUser:() -> Unit,
    doGoogleLogin:() -> Unit,
    doTranslateDownload:() -> Unit,
    doFacebookLogin:() -> Unit,
    doKakaoLogin:() -> Unit,
    doNaverLogin:() -> Unit,
) {

    val emailId = remember {
        mutableStateOf("")
    }
    val password = remember {
        mutableStateOf("")
    }
    var isVisibility by remember {
        mutableStateOf(false)
    }

    var text by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.threestones),
            contentDescription = "three stones"
        )
        Text( text = stringResource(id = R.string.welCome), style = typography.h3)
        OutlinedTextField(
            value = emailId.value,
            onValueChange = { emailId.value = it},
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            textStyle = typography.body1,
            label = { Text(text = stringResource(id = R.string.Email), style = typography.body1)},
            placeholder = { Text(text = stringResource(id = R.string.EnterEmail), style = typography.body2) },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
            singleLine = true,
        )
        OutlinedTextField(
            value = password.value,
            onValueChange = { password.value = it},
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            textStyle = typography.body1,
            label = { Text(text = stringResource(id = R.string.Password), style = typography.body1) },
            placeholder = { Text(text = stringResource(id = R.string.EnterPassword), style = typography.body2) },
            visualTransformation = if (isVisibility) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = { isVisibility = !isVisibility }) {
                    var imageVector = Icons.Outlined.Visibility
                    var contentDescription = "Visibility"
                    if (isVisibility) {
                        imageVector = Icons.Outlined.VisibilityOff
                        contentDescription = "VisibilityOff"
                    }
                    Icon(
                        imageVector = imageVector,
                        contentDescription = contentDescription,
                        tint = softBlue.copy(alpha = 0.3f)
                    )
                }
            },
        )
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = { doForGotPassword(emailId) },
                modifier = Modifier.padding(2.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.forGotPassword),
                    style = typography.button,
                    color = softBlue
                )
            }
            IconButton(onClick = { doTranslateDownload() }) {
                Icon(imageVector = Icons.Outlined.Translate, contentDescription = "translate Language")
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, color = Color.Black.copy(alpha = 0.3f))
                .background(softBlue, shape = Shapes.medium),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = { doLogin(emailId, password) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(id = R.string.login), style = typography.body1, color = white850)
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = { doRegisterUser() },
                modifier = Modifier.padding(2.dp)
            ) {
                Text( text = stringResource(id = R.string.notMember), style = typography.body1)
                Spacer(modifier = Modifier.padding(3.dp))
                Text( text = stringResource(id = R.string.registerNow), style = typography.body1, color = softBlue)
            }
        }
        Image(
            painter = painterResource(id = R.drawable.divider),
            contentDescription = "divider"
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text( text = stringResource(id = R.string.orContinueWith), style = typography.body1)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { doGoogleLogin() }) {
                Image(
                    painter = painterResource(id = R.drawable.google_01),
                    contentDescription = "google login"
                )
            }

            IconButton(onClick = { doFacebookLogin() }) {
                Image(
                    painter = painterResource(id = R.drawable.facebook_02),
                    contentDescription = "kakao login"
                )
            }

            IconButton(onClick = { doKakaoLogin() }) {
                Image(
                    painter = painterResource(id = R.drawable.kakao_01),
                    contentDescription = "kakao login"
                )
            }

            IconButton(onClick = { doNaverLogin() }) {
                Image(
                    painter = painterResource(id = R.drawable.naver_01),
                    contentDescription = "naver login"
                )
            }

        }
    }
}
