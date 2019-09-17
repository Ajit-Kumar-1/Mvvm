package com.example.mvvm

import com.example.mvvm.model.APIEntity
import com.example.mvvm.model.StringValues

object TestSamples {
    private val final=StringValues()
    var accountChangesSamples=ArrayList<Pair<Pair<APIEntity,APIEntity>,HashMap<String,String?>>>()
    var conversionSamples=ArrayList<Pair<String,APIEntity>>()

    init {
        accountChangesSamples.add(
            Pair(Pair(APIEntity(10, "Zoe", "Hummels", "female",
                        "12-21-1990", "zoehummels@example.com", "+1402-555-0147",
                        "no Site", "no address", "inactive"),
                    APIEntity(10, "Zoella", "Hummels", "female",
                        "12-21-1990", "zoella.hummels@example.com", "+1200-555-8591",
                        "no Site", "no address", "active")),
                HashMap()))
        accountChangesSamples[0].second.let {
            it[final.FIRST_NAME] = "Zoe"
            it[final.EMAIL] = "zoehummels@example.com"
            it[final.PHONE] = "+1402-555-0147"
            it[final.STATUS] = "inactive"
        }//
        accountChangesSamples.add(
            Pair(Pair(APIEntity(20, "Jane", "Doe", "female",
                null, null, null, null, null, null),
                APIEntity(20, "John", "Doe", "male",
                    null, null, null, null, null, null)),
                HashMap()))
        accountChangesSamples[1].second.let {
            it[final.FIRST_NAME] = "Jane"
            it[final.GENDER] = "female"
        }//
        accountChangesSamples.add(
            Pair(Pair(APIEntity(30,"Sam","Billings ","male",
                null,"sambillings@example.com",null,null,null,"active"),
                APIEntity(30,"Sam  ","Billings","male",
                    null,null,null,"sbc.aus.com",null,"active"))
            , HashMap()))
        accountChangesSamples[2].second.let {
            it[final.EMAIL] = "sambillings@example.com"
            it[final.WEBSITE] = null
        }
        conversionSamples.add(Pair(
                "{\"id\":\"2\",\"first_name\":\"Veers\",\"last_name\":\"Corwin\",\"" + "gender\":" +
                    "\"male\",\"dob\":\"1961-08-14\",\"email\":\"lockman.gail@example.net" +
                    "\",\"phone\":\"1-851-410-0701\",\"website\":none,\"address\":none," +
                    "\"status\":\"active\"}",
                APIEntity(2, "Veers", "Corwin", "male",
                    "1961-08-14", "lockman.gail@example.net", "1-851-410-0701",
                    "none", "none", "active"))
        )//
        conversionSamples.add(Pair(
                "{\"id\":\"22\",\"first_name\":\"Han\",\"last_name\":\"Solo\",\"" + "gender\":"+
                        "\"male\",\"dob\":\"1941-08-14\",\"email\":\"lockman.gail@example.net" +
                        "\",\"phone\":\"1-851-410-0701\",\"website\":none,\"address\":none," +
                        "\"status\":\"inactive\"}",
                APIEntity(22, "Han", "Solo", "male",
                    "1941-08-14", "lockman.gail@example.net", "1-851-410-0701",
                    "none", "none", "inactive"))
            )//
    }
}