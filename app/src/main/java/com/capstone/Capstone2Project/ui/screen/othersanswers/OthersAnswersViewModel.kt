package com.capstone.Capstone2Project.ui.screen.othersanswers

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.Capstone2Project.data.resource.Resource
import com.capstone.Capstone2Project.data.resource.successOrNull
import com.capstone.Capstone2Project.repository.NetworkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OthersAnswersViewModel @Inject constructor (
    private val repository: NetworkRepository
) : ViewModel() {


    private var _othersAnswersData: MutableStateFlow<Resource<OthersAnswersData>?> = MutableStateFlow(null)
    val othersAnswersData: StateFlow<Resource<OthersAnswersData>?> = _othersAnswersData

    private var _isRefreshing: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    fun changeMyAnswerLike(myAnswerData: AnswerData, like: Boolean) = viewModelScope.launch {

        val ad = othersAnswersData.value?.successOrNull() ?: return@launch

        val result = repository.updateLikeForAnswerData(myAnswerData.uuid, like)

        if(result.successOrNull() != "성공") {
            _othersAnswersData.value = Resource.Error(Exception("네트워크 오류"))
            return@launch
        }


        _othersAnswersData.update {
            Resource.Success(
                ad.copy(
                    myAnswer = ad.myAnswer.copy(
                        like = like,
                        likeCount = (if(like) ad.myAnswer.likeCount+1 else ad.myAnswer.likeCount-1).coerceAtLeast(0)
                    )
                )
            )
        }


    }

    fun changeOthersAnswerLike(othersAnswerData: AnswerData, like: Boolean) = viewModelScope.launch {

        val ad = othersAnswersData.value?.successOrNull() ?: return@launch


        val result = repository.updateLikeForAnswerData(othersAnswerData.uuid, like)


        if(result.successOrNull() != "성공") {
            _othersAnswersData.value = Resource.Error(Exception("네트워크 오류"))
            return@launch
        }


        val copiedOthersAnswers = ad.othersAnswers.toMutableList()

        for(i in copiedOthersAnswers.indices) {

            val currentAnswerData = copiedOthersAnswers[i]

            if (currentAnswerData == othersAnswerData) {
                copiedOthersAnswers[i] = currentAnswerData.copy(
                    like = like,
                    likeCount = (if(like) currentAnswerData.likeCount+1 else currentAnswerData.likeCount-1).coerceAtLeast(0)
                )
            }
        }


        _othersAnswersData.update {
            Resource.Success(
                ad.copy(
                    othersAnswers = copiedOthersAnswers
                )
            )
        }

    }

    fun fetchData(uuid: String) = viewModelScope.launch {
        _othersAnswersData.value = Resource.Loading
        delay(2000)
        val result = repository.getOthersAnswersData(uuid)
        _othersAnswersData.value = result

    }


    fun refreshOthersAnswersData(uuid: String) = viewModelScope.launch {
        _isRefreshing.value = true

        val result = repository.getOthersAnswersData(uuid)

        delay(2000)

        _othersAnswersData.value = result

        _isRefreshing.value = false

    }


}

data class OthersAnswersData(
    val myAnswer: AnswerData,
    val questionData: QuestionData,
    val othersAnswers: List<AnswerData>
)


data class QuestionData(
    val field: String,
    val question: String
)

data class AnswerData(
    val uuid: String,
    val nickName: String,
    val email: String,
    val content: String,
    var like: Boolean,
    var likeCount: Int
)

