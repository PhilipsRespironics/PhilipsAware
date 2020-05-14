package com.philips.src.ai.aware.ui.login

import android.content.Intent
import android.net.Uri
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
import kotlinx.android.synthetic.main.wearable_consent_fragment.view.*
import org.json.JSONObject

class WearableConsentFragment : Fragment() {

    companion object {
        fun newInstance() = WearableConsentFragment()
    }

    private lateinit var viewModel: WearableConsentViewModel
    val args: WearableConsentFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val queue = Volley.newRequestQueue(this.context)
        val localHost = if(args.host != null && (args.host as String?)?.trim()?.isNotEmpty() == true) args.host as String
                        else "4c49c59e.ngrok.io"
        val localFitbitUrl = "https://$localHost/api/fitbit/authorization/url"
        val localGoogleUrl = "https://$localHost/api/googlefit/authorization/url"
        val devFitbitUrl = "https://keymaster-service-dev.cloud.pcftest.com/api/fitbit/authorization/url"
        val devGoogleUrl = "https://keymaster-service-dev.cloud.pcftest.com/api/googlefit/authorization/url"
        val view = inflater.inflate(R.layout.wearable_consent_fragment, container, false)

        view.fitbitSwitch.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                val fitbitUrl = if(args.env?.toLowerCase() == "dev") devFitbitUrl else localFitbitUrl
                val stringRequest = SecuredStringRequest(
                    Request.Method.GET, fitbitUrl,
                    Response.Listener{ response ->
                        Log.d("kevin", "Response = $response")
                        val reader = JSONObject(response)
                        val consentUrl = reader.getString("url")
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(consentUrl))
                        startActivity(browserIntent)
                    },
                    Response.ErrorListener {
                        Log.d("kevin", "the call failed")
                        Log.d("kevin", it.message ?: "no message")
                    },
                    args.username ?: "",
                    args.password ?: ""
                )
                queue.add(stringRequest)
            }
        }

        view.googleFitSwitch.setOnCheckedChangeListener { _, isChecked ->
            val googleUrl = if(args.env?.toLowerCase() == "dev") devGoogleUrl else localGoogleUrl
            if(isChecked){
                val stringRequest = SecuredStringRequest(
                    Request.Method.GET, googleUrl,
                    Response.Listener{ response ->
                        Log.d("kevin", "Response = $response")
                        val reader = JSONObject(response)
                        val consentUrl = reader.getString("url")
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(consentUrl))
                        startActivity(browserIntent)
                    },
                    Response.ErrorListener {
                        Log.d("kevin", "the call failed")
                    },
                    args.username ?: "",
                    args.password ?: ""
                )
                queue.add(stringRequest)
            }
        }

        view.consent_done_button.setOnClickListener{ btnView ->
            val action = WearableConsentFragmentDirections.actionWearableConsentFragmentToNavigationDashboard(args.username, args.password)
            btnView.findNavController().navigate(action)
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(WearableConsentViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
