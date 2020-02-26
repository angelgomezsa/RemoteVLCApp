package com.example.android.remotevlcapp.ui.browse

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.updatePadding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.core.result.EventObserver
import com.example.android.core.result.Result
import com.example.android.model.FileInfo
import com.example.android.remotevlcapp.R
import com.example.android.remotevlcapp.ui.MainNavigationFragment
import com.example.android.remotevlcapp.util.dpToPx
import com.example.android.remotevlcapp.widget.BottomSheetBehavior
import com.example.android.remotevlcapp.widget.BottomSheetBehavior.Companion.STATE_COLLAPSED
import com.example.android.remotevlcapp.widget.BottomSheetBehavior.Companion.STATE_EXPANDED
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN
import kotlinx.android.synthetic.main.fragment_browse.*
import javax.inject.Inject
import kotlin.math.abs

class BrowseFragment : MainNavigationFragment(), BrowseAdapter.OnFileClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: BrowseViewModel
    private lateinit var browseAdapter: BrowseAdapter
    private lateinit var behavior: BottomSheetBehavior<*>

    private var toolbarTitle: String? = null
    private var isTitleDisplayed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (behavior.state == STATE_EXPANDED) {
                behavior.state = STATE_COLLAPSED
            } else if (!toParentDirectory()) {
                findNavController().popBackStack()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this, viewModelFactory).get(BrowseViewModel::class.java)
        return inflater.inflate(R.layout.fragment_browse, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val behavior = BottomSheetBehavior.from<View>(view.findViewById(R.id.now_playing_sheet))
        behavior = BottomSheetBehavior.from(view.findViewById(R.id.now_playing_sheet))

        browseAdapter = BrowseAdapter(this)
        recyclerView.adapter = browseAdapter
        browseAdapter.differ.submitList(listOf(Result.Loading))

        viewModel.browseUiData.observe(viewLifecycleOwner, Observer {
            toolbarTitle = it.directory
            if (isTitleDisplayed) toolbar.title = toolbarTitle
            directoryText.text = it.directory
            pathText.text = it.path

            recyclerView.run {
                if (itemDecorationCount > 0) {
                    for (i in itemDecorationCount - 1 downTo 0) {
                        removeItemDecorationAt(i)
                    }
                }
                when (it.result) {
                    is Result.Loading,
                    is Result.Error -> browseAdapter.differ.submitList(listOf(it.result))
                    is Result.Success -> {
                        browseAdapter.differ.submitList(it.result.data)
                        addItemDecoration(
                            DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
                        )
                    }
                }
            }
        })

        viewModel.swipeRefreshing.observe(viewLifecycleOwner, EventObserver {
            swipeRefresh.isRefreshing = it
        })

        swipeRefresh.setOnRefreshListener {
            viewModel.onSwipeRefresh()
        }

        appBarLayout.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            val triggerHeight = dpToPx(context!!, 80f)

            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                if (!isTitleDisplayed && abs(verticalOffset) >= triggerHeight) {
                    isTitleDisplayed = true
                    toolbar.title = toolbarTitle
                } else if (isTitleDisplayed && abs(verticalOffset) < triggerHeight) {
                    isTitleDisplayed = false
                    toolbar.title = null
                }
            }
        })

        val _56dp = dpToPx(context!!, 56f)
        val addPaddingAnimator = ValueAnimator.ofInt(0, _56dp).apply {
            addUpdateListener {
                recyclerView.updatePadding(bottom = it.animatedValue as Int)
            }
            duration = 200L
        }
        val removePaddingAnimator = ValueAnimator.ofInt(_56dp, 0).apply {
            addUpdateListener {
                recyclerView.updatePadding(bottom = it.animatedValue as Int)
            }
            duration = 200L
        }

        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    STATE_COLLAPSED -> {
                        if (recyclerView.paddingBottom == 0) {
                            addPaddingAnimator.start()
                        }
                    }
                    STATE_HIDDEN -> removePaddingAnimator.start()
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
    }

    override fun onClick(file: FileInfo) {
        if (file.type == "dir" || file.name == "..") {
            viewModel.browse(file.uri)
        } else {
            viewModel.openFile(file.uri)
        }
    }

    private fun toParentDirectory(): Boolean {
        val item = browseAdapter.differ.currentList[0]
        if (item is FileInfo && item.type == "dir" && item.name == "..") {
            onClick(item)
            return true
        }
        return false
    }
}
