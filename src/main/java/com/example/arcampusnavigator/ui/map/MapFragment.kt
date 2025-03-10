package com.example.arcampusnavigator.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.arcampusnavigator.R
import com.example.arcampusnavigator.data.model.Building
import com.example.arcampusnavigator.data.model.Room
import com.example.arcampusnavigator.databinding.FragmentMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel

class MapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MapViewModel by viewModel()
    private lateinit var googleMap: GoogleMap

    private lateinit var searchAdapter: SearchResultAdapter
    private lateinit var filterBottomSheetBehavior: BottomSheetBehavior<View>

    private var selectedDestinationId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize map
        val mapFragment = childFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Setup search
        setupSearch()

        // Setup filters
        setupFilters()

        // Setup location button
        binding.fabMyLocation.setOnClickListener {
            viewModel.getCurrentLocation()
        }

        // Setup navigate button
        binding.btnNavigate.setOnClickListener {
            selectedDestinationId?.let { destId ->