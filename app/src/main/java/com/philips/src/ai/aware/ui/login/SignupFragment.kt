package com.philips.src.ai.aware.ui.login

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.Volley

import com.philips.src.ai.aware.R
import com.philips.src.ai.aware.ui.SecuredStringRequest
import kotlinx.android.synthetic.main.signup_fragment.view.*
import java.util.*

class SignupFragment : Fragment() {

    private lateinit var viewModel: SignupViewModel
    private val args: SignupFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val queue = Volley.newRequestQueue(this.context)
        val host = if (args.env?.toLowerCase(Locale.ROOT) == "dev")
            "gatekeeper-service-dev.cloud.pcftest.com"
        else{
            "localhost:8082"
        }
        val url = "https://$host/api/identity"
        viewModel =
            ViewModelProvider(this).get(SignupViewModel::class.java)
        val view = inflater.inflate(R.layout.signup_fragment, container, false)
        view.registerButton.setOnClickListener { btnView ->
            val body = """
                {
                    "username": "${view.usernameText.text.toString()}",
                    "password": "${view.passwordText.text.toString()}",
                    "roles": ["USER"]
                }        
            """.trimIndent()
            val stringRequest = SecuredStringRequest(
                method = Request.Method.POST,
                url = url,
                listener = Response.Listener{
                    val action = SignupFragmentDirections.actionSignupFragmentToNavigationLogin()
                    btnView.findNavController().navigate(action)
                },
                errorListener = Response.ErrorListener {
                    Log.d("kevin", "the call failed")
                    Log.d("kevin", it.message ?: "no message")
                },
                username = "admin",
                password = "supersecret",
                body = body
            )
            queue.add(stringRequest)
        }
        view.cancelButton.setOnClickListener{ btnView ->
            val action = SignupFragmentDirections.actionSignupFragmentToNavigationLogin()
            btnView.findNavController().navigate(action)
        }
        return view
    }
}
