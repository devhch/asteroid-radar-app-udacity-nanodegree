package com.devhch.asteroidradar.main

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.devhch.asteroidradar.R
import com.devhch.asteroidradar.database.AsteroidDatabase
import com.devhch.asteroidradar.databinding.FragmentMainBinding
import com.google.android.material.snackbar.Snackbar


class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private lateinit var mainViewModel: MainViewModel

    /**
     * Called when the Fragment is ready to display content to the screen.
     *
     * This function uses DataBindingUtil to inflate R.layout.fragment_main.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Get a reference to the binding object and inflate the fragment views.
        binding = FragmentMainBinding.inflate(inflater)

        val application = requireNotNull(this.activity).application

        val dataSource = AsteroidDatabase.getInstance(application).asteroidDao

        val viewModelFactory = MainViewModelFactory(dataSource, application)
        mainViewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]

        // Giving the binding access to the MainViewModel
        binding.viewModel = mainViewModel

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up menu
        setupMenu()

        binding.statusLoadingWheel.visibility = View.VISIBLE

        // Sets the adapter of the asteroid RecyclerView with clickHandler lambda that
        // tells the viewModel when our property is clicked
        val adapter: AsteroidAdapter = AsteroidAdapter(AsteroidListener { asteroidId ->
            mainViewModel.onAsteroidClicked(asteroidId)
        })
        binding.asteroidRecyclerView.adapter = adapter

        // Observe the asteroids LiveData and Add asteroids to
        // AsteroidAdapter when it isn't null
        mainViewModel.asteroids.observe(viewLifecycleOwner) { asteroids ->
            Log.i("asteroids", "asteroids: ${asteroids.size}")
            if (asteroids != null && asteroids.isNotEmpty()) {
                binding.statusLoadingWheel.visibility = View.GONE
                adapter.addHeaderAndSubmitList(asteroids)
            }
        }

        // Observe the navigateToDetailFragment LiveData and Navigate when it isn't null
        // After navigating, call displayPropertyDetailsComplete() so that the ViewModel is ready
        // for another navigation event.
        mainViewModel.navigateToDetailFragment.observe(viewLifecycleOwner) { asteroid ->
            if (asteroid != null) {
                // Must find the NavController from the Fragment
                this.findNavController().navigate(MainFragmentDirections.actionShowDetail(asteroid))

                // Tell the ViewModel we've made the navigate call to prevent multiple navigation
                mainViewModel.navigateToDetailFragmentComplete()
            }
        }

        // Add an Observer on the state variable for showing a Snackbar message
        // when the CLEAR button is pressed.
        mainViewModel.showSnackBarEvent.observe(viewLifecycleOwner, Observer {
            if (it == true) { // Observed state is true.
                Snackbar.make(
                    requireActivity().findViewById(android.R.id.content),
                    getString(R.string.exception_data),
                    Snackbar.LENGTH_SHORT // How long to display the message.
                ).show()
                // Reset state to make sure the snackbar is only shown once, even if the device
                // has a configuration change.
                mainViewModel.doneShowingSnackBar()
            }
        })
    }

    private fun setupMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onPrepareMenu(menu: Menu) {
                // Handle for example visibility of menu items
            }

            /**
             * Inflates the overflow menu that contains filtering options.
             */
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.main_overflow_menu, menu)
            }

            /**
             * Updates the filter in the [MainViewModel] when the menu items are selected from the
             * overflow menu.
             */
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.week_asteroids_menu -> mainViewModel.onWeekAsteroidsMenuClicked()
                    R.id.today_asteroids_menu -> mainViewModel.onTodayAsteroidsMenuClicked()
                    R.id.saved_asteroids_menu -> mainViewModel.onSavedAsteroidsMenuClicked()
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

}
