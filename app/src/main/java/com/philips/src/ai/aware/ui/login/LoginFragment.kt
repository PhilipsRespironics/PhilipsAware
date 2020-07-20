package com.philips.src.ai.aware.ui.login

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.firebase.iid.FirebaseInstanceId
import com.philips.src.ai.aware.Globals
import com.philips.src.ai.aware.R
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.view.*
import okhttp3.*
import java.util.*


class LoginFragment : Fragment() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var loginView: View
    private val httpClient = OkHttpClient()


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        loginViewModel =
                ViewModelProvider(this).get(LoginViewModel::class.java)
        loginView = inflater.inflate(R.layout.fragment_login, container, false)
        loginView.login_button.setOnClickListener { btnView ->
            val host = hostText.text.toString()
            val env = (environment_spn.selectedItem as String?) ?: "local"
            val username = usernameText.text.toString()
            val password = passwordText.text.toString()

            val action = LoginFragmentDirections.actionNavigationLoginToWearableConsentFragment(
                username = username,
                password = password,
                env = env,
                host = host
            )
            if(doLogin(username, password)){
                FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener{ task ->
                    val token = task.result?.token
                    Log.d("Token", token ?: "none")
                    val updateTokenUrl = "${getHermesHost()}/api/devices/-"
                    val body = RequestBody.create(MediaType.parse("application/json"), "{\"token\":\"${token}\", \"appId\":\"hermes-e9798\"}")
                    val updateTokenReq = Request.Builder().url(updateTokenUrl)
                        .post(body)
                        .addHeader("Authorization", Credentials.basic(username, password))
                        .build()
                    val updateTokenTask = HttpTask(updateTokenReq, httpClient ).execute() as HttpTask
                    while(updateTokenTask.theResponse == null) Thread.sleep(1000)
                    if(updateTokenTask.theResponse?.isSuccessful == true)
                        Log.d("token", "Token updated successfully")
                    else {
                        Log.d("token", "Token update failed")
                        Log.d("token", "Response: ${updateTokenTask.theResponse}")
                    }
                    val navController = btnView.findNavController()
                    navController.navigate(action)
                }
            }
            else{
                Log.d("LOGIN", "Login Failed.")
                val toast = Toast.makeText(context, "Login Failed", Toast.LENGTH_SHORT)
                toast.show()
            }
        }
        loginView.signupLink.setOnClickListener { linkView ->
            val action = LoginFragmentDirections.actionNavigationLoginToSignupFragment(
                env = (environment_spn.selectedItem as String?) ?: "local",
                host = hostText.text.toString()
            )
            linkView.findNavController().navigate(action)
        }
        return loginView
    }

    private fun doLogin(username: String, password: String): Boolean{
        val httpTask
                = HttpTask(buildLoginRequest(username, password), httpClient).execute() as HttpTask
        while(httpTask.theResponse == null){
            Thread.sleep(1000)
        }
        return httpTask.theResponse?.isSuccessful == true
    }

    private fun buildLoginRequest(username: String, password: String):Request{
        return Request.Builder().url(getLoginUrl())
            .addHeader("Authorization", Credentials.basic(username, password))
            .build()
   }

    private fun getLoginUrl(): String{
        return "${getKeymasterHost()}/api/identity/-"
    }

    private fun getKeymasterHost():String{
        val host: String = hostText.text.toString()
        val env = (environment_spn.selectedItem as String?) ?: "local"
        val localKeymasterHost = if(host.trim().isNotEmpty()) host
        else "4c49c59e.ngrok.io"
        return if (env.toLowerCase(Locale.ROOT) == "dev")
            "https://keymaster-service-dev.cloud.pcftest.com"
        else localKeymasterHost
    }

    private fun getHermesHost():String{
//        val env = (environment_spn.selectedItem as String?) ?: "local"
        return "https://hermes-service-dev.cloud.pcftest.com"
//          "https://a9aa6e0d.ngrok.io"
//        if (env.toLowerCase(Locale.ROOT) == "dev")
//            "https://hermes-service-dev.cloud.pcftest.com"
//        else "http://a9aa6e0d.ngrok.io"
    }

    class HttpTask(private val request: Request,
                    private val httpClient: OkHttpClient):
        AsyncTask<Void, Void, Void>() {
        var theResponse: Response? = null
        override fun doInBackground(vararg params: Void?): Void? {
            val theCall = httpClient.newCall(request)
            Log.d("HTTP", "executing the call....")
            theResponse = theCall.execute()
            Log.d("HTTP", "call completed....")
            return null
        }
    }
}
