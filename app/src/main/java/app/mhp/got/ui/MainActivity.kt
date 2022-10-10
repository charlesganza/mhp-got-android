package app.mhp.got.ui

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.Navigation
import app.mhp.got.R
import app.mhp.got.databinding.ActivityMainBinding
import app.mhp.got.ui.utils.Fonts
import dagger.hilt.android.AndroidEntryPoint
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump
import io.github.inflationx.viewpump.ViewPumpContextWrapper

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val installedSplashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)

        /* set font across the entire app
        * bold styles must be set individually
        *  */
        ViewPump.init(ViewPump.builder()
                .addInterceptor(CalligraphyInterceptor(CalligraphyConfig.Builder()
                .setDefaultFontPath(Fonts.FONT_NORMAL)
                .setFontAttrId(R.attr.fontPath)
                .build())).build())

        setContentView(binding.root)

        //setup navigation
        val navController: NavController = Navigation.findNavController(this, R.id.navHostFragment)
        val navGraph = navController.navInflater.inflate(R.navigation.app_navigation_graph)
        navController.graph = navGraph

    }

}
