package com.example.android.remotevlcapp.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.example.android.remotevlcapp.R
import dagger.android.support.DaggerFragment

interface NavigationHost {
    fun registerToolbarWithNavigation(toolbar: Toolbar)
}


open class MainNavigationFragment : DaggerFragment() {

    protected var navigationHost: NavigationHost? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is NavigationHost) {
            navigationHost = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        navigationHost = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // If we have a toolbar and we are attached to a proper navigation host, set up the toolbar
        // navigation icon.
        val host = navigationHost ?: return
        val mainToolbar: Toolbar = view.findViewById(R.id.toolbar) ?: return
        mainToolbar.apply {
            host.registerToolbarWithNavigation(this)
        }
    }

}