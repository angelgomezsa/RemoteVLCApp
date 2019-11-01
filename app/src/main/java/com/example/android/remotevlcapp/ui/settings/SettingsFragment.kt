package com.example.android.remotevlcapp.ui.settings

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.android.remotevlcapp.R
import com.example.android.remotevlcapp.model.Theme
import com.example.android.remotevlcapp.ui.MainNavigationFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_settings.*
import javax.inject.Inject

class SettingsFragment : MainNavigationFragment() {

    companion object {
        private const val THEME_SETTING_DIALOG = "theme_setting_dialog"
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: SettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(SettingsViewModel::class.java)
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        settings_choose_theme.setOnClickListener {
            ThemeSettingDialog().show(childFragmentManager, THEME_SETTING_DIALOG)
        }

        current_theme.text = viewModel.theme
        settings_enable_notifications.isChecked = viewModel.isNotificationsEnabled
        settings_enable_notifications.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setNotificationEnabled(isChecked)
        }

        settings_keep_screen_on.isChecked = viewModel.keepScreenOn
        settings_keep_screen_on.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setKeepScreenOn(isChecked)
        }
    }

    class ThemeSettingDialog : DialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val viewModel = (requireParentFragment() as SettingsFragment).viewModel
            val theme = viewModel.theme
            val themes = arrayOf(Theme.LIGHT, Theme.DARK)
            val checkedPosition = (themes.indices).indexOfFirst { index ->
                themes[index] == theme
            }

            return MaterialAlertDialogBuilder(context)
                .setTitle(context?.getString(R.string.theme_dialog_title))
                .setSingleChoiceItems(themes, checkedPosition) { dialog, position ->
                    viewModel.setTheme(themes[position])
                    dialog.dismiss()
                }
                .setNegativeButton(android.R.string.cancel, null)
                .create()
        }
    }
}