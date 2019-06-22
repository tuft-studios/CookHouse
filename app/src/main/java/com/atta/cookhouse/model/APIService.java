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
            @Field("address_id") int address,
            @Field("phone") String phone,
            @Field("user_id") int userId,
            @Field("schedule") String schedule,
            @Field("order_time") String orderTime,
            @Field("creation_time") String creationTime,
            @Field("location") String location
    );


    @FormUrlEncoded
    @POST("get_order_dishes")
    Call<Dishes> getOrderDishes(
            @Field("order_id") int id
    );


    @FormUrlEncoded
    @POST("save_token")
    Call<Result> saveToken(
            @Field("token") String token,
            @Field("user_id") int userId
    );


    @FormUrlEncoded
    @POST("get_orders")
    Call<MyOrdersResult> getMyOrders(
            @Field("user_id") int userId
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
    @POST("update_user")
    Call<Result> updateProfile(
            @Field("id") int id,
            @Field("name") String name,
            @Field("add_id") int addId,
            @Field("date_of_birth") String DateOfBirth,
            @Field("job") String job,
            @Field("location") String location

    );

    @FormUrlEncoded
    @POST("phone_update.php")
    Call<SmsResult> sendSms(
            @Field("phone_number") String phoneNumber,
            @Field("user_id") int userId
    );


    @FormUrlEncoded
    @POST("confirm_phone_code")
    Call<Result> confirmPhoneCode(
            @Field("phone") String phoneNumber,
            @Field("user_id") int userId,
            @Field("rand") String rand
    );


    @FormUrlEncoded
    @POST("forgot_password.php")
    Call<SmsResult> sendPasswordSms(
            @Field("phone_number") String phoneNumber
    );


    @FormUrlEncoded
    @POST("confirm_password_code")
    Call<Result> confirmPasswordCode(
            @Field("password") String password,
            @Field("mobile") String mobile,
            @Field("rand") String rand
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

    @FormUrlEncoded
    @POST("add_address")
    Call<Result> addAddress(
            @Field("user_id") int userId,
            @Field("floor") String floor,
            @Field("apartmentNumber") String apartmentNumber,
            @Field("buildingNumber") String buildingNumber,
            @Field("area") String area,
            @Field("addressName") String addressName,
            @Field("fullAddress") String fullAddress,
            @Field("street") String street,
            @Field("landMark") String landMark,
            @Field("latitude") float latitude,
            @Field("longitude") float longitude

    );


    @PUT("edit_address/{id}/{user_id}/{floor}/{apartmentNumber}/{buildingNumber}/{area}/{addressName}/{fullAddress}/{street}/{landMark}/{latitude}/{longitude}")
    Call<Result> editAddress(
            @Path("id") int id,
            @Path("user_id") int userId,
            @Path("floor") String floor,
            @Path("apartmentNumber") String apartmentNumber,
            @Path("buildingNumber") String buildingNumber,
            @Path("area") String area,
            @Path("addressName") String addressName,
            @Path("fullAddress") String fullAddress,
            @Path("street") String street,
            @Path("landMark") String landMark,
            @Path("latitude") float latitude,
            @Path("longitude") float longitude

    );

    @DELETE("remove_address/{id}")
    Call<Result> deleteAddress(
            @Path("id") int id
    );


    //the image call
    @retrofit.http.GET("images/{imageName}")
    retrofit.Call<ResponseBody> getImageDetails(
            @retrofit.http.Path("imageName")  String imageName);



}
