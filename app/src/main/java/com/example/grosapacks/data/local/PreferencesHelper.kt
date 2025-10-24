package com.example.grosapacks.data.local

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.example.grosapacks.data.model.MenuItemModel
import com.example.grosapacks.data.model.ShopConfigurationModel
import com.example.grosapacks.data.model.UserModel
import com.example.grosapacks.utils.AppConstants
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PreferencesHelper(context: Context) : AppPreferencesHelper {

    private val loginPreferences: SharedPreferences =
        context.getSharedPreferences(AppConstants.LOGIN_PREFS, MODE_PRIVATE)
    private val customerPreferences: SharedPreferences =
        context.getSharedPreferences(AppConstants.CUSTOMER_PREFS, MODE_PRIVATE)
    private val cartPreferences: SharedPreferences =
        context.getSharedPreferences(AppConstants.CART_PREFERENCES, MODE_PRIVATE)

    // Implement all interface properties
    override var userToken: String?
        get() = loginPreferences.getString("user_token", null)
        set(value) = loginPreferences.edit().putString("user_token", value).apply()

    override var isLoggedIn: Boolean
        get() = loginPreferences.getBoolean("is_logged_in", false)
        set(value) = loginPreferences.edit().putBoolean("is_logged_in", value).apply()

    override var password: String?
        get() = loginPreferences.getString(AppConstants.USER_PASSWORD, null)
        set(value) = loginPreferences.edit().putString(AppConstants.USER_PASSWORD, value).apply()

    override var name: String?
        get() = customerPreferences.getString(AppConstants.CUSTOMER_NAME, null)
        set(value) = customerPreferences.edit().putString(AppConstants.CUSTOMER_NAME, value).apply()

    override var email: String?
        get() = customerPreferences.getString(AppConstants.CUSTOMER_EMAIL, null)
        set(value) = customerPreferences.edit().putString(AppConstants.CUSTOMER_EMAIL, value).apply()

    override var mobile: String?
        get() = customerPreferences.getString(AppConstants.CUSTOMER_MOBILE, null)
        set(value) = customerPreferences.edit().putString(AppConstants.CUSTOMER_MOBILE, value).apply()

    override var role: String?
        get() = customerPreferences.getString(AppConstants.CUSTOMER_ROLE, null)
        set(value) = customerPreferences.edit().putString(AppConstants.CUSTOMER_ROLE, value).apply()

    override var oauthId: String?
        get() = loginPreferences.getString(AppConstants.AUTH_TOKEN, null)
        set(value) = loginPreferences.edit().putString(AppConstants.AUTH_TOKEN, value).apply()

    override var userId: Int?
        get() = loginPreferences.getInt(AppConstants.USER_ID, -1)
        set(value) = loginPreferences.edit().putInt(AppConstants.USER_ID, value ?: -1).apply()

    override var fcmToken: String?
        get() = loginPreferences.getString(AppConstants.FCM_TOKEN, null)
        set(value) = loginPreferences.edit().putString(AppConstants.FCM_TOKEN, value).apply()

    override var place: String?
        get() = customerPreferences.getString(AppConstants.CUSTOMER_PLACE, null)
        set(value) = customerPreferences.edit().putString(AppConstants.CUSTOMER_PLACE, value).apply()

    override var shopList: String?
        get() = customerPreferences.getString(AppConstants.SHOP_LIST, null)
        set(value) = customerPreferences.edit().putString(AppConstants.SHOP_LIST, value).apply()

    override var cart: String?
        get() = cartPreferences.getString(AppConstants.CART, null)
        set(value) = cartPreferences.edit().putString(AppConstants.CART, value).apply()

    override var cartShop: String?
        get() = cartPreferences.getString(AppConstants.CART_SHOP, null)
        set(value) = cartPreferences.edit().putString(AppConstants.CART_SHOP, value).apply()

    override var cartDeliveryPref: String?
        get() = cartPreferences.getString(AppConstants.CART_DELIVERY, null)
        set(value) = cartPreferences.edit().putString(AppConstants.CART_DELIVERY, value).apply()

    override var cartShopInfo: String?
        get() = cartPreferences.getString(AppConstants.CART_SHOP_INFO, null)
        set(value) = cartPreferences.edit().putString(AppConstants.CART_SHOP_INFO, value).apply()

    override var cartDeliveryLocation: String?
        get() = cartPreferences.getString(AppConstants.CART_DELIVERY_LOCATION, null)
        set(value) = cartPreferences.edit().putString(AppConstants.CART_DELIVERY_LOCATION, value).apply()

    override var tempMobile: String?
        get() = customerPreferences.getString(AppConstants.TEMP_MOBILE, null)
        set(value) = customerPreferences.edit().putString(AppConstants.TEMP_MOBILE, value).apply()

    override var tempOauthId: String?
        get() = customerPreferences.getString(AppConstants.TEMP_OAUTHID, null)
        set(value) = customerPreferences.edit().putString(AppConstants.TEMP_OAUTHID, value).apply()

    // Implement the saveUser method from interface
    override fun saveUser(
        userId: Int?,
        name: String?,
        email: String?,
        mobile: String?,
        role: String?,
        oauthId: String?,
        password: String?
    ) {
        customerPreferences.edit().apply {
            putString(AppConstants.CUSTOMER_NAME, name)
            putString(AppConstants.CUSTOMER_EMAIL, email)
            putString(AppConstants.CUSTOMER_MOBILE, mobile)
            putString(AppConstants.CUSTOMER_ROLE, role)
            apply()
        }

        loginPreferences.edit().apply {
            putString(AppConstants.AUTH_TOKEN, oauthId)
            putString(AppConstants.USER_PASSWORD, password)
            putBoolean("is_logged_in", true)
            userId?.let { putInt(AppConstants.USER_ID, it) }
            apply()
        }
    }

    // Additional method for OTP verification with authMode (not in interface)
    fun saveUserAfterVerification(
        userId: Long,
        name: String,
        email: String,
        userToken: String,
        mobile: String? = null,
        authMode: Int = -1
    ) {
        customerPreferences.edit().apply {
            putString(AppConstants.CUSTOMER_NAME, name)
            putString(AppConstants.CUSTOMER_EMAIL, email)
            mobile?.let { putString(AppConstants.CUSTOMER_MOBILE, it) }
            apply()
        }

        loginPreferences.edit().apply {
            putInt(AppConstants.USER_ID, userId.toInt())
            putString("user_token", userToken)
            putBoolean("is_logged_in", true)
            putInt("auth_mode", authMode) // Store auth mode separately
            apply()
        }
    }

    override fun clearPreferences() {
        loginPreferences.edit().clear().apply()
        customerPreferences.edit().clear().apply()
        cartPreferences.edit().clear().apply()
    }

    override fun clearCartPreferences() {
        cartPreferences.edit().clear().apply()
    }

    fun getUser(): UserModel? {
        return UserModel(userId, email, mobile, name, oauthId, role, password)
    }

    fun getCart(): List<MenuItemModel>? {
        val listType = object : TypeToken<List<MenuItemModel?>?>() {}.type
        return Gson().fromJson(cart, listType)
    }

    fun getShopList(): List<ShopConfigurationModel>? {
        val listType = object : TypeToken<List<ShopConfigurationModel?>?>() {}.type
        return Gson().fromJson(shopList, listType)
    }

    fun getCartShop(): ShopConfigurationModel? {
        return Gson().fromJson(cartShop, ShopConfigurationModel::class.java)
    }

    // Helper method to get auth mode (not in interface)
    fun getAuthMode(): Int {
        return loginPreferences.getInt("auth_mode", -1)
    }
}