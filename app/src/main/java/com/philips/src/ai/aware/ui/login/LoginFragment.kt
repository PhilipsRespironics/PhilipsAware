package com.philips.src.ai.aware.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.philips.src.ai.aware.R
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.view.*

class LoginFragment : Fragment() {

    private lateinit var loginViewModel: LoginViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        loginViewModel =
                ViewModelProvider(this).get(LoginViewModel::class.java)
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        view.login_button.setOnClickListener { btnView ->
            val action = LoginFragmentDirections.actionNavigationLoginToWearableConsentFragment(
                username = usernameText.text.toString(),
                password = passwordText.text.toString(),
                env = (environment_spn.selectedItem as String?) ?: "local",
                host = hostText.text.toString()
            )
            btnView.findNavController().navigate(action)
        }
        view.signupLink.setOnClickListener { linkView ->
            val action = LoginFragmentDirections.actionNavigationLoginToSignupFragment(
                env = (environment_spn.selectedItem as String?) ?: "local",
                host = hostText.text.toString()
            )
            linkView.findNavController().navigate(action)
        }
        return view
    }
}
