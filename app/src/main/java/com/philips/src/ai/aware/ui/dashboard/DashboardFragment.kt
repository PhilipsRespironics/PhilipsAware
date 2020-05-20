package com.philips.src.ai.aware.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.philips.src.ai.aware.R
import com.philips.src.ai.aware.ui.SecuredStringRequest
import com.philips.src.ai.aware.ui.login.SignupFragmentDirections
import com.philips.src.ai.aware.ui.login.WearableConsentFragmentArgs
import kotlinx.android.synthetic.main.fragment_dashboard.view.*
import kotlinx.android.synthetic.main.signup_fragment.view.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    val args: DashboardFragmentArgs by navArgs()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)
        val queue = Volley.newRequestQueue(this.context)
        val host = "steward-service-dev.cloud.pcftest.com"
        val url = "https://$host/api/steward/heart_rate/-"
        view.refreshButton.setOnClickListener {_ ->
            val stringRequest = SecuredStringRequest(
                method = Request.Method.GET,
                url = url,
                listener = Response.Listener{ response ->
                    val jsonResp = JSONArray(response)
                    view.heartReatText.text.clear()
                    if(jsonResp.length() > 0) {
                        val lastRate = jsonResp.getJSONObject(jsonResp.length() - 1)
                        view.heartReatText.text.append(lastRate.getInt("heartRate").toString())
                        view.heartReatText.text.append(" at ")
                        view.heartReatText.text.append(lastRate.getString("endTime").toString())
                    }
                    else{
                        view.heartReatText.text.append("No Data")
                    }
                },
                errorListener = Response.ErrorListener {
                    Log.d("kevin", "the call failed")
                    Log.d("kevin", it.message ?: "no message")
                },
                username = args.username?:"",
                password = args.password?:""
            )
            queue.add(stringRequest)
        }

        return view
    }
}
