package com.atta.cookhouse.model;

import com.squareup.okhttp.ResponseBody;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface APIService {


    //The register call
    @FormUrlEncoded
    @POST("register")
    Call<Result> createUser(
            @Field("name") String name,
            @Field("email") String email,
            @Field("job") String job,
            @Field("password") String password,
            @Field("phone") String phone,
            @Field("birthday") String birthday,
            @Field("location") String location
    );

    @FormUrlEncoded
    @POST("add_order")
    Call<OrdersResult> addOrder(
            @FieldMap Map<String, String> dishes ,
            @FieldMap Map<String, String> quantities,
            @Field("subtotal") int subtotal,
            @Field("delivery") int delivery,
            @Field("discount") int discount,
            @Field("total") int total,
            @Field("address") String address,
            @Field("user_id") int userId,
            @Field("schedule") String schedule,
            @Field("order_time") String orderTime,
            @Field("creation_time") String creationTime,
            @Field("kitchen") int kitchen,
            @Field("location") String location
    );

    //the signin call
    @FormUrlEncoded
    @POST("login")
    Call<Result> userLogin(
            @Field("email") String email,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("get_profile")
    Call<Profile> getProfile(
            @Field("user_id") int userId
    );

    @FormUrlEncoded
    @POST("example.php")
    Call<SmsResult> sendSms(
            @Field("phone_number") String phoneNumber
    );

    //the signin call
    @FormUrlEncoded
    @POST("get_menu")
    Call<Dishes> getMenu(
            @Field("type") String type,
            @Field("location") String location
    );

    @FormUrlEncoded
    @POST("add_to_favorite")
    Call<AddFavResult> addToFavorite(
            @Field("dish_id") int propertyId,
            @Field("user_id") int userId
    );


    @PUT("update_password/{id}/{old_password}/{password}")
    Call<Result> resetPassword(
            @Path("id") int userId,
            @Path("old_password") String oldPassword,
            @Path("password") String password
    );

    @DELETE("remove_from_favorite/{id}")
    Call<Result> removeFromFavorite(
            @Path("id") int id
    );

    @FormUrlEncoded
    @POST("remove_from_favorite")
    Call<Result> removeFromFavorite(
            @Field("user_id") int userId,
            @Field("dish_id") int dishId
    );


    @FormUrlEncoded
    @POST("check_if_favorite")
    Call<FavResult> checkIfFavorite(
            @Field("dish_id") int propertyId,
            @Field("user_id") int userId
    );

    @FormUrlEncoded
    @POST("get_favorite")
    Call<Dishes> getFavorite(
            @Field("user_id") int userId
    );

    @FormUrlEncoded
    @POST("get_addresses")
    Call<Addresses> getAddresses(
            @Field("user_id") int userId
    );

    @DELETE("remove_address/{id}")
    Call<Result> removeAddress(
            @Path("id") int id
    );

    //the image call
    @retrofit.http.GET("images/{imageName}")
    retrofit.Call<ResponseBody> getImageDetails(
            @retrofit.http.Path("imageName")  String imageName);



}
