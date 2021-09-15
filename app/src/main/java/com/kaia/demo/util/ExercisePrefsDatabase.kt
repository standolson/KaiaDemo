package com.kaia.demo.util

import android.content.Context
import android.content.SharedPreferences

object ExercisePrefsDatabase {

    private val PREFERENCES_NAME = "favorites_pref"
    private val FAVORITES_KEY = "favorites_list"

    public fun setFavoriteState(context: Context, id: Int?, state: Boolean)
    {
        if (id == null)
            return

        val prefs =
            context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE) ?: return
        val favoriteList = getFavoriteList(prefs)

        // Adding
        if (state) {
            if (favoriteList.contains(id.toString()))
                return
            favoriteList.add(id.toString())
            writeFavoriteList(prefs, favoriteList)
            return
        }

        // Removing
        if (!favoriteList.contains(id.toString()))
            return
        favoriteList.remove(id.toString())
        writeFavoriteList(prefs, favoriteList)
    }

    public fun getFavoriteList(prefs: SharedPreferences): MutableList<String>
    {
        val favoriteList = prefs.getString(FAVORITES_KEY, null)
        if (favoriteList == null || favoriteList.length == 0)
            return mutableListOf()
        val list = favoriteList.split(",")
        return list.toMutableList()
    }

    public fun getFavoriteState(context: Context, id: Int?): Boolean
    {
        if (id == null)
            return false

        val prefs =
            context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE) ?: return false
        val favoriteList = ExercisePrefsDatabase.getFavoriteList(prefs)
        if (favoriteList.size == 0)
            return false

        val state = favoriteList.contains(id.toString())
        return state
    }

    private fun writeFavoriteList(prefs: SharedPreferences, favorites: MutableList<String>)
    {
        val builder: StringBuilder = StringBuilder()

        for (item in favorites) {
            if (builder.length == 0)
                builder.append(item)
            else
                builder.append("," + item)
        }

        with (prefs.edit()) {
            putString(FAVORITES_KEY, builder.toString())
            apply()
        }
    }
}