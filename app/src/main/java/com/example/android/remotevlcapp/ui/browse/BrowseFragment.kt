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
import com.example.android.core.result.Result
import com.example.android.model.FileInfo
import com.example.android.remotevlcapp.R
import com.example.android.remotevlcapp.ui.MainNavigationFragment
import com.example.android.remotevlcapp.util.dpToPx
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.*
import kotlinx.android.synthetic.main.browse_loading.*
import kotlinx.android.synthetic.main.browse_no_connection.*
import kotlinx.android.synthetic.main.fragment_browse.*
import javax.inject.Inject
import kotlin.math.abs

class BrowseFragment : MainNavigationFragment(), BrowseAdapter.OnFileClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: BrowseViewModel
    private lateinit var browseAdapter: BrowseAdapter
    private lateinit var behavior: BottomSheetBehavior<View>

    private var toolbarTitle: String = ""
    private var isTitleDisplayed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            onBackPressed()
        }

        viewModel = ViewModelProvider(this, viewModelFactory).get(BrowseViewModel::class.java)

        arguments?.let {
            val args = BrowseFragmentArgs.fromBundle(it)
            val path = args.path ?: "file:///"
            viewModel.browse(path)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_browse, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nowPlayingSheet = view.findViewById<View>(R.id.now_playing_sheet)
        behavior = BottomSheetBehavior.from(nowPlayingSheet)

        browseAdapter = BrowseAdapter(this)
        browse_recyclerView.adapter = browseAdapter

        swipeRefresh.setOnRefreshListener { viewModel.onSwipeRefresh() }
        no_connection.setOnClickListener { viewModel.browse() }
        setRecyclerViewPadding()

        appBarLayout.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            val triggerHeight = dpToPx(context!!, 80f)
            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                if (abs(verticalOffset) >= triggerHeight && !isTitleDisplayed) {
                    toolbar.title = toolbarTitle
                    isTitleDisplayed = true
                } else if (abs(verticalOffset) < triggerHeight && isTitleDisplayed) {
                    toolbar.title = ""
                    isTitleDisplayed = false
                }
            }
        })

        viewModel.browseUiData.observe(viewLifecycleOwner, Observer {
            updateBrowseUi(it)
        })
    }

    private fun setRecyclerViewPadding() {
        val recyclerViewPadding = dpToPx(context!!, 56f)
        val addPaddingAnimator = ValueAnimator.ofInt(0, recyclerViewPadding).apply {
            addUpdateListener {
                browse_recyclerView.updatePadding(bottom = it.animatedValue as Int)
            }
            duration = 200L
        }
        val removePaddingAnimator = ValueAnimator.ofInt(recyclerViewPadding, 0).apply {
            addUpdateListener {
                browse_recyclerView.updatePadding(bottom = it.animatedValue as Int)
            }
            duration = 200L
        }

        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    STATE_COLLAPSED -> {
                        if (browse_recyclerView.paddingBottom == 0) {
                            addPaddingAnimator.start()
                        }
                    }
                    STATE_HIDDEN -> removePaddingAnimator.start()
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        if (behavior.state == STATE_COLLAPSED) {
            addPaddingAnimator.start()
        }
    }

    private fun updateBrowseUi(uiData: BrowseUiData) {
        swipeRefresh.isRefreshing = false
        toolbarTitle = uiData.directory
        toolbar.title = toolbarTitle
        directoryText.text = uiData.directory
        pathText.text = uiData.path
        when (uiData.result) {
            is Result.Loading -> {
                browse_progressBar.visibility = View.VISIBLE
                loading.visibility = View.VISIBLE
                no_connection.visibility = View.GONE
                swipeRefresh.visibility = View.GONE
            }
            is Result.Error -> {
                browse_progressBar.visibility = View.GONE
                loading.visibility = View.GONE
                no_connection.visibility = View.VISIBLE
                swipeRefresh.visibility = View.GONE
            }
            is Result.Success -> {
                browse_progressBar.visibility = View.GONE
                loading.visibility = View.GONE
                no_connection.visibility = View.GONE
                swipeRefresh.visibility = View.VISIBLE
                val list = uiData.result.data
                browseAdapter.submitList(list)

                browse_recyclerView.run {
                    if (itemDecorationCount > 0) {
                        for (i in itemDecorationCount - 1 downTo 0) {
                            removeItemDecorationAt(i)
                        }
                    }
                    addItemDecoration(
                        DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
                    )
                }
            }
        }
    }

    override fun onClick(file: FileInfo) {
        if (file.type == "dir" || file.name == "..") {
            findNavController().navigate(BrowseFragmentDirections.toDirectory(file.uri))
        } else {
            viewModel.openFile(file.uri)
        }
    }

    private fun onBackPressed(): Boolean {
        return if (::behavior.isInitialized && behavior.state == STATE_EXPANDED) {
            behavior.state = STATE_COLLAPSED
            true
        } else {
            findNavController().popBackStack()
            false
        }
    }
}
