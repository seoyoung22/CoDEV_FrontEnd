package com.example.codev

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

import com.example.codev.addpage.AddNewProjectActivity

import com.example.codev.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityMainBinding
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private val googleLoginLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    )
    { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleSignInResult(task)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        AndroidKeyStoreUtil.init(this)

        // 자동 로그인 방지
        //UserSharedPreferences.clearUser(this)

        //구글
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.server_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        //깃허브
        getGithubCode()

        viewBinding.btnGithub.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/login/oauth/authorize?client_id=Iv1.90b1ea1a45795609&redirect_url=codev://login"))
            startActivity(intent)
        }

        viewBinding.btnGoogle.setOnClickListener {
            getGoogleIdToken()
        }

        viewBinding.btnRegister.setOnClickListener {
            val reqSignUp = ReqSignUp("","","","","","","","CODEV")
            val intent = Intent(this,RegisterTosActivity::class.java)
            intent.putExtra("signUp",reqSignUp)
            startActivity(intent)
        }

        viewBinding.btnLogin.setOnClickListener {
            if (viewBinding.etEmail.text.isNullOrBlank() or viewBinding.etPassword.text.isNullOrBlank()){
                Toast.makeText(this, "아이디와 비밀번호를 확인하세요", Toast.LENGTH_SHORT).show()
            }else{
                signIn(this,viewBinding.etEmail.text.toString(),viewBinding.etPassword.text.toString())
            }
        }

        // 자동로그인 확인
        if(checkAutoLogin(this)) {
            val intent = Intent(this,MainAppActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun getGithubCode() {
        if (Intent.ACTION_VIEW.equals(intent.action)) {
            var uri = intent.data
            if (uri != null) {
                var code = uri.getQueryParameter("code")
                Log.d("test",code.toString())
                sendIdToken(this,"GITHUB",code.toString())
            }
        }
    }

    private fun getGoogleIdToken() {
        val signInIntent = mGoogleSignInClient!!.signInIntent
        googleLoginLauncher.launch(signInIntent)
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val idToken = account.idToken
            Log.d("test",account.toString())
            Log.d("test",idToken.toString())
            sendIdToken(this,"GOOGLE",idToken.toString())
        } catch (e: ApiException) {
            Log.d("test", "handleSignInResult:error", e)
        }
    }

    private fun sendIdToken(context: Context,loginType: String, token: String){
        if (loginType == "GOOGLE"){
            RetrofitClient.service.googleSignIn(ReqGoogleSignIn(loginType,token)).enqueue(object: Callback<ResSignIn>{
                override fun onResponse(call: Call<ResSignIn>, response: Response<ResSignIn>) {
                    if(response.isSuccessful.not()){
                        Log.d("test: 로그인 실패1",response.toString())
                        Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }else{
                        when(response.code()){
                            200->{
                                // 토큰 암호화
                                response.body()?.let {
                                    Log.d("test: 로그인 성공", "\n${it.toString()}")
                                    if (it.code == 505){
                                        //로그인 타입 가지고 회원가입 페이지
                                        val reqSignUp = ReqSignUp(it.result.co_email,it.result.co_password,"","","","","",loginType)
                                        val intent = Intent(context,RegisterTosActivity::class.java)
                                        intent.putExtra("signUp",reqSignUp)
                                        startActivity(intent)
                                    }else{
                                        //자동 로그인 설정
                                        UserSharedPreferences.setAutoLogin(context,"TRUE")
                                        //기존 로그인 로직
                                        UserSharedPreferences.setUserAccessToken(context,AndroidKeyStoreUtil.encrypt(it.result.accessToken))
                                        UserSharedPreferences.setUserRefreshToken(context,AndroidKeyStoreUtil.encrypt(it.result.refreshToken))
                                        Log.d("test: 로그인 성공",AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context)))
                                        Log.d("test: 로그인 성공",AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserRefreshToken(context)))
                                        val intent = Intent(context,MainAppActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                }
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ResSignIn>, t: Throwable) {
                    Log.d("test: 로그인 실패2", "[Fail]${t.toString()}")
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            })
        }else if(loginType == "GITHUB"){
            RetrofitClient.service.githubSignIn(ReqGithubSignIn(loginType,token)).enqueue(object: Callback<ResSignIn>{
                override fun onResponse(call: Call<ResSignIn>, response: Response<ResSignIn>) {
                    if(response.isSuccessful.not()){
                        Log.d("test: 로그인 실패1",response.toString())
                        Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }else{
                        when(response.code()){
                            200->{
                                response.body()?.let {
                                    Log.d("test: 로그인 성공", "\n${it.toString()}")
                                    if (it.code == 505){
                                        //로그인 타입 가지고 회원가입 페이지
                                        val reqSignUp = ReqSignUp(it.result.co_email,it.result.co_password,"","","","","",loginType)
                                        val intent = Intent(context,RegisterTosActivity::class.java)
                                        intent.putExtra("signUp",reqSignUp)
                                        startActivity(intent)
                                    }else{
                                        //자동 로그인 설정
                                        UserSharedPreferences.setAutoLogin(context,"TRUE")
                                        //기존 로그인 로직
                                        UserSharedPreferences.setUserAccessToken(context,AndroidKeyStoreUtil.encrypt(it.result.accessToken))
                                        UserSharedPreferences.setUserRefreshToken(context,AndroidKeyStoreUtil.encrypt(it.result.refreshToken))
                                        Log.d("test: 로그인 성공",AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context)))
                                        Log.d("test: 로그인 성공",AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserRefreshToken(context)))
                                        val intent = Intent(context,MainAppActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                }
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ResSignIn>, t: Throwable) {
                    Log.d("test: 로그인 실패2", "[Fail]${t.toString()}")
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }


    private fun signIn(context:Context, email:String, pwd:String) {
        RetrofitClient.service.signIn(ReqSignIn(email,pwd)).enqueue(object: Callback<ResSignIn>{
            override fun onResponse(call: Call<ResSignIn>, response: Response<ResSignIn>) {
                if(response.isSuccessful.not()){
                    Log.d("test: 로그인 실패1",response.toString())
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                }else{
                    when(response.code()){
                        200->{
                            if(viewBinding.loginAuto.isChecked){
                                UserSharedPreferences.setAutoLogin(context,"TRUE")
                            }
                            // 토큰 암호화
                            response.body()?.let {
                                UserSharedPreferences.setUserAccessToken(context,AndroidKeyStoreUtil.encrypt(it.result.accessToken))
                                UserSharedPreferences.setUserRefreshToken(context,AndroidKeyStoreUtil.encrypt(it.result.refreshToken))
                                Log.d("test: 로그인 성공", "\n${it.toString()}")
                                Log.d("test: 로그인 성공",AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context)))
                                Log.d("test: 로그인 성공",AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserRefreshToken(context)))
                            }
                            val intent = Intent(context,MainAppActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResSignIn>, t: Throwable) {
                Log.d("test: 로그인 실패2", "[Fail]${t.toString()}")
                Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // AutoLogin 값이 있다면 true, 없다면 false
    private fun checkAutoLogin(context: Context): Boolean {
        return !UserSharedPreferences.getAutoLogin(context).isNullOrBlank()
    }
}