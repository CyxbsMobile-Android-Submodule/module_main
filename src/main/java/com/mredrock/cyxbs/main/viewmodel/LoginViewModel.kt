package com.mredrock.cyxbs.main.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.bean.User
import com.mredrock.cyxbs.common.event.LoginStateChangeEvent
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.network.exception.UnsetUserInfoException
import com.mredrock.cyxbs.common.utils.extensions.*
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.common.viewmodel.event.ProgressDialogEvent
import com.mredrock.cyxbs.main.R
import com.mredrock.cyxbs.main.network.ApiService
import com.umeng.analytics.MobclickAgent
import io.reactivex.rxkotlin.zipWith
import org.greenrobot.eventbus.EventBus

/**
 * Created By jay68 on 2018/8/12.
 */
class LoginViewModel : BaseViewModel() {
    val backToMainOrEditInfoEvent: LiveData<Boolean> = MutableLiveData()

    fun login(stuNum: String?, idNum: String?) {
        if (stuNum?.length ?: 0 < 10) {
            toastEvent.value = R.string.main_activity_login_not_input_account
            return
        } else if (idNum?.length ?: 0 < 6) {
            toastEvent.value = R.string.main_activity_login_not_input_password
            return
        }
        verifyByWeb(stuNum!!, idNum!!)
    }

    private fun verifyByWeb(stuNum: String, idNum: String) {
        val apiService = ApiGenerator.getApiService(ApiService::class.java)
        val observableSource = apiService.getPersonInfo(stuNum, idNum)
                .map {
                    val user = it.nextOrError()
                    if (user.stunum.isNullOrEmpty()) {
                        throw IllegalStateException(BaseApp.context.getString(R.string.main_user_info_error))
                    } else if (user.nickname.isNullOrEmpty()) {
                        throw UnsetUserInfoException()
                    }
                    user
                }

        apiService.verify(stuNum, idNum)
                .mapOrThrowApiException()
                .zipWith(observableSource, User.CREATOR::cloneFromUserInfo)
                .setSchedulers()
                .doOnErrorWithDefaultErrorHandler {
                    if (it is UnsetUserInfoException) {
                        (backToMainOrEditInfoEvent as MutableLiveData).value = false
                        return@doOnErrorWithDefaultErrorHandler true
                    } else if (it is IllegalStateException && it.message.equals(BaseApp.context.getString(R.string.main_user_info_error))) {
                        toastEvent.value = R.string.main_user_info_error
                        return@doOnErrorWithDefaultErrorHandler true
                    }
                    return@doOnErrorWithDefaultErrorHandler false
                }
                .doFinally { progressDialogEvent.value = ProgressDialogEvent.DISMISS_DIALOG_EVENT }
                .doOnSubscribe { progressDialogEvent.value = ProgressDialogEvent.SHOW_NONCANCELABLE_DIALOG_EVENT }
                .safeSubscribeBy {
                    (backToMainOrEditInfoEvent as MutableLiveData).value = true
                    BaseApp.user = it
                    MobclickAgent.onProfileSignIn(it.stuNum)
                    EventBus.getDefault().post(LoginStateChangeEvent(true))
                }
                .lifeCycle()
    }
}