package com.example.cse438.blackjack.util

import android.content.Context

class Card(Id : Int, context: Context) {

    private val id = Id
    private val parentContext = context
    private val value = getValueWithId(Id)


    fun getId() : Int{
        return id
    }

    fun getValue() : Int {
        return value
    }

    private fun getValueWithId(Id : Int) : Int{
            val resName = parentContext.resources.getResourceEntryName(Id)
            var startIndex = resName.lastIndexOf('_')
            if(startIndex<0){
                //card with number
                startIndex = resName.lastIndexOf('s')
            }
            val valueString = resName.substring(startIndex+1)

            return when(valueString){
                "2" -> 2
                "3" -> 3
                "4" -> 4
                "5" -> 5
                "6" -> 6
                "7" -> 7
                "8" -> 8
                "9" -> 9
                "10","jack","queen","king" -> 10
                "ace" -> 11
                else -> -1
            }



    }

}