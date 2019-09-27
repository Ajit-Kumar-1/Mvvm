package com.example.mvvm

import com.example.mvvm.model.AccountEntity
import org.json.JSONObject
import java.util.*

object TestMethods {

    private const val ADDRESS_KEY: String = "address"
    private const val AVATAR_KEY: String = "avatar"
    private const val DOB_KEY: String = "dob"
    private const val EMAIL_KEY: String = "email"
    private const val FIRST_NAME_KEY: String = "first_name"
    private const val GENDER_KEY: String = "gender"
    private const val HREF_KEY: String = "href"
    private const val ID_KEY: String = "id"
    private const val LAST_NAME_KEY: String = "last_name"
    private const val LINKS_KEY: String = "links"
    private const val PHONE_KEY: String = "phone"
    private const val STATUS_KEY: String = "status"
    private const val WEBSITE_KEY: String = "website"


    fun getEntityFromJSON(jsonObject: JSONObject): AccountEntity = jsonObject.let {
        AccountEntity(
            it.getString(ID_KEY).toInt(),
            it.getString(FIRST_NAME_KEY).trim(),
            it.getString(LAST_NAME_KEY).trim(),
            it.getString(GENDER_KEY).trim(),
            it.getString(DOB_KEY).trim(),
            it.getString(EMAIL_KEY).trim().toLowerCase(Locale.ROOT),
            it.getString(PHONE_KEY).trim(),
            it.getString(WEBSITE_KEY).trim(),
            it.getString(ADDRESS_KEY).trim(),
            it.getString(STATUS_KEY).trim(),
            it.getJSONObject(LINKS_KEY).getJSONObject(AVATAR_KEY).getString(HREF_KEY)
        )
    }

    fun findChanges(currentAccount: AccountEntity?, originalAccount: AccountEntity?):
            HashMap<String, String?> = HashMap<String, String?>().also { map ->
        currentAccount?.run {
            originalAccount?.let {
                if (it.firstName?.trim() != firstName?.trim())
                    map[FIRST_NAME_KEY] = firstName?.trim()
                if (it.lastName?.trim() != lastName?.trim()) map[LAST_NAME_KEY] = lastName?.trim()
                if (it.gender?.trim() != gender) map[GENDER_KEY] = gender?.trim()
                if (it.dob?.trim() != dob?.trim()) map[DOB_KEY] = dob?.trim()
                if (it.email?.trim() != email?.trim()) map[EMAIL_KEY] = email?.trim()
                if (it.phone?.trim() != phone?.trim()) map[PHONE_KEY] = phone?.trim()
                if (it.website?.trim() != website?.trim()) map[WEBSITE_KEY] = website?.trim()
                if (it.address?.trim() != address?.trim()) map[ADDRESS_KEY] = address?.trim()
                if (it.status?.trim() != status?.trim()) map[STATUS_KEY] = status?.trim()
            }
        }
    }

}
