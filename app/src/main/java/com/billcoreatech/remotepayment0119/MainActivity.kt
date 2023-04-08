package com.billcoreatech.remotepayment0119

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.billcoreatech.remotepayment0119.composables.JoinUserScreen
import com.billcoreatech.remotepayment0119.composables.LoginOptions
import com.billcoreatech.remotepayment0119.composables.NavGraphs
import com.billcoreatech.remotepayment0119.composables.destinations.JoinUserScreenDestination
import com.billcoreatech.remotepayment0119.composables.destinations.LoginOptionsDestination
import com.billcoreatech.remotepayment0119.httpRequest.RequestBean
import com.billcoreatech.remotepayment0119.httpRequest.RequestGoogleBean
import com.billcoreatech.remotepayment0119.httpRequest.ResponseBean
import com.billcoreatech.remotepayment0119.httpRequest.RetrofitApi
import com.billcoreatech.remotepayment0119.ui.theme.RemotePayment0119Theme
import com.billcoreatech.remotepayment0119.viewModel.TranslateViewModel
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginConfiguration
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.identity.GetSignInIntentRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.manualcomposablecalls.composable
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.security.MessageDigest

class MainActivity : ComponentActivity() {

    private val translateView : TranslateViewModel by viewModels()
    private lateinit var auth: FirebaseAuth;
    lateinit var sp : SharedPreferences
    private lateinit var editor : SharedPreferences.Editor
    var resultString = ""

    private lateinit var oneTapClient: SignInClient

    private val TAG = "---"

    val callbackManager = CallbackManager.Factory.create()
    val loginManager = LoginManager.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sp = getSharedPreferences(packageName, MODE_PRIVATE)
        auth = FirebaseAuth.getInstance()

        oneTapClient = Identity.getSignInClient(this@MainActivity)

        printHashKey(this@MainActivity)

        // 간단한 문서 번역 ML
        translateView.sourceLang.value = TranslateViewModel.Language("en")
        translateView.targetLang.value = TranslateViewModel.Language("ko")
        if (!sp.getBoolean("isDownloadKR", false)) {
            translateView.downloadLanguage(TranslateViewModel.Language("ko"))
        }
        translateView.availableModels.observe(
            this@MainActivity
        ) {result ->
            for (lang in result) {
                Log.e("", "lang=${lang}")
                if (lang == "kr") {
                    editor = sp.edit()
                    editor.putBoolean("isDownloadKR", true)
                    editor.apply()
                }
            }
        }

        setContent {

            val scrollableState = rememberScrollState()

            RemotePayment0119Theme {
                // A surface container using the 'background' color from the theme
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(30.dp)
                        .verticalScroll(scrollableState),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    DestinationsNavHost(navGraph = NavGraphs.root) {
                        composable(JoinUserScreenDestination) {
                            JoinUserScreen(
                                navigator = destinationsNavigator,
                                doSignup = { emailId, password ->
                                    doSignupUser(emailId, password)
                                }
                            )
                        }
                        composable(LoginOptionsDestination) {
                            LoginOptions(
                                doLogin = { email, password ->
                                    doEmailLogin(email, password)
                                },
                                doForGotPassword = { email ->
                                    doForgotPassword(email)
                                },
                                doGoogleLogin = {
                                    doGoogleLogin()
                                },
                                doFacebookLogin = {
                                    doFacebookLogin()
                                },
                                doRegisterUser = {
                                    destinationsNavigator.navigate(JoinUserScreenDestination)
                                },
                                doTranslateDownload = {
                                    translateView.downloadLanguage(TranslateViewModel.Language("ko"))
                                    doToastMake(R.string.msgBeginDownload)
                                },
                                doKakaoLogin = {
                                    doKakaoLogin()
                                },
                                doNaverLogin = {
                                    doNaverLogin()
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun doFacebookLogin() {
        loginManager.logIn(this@MainActivity, callbackManager, listOf("email", "public_profile","openid"))
        loginManager.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onCancel() {
                Log.e("", "onCancel")
                loginManager.logOut()
            }

            override fun onError(error: FacebookException) {
                Log.e("", "error=${error.localizedMessage}")
            }

            override fun onSuccess(result: LoginResult) {
                Log.e("", "accessToken Removed authToken=${result.authenticationToken}")
                handleFacebookAccessToken(result.accessToken)
            }
        })

    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.e(TAG, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.e(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
//                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.e(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
//                    updateUI(null)
                }
            }
    }


    private val naverLauncher = registerForActivityResult<Intent, ActivityResult>(ActivityResultContracts.StartActivityForResult()) { result ->
        when(result.resultCode) {
            RESULT_OK -> {
                // 네이버 로그인 인증이 성공했을 때 수행할 코드 추가
//                binding.tvAccessToken.text = NaverIdLoginSDK.getAccessToken()
//                binding.tvRefreshToken.text = NaverIdLoginSDK.getRefreshToken()
//                binding.tvExpires.text = NaverIdLoginSDK.getExpiresAt().toString()
//                binding.tvType.text = NaverIdLoginSDK.getTokenType()
//                binding.tvState.text = NaverIdLoginSDK.getState().toString()
                Log.e("", "accessToken=${NaverIdLoginSDK.getAccessToken()}")
                doSigninNaverToken(NaverIdLoginSDK.getAccessToken().toString())
            }
            RESULT_CANCELED -> {
                // 실패 or 에러
                val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                doToastMakeAppend(R.string.titleFailure, "$errorDescription")
            }
        }
    }

    private fun doNaverLogin() {
        NaverIdLoginSDK.initialize(this@MainActivity, BuildConfig.NAVER_CLIENT, BuildConfig.NAVER_SECERT, BuildConfig.APPLICATION_ID)
        NaverIdLoginSDK.authenticate(this@MainActivity, naverLauncher)
    }

    private val kakaoCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            Log.e(TAG, "카카오계정으로 로그인 실패", error)
        } else if (token != null) {
            Log.e(TAG, "카카오계정으로 로그인 성공 ${token.accessToken} ${token.idToken}")
            doSigninKakaoToken("${token.accessToken}")
        }
    }

    private fun doKakaoLogin() {

        // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this@MainActivity)) {
            UserApiClient.instance.loginWithKakaoTalk(this@MainActivity) { token, error ->
                if (error != null) {
                    Log.e(TAG, "카카오톡으로 로그인 실패", error)

                    // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                    // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }

                    // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                    UserApiClient.instance.loginWithKakaoAccount(this@MainActivity, callback = kakaoCallback)
                } else if (token != null) {
                    Log.e(TAG, "카카오톡으로 로그인 성공 ${token.accessToken} ${token.idToken}")
                    doSigninKakaoToken("${token.accessToken}")
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(this@MainActivity, callback = kakaoCallback)
        }

    }

    private fun doSignupUser(emailId: MutableState<String>, password: MutableState<String>) {

        if (emailId.value == "") {
            doToastMake(R.string.EnterEmail)
            return
        }
        if (password.value == "") {
            doToastMake(R.string.EnterPassword)
            return
        }
        auth.createUserWithEmailAndPassword(emailId.value, password.value)
            .addOnSuccessListener {
                doToastMake(R.string.msgCreateUserCompleted)
            }
            .addOnFailureListener {
                doToastMakeAppend(R.string.msgCreateUserFailure, it.localizedMessage)
            }
    }

    /**
     * google oneTap 로그인
     */
    private val intentSenderRequestActivityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            try {
                if (result.resultCode == Activity.RESULT_OK) {
                    result.data?.let { intent ->
                        oneTapClient.getSignInCredentialFromIntent(intent)
                    }?.also { signInCredential ->

                        val username = signInCredential.id
                        val displayName = signInCredential.displayName

                        Log.e("", "loginOk $username, $displayName")

                        doSigninGoogleToken(username, displayName.toString())

                    }
                }
            } catch (e: ApiException) {
                when (e.statusCode) {
                    CommonStatusCodes.CANCELED -> {
                        Log.d(TAG, "One-tap dialog was closed.: ${e.message.toString()}")
                    }
                    CommonStatusCodes.NETWORK_ERROR -> {
                        Log.d(TAG, "One-tap encountered a network error.: ${e.message.toString()}")
                    }
                    else -> {
                        Log.d(TAG, "Couldn't get credential from result.: ${e.localizedMessage}")
                    }
                }
            }
        }

    /**
     * google oneTap 로그인
     */
    private fun doGoogleLogin() {

        Log.e(TAG, "google login start ...")

        GetSignInIntentRequest.builder()
            .setServerClientId(getString(R.string.default_web_client_id))
            .build().also { getSignInIntentRequest ->

                Identity.getSignInClient(this@MainActivity)
                    .getSignInIntent(getSignInIntentRequest)
                    .addOnSuccessListener { pendingIntent ->
                        IntentSenderRequest.Builder(pendingIntent.intentSender).build()
                            .also { intentSenderRequest ->
                                try {
                                    intentSenderRequestActivityResultLauncher.launch(
                                        intentSenderRequest
                                    )
                                } catch (e: ActivityNotFoundException) {
                                    Log.d(
                                        TAG,
                                        "addOnSuccessListener:Google Sign-in failed:$e "
                                    )
                                }
                            }

                    }
                    .addOnFailureListener { exception ->
                        Log.d(TAG, "addOnFailureListener:Google Sign-in failed:$exception ")
                    }
            }
    }

    /**
     * customToken 의 값은 split("'") 해서 가운데 들어 오는 값만 사용해야 함.
     */
    private fun doSigninGoogleToken(username: String, displayName: String) {

        RetrofitApi.create().doGetWithGoogleToken(RequestGoogleBean(email=username, nickName=displayName))
            .enqueue( object : Callback<ResponseBean> {
            override fun onResponse(call: Call<ResponseBean>, response: Response<ResponseBean>) {
                var customToken = response.body()!!.customToken.split("'")
                Log.e("", "token=${customToken[1]}")
                auth.signInWithCustomToken(customToken[1])
                    .addOnSuccessListener {
                        doToastMake(R.string.msgSigninCompleted)
                        val editor = sp.edit()
                        editor.putString("loginMath", "google")
                        editor.apply()
                    }
                    .addOnFailureListener {
                        doToastMakeAppend(R.string.titleFailure, "${it.localizedMessage}")
                    }
            }

            override fun onFailure(call: Call<ResponseBean>, t: Throwable) {
                doToastMakeAppend(R.string.titleFailure, "${t.localizedMessage}")
            }
        })
    }

    /**
     * customToken 의 값은 split("'") 해서 가운데 들어 오는 값만 사용해야 함.
     */
    private fun doSigninNaverToken(token: String) {

        RetrofitApi.create().doGetWithNaverToken(RequestBean(token)).enqueue( object : Callback<ResponseBean> {
            override fun onResponse(call: Call<ResponseBean>, response: Response<ResponseBean>) {
                var customToken = response.body()!!.customToken.split("'")
                Log.e("", "token=${customToken[1]}")
                auth.signInWithCustomToken(customToken[1])
                    .addOnSuccessListener {
                        doToastMake(R.string.msgSigninCompleted)
                        val editor = sp.edit()
                        editor.putString("loginMath", "naver")
                        editor.apply()
                    }
                    .addOnFailureListener {
                        doToastMakeAppend(R.string.titleFailure, "${it.localizedMessage}")
                    }
            }

            override fun onFailure(call: Call<ResponseBean>, t: Throwable) {
                doToastMakeAppend(R.string.titleFailure, "${t.localizedMessage}")
            }
        })
    }

    /**
     * customToken 의 값은 split("'") 해서 가운데 들어 오는 값만 사용해야 함.
     */
    private fun doSigninKakaoToken(token: String) {

        RetrofitApi.create().doGetWithKakaoToken(RequestBean(token)).enqueue( object : Callback<ResponseBean> {
            override fun onResponse(call: Call<ResponseBean>, response: Response<ResponseBean>) {
                var customToken = response.body()!!.customToken.split("'")
                Log.e("", "token=${customToken[1]}")
                auth.signInWithCustomToken(customToken[1])
                    .addOnSuccessListener {
                        doToastMake(R.string.msgSigninCompleted)
                        val editor = sp.edit()
                        editor.putString("loginMath", "kakao")
                        editor.apply()
                    }
                    .addOnFailureListener {
                        doToastMakeAppend(R.string.titleFailure, "${it.localizedMessage}")
                    }
            }

            override fun onFailure(call: Call<ResponseBean>, t: Throwable) {
                doToastMakeAppend(R.string.titleFailure, "${t.localizedMessage}")
            }
        })
    }



    private fun doForgotPassword(email: MutableState<String>) {

        if (email.value == "") {
            doToastMake(R.string.EnterEmail)
            return
        }

        auth.sendPasswordResetEmail(email.value)
            .addOnSuccessListener {
                doToastMake(R.string.msgSendEmailForResetPassword)
            }
            .addOnFailureListener {
                doToastMakeAppend(R.string.msgPasswordResetFailure, it.localizedMessage)
            }

    }

    private fun doEmailLogin(email: MutableState<String>, password: MutableState<String>) {

        if (email.value == "") {
            doToastMake(R.string.EnterEmail)
            return
        }
        if (password.value == "") {
            doToastMake(R.string.EnterPassword)
            return
        }

        auth.signInWithEmailAndPassword(email.value, password.value)
            .addOnSuccessListener {
                doToastMake(R.string.msgSigninCompleted)
                val editor = sp.edit()
                editor.putString("loginMath", "email")
                editor.apply()
            }
            .addOnFailureListener {
                doToastMakeAppend(R.string.msgUserIdOrPasswordError, it.localizedMessage)
            }
    }

    private fun doToastMake(msgResource: Int) {
        Toast.makeText(this@MainActivity, getString(msgResource), Toast.LENGTH_SHORT).show()
    }

    private fun doToastMakeAppend(msgResource: Int, localizedMessage: String?) {

        translateView.sourceText.value = "$localizedMessage"
        translateView.translatedText.observe(
            this@MainActivity
        ) { result ->
            Log.e("", "result = ${localizedMessage} ${result.error} ${result.result}")
            resultString = result.result.toString()

            Toast.makeText(this@MainActivity, "${getString(msgResource)}:${resultString}", Toast.LENGTH_SHORT).show()
        }

    }

    fun printHashKey(context: Context): String {

        val TAG = "HASH_KEY"
        var hashKey : String? = null
        try {
            val info : PackageInfo = context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                var md : MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                hashKey = String(Base64.encode(md.digest(), 0))
                Log.e(TAG, "hashKey=$hashKey")
            }

        } catch (e:Exception){
            Log.e(TAG, e.toString())
        }

        return "$hashKey"

    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    RemotePayment0119Theme {
        DestinationsNavHost(navGraph = NavGraphs.root)
    }
}