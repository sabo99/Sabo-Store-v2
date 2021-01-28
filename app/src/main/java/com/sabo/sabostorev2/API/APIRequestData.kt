package com.sabo.sabostorev2.API


import com.sabo.sabostorev2.Model.ExchangeRates.Currency
import com.sabo.sabostorev2.Model.ResponseModel
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*


interface APIRequestData {

    /**
     * ExchangeRatesAPI
     */

    @GET("latest?base=USD")
    fun getExchangeRatesAPI(): Call<Currency>


    /**
     * Sign Up
     */
    @FormUrlEncoded
    @POST("signUp.php")
    fun signUp(@Field("email") email: String?,
               @Field("username") username: String?,
               @Field("password") password: String?): Call<ResponseModel>


    /**
     * Sign In
     */
    @FormUrlEncoded
    @POST("signIn.php")
    fun signIn(@Field("email") email: String?,
               @Field("username") username: String?,
               @Field("password") password: String?): Call<ResponseModel>

    /**
     * Sign In with PIN
     */
    @FormUrlEncoded
    @POST("signInWithPIN.php")
    fun signInWithPIN(@Field("uid") uid: String?,
                      @Field("PIN") PIN: String?): Call<ResponseModel>

    /**
     * Sign Out
     */
    @FormUrlEncoded
    @POST("signOut.php")
    fun signOut(@Field("uid") uid: String?): Call<ResponseModel>


    /**
     * Forgot Password
     */
    @FormUrlEncoded
    @POST("checkEmailExist.php")
    fun checkEmailExist(@Field("email") email: String?): Call<ResponseModel>

    @FormUrlEncoded
    @POST("forgotPassword.php")
    fun forgotPassword(@Field("email") email: String?,
                       @Field("password") password: String?): Call<ResponseModel>

    /**
     * Get User Information
     */
    @FormUrlEncoded
    @POST("getUser.php")
    fun getUser(@Field("uid") uid: String?): Call<ResponseModel>


    /**
     * Update User EmailUsername
     * - Email
     * - Username
     */
    @FormUrlEncoded
    @POST("updateUserEmailUsername.php")
    fun updateUserEmailUsername(@Field("uid") uid: String?,
                                @Field("password") password: String?,
                                @Field("email") email: String?,
                                @Field("username") username: String?): Call<ResponseModel>

    /**
     * Update User Nickname
     */
    @FormUrlEncoded
    @POST("updateUserNickname.php")
    fun updateUserNickname(@Field("uid") uid: String?,
                           @Field("password") password: String?,
                           @Field("nickname") nickname: String?): Call<ResponseModel>


    /**
     * Update User Gender
     * - Gender
     */
    @FormUrlEncoded
    @POST("updateUserGender.php")
    fun updateUserGender(@Field("uid") uid: String?,
                         @Field("gender") gender: Int): Call<ResponseModel>


    /**
     * Update User Image
     */
    @Multipart
    @POST("updateUserImage.php")
    fun updateUserImage(@Part uid: MultipartBody.Part?,
                        @Part file: MultipartBody.Part?,
                        @Part oldFileName: MultipartBody.Part?): Call<ResponseModel>

    /**
     * Remove User Image
     */
    @FormUrlEncoded
    @POST("removeUserImage.php")
    fun removeUserImage(@Field("uid") uid: String?,
                        @Field("image") image: String?): Call<ResponseModel>

    /**
     * Get OTP
     */
    @FormUrlEncoded
    @POST("getOTP.php")
    fun getOTP(@Field("uid") uid: String?,
               @Field("phone") phone: String?,
               @Field("code") code: Int): Call<ResponseModel>


    /**
     * Update User Phone Number
     */
    @FormUrlEncoded
    @POST("updateUserPhone.php")
    fun updateUserPhone(@Field("uid") uid: String?,
                        @Field("phone") phone: String?,
                        @Field("countryCode") countryCode: String?): Call<ResponseModel>

    /**
     * UpdateUserPIN
     */
    @FormUrlEncoded
    @POST("updateUserPIN.php")
    fun updateUserPIN(@Field("uid") uid: String?,
                      @Field("isPIN") isPIN: Int?,
                      @Field("PIN") PIN: String?): Call<ResponseModel>


    /**
     * Remove User Account
     */
    @FormUrlEncoded
    @POST("removeAccount.php")
    fun removeAccount(@Field("uid") uid: String?,
                      @Field("email") email: String?,
                      @Field("password") password: String?): Call<ResponseModel>


    /**
     * Reset Password
     */
    @FormUrlEncoded
    @POST("resetPassword.php")
    fun resetPassword(@Field("uid") uid: String?,
                      @Field("newPassword") newPassword: String?): Call<ResponseModel>


    /**
     * Delivery Cost
     */
    @FormUrlEncoded
    @POST("getDelivery.php")
    fun getDeliveryCost(@Field("code") code: String?): Call<ResponseModel>


    /**
     * Slider
     */
    @GET("getSlider.php")
    fun getSlider(): Call<ResponseModel>


    /**
     * ItemSearch
     */
    @GET("getItemSearch.php")
    fun getItemSearch(): Call<ResponseModel>


    /**
     * Item Most Popular
     */
    @GET("getMostPopular.php")
    fun getMostPopular(): Call<ResponseModel>


    /**
     * ItemStore
     */
    @GET("getItemStore.php")
    fun getItemStore(): Call<ResponseModel>


    /**
     * Items
     */
    @FormUrlEncoded
    @POST("getItems.php")
    fun getItems(@Field("itemId") itemId: String?): Call<ResponseModel>


//    /**
//     * Item Details
//     */
//    @FormUrlEncoded
//    @POST("getItemDetails.php")
//    fun getItemsDetails(@Field("id") id: String?): Call<ResponseModel>


    /**
     * Items ORDER
     */
    @FormUrlEncoded
    @POST("inOrders.php")
    fun order(@Field("orderID") orderID: String?,
              @Field("uid") uid: String?,
              @Field("totalPrice") totalPrice: Double?): Call<ResponseModel>


    @FormUrlEncoded
    @POST("inOrderDetails.php")
    fun orderDetail(@Field("orderID") orderID: String?,
                    @Field("itemName") itemName: String?,
                    @Field("itemQuantity") itemQuantity: Int?,
                    @Field("itemPrice") price: Double?,
                    @Field("itemImage") itemImage: String?): Call<ResponseModel>


    @FormUrlEncoded
    @POST("getOrders.php")
    fun getOrders(@Field("uid") uid: String?): Call<ResponseModel>


    @FormUrlEncoded
    @POST("getOrdersByStatus.php")
    fun getOrdersByStatus(@Field("uid") uid: String?,
                          @Field("orderStatus") orderStatus: Int?): Call<ResponseModel>


    @FormUrlEncoded
    @POST("getOrderDetails.php")
    fun getOrderDetails(@Field("orderID") orderID: String?): Call<ResponseModel>
}