package com.example.recipecreator.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Recipe
    (
    var image: String? = "",
    var title: String? = "",
    var category: String? = "",
    var recipeDetail: String? = ""
) : Parcelable