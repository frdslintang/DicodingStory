package com.dicoding.storyapplintang.loginApp.data.remote

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class RegisterResponse(

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readByte() != 0.toByte(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte(if (error) 1 else 0)
        parcel.writeString(message)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RegisterResponse> {
        override fun createFromParcel(parcel: Parcel): RegisterResponse {
            return RegisterResponse(parcel)
        }

        override fun newArray(size: Int): Array<RegisterResponse?> {
            return arrayOfNulls(size)
        }
    }
}