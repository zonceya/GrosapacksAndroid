package com.example.grosapacks.data.retrofit

import com.example.grosapacks.data.model.OrderStatusRequest
import com.example.grosapacks.data.model.PlaceOrderRequest
import com.example.grosapacks.data.model.RatingRequest
import retrofit2.Retrofit

class OrderRepository(retrofit: Retrofit) {
    private val services = retrofit.create(CustomApi::class.java)
    suspend fun getOrderById(orderId: Int) = services.getOrderById(orderId)
    suspend fun getOrders(id: String, pageNum: Int, pageCount: Int) = services.getOrders(id, pageNum, pageCount)
    suspend fun insertOrder(placeOrderRequest: PlaceOrderRequest) = services.insertOrder(placeOrderRequest)
    suspend fun placeOrder(orderId: String) = services.placeOrder(orderId)
    suspend fun rateOrder(ratingRequest: RatingRequest) = services.rateOrder(ratingRequest)
    suspend fun cancelOrder(orderStatusRequest: OrderStatusRequest) = services.cancelOrder(orderStatusRequest)
}