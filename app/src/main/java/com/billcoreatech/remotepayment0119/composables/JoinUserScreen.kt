package com.billcoreatech.remotepayment0119.composables

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun JoinUserScreen (
    navigator: DestinationsNavigator,
    doSignup: (emailId: MutableState<String>, password: MutableState<String>) -> Unit,
){
    val context = LocalContext.current
    val emailId = remember {
        mutableStateOf("")
    }
    val password = remember {
        mutableStateOf("")
    }
    val password2 = remember {
        mutableStateOf("")
    }
    var isVisibility by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navigator.popBackStack() }) {
                Icon(imageVector = Icons.Outlined.ArrowBack, contentDescription = "ArrowBack", tint = softBlue.copy(alpha = 0.8f))
            }
        }
        Image(
            painter = painterResource(id = R.drawable.threestones),
            contentDescription = "three stones"
        )
        Text( text = stringResource(id = R.string.SingupUser), style = typography.h3)
        OutlinedTextField(
            value = emailId.value,
            onValueChange = { emailId.value = it},
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            textStyle = typography.body1,
            label = { Text(text = stringResource(id = R.string.Email), style = typography.body1) },
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
        if (!isVisibility) {
            OutlinedTextField(
                value = password2.value,
                onValueChange = { password2.value = it},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                textStyle = typography.body1,
                label = { Text(text = stringResource(id = R.string.Password), style = typography.body1) },
                placeholder = { Text(text = stringResource(id = R.string.EnterPassword), style = typography.body2) },
                visualTransformation = if (isVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
                singleLine = true,
            )
        }
        Spacer(modifier = Modifier.padding(5.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, color = Color.Black.copy(alpha = 0.3f))
                .background(softBlue, shape = Shapes.medium),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = {
                    if (isVisibility) {
                        password2.value = password.value
                    }
                    if (password.value != password2.value) {
                        Toast.makeText(context, context.getString(R.string.msgPasswordCheckError), Toast.LENGTH_SHORT).show()
                        return@TextButton
                    }
                    doSignup(emailId, password)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(id = R.string.SingupUser), style = typography.body1, color = white850)
            }
        }
    }
}