package com.yasincidemcarto.androidtest.ui.fragment

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.yasincidemcarto.androidtest.R
import com.yasincidemcarto.androidtest.ui.adapter.SearchPoiAdapter
import com.yasincidemcarto.androidtest.core.ext.toPixelFromDip
import com.yasincidemcarto.androidtest.core.util.ResultOf
import com.yasincidemcarto.androidtest.databinding.FragmentPoiListDialogListDialogBinding
import com.yasincidemcarto.androidtest.ui.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PoiListDialogFragment : BottomSheetDialogFragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private var searchPoiAdapter: SearchPoiAdapter? = null
    private lateinit var binding: FragmentPoiListDialogListDialogBinding
    private val sheetDialog: BottomSheetDialog by lazy {
        this.dialog as BottomSheetDialog
    }

    override fun getTheme(): Int {
        return R.style.CustomBottomSheetDialog
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                viewModel.setSearchQuery()
                showSearchBar()
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPoiListDialogListDialogBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        configureDialog(sheetDialog, requireActivity().resources.configuration)

        binding.searchEditText.addTextChangedListener {
            viewModel.setSearchQuery(it.toString())
        }
        viewModel.filteredListOfPoi.observe(viewLifecycleOwner) { response ->
            when (response) {
                is ResultOf.Success -> {
                    binding.notFoundImage.visibility = if (response.data.isEmpty()) View.VISIBLE else View.GONE
                    searchPoiAdapter = SearchPoiAdapter { poiClicked ->
                        viewModel.updatePoint(poiClicked)
                        viewModel.setSearchQuery()
                        showSearchBar()
                        sheetDialog.dismiss()
                    }
                    binding.listOfPoiRecyclerView.setHasFixedSize(true)
                    searchPoiAdapter?.submitList(response.data)
                    binding.listOfPoiRecyclerView.layoutManager = LinearLayoutManager(context)
                    binding.listOfPoiRecyclerView.adapter = searchPoiAdapter
                    binding.shimmerFrameLayout.visibility = View.GONE
                }
                is ResultOf.Failure -> {
                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        configureDialog(sheetDialog, newConfig)
    }

    @SuppressLint("SwitchIntDef")
    private fun configureDialog(dialog: BottomSheetDialog?, configuration: Configuration) {
        dialog?.behavior?.apply {
            when(configuration.orientation) {
                Configuration.ORIENTATION_LANDSCAPE -> {
                    isFitToContents = true
                }
                Configuration.ORIENTATION_PORTRAIT -> {
                    isFitToContents = false
                    expandedOffset = context?.toPixelFromDip(16f) ?: 0
                }
            }
            skipCollapsed = true
            state = STATE_EXPANDED
            isGestureInsetBottomIgnored = false
            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                        BottomSheetBehavior.STATE_HALF_EXPANDED ->  {
                            state = BottomSheetBehavior.STATE_HIDDEN
                        }
                        BottomSheetBehavior.STATE_HIDDEN, BottomSheetBehavior.STATE_COLLAPSED -> {
                            showSearchBar()
                        }
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    if (slideOffset > 0 && slideOffset < 1.0 ) {
                        requireActivity().apply {
                            findViewById<ViewGroup>(R.id.appBarLayout).alpha = 1 - slideOffset
                            findViewById<TextView>(R.id.textView).alpha = 1 - slideOffset
                            findViewById<ImageView>(R.id.imageView).alpha = 1 - slideOffset
                        }
                    }
                    else if (slideOffset < 0)
                       showSearchBar()
                }
            })
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        viewModel.setSearchQuery()
        searchPoiAdapter = null
        super.onDismiss(dialog)
    }

    override fun onDestroy() {
        viewModel.setSearchQuery()
        searchPoiAdapter = null
        super.onDestroy()
    }

    private fun showSearchBar() {
        requireActivity().apply {
            findViewById<ViewGroup>(R.id.appBarLayout).alpha = 1.0f
            findViewById<ImageView>(R.id.imageView).alpha = 1.0f
            findViewById<TextView>(R.id.textView).alpha = 1.0f
        }
    }

}