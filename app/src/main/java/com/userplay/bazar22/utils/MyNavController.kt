//package com.dev360.m777.utils
//
//import androidx.activity.OnBackPressedCallback
//import androidx.appcompat.app.AlertDialog
//import androidx.navigation.NavOptions
//import androidx.navigation.fragment.NavHostFragment
//import androidx.navigation.fragment.findNavController
//import com.dev360.m777.R
//
//
//class MyNavController : NavHostFragment() {
//
//    override fun onResume() {
//        super.onResume()
//
//        val navController = findNavController()
//        val callback = object : OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
//                if (navController.currentDestination?.id == R.id.homeFragment) {
//                    showExitDialog()
//                } else {
//                    isEnabled = false
//                    requireActivity().onBackPressed()
//                }
//            }
//        }
//        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
//    }
//
//    private fun showExitDialog() {
//        AlertDialog.Builder(requireContext())
//            .setTitle("Exit App")
//            .setMessage("Are you sure you want to exit?")
//            .setPositiveButton("Yes") { _, _ -> requireActivity().finish() }
//            .setNegativeButton("No", null)
//            .show()
//    }
//
//    private fun navigateTo(destination: Int) {
//        val navOptions = NavOptions.Builder()
//            .setLaunchSingleTop(true)
//            .setPopUpTo(R.id.homeFragment, false)
//            .addToBackStack(null) // Add the transaction to the back stack
//            .build()
//        findNavController().navigate(destination, null, navOptions)
//    }
//}
