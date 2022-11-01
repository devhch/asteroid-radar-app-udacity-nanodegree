package com.devhch.asteroidradar.detail


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.devhch.asteroidradar.R
import com.devhch.asteroidradar.databinding.FragmentDetailBinding


class DetailFragment : Fragment() {
    private lateinit var viewModel: DetailViewModel

    /**
     * Called when the Fragment is ready to display content to the screen.
     *
     * This function uses DataBindingUtil to inflate R.layout.fragment_detail.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Get a reference to the binding object and inflate the fragment views.
        val binding = FragmentDetailBinding.inflate(inflater)

        viewModel = ViewModelProvider(this)[DetailViewModel::class.java]

        // Giving the binding access to the MainViewModel
        binding.viewModel = viewModel

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        // Get asteroid
        val asteroid = DetailFragmentArgs.fromBundle(requireArguments()).selectedAsteroid
        binding.asteroid = asteroid

        viewModel.displayExplanationDialog.observe(viewLifecycleOwner) { displayExplanationDialog ->
            if (displayExplanationDialog) {
                displayAstronomicalUnitExplanationDialog()
                viewModel.onDisplayExplanationDialogDone()
            }
        }

        binding.helpButton.setOnClickListener {
            displayAstronomicalUnitExplanationDialog()
        }

        return binding.root
    }

    private fun displayAstronomicalUnitExplanationDialog() {
        val builder = AlertDialog.Builder(requireActivity())
            .setMessage(getString(R.string.astronomica_unit_explanation))
            .setPositiveButton(android.R.string.ok, null)
        builder.create().show()
    }
}
