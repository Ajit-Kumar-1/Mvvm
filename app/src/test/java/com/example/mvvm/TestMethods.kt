package com.example.mvvm

import com.example.mvvm.model.AccountEntity
import org.json.JSONObject
import java.util.*

object TestMethods {

    private const val ADDRESS: String = "address"
    private const val AVATAR: String = "avatar"
    private const val DOB: String = "dob"
    private const val EMAIL: String = "email"
    private const val FIRST_NAME: String = "first_name"
    private const val GENDER: String = "gender"
    private const val HREF: String = "href"
    private const val ID: String = "id"
    private const val LAST_NAME: String = "last_name"
    private const val LINKS: String = "links"
    private const val PHONE: String = "phone"
    private const val STATUS: String = "status"
    private const val WEBSITE: String = "website"


    fun getEntityFromJSON(jsonObject: JSONObject): AccountEntity = jsonObject.let {
        AccountEntity(
            it.getString(ID).toInt(),
            it.getString(FIRST_NAME).trim(),
            it.getString(LAST_NAME).trim(),
            it.getString(GENDER).trim(),
            it.getString(DOB).trim(),
            it.getString(EMAIL).toLowerCase(Locale.ROOT),
            it.getString(PHONE).trim(),
            it.getString(WEBSITE).trim(),
            it.getString(ADDRESS).trim(),
            it.getString(STATUS).trim(),
            it.getJSONObject(LINKS).getJSONObject(AVATAR).getString(HREF)
        )
    }

    fun putChanges(data: Pair<AccountEntity?, AccountEntity?>): HashMap<String, String?> {
        val putMap = HashMap<String, String?>()
        data.first?.apply {
            data.second?.let {
                if (it.firstName?.trim() != firstName?.trim())
                    putMap[FIRST_NAME] = firstName?.trim()
                if (it.lastName?.trim() != lastName?.trim())
                    putMap[LAST_NAME] = lastName?.trim()
                if (it.gender?.trim() != gender)
                    putMap[GENDER] = gender?.trim()
                if (it.dob?.trim() != dob?.trim())
                    putMap[DOB] = dob?.trim()
                if (it.email?.trim() != email?.trim())
                    putMap[EMAIL] = email?.trim()
                if (it.phone?.trim() != phone?.trim())
                    putMap[PHONE] = phone?.trim()
                if (it.website?.trim() != website?.trim())
                    putMap[WEBSITE] = website?.trim()
                if (it.address?.trim() != address?.trim())
                    putMap[ADDRESS] = address?.trim()
                if (it.status?.trim() != status?.trim())
                    putMap[STATUS] = status?.trim()
            }
        }
        return putMap
    }

}
