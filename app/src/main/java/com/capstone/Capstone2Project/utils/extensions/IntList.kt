package com.capstone.Capstone2Project.utils.extensions

fun List<Int>.sumElements(): Int {
    var sum = 0

    for (element in this){
        sum += element
    }
    return sum
}