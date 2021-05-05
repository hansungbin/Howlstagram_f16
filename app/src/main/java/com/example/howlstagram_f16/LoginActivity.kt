package com.example.howlstagram_f16

import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.Login
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*


class LoginActivity : AppCompatActivity() {
    var auth : FirebaseAuth? = null
    var googleSignInClient : GoogleSignInClient? = null
    var GOOGLE_LOGIN_CODE = 9001
    var callbackManager : CallbackManager? = null
    var file = "LoginActivity.kt -"

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("ERROR",file +"onCreate 001")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()
        email_login_button.setOnClickListener {
            signinAndSignup()
        }
        google_sign_in_button.setOnClickListener {
            //First step
            googleLogin()
        }
        facebook_login_button.setOnClickListener{
            //First step
            Log.d("ERROR",file +" facebook_login_button.setOnClickListener")
            facebookLogin()
            Log.d("ERROR",file +"facebookLogin finish 001")

        }

        Log.d("ERROR",file +"gso 001")
        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("697336115550-5c2o53v0j00l10j3ebld7pd4vkpn6a9v.apps.googleusercontent.com")
            .requestEmail()
            .build()
        Log.d("ERROR",file +"gso 002")
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        Log.d("ERROR",file +"gso 003")
//        printHashKey()
//        hVk24c/qdoa1+/sGUaGDBSwlvvc=
//        oMAqMtLBI9zPOjo2T7+wFnugHyA=

        Log.d("ERROR",file +"gso 004")
        callbackManager = CallbackManager.Factory.create()
        Log.d("ERROR",file +"gso 005")
    }

    fun printHashKey() {
        try {
            val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey: String = String(Base64.encode(md.digest(), 0))
                Log.i("TAG", "printHashKey() Hash Key: $hashKey")
            }
        } catch (e: NoSuchAlgorithmException) {
            Log.e("TAG", "printHashKey()", e)
        } catch (e: Exception) {
            Log.e("TAG", "printHashKey()", e)
        }
    }

    fun googleLogin(){
        var signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent, GOOGLE_LOGIN_CODE)
    }

    fun facebookLogin(){
        Log.d("ERROR",file +"facebookLogin 01")
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"))
        Log.d("ERROR",file +"facebookLogin 02")

        //계정이 존재하지 않으면 들어가지지 않음음
       LoginManager.getInstance()
            .registerCallback(callbackManager, object  : FacebookCallback<LoginResult>{
                override fun onSuccess(result: LoginResult?) {
                    //Second step
                    Log.d("ERROR",file +"onSuccess 01")
                    handleFacebookAccessToken(result?.accessToken)
                }

                override fun onCancel() {
                    Log.d("ERROR",file +"onCancel 01")
                    Log.e("TAG", "LoginActivity_ fun onCancel")
                }

                override fun onError(error: FacebookException?) {
                    Log.d("ERROR",file +"onError 01")
                    Log.e("TAG", "LoginActivity_ fun onError")
                }

            })
    }

    fun handleFacebookAccessToken(token : AccessToken?){
        Log.d("ERROR",file +"handleFacebookAccessToken 01")
        var credential = FacebookAuthProvider.getCredential(token?.token!!)
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener { task ->
                if(task.isSuccessful){
                    //Third step
                    //Login
                    moveMainPage(task.result?.user)
                }else {
                    //Show the error message
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("ERROR",file +"onActivityResult 01")
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("ERROR",file +"onActivityResult 01")
        Log.d("ERROR",file +"requestCode = $requestCode")
        Log.d("ERROR",file +"resultCode = $resultCode")
        Log.d("ERROR",file +"data = $data")
        callbackManager?.onActivityResult(requestCode,resultCode, data)
        if(requestCode == GOOGLE_LOGIN_CODE){
            var result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if(result!!.isSuccess){
                var account = result.signInAccount
                //Second step
                firebaseAuthWithGoogle(account)
            }
        }
    }

    fun firebaseAuthWithGoogle(account: GoogleSignInAccount?){
        var credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener { task ->
                if(task.isSuccessful){
                    //Login
                    moveMainPage(task.result?.user)
                }else {
                    //Show the error message
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
    }

    fun signinAndSignup(){
        Log.d("ERROR",file +"signinAndSignup 01")
        auth?.createUserWithEmailAndPassword(
            email_edittext.text.toString(),
            password_edittext.text.toString()
        )?.addOnCompleteListener { task ->
                if (task.isSuccessful){
                    //Creating a user account
                    Log.d("ERROR",file +"signinAndSignup 02")
                    moveMainPage(task.result?.user)

                }else if(task.exception?.message.isNullOrEmpty()){
                    Log.d("ERROR",file +"signinAndSignup 03")
                    //Show the error message
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()

                }else {
                    Log.d("ERROR",file +"signinAndSignup 04")
                    //Login if you have account
                    signinEmail()
                }
        }
    }

    fun signinEmail(){
        auth?.signInWithEmailAndPassword(
            email_edittext.text.toString(),
            password_edittext.text.toString()
        )
            ?.addOnCompleteListener { task ->
                if(task.isSuccessful){
                    //Login
                    moveMainPage(task.result?.user)
                }else {
                    //Show the error message
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
    }

    fun moveMainPage(user: FirebaseUser?){
        Log.d("ERROR",file +"moveMainPage ")
        if (user != null){
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}