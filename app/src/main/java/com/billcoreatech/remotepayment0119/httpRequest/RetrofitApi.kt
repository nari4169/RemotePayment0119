package com.billcoreatech.remotepayment0119.httpRequest

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.lang.reflect.Type

interface RetrofitApi {

    @Headers("Content-Type: application/json")
    @POST("/boss0426_kakao_token")
    fun doGetWithKakaoToken(
        @Body requestBean: RequestBean,
    ): Call<ResponseBean>

    @Headers("Content-Type: application/json")
    @POST("/boss0426_naver_token")
    fun doGetWithNaverToken(
        @Body requestBean: RequestBean,
    ): Call<ResponseBean>

    @Headers("Content-Type: application/json")
    @POST("/boss0426_google_token")
    fun doGetWithGoogleToken(
        @Body requestGoogleBean: RequestGoogleBean,
    ): Call<ResponseBean>

    companion object { // static 처럼 공유객체로 사용가능함. 모든 인스턴스가 공유하는 객체로서 동작함.
        private const val BASE_URL = "https://us-central1-boss0426-f0490.cloudfunctions.net"

        private val client = OkHttpClient.Builder().build()

        fun create(): RetrofitApi {
            val gson : Gson =   GsonBuilder().setLenient().create();
            /** 비어있는(length=0)인 Response를 받았을 경우 처리 */
            val nullOnEmptyConverterFactory = object : Converter.Factory() {
                fun converterFactory() = this
                override fun responseBodyConverter(type: Type, annotations: Array<out Annotation>, retrofit: Retrofit) = object :
                    Converter<ResponseBody, Any?> {
                    val nextResponseBodyConverter = retrofit.nextResponseBodyConverter<Any?>(converterFactory(), type, annotations)
                    override fun convert(value: ResponseBody) = if (value.contentLength() != 0L) {
                        try{
                            nextResponseBodyConverter.convert(value)
                        }catch (e:Exception){
                            e.printStackTrace()
                            null
                        }
                    } else{
                        null
                    }
                }
            }

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(nullOnEmptyConverterFactory)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build()
                .create(RetrofitApi::class.java)
        }

    }
}