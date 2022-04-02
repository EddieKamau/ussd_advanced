/*
 * Copyright (c) 2020. BoostTag E.I.R.L. Romell D.Z.
 * All rights reserved
 * porfile.romellfudi.com
 */

/**
 * BoostTag E.I.R.L. All Copyright Reserved
 * www.boosttag.com
 */
package com.phan_tech.ussd_advanced

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.telecom.TelecomManager
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import androidx.annotation.RequiresApi

/**
 * @author Romell Dominguez
 * @version 1.1.i 2019/04/18
 * @since 1.1.i
 */

val mapM = hashMapOf(
    "KEY_LOGIN" to listOf("espere", "waiting", "loading", "esperando"),
    "KEY_ERROR" to listOf("problema", "problem", "error", "null"))

@SuppressLint("StaticFieldLeak")
object USSDController : USSDInterface, USSDApi {

    internal const val KEY_LOGIN = "KEY_LOGIN"
    internal const val KEY_ERROR = "KEY_ERROR"

    private val simSlotName = arrayOf("extra_asus_dial_use_dualsim",
            "com.android.phone.extra.slot", "slot", "simslot", "sim_slot", "subscription",
            "Subscription", "phone", "com.android.phone.DialingMode", "simSlot", "slot_id",
            "simId", "simnum", "phone_type", "slotId", "slotIdx")

    lateinit var context: Context
        private set

    var map: HashMap<String, List<String>> = mapM
        private set

    lateinit var callbackInvoke: CallbackInvoke

    var callbackMessage: ((AccessibilityEvent) -> Unit)? = null
        private set

    var isRunning: Boolean? = false
        private set

    var sendType: Boolean? = false
        private set

    private var ussdInterface: USSDInterface? = null

    init {
        ussdInterface = this
    }

    /**
     * Invoke a dial-up calling a ussd number
     *
     * @param ussdPhoneNumber ussd number
     * @param map             Map of Login and problem messages
     * @param callbackInvoke  a listener object as to return answer
     */
    @RequiresApi(Build.VERSION_CODES.M)
    override fun callUSSDInvoke(context: Context, ussdPhoneNumber: String,
                                callbackInvoke: CallbackInvoke) {
        this.context = context
        callUSSDInvoke(this.context, ussdPhoneNumber, 0, callbackInvoke)
    }


    /**
     * Invoke a dial-up calling a ussd number
     *
     * ```
     * ussdApi.callUSSDInvoke(activity,"*515#",0,
     *              hashOf("KEY_LOGIN" to listOf("loading", "waiting"),
     *                      "KEY_ERROR" to listOf("null", "problem")  ),
     *              object : USSDController.CallbackInvoke {
     *                  override fun responseInvoke(message: String) {
     *                  }
     *                  override fun over(message: String) {
     *                  }
     *              }
     *         )
     * ```
     *
     * @param ussdPhoneNumber ussd number
     * @param simSlot         location number of the SIM
     * @param map         Map of Login and problem messages
     * @param callbackInvoke        a listener object as to return answer
     */
    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("MissingPermission")
    override fun callUSSDInvoke(context: Context, ussdPhoneNumber: String, simSlot: Int,
                                callbackInvoke: CallbackInvoke) {
		sendType = false
        this.context = context
        this.callbackInvoke = callbackInvoke
        if (verifyAccessibilityAccess(this.context)) {
            dialUp(ussdPhoneNumber, simSlot)
        } else {
            this.callbackInvoke.over("Check your accessibility")
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun dialUp(ussdPhoneNumber: String, simSlot: Int) {
        when {
            !map.containsKey(KEY_LOGIN) || !map.containsKey(KEY_ERROR) ->
                callbackInvoke.over("Bad Mapping structure")
            ussdPhoneNumber.isEmpty() -> callbackInvoke.over("Bad ussd number")
            else -> {
                val phone = Uri.encode("#")?.let {
                    ussdPhoneNumber.replace("#", it)
                }
                isRunning = true
                context.startActivity(getActionCallIntent(Uri.parse("tel:$phone"), simSlot))
            }
        }
    }

    /**
     * get action call Intent
     * url: https://stackoverflow.com/questions/25524476/make-call-using-a-specified-sim-in-a-dual-sim-device
     *
     * @param uri     parsed uri to call
     * @param simSlot simSlot number to use
     */
    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("MissingPermission")
    private fun getActionCallIntent(uri: Uri?, simSlot: Int): Intent {
        val telcomManager = context.getSystemService(Context.TELECOM_SERVICE) as? TelecomManager
        return Intent(Intent.ACTION_CALL, uri).apply {
            simSlotName.map { sim -> putExtra(sim, simSlot) }
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra("com.android.phone.force.slot", true)
            putExtra("Cdma_Supp", true)
            telcomManager?.callCapablePhoneAccounts?.let { handles ->
                if (handles.size > simSlot)
                    putExtra("android.telecom.extra.PHONE_ACCOUNT_HANDLE", handles[simSlot])
            }
        }
    }

    /**
     * The aim of this function is to send text via [android.widget.EditText]
     *
     * ```
     * ussdApi.sendData("12345")
     * ```
     * @param[text] String will be sent by EditText
     */
    override fun sendData(text: String) = USSDServiceKT.send(text)
    override fun sendData2(text: String, event: AccessibilityEvent) = USSDServiceKT.send2(text, event)


    override fun stopRunning() {
        isRunning = false
    }

    /**
     * The aim of this is to send text(contains 145 characters in avg.)
     *
     * ```
     * ussdApi.send("12345") { it -> "ArrayString"
     *   it.toString()
     * }
     * ```
     *
     * @param[text] String send it into @see [android.widget.EditText]
     * @param[callbackMessage] The listener to get response, for example: ```[Hello,Cancel,Accept]```
     */
    override fun send(text: String, callbackMessage: (AccessibilityEvent) -> Unit) {
        this.callbackMessage = callbackMessage
        sendType = true
        ussdInterface?.sendData(text)
    }
    override fun send2(text: String, event: AccessibilityEvent, callbackMessage: (AccessibilityEvent) -> Unit) {
        this.callbackMessage = callbackMessage
        sendType = true
        ussdInterface?.sendData2(text, event)
    }

    /**
     * Cancel the USSD flow in processing
     *
     * ```
     * ussdApi.callUSSDInvoke( ... ) {
     *      ussdApi.cancel()
     * }
     * ```
     *
     * @see callUSSDInvoke
     *
     */
    override fun cancel() = USSDServiceKT.cancel()
    override fun cancel2(event: AccessibilityEvent) = USSDServiceKT.cancel2(event)

    /**
     * Invoke class to comunicate messages between USSD and App
     */
    interface CallbackInvoke {
        fun responseInvoke(event: AccessibilityEvent)
        fun over(message: String)
    }

    /**
     * The aim of this is to check whether accessibility is enabled or not
     * @param[context] The application context
     * @return The enable value of the accessibility
     */
    override fun verifyAccessibilityAccess(context: Context): Boolean =
            isAccessibilityServicesEnable(context).also {
                if (!it) openSettingsAccessibility(context as Activity)
            }


    private fun openSettingsAccessibility(activity: Activity) {
        activity.startActivityForResult(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS), 1)
    }

    private fun isAccessibilityServicesEnable(context: Context): Boolean {
        (context.getSystemService(Context.ACCESSIBILITY_SERVICE) as? AccessibilityManager)?.apply {
            installedAccessibilityServiceList.forEach { service ->
                if (service.id.contains(context.packageName) &&
                        Settings.Secure.getInt(context.applicationContext.contentResolver, Settings.Secure.ACCESSIBILITY_ENABLED) == 1){
                    Settings.Secure.getString(context.applicationContext.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)?.let {
                        if (it.split(':').contains(service.id)) return true
                    }
                }else if(service.id.contains(context.packageName) && Settings.Secure.getString(context.applicationContext.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES).toString().contains(service.id)){
                    return true;
                }


            }
        }
        return false
    }
}
