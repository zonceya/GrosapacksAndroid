package com.example.grosapacks.data.retrofit

import com.example.grosapacks.data.model.LoginRequest
import com.example.grosapacks.data.model.LoginResponse
import com.example.grosapacks.data.model.LoginVerifyOTPResponse
import com.example.grosapacks.data.model.NotificationTokenUpdate
import com.example.grosapacks.data.model.OrderItemListModel
import com.example.grosapacks.data.model.OrderStatusRequest
import com.example.grosapacks.data.model.OtpResponse
import com.example.grosapacks.data.model.PlaceOrderRequest
import com.example.grosapacks.data.model.RatingRequest
import com.example.grosapacks.data.model.ShopConfigurationModel
import com.example.grosapacks.data.model.SignupRequest
import com.example.grosapacks.data.model.SignupResponse
import com.example.grosapacks.data.model.SignupVerifyOTPRequest
import com.example.grosapacks.data.model.SignupVerifyOTPResponse
import com.example.grosapacks.data.model.UpdateUserRequest
import com.example.grosapacks.data.model.UserModel
import com.example.grosapacks.data.model.VerifyOrderResponse
import retrofit2.Response
import retrofit2.http.*

interface CustomApi {
    @POST("/auth/signup")
    suspend fun signup(@Body signupRequest: SignupRequest): Response<SignupResponse>

    @POST("/auth/verify_signup")
   // suspend fun verifySignup(@Body verifyRequest: SignupVerifyOTPRequest): Response<OtpResponse>
    suspend fun verifySignupOtp(@Body verifyRequest: SignupVerifyOTPRequest): Response<SignupVerifyOTPResponse>
    @POST("/auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>
    @POST("/auth/verify_login")
    suspend fun verifyLoginOtp(@Body loginVerifyRequest: SignupVerifyOTPRequest): Response<LoginVerifyOTPResponse>
    //USER REPO
  //  suspend fun verifyLoginOtp(request: SignupVerifyOTPRequest): Response<LoginVerifyOTPResponse>
 //   @POST("/user/customer")
  //suspend fun login(@Body loginRequest: LoginRequest): Response<UserModel>

    @PATCH("/user/place") //This can be used for both sign-up and updating profile
    suspend fun updateUser(@Body updateUserRequest: UpdateUserRequest): Response<UserModel>
    @PATCH("/user/notif")
    suspend fun updateFcmToken(@Body notificationTokenUpdateModel: NotificationTokenUpdate): Response<String>

    //SHOP REPO
    @GET("/shop/place/{placeId}")
    suspend fun getShops(@Path("placeId") placeId: String): Response<List<ShopConfigurationModel>>

    //PLACE REPO
    @GET("/place")
  //  suspend fun getPlaceList(): Response<List<PlaceModel>>

    //ITEM REPO
  //  @GET("/menu/{placeId}/{query}")
   // suspend fun searchItems(@Path("placeId") placeId: String, @Path("query") query: String): Response<List<MenuItemModel>>
 //   @GET("/menu/shop/{shopId}")
 //   suspend fun getMenu(@Path("shopId") shopId: String): Response<List<MenuItemModel>>

    //ORDER REPO
  //  @GET("/order/{orderId}")
    suspend fun getOrderById(@Path("orderId") orderId: Int): Response<OrderItemListModel>
    @GET("/order/customer/{userId}/{pageNum}/{pageCount}")
    suspend fun getOrders(
            @Path("userId") id: String,
            @Path("pageNum") pageNum: Int,
            @Path("pageCount") pageCount: Int): Response<List<OrderItemListModel>>
    @POST("/order")
    suspend fun insertOrder(@Body placeOrderRequest: PlaceOrderRequest): Response<VerifyOrderResponse>
    @POST("/order/place/{orderId}")
    suspend fun placeOrder(@Path("orderId") orderId: String): Response<String>
    @PATCH("/order/rating")
    suspend fun rateOrder(@Body ratingRequest: RatingRequest): Response<String>
    @PATCH("/order/status")
    suspend fun cancelOrder(@Body orderStatusRequest: OrderStatusRequest): Response<String>


}