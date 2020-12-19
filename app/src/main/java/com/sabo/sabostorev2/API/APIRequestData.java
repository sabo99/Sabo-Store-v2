package com.sabo.sabostorev2.API;

import com.sabo.sabostorev2.Model.ResponseModel;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface APIRequestData {

    /**
     * Sign Up
     */
    @FormUrlEncoded
    @POST("signUp.php")
    Call<ResponseModel> signUp(@Field("email") String email,
                               @Field("username") String username,
                               @Field("password") String password);


    /**
     * Sign In
     */
    @FormUrlEncoded
    @POST("signIn.php")
    Call<ResponseModel> signIn(@Field("email") String email,
                               @Field("username") String username,
                               @Field("password") String password);

    /**
     * Sign Out
     */
    @FormUrlEncoded
    @POST("signOut.php")
    Call<ResponseModel> signOut(@Field("uid") String uid);



    /**
     * Forgot Password
     */
    @FormUrlEncoded
    @POST("checkEmailExist.php")
    Call<ResponseModel> checkEmailExist(@Field("email") String email);

    @FormUrlEncoded
    @POST("forgotPassword.php")
    Call<ResponseModel> forgotPassword(@Field("email") String email,
                                       @Field("password") String password);

    /**
     * Get User Information
     */
    @FormUrlEncoded
    @POST("getUser.php")
    Call<ResponseModel> getUser(@Field("uid") String uid);


    /**
     * Update User Profile
     * - Email
     * - Username
     * - New Password
     */
    @FormUrlEncoded
    @POST("updateUserProfile.php")
    Call<ResponseModel> updateUserProfile(@Field("uid") String uid,
                                          @Field("oldPassword") String oldPassword,
                                          @Field("email") String email,
                                          @Field("username") String username,
                                          @Field("newPassword") String newPassword);

    /**
     * Update User Gender
     * - Gender
     */
    @FormUrlEncoded
    @POST("updateUserGender.php")
    Call<ResponseModel> updateUserGender(@Field("uid") String uid,
                                         @Field("gender") int gender);


    /**
     * Update User Image
     */
    @Multipart
    @POST("updateUserImage.php")
    Call<ResponseModel> updateUserImage(@Part MultipartBody.Part uid,
                                        @Part MultipartBody.Part file,
                                        @Part MultipartBody.Part oldFileName);

    /**
     * Remove User Image
     */
    @FormUrlEncoded
    @POST("removeUserImage.php")
    Call<ResponseModel> removeUserImage(@Field("uid") String uid,
                                        @Field("image") String image);

    /**
     * Get OTP
     * Update User Phone Number
     */
    @FormUrlEncoded
    @POST("getOTP.php")
    Call<ResponseModel> getOTP(@Field("uid") String uid,
                               @Field("phone") String phone,
                               @Field("code") int code);


    /**
     * Remove User Account
     */
    @FormUrlEncoded
    @POST("removeAccount.php")
    Call<ResponseModel> removeAccount(@Field("uid") String uid);


    /**
     * Delivery Cost
     */
    @FormUrlEncoded
    @POST("getDelivery.php")
    Call<ResponseModel> getDeliveryCost(@Field("code") String code);


    /**
     * Slider
     */
    @GET("getSlider.php")
    Call<ResponseModel> getSlider();


    /**
     * ItemStore
     */
    @GET("getItemStore.php")
    Call<ResponseModel> getItemStore();


    /**
     * Items
     */
    @FormUrlEncoded
    @POST("getItems.php")
    Call<ResponseModel> getItems(@Field("itemId") String itemId);


    /**
     * Item Details
     */
    @FormUrlEncoded
    @POST("getItemDetails.php")
    Call<ResponseModel> getItemsDetails(@Field("id") String id);


}
