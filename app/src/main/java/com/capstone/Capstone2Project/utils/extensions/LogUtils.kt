package com.capstone.Capstone2Project.utils.etc

import com.capstone.Capstone2Project.data.model.LiveFeedback
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

fun Face.toLiveFeedbackInfo(): Pair<LiveFeedback.Type, String>? {

    val type: LiveFeedback.Type = LiveFeedback.Type.Camera

    val message: String

    when {
        headEulerAngleX in -10.0f..10.0f -> Unit

        headEulerAngleX > 10.0f -> {
            message = "고개를 살짝 내려주세요"

            return Pair(type,message)
        }
        headEulerAngleX < -10.0f -> {
            message = "고개를 살짝 들어주세요"

            return Pair(type,message)
        }
    }

    when {
        headEulerAngleY in -15.0f..15.0f -> Unit

        headEulerAngleY > 15.0f -> {
            message = "얼굴을 오른쪽으로 살짝 돌려주세요"

            return Pair(type,message)
        }
        headEulerAngleY < -15.0f -> {
            message = "얼굴을 왼쪽으로 살짝 돌려주세요"

            return Pair(type,message)
        }
    }

    when {
        headEulerAngleZ in -10.0f..10.0f -> Unit

        headEulerAngleZ > 15.0f -> {
            message = "얼굴을 반시계 방향으로 살짝 돌려주세요"

            return Pair(type,message)
        }
        headEulerAngleZ < -15.0f -> {
            message = "얼굴을 시계 방향으로 살짝 돌려주세요"

            return Pair(type,message)
        }
    }

    return null
}

fun Pose.toLiveFeedbackInfo(): Triple<LiveFeedback.Type, String, Int>? {

    val type = LiveFeedback.Type.Pose

    val leftShoulderPosition = getPoseLandmark(PoseLandmark.LEFT_SHOULDER)?.position

    val rightShoulderPosition = getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)?.position

    if (leftShoulderPosition != null && rightShoulderPosition != null) {
        val diff = abs(leftShoulderPosition.y - rightShoulderPosition.y)

        if(diff > 30.0f) {
            return Triple(type,"자세를 교정해주세요",0)
        }

    }

    val nosePosition3D = getPoseLandmark(PoseLandmark.NOSE)?.position3D

    val leftHandPosition3D = getPoseLandmark(PoseLandmark.LEFT_INDEX)?.position3D

    val rightHandPosition3D = getPoseLandmark(PoseLandmark.RIGHT_INDEX)?.position3D

    if(nosePosition3D != null && leftHandPosition3D != null) {
        val xSquare = (nosePosition3D.x - leftHandPosition3D.x).pow(2)
        val ySquare = (nosePosition3D.y - leftHandPosition3D.y).pow(2)

        val diff = sqrt(xSquare+ySquare)

        if(diff < 200.0f) {
            return Triple(type,"긴장을 풀어주세요",1)
        }

    }

    if(nosePosition3D != null && rightHandPosition3D != null) {
        val xSquare = (nosePosition3D.x - rightHandPosition3D.x).pow(2)
        val ySquare = (nosePosition3D.y - rightHandPosition3D.y).pow(2)

        val diff = sqrt(xSquare+ySquare)

        if(diff < 200.0f) {
            return Triple(type,"긴장을 풀어주세요",1)
        }
    }

    return null
}