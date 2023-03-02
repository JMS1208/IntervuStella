package com.capstone.Capstone2Project.utils.extensions

import java.text.DecimalFormat

fun Float.string() = DecimalFormat("#.#").format(this)