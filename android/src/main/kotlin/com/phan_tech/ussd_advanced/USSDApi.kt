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

import android.content.Context
import android.view.accessibility.AccessibilityEvent
import java.util.*

/**
 *
 * @author Romell Dominguez
 * @version 1.1.i 2019/04/18
 * @since 1.1.i
 */
interface USSDApi {
    fun send(text: String, callbackMessage: (AccessibilityEvent) -> Unit)
    fun send2(text: String, event: AccessibilityEvent, callbackMessage: (AccessibilityEvent) -> Unit)
    fun cancel()
    fun cancel2(event: AccessibilityEvent)
    fun callUSSDInvoke(context: Context, ussdPhoneNumber: String,
                       callbackInvoke: USSDController.CallbackInvoke)

    fun callUSSDInvoke(context: Context, ussdPhoneNumber: String, simSlot: Int,
                       callbackInvoke: USSDController.CallbackInvoke)

    fun verifyAccessibilityAccess(context: Context): Boolean
}
