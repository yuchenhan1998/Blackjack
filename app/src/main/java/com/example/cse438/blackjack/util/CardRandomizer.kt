package com.example.cse438.blackjack.util

import android.content.Context
import com.example.cse438.blackjack.R

import java.util.ArrayList

/**
 * Created by dennis on 26/01/2016.
 * Method returns an array of card ID's
 */
class CardRandomizer {
    fun getIDs(context: Context): ArrayList<Int> {

        val res = ArrayList<Int>()
        val drawableResources = R.drawable()
        val c = R.drawable::class.java
        val fields = c.declaredFields

        var i = 0
        val max = fields.size
        while (i < max) {
            val resourceId: Int
            try {
                resourceId = fields[i].getInt(drawableResources)
                val name = context.resources.getResourceEntryName(resourceId)
                //Use regex to filter out system ressources
                if (name.matches("(clubs|joker|spades|diamonds|hearts).*".toRegex()))
                    res.add(resourceId)
            } catch (e: Exception) {
                i++
                continue
            }

            i++
        }
        //return the resulting array
        return res
    }

}
