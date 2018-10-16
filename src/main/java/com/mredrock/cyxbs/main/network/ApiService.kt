package com.mredrock.cyxbs.main.network

import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import com.mredrock.cyxbs.common.bean.User
import com.mredrock.cyxbs.main.bean.StartPage
import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created By jay68 on 2018/8/10.
 */
interface ApiService {
    @POST("/cyxbsMobile/index.php/Home/Photo/showPicture")
    fun getStartPage(): Observable<RedrockApiWrapper<List<StartPage>>>

    @FormUrlEncoded
    @POST("/api/verify")
    fun verify(@Field("stuNum") stuNum: String, @Field("idNum") idNum: String): Observable<RedrockApiWrapper<User>>

    @FormUrlEncoded
    @POST("/cyxbsMobile/index.php/Home/Person/search")
    fun getPersonInfo(@Field("stuNum") stuNum: String, @Field("idNum") idNum: String): Observable<RedrockApiWrapper<User>>
}