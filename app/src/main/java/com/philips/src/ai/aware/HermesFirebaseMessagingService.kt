/*
 * Copyright (C) 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.philips.src.ai.aware

import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*

class HermesFirebaseMessagingService : FirebaseMessagingService() {

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "got a message -> ")
        Log.d(TAG, "${remoteMessage.notification?.body}")
        Log.d(TAG, "-----------------------")
        Log.d(TAG, "$remoteMessage")
        val builder = NotificationCompat.Builder(this.applicationContext, "default")
            .setContentTitle("Philips Aware Alert!!!")
            .setContentText(remoteMessage.notification?.body)
            .setSmallIcon(R.drawable.ic_notifications_black_24dp)
            .setAutoCancel(true)
        Log.d(TAG, "Created Notification Builder")
        with(NotificationManagerCompat.from(this.applicationContext)){
            Log.d(TAG, "Creating Notification")
            notify(888, builder.build())
            Log.d(TAG, "Notify called")
        }
    }
    // [END receive_message]

    //TODO Step 3.2 log registration token
    // [START on_new_token]
    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token)
    }
    // [END on_new_token]


    /**
     * Persist token to third-party servers.
     *
     * @param token The new token.
     */
    private fun sendRegistrationToServer(token: String?) {
        // TODO: Implement this method to send token to your app server.
        Log.d(TAG, "Sending a token to the server: $token")
        Globals.fcmToken = token
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private fun sendNotification(messageBody: String) {
        println("sending a message: $messageBody")
    }

    companion object {
        private const val TAG = "HermesFirebaseMessagingService"
    }
}