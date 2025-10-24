package com.example.grosapacks.di

import com.example.grosapacks.ui.login.LoginViewModel
import com.example.grosapacks.ui.otp.OtpViewModel
import com.example.grosapacks.ui.signup.SignUpViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
   // viewModel { HomeViewModel(get()) }
   viewModel { LoginViewModel(get()) }
  //  viewModel { OtpViewModel(get()) }
   // viewModel { RestaurantViewModel(get()) }

    viewModel { SignUpViewModel(get()) } // only one `get()` for UserRepository
    viewModel { OtpViewModel(get()) }    // only one `get()` for UserRepository
 //   viewModel { ProfileViewModel(get(), get(),get()) }
 //   viewModel { SearchViewModel(get(), get()) }
 //   viewModel { OrderViewModel(get()) }
//    viewModel { CartViewModel(get()) }
 //   viewModel { PlaceOrderViewModel(get()) }
 //   viewModel { PaymentViewModel(get()) }
 //   viewModel { ContributorViewModel() }
}