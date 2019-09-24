package com.example.mvvm.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "data")
data class AccountEntity(
    @PrimaryKey(autoGenerate = true) @SerializedName("id") var id: Int,
    @SerializedName("first_name") var firstName: String?,
    @SerializedName("last_name") var lastName: String?,
    @SerializedName("gender") var gender: String?,
    @SerializedName("dob") var dob: String?,
    @SerializedName("email") var email: String?,
    @SerializedName("phone") var phone: String?,
    @SerializedName("website") var website: String?,
    @SerializedName("address") var address: String?,
    @SerializedName("status") var status: String?,
    var imageURL: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(firstName)
        parcel.writeString(lastName)
        parcel.writeString(gender)
        parcel.writeString(dob)
        parcel.writeString(email)
        parcel.writeString(phone)
        parcel.writeString(website)
        parcel.writeString(address)
        parcel.writeString(status)
        parcel.writeString(imageURL)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AccountEntity> {
        override fun createFromParcel(parcel: Parcel): AccountEntity {
            return AccountEntity(parcel)
        }

        override fun newArray(size: Int): Array<AccountEntity?> {
            return arrayOfNulls(size)
        }
    }

}
