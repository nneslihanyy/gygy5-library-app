package com.turkcell.libraryapp.ui.navigation


//Sayfa Route tanımları
sealed class Screen (val route: String){

        object Login: Screen("login")
        object Register: Screen("register")

}