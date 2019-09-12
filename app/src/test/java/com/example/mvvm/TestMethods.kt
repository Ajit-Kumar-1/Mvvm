package com.example.mvvm

import com.example.mvvm.model.APIEntity
import com.example.mvvm.model.StringValues
import org.json.JSONObject
import java.util.*

object TestMethods {
    private val final= StringValues()
    fun retrieveDetails(jsonObject:JSONObject):APIEntity= jsonObject.let {
        APIEntity(it.getString(final.ID).toInt(), it.getString(final.FIRST_NAME),
            it.getString(final.LAST_NAME), it.getString(final.GENDER), it.getString(final.DOB),
            it.getString(final.EMAIL).toLowerCase(Locale.ROOT), it.getString(final.PHONE),
            it.getString(final.WEBSITE), it.getString(final.ADDRESS), it.getString(final.STATUS))
    }
    fun putChanges(data:Pair<APIEntity?,APIEntity?>): HashMap<String, String?> {
        val putMap = HashMap<String, String?>()
        data.first?.apply {
            data.second?.let {
                if (it.firstName?.trim() != firstName?.trim())
                    putMap[final.FIRST_NAME] = firstName?.trim()
                if (it.lastName?.trim() != lastName?.trim())
                    putMap[final.LAST_NAME] = lastName?.trim()
                if (it.gender?.trim() != gender)
                    putMap[final.GENDER] = gender?.trim()
                if (it.dob?.trim() != dob?.trim())
                    putMap[final.DOB] = dob?.trim()
                if (it.email?.trim() != email?.trim())
                    putMap[final.EMAIL] = email?.trim()
                if (it.phone?.trim() != phone?.trim())
                    putMap[final.PHONE] = phone?.trim()
                if (it.website?.trim() != website?.trim())
                    putMap[final.WEBSITE] = website?.trim()
                if (it.address?.trim() != address?.trim())
                    putMap[final.ADDRESS] = address?.trim()
                if (it.status?.trim() != status?.trim())
                    putMap[final.STATUS] = status?.trim()
            }
        }
        return putMap
    }
}