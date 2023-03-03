package br.com.alura.panucci

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import br.com.alura.panucci.navigation.AppDestination
import br.com.alura.panucci.sampledata.bottomAppBarItems
import br.com.alura.panucci.sampledata.sampleProducts
import br.com.alura.panucci.ui.components.BottomAppBarItem
import br.com.alura.panucci.ui.components.PanucciBottomAppBar
import br.com.alura.panucci.ui.screens.*
import br.com.alura.panucci.ui.theme.PanucciTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navControler = rememberNavController()
            val backStackEntryState by navControler.currentBackStackEntryAsState()
            val currentDestination = backStackEntryState?.destination

            PanucciTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val selectedItem by remember(currentDestination) {
                        val item = currentDestination?.let { destination ->
                            bottomAppBarItems.find {
                                it.route == destination.route
                            }

                        } ?: bottomAppBarItems.first()
                        mutableStateOf(item)
                    }

                    val containsInBottomAppBarItems = currentDestination?.let { destination ->
                        bottomAppBarItems.find {
                            it.route == destination.route
                        }
                    } != null

                    val isShowFab = when (currentDestination?.route) {
                        AppDestination.Menu.route,
                        AppDestination.Drinks.route -> true

                        else -> false
                    }

                    PanucciApp(
                        bottomAppBarItemSelected = selectedItem,
                        onBottomAppBarItemSelectedChange = {
                            val route = it.route
                            navControler.navigate(route = route) {
                                launchSingleTop = true
                                popUpTo(route)
                            }

                        },
                        onFabClick = {
                            navControler.navigate(AppDestination.Checkout.route)

                        },
                        isShowTopBar = containsInBottomAppBarItems,
                        isShowFab = isShowFab,
                        isShowBottomBar = containsInBottomAppBarItems
                    ) {
                        NavHost(
                            navController = navControler,
                            startDestination = AppDestination.HighLights.route
                        ) {
                            composable(AppDestination.HighLights.route) {
                                HighlightsListScreen(
                                    products = sampleProducts,
                                    onNavigateToCheckout = {
                                        navControler.navigate(AppDestination.Checkout.route)
                                    },
                                    onNavigateToDetails = { product ->
                                        navControler.navigate(
                                            "${AppDestination.ProductDetails.route}/${product.id}"
                                        )
                                    }
                                )
                            }
                            composable(AppDestination.Menu.route) {
                                MenuListScreen(
                                    products = sampleProducts,
                                    onNavigateToDetails = { product ->
                                        navControler.navigate(
                                            "${AppDestination.ProductDetails.route}/${product.id}"
                                        )
                                    }
                                )
                            }
                            composable(AppDestination.Drinks.route) {
                                DrinksListScreen(
                                    products = sampleProducts,
                                    onNavigateToDetails = { product ->
                                        navControler.navigate(
                                            "${AppDestination.ProductDetails.route}/${product.id}"
                                        )

                                    }
                                )
                            }
                            composable(AppDestination.Checkout.route) {
                                CheckoutScreen(
                                    products = sampleProducts,
                                    onPopBackStack = {
                                        navControler.navigateUp()
                                    }
                                )
                            }
                            composable("${AppDestination.ProductDetails.route}/{productId}") { backStackEntry ->
                                val id = backStackEntry.arguments?.getString("productId")
                                sampleProducts.find {
                                    it.id == id
                                }?.let { product ->
                                    ProductDetailsScreen(
                                        product = product,
                                        onNavigateToCheckout = {
                                            navControler.navigate(AppDestination.Checkout.route)


                                        }
                                    )

                                } ?: LaunchedEffect(Unit) {
                                    navControler.navigateUp()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PanucciApp(
    bottomAppBarItemSelected: BottomAppBarItem = bottomAppBarItems.first(),
    onBottomAppBarItemSelectedChange: (BottomAppBarItem) -> Unit = {},
    onFabClick: () -> Unit = {},
    isShowTopBar: Boolean = false,
    isShowFab: Boolean = false,
    isShowBottomBar: Boolean = false,
    content: @Composable () -> Unit

) {
    Scaffold(

        topBar = {
            if (isShowTopBar) {
                CenterAlignedTopAppBar(
                    title = {
                        Text(text = "Ristorante Panucci")
                    },
                )

            }

        },
        bottomBar = {
            if (isShowBottomBar) {
                PanucciBottomAppBar(
                    item = bottomAppBarItemSelected,
                    items = bottomAppBarItems,
                    onItemChange = onBottomAppBarItemSelectedChange,
                )

            }
        },
        floatingActionButton = {

            if (isShowFab) {
                FloatingActionButton(
                    onClick = onFabClick
                ) {
                    Icon(
                        Icons.Filled.PointOfSale,
                        contentDescription = null
                    )
                }

            }
        }
    ) {
        Box(
            modifier = Modifier.padding(it)
        ) {
            content()
        }
    }
}

@Preview
@Composable
private fun PanucciAppPreview() {
    PanucciTheme {
        Surface {
            PanucciApp {}
        }
    }
}