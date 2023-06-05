  package com.capstone.Capstone2Project.ui.screen.interview3

import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.capstone.Capstone2Project.utils.RequestPermissions
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import java.util.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
@Preview(showBackground = true)
fun RecognitionScreen() {

    val permissions = remember {
        listOf(android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.INTERNET)
    }

    val permissionState = rememberMultiplePermissionsState(permissions = permissions)

    if (permissionState.allPermissionsGranted) {
        RecognitionContent()
    } else {
        RequestPermissions(permissionState = permissionState)
    }

}

@Composable
fun RecognitionContent() {
    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

    val context = LocalContext.current

    intent.apply {
        //음성 인식기에 사용되는 음성모델 정보 설정
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH)
        putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
        //음성 인식기에 인식되는 언어 설정
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "ko-KR")

        putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)

        //부분 인식결과를 출력할 것인지를 설정 default가 false라서 여기서는 true로 지정
        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        //신뢰도를 의미

        //putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 20000)
        //최소한 이 시간이 지나야 인식이 끝남
        //putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 5000)
        //putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 10000)

        //putExtra(RecognizerIntent.EXTRA_PROMPT, "아무말이나 해보세요")
    }

    var text by remember {
        mutableStateOf("")
    }

    var partialText by remember {
        mutableStateOf("")
    }

    val speechFinished = remember {
        mutableStateOf(false)
    }




    val speechRecognizer: SpeechRecognizer = remember {
        SpeechRecognizer.createSpeechRecognizer(context)
    }

    val listener = object : RecognitionListener {


        override fun onReadyForSpeech(params: Bundle?) {
            //말하기 시작할 준비가 되면 호출

        }

        override fun onBeginningOfSpeech() {
            //말하기 시작했을 때 호출

        }

        override fun onRmsChanged(rmsdB: Float) {
            //입력받는 소리의 크기를 알려줌
            partialText += "데시벨($rmsdB)"
        }

        override fun onBufferReceived(buffer: ByteArray?) {
            //말을 시작하고 인식이 된 단어를 buffer에 담음
        }

        override fun onEndOfSpeech() {
            //말하기를 잠시 중지하면 호출
            Log.d("TAG", "onEndOfSpeech 호출")
        }

        override fun onError(error: Int) {
            //네트워크 또는 인식 오류가 발생했을 때 호출
            when (error) {
                SpeechRecognizer.ERROR_AUDIO -> { //3
                    //오디오 에러
                }
                SpeechRecognizer.ERROR_CLIENT -> { //5
                    //클라이언트 에러
                    //start, cancel, stop 등을 실패할 경우

                }
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> {
                    //퍼미션 부족
                }
                SpeechRecognizer.ERROR_NETWORK -> {
                    //네트워크 에러
                }
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> {
                    //네트워크 타임 아웃
                }
                SpeechRecognizer.ERROR_NO_MATCH -> { //7
                    //에러 찾을 수 없음

                }
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> { //8
                    //Recognizer 가 바쁨

                }
                SpeechRecognizer.ERROR_SERVER -> {
                    //서버가 이상함
                }
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> {
                    //말하는 시간 초과
                }
                else -> {
                    //알 수 없는 오류
                }
            }
            Toast.makeText(context, "에러: $error", Toast.LENGTH_SHORT).show()

        }

        override fun onResults(results: Bundle?) {
            //인식 결과가 준비되면 호출
            //말을 하면 ArrayList에 단어를 넣고 textView에 단어를 이어줌
            results ?: return
            val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            //val confidenceScore = results.getFloat(RecognizerIntent.EXTRA_CONFIDENCE_SCORES)
            matches?.let {
                it.forEach { match ->
                    text += " $match"
                }
//                speechFinished.value = true
            }


        }

        override fun onPartialResults(partialResults: Bundle?) {
            //부분 인식 결과를 사용할 수 있을 때 호출
            partialResults ?: return
            val matches = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)

            matches?.forEach { match ->
                partialText = match
            }
        }

        override fun onEvent(eventType: Int, params: Bundle?) {
            //이벤트를 추가하기 위해 예약
        }

    }


    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("부분 인식")
        Text(text = partialText, color = Color.Blue)

        Text("누적")
        Text(text = text)

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    speechRecognizer.apply {
                        setRecognitionListener(listener)
                        startListening(intent)
                    }
                    speechFinished.value = false
                }
            ) {
                Text("시작")
            }

            Button(
                onClick = {
                    speechRecognizer.apply {
                        stopListening()

                    }
                }
            ) {
                Text("중지")
            }
        }

    }
}