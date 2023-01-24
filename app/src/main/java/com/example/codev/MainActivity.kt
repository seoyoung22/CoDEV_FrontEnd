package com.example.codev

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.codev.databinding.ActivityMainBinding
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.security.KeyStore

class MainActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        AndroidKeyStoreUtil.init(this)

        //UserSharedPreferences.clearUser(this)

        viewBinding.btnRegister.setOnClickListener {
            val intent = Intent(this,RegisterTosActivity::class.java)
            startActivity(intent)
        }

        viewBinding.btnLogin.setOnClickListener {
            if (viewBinding.etEmail.text.isNullOrBlank() or viewBinding.etPassword.text.isNullOrBlank()){
                Toast.makeText(this, "아이디와 비밀번호를 확인하세요", Toast.LENGTH_SHORT).show()
            }else{
                signUp(this,viewBinding.etEmail.text.toString(),viewBinding.etPassword.text.toString())
            }
        }

        if (UserSharedPreferences.getUserId(this).isNullOrBlank()){

        }else{
            val intent = Intent(this,MainAppActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun signUp(context:Context, email:String, pwd:String){
        RetrofitClient.service.signUp(ReqSignUp(email,pwd)).enqueue(object: Callback<ResSignUp>{
            override fun onResponse(call: Call<ResSignUp>, response: Response<ResSignUp>) {
                if(response.isSuccessful.not()){
                    Log.d("test",response.toString())
                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                    return
                }
                response.body()?.let {
                    if(viewBinding.loginAuto.isChecked){
                        UserSharedPreferences.setUserId(context,email)
                        UserSharedPreferences.setUserPwd(context,AndroidKeyStoreUtil.encrypt(pwd))
                    }
                    UserSharedPreferences.setUserAccessToken(context,AndroidKeyStoreUtil.encrypt(it.result.accessToken))
                    UserSharedPreferences.setUserRefreshToken(context,AndroidKeyStoreUtil.encrypt(it.result.refreshToken))
                    Log.d("test", "\n${it.toString()}")
                    Log.d("test",AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken(context)))
                    Log.d("test",AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserRefreshToken(context)))
                }
                val intent = Intent(context,MainAppActivity::class.java)
                startActivity(intent)
                finish()
            }

            override fun onFailure(call: Call<ResSignUp>, t: Throwable) {
                Log.d("test", "[Fail]${t.toString()}")
            }

        })
    }
}