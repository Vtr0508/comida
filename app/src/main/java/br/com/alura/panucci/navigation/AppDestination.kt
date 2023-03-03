package br.com.alura.panucci.navigation

sealed class AppDestination(val route: String) {
    object HighLights: AppDestination("highlights")
    object Menu: AppDestination("menu")
    object Drinks: AppDestination("drinks")
    object Checkout: AppDestination("checkout")
    object ProductDetails: AppDestination("productDetails")
}