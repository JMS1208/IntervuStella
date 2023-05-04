package com.capstone.Capstone2Project.utils.etc

import com.capstone.Capstone2Project.data.model.LogLine
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

fun Face.toLogLine(): LogLine? {

    val type: LogLine.Type = LogLine.Type.Camera

    val message: String

    when {
        headEulerAngleX in -10.0f..10.0f -> Unit

        headEulerAngleX > 10.0f -> {
            message = "고개를 살짝 내려주세요"

            return LogLine(
                type = type,
                message = message
            )
        }
        headEulerAngleX < -10.0f -> {
            message = "고개를 살짝 들어주세요"

            return LogLine(
                type = type,
                message = message
            )
        }
    }

    when {
        headEulerAngleY in -15.0f..15.0f -> Unit

        headEulerAngleY > 15.0f -> {
            message = "얼굴을 오른쪽으로 살짝 돌려주세요"

            return LogLine(
                type = type,
                message = message
            )
        }
        headEulerAngleY < -15.0f -> {
            message = "얼굴을 왼쪽으로 살짝 돌려주세요"

            return LogLine(
                type = type,
                message = message
            )
        }
    }

    when {
        headEulerAngleZ in -10.0f..10.0f -> Unit

        headEulerAngleZ > 15.0f -> {
            message = "얼굴을 반시계 방향으로 살짝 돌려주세요"

            return LogLine(
                type = type,
                message = message
            )
        }
        headEulerAngleZ < -15.0f -> {
            message = "얼굴을 시계 방향으로 살짝 돌려주세요"

            return LogLine(
                type = type,
                message = message
            )
        }
    }

//    smilingProbability?.let {
//        when {
//            it < 0.001f -> {
//                message = "살짝 미소를 띄워볼까요?"
//
//                return LogLine(
//                    type = type,
//                    message = message
//                )
//            }
//
//            it > 0.8f -> {
//                message = "크게 웃는 모습 좋아요"
//
//                return LogLine(
//                    type = type,
//                    message = message
//                )
//            }
//            else -> Unit
//        }
//    }

    return null
}

fun Pose.toLogLine(): LogLine? {

    val type = LogLine.Type.Pose

    val leftShoulderPosition = getPoseLandmark(PoseLandmark.LEFT_SHOULDER)?.position

    val rightShoulderPosition = getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)?.position

    if (leftShoulderPosition != null && rightShoulderPosition != null) {
        val diff = abs(leftShoulderPosition.y - rightShoulderPosition.y)

        if(diff > 30.0f) {
            return LogLine(
                type = type,
                message = "자세를 교정해주세요",
                index = 0
            )
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
            return LogLine(
                type = type,
                message = "긴장을 풀어주세요",
                index = 1
            )
        }

    }

    if(nosePosition3D != null && rightHandPosition3D != null) {
        val xSquare = (nosePosition3D.x - rightHandPosition3D.x).pow(2)
        val ySquare = (nosePosition3D.y - rightHandPosition3D.y).pow(2)

        val diff = sqrt(xSquare+ySquare)

        if(diff < 200.0f) {
            return LogLine(
                type = type,
                message = "긴장을 풀어주세요",
                index = 1
            )
        }
    }


    return null
}