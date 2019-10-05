package com.mredrock.cyxbs.main.network

import com.mredrock.cyxbs.common.bean.RedrockApiStatus
import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import com.mredrock.cyxbs.common.bean.TokenBean
import com.mredrock.cyxbs.common.bean.User
import com.mredrock.cyxbs.main.bean.StartPage
import com.mredrock.cyxbs.main.viewmodel.LoginBody
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created By jay68 on 2018/8/10.
 */
interface ApiService {
    @POST("/app/index.php/Home/Photo/showPicture")
    fun getStartPage(): Observable<RedrockApiWrapper<List<StartPage>>>

    @FormUrlEncoded
    @POST("/api/verify")
    fun verify(@Field("stuNum") stuNum: String, @Field("idNum") idNum: String): Observable<RedrockApiWrapper<User>>

    @FormUrlEncoded
    @POST("/app/index.php/Home/Person/search")
    fun getPersonInfo(@Field("stuNum") stuNum: String, @Field("idNum") idNum: String): Observable<RedrockApiWrapper<User>>

    @FormUrlEncoded
    @POST("/app/index.php/Home/Person/setInfo")
    fun updateUserInfo(@Field("stuNum") stuNum: String,
                       @Field("idNum") idNum: String,
                       @Field("nickname") nickname: String): Observable<RedrockApiStatus>

    @FormUrlEncoded
    @POST("/app/index.php/QA/Question/getQuestionInfo")
    fun getQuestion(
            @Field("stunum") stuNum: String,
            @Field("idnum") idNum: String,
            @Field("question_id") qid: String): Observable<ResponseBody>


    @POST("/app/token")
    fun getPersonInfoByToken(@Body a: LoginBody): Observable<RedrockApiWrapper<TokenBean>>

}