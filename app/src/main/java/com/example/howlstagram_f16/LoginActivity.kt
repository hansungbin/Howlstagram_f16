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
    var TAG : String? = "로그 LoginActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG,file +"onCreate 001")
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
            Log.d(TAG,file +" facebook_login_button.setOnClickListener")
            facebookLogin()
            Log.d(TAG,file +"facebookLogin finish 001")

        }

        Log.d(TAG,file +"gso 001")
        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("697336115550-5c2o53v0j00l10j3ebld7pd4vkpn6a9v.apps.googleusercontent.com")
            .requestEmail()
            .build()
        Log.d(TAG,file +"gso 002")
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        Log.d(TAG,file +"gso 003")
//        printHashKey()
//        Ztij3BDz1azTzbPdYaZV0cAhq7A=

        Log.d(TAG,file +"gso 004")
        callbackManager = CallbackManager.Factory.create()
        Log.d(TAG,file +"gso 005")
    }

    override fun onStart(){
        Log.d(TAG,"onStart is called")
        super.onStart()
        moveMainPage(auth?.currentUser)
    }

//    fun printHashKey() {
//        try {
//            val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
//            for (signature in info.signatures) {
//                val md: MessageDigest = MessageDigest.getInstance("SHA")
//                md.update(signature.toByteArray())
//                val hashKey: String = String(Base64.encode(md.digest(), 0))
//                Log.i("TAG", "printHashKey() Hash Key: $hashKey")
//            }
//        } catch (e: NoSuchAlgorithmException) {
//            Log.e("TAG", "printHashKey()", e)
//        } catch (e: Exception) {
//            Log.e("TAG", "printHashKey()", e)
//        }
//    }

    fun googleLogin(){
        Log.d(TAG,"googleLogin is called")
        var signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent, GOOGLE_LOGIN_CODE)
    }

    fun facebookLogin(){
        Log.d(TAG,file +"facebookLogin 01")
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"))
        Log.d(TAG,file +"facebookLogin 02")

        //계정이 존재하지 않으면 들어가지지 않음음
       LoginManager.getInstance()
            .registerCallback(callbackManager, object  : FacebookCallback<LoginResult>{
                override fun onSuccess(result: LoginResult?) {
                    //Second step
                    Log.d(TAG,file +"onSuccess 01")
                    handleFacebookAccessToken(result?.accessToken)
                }

                override fun onCancel() {
                    Log.d(TAG,file +"onCancel 01")
                    Log.e("TAG", "LoginActivity_ fun onCancel")
                }

                override fun onError(error: FacebookException?) {
                    Log.d(TAG,file +"onError 01")
                    Log.e("TAG", "LoginActivity_ fun onError")
                }

            })
    }

    fun handleFacebookAccessToken(token : AccessToken?){
        Log.d(TAG,file +"handleFacebookAccessToken 01")
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
        Log.d(TAG,file +"onActivityResult 01")
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG,file +"onActivityResult 02")
        Log.d(TAG,file +"requestCode = $requestCode")
        Log.d(TAG,file +"resultCode = $resultCode")
        Log.d(TAG,file +"data = $data")
        Log.d(TAG,file +"requestCode = $requestCode")
        callbackManager?.onActivityResult(requestCode,resultCode, data)
        if(requestCode == GOOGLE_LOGIN_CODE){
            var result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            Log.d(TAG,"onActivityResult is called 03")
            if(result!!.isSuccess){
                Log.d(TAG,"onActivityResult is called 04")
                var account = result.signInAccount
                //Second step
                firebaseAuthWithGoogle(account)
            }
        }
    }

    fun firebaseAuthWithGoogle(account: GoogleSignInAccount?){
        Log.d(TAG,"firebaseAuthWithGoogle is called")
        var credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener { task ->
                if(task.isSuccessful){
                    //Login
                        Log.d(TAG,"firebaseAuthWithGoogle is called // task.isSuccessful = " + task.isSuccessful.toString() )
                    moveMainPage(task.result?.user)
                }else {
                    //Show the error message
                        Log.d(TAG,"firebaseAuthWithGoogle is called //error ")
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
    }

    fun signinAndSignup(){
        Log.d(TAG,file +"signinAndSignup 01")
        auth?.createUserWithEmailAndPassword(
            email_edittext.text.toString(),
            password_edittext.text.toString()
        )?.addOnCompleteListener { task ->
                if (task.isSuccessful){
                    //Creating a user account
                    Log.d(TAG,file +"signinAndSignup 02")
                    moveMainPage(task.result?.user)

                }else if(task.exception?.message.isNullOrEmpty()){
                    Log.d(TAG,file +"signinAndSignup 03")
                    //Show the error message
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()

                }else {
                    Log.d(TAG,file +"signinAndSignup 04")
                    //Login if you have account
                    signinEmail()
                }
        }
    }

    fun signinEmail(){
        Log.d(TAG,"signinEmail is called" )
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
        Log.d(TAG,file +"moveMainPage ")
        if (user != null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()

        }
    }
}