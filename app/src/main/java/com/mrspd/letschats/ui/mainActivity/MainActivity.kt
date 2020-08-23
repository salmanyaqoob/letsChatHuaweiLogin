package com.mrspd.letschats.ui.mainActivity

import android.content.Context
import android.os.Bundle
import android.util.Log.d
import android.util.Log.w
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.facebook.CallbackManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*
import com.google.firebase.firestore.DocumentReference
import com.mrspd.letschats.R
import com.mrspd.letschats.databinding.ActivityMainBinding
import com.mrspd.letschats.util.AuthUtil
import com.mrspd.letschats.util.FirestoreUtil
import com.mrspd.letschats.util.eventbus_events.ConnectionChangeEvent
import com.mrspd.letschats.util.eventbus_events.KeyboardEvent
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MainActivity : AppCompatActivity() {
    private var userDocRef: DocumentReference? = AuthUtil.getAuthId().let {
        FirestoreUtil.firestoreInstance.collection("users").document(it)
    }
    var isActivityRecreated = false

    lateinit var callbackManager: CallbackManager
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = FirestoreUtil.firestoreInstance
        db.collection("users").document(AuthUtil.getAuthId()).update("status", true)



//        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        val database = FirebaseDatabase.getInstance()
        val myConnectionsRef = database.getReference("users")

// Stores the timestamp of my last disconnect (the last time I was seen online)
        val lastOnlineRef = database.getReference("/users/${AuthUtil.getAuthId()}/lastOnline")
        val status = database.getReference("/users/${AuthUtil.getAuthId()}/status")

        val connectedRef = database.getReference(".info/connected")
        connectedRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected: Boolean = (snapshot.getValue() ?: false) as Boolean
                if (connected) {
                    status.setValue("online")
             //       db.collection("users").document(AuthUtil.getAuthId()).update("status", true)

                    // When this device disconnects, remove it
//                    val con = myConnectionsRef.push()
//
//                    // When this device disconnects, remove it
//                    con.onDisconnect().removeValue(DatabaseReference.CompletionListener { error, ref ->
//                        db.collection("users").document(AuthUtil.getAuthId())
//                            .update("status", "offldgdfgine")
//                        d("gghh", "status is : offline")
//                    })

                    // When I disconnect, update the last time I was seen online
                    lastOnlineRef.onDisconnect().setValue(ServerValue.TIMESTAMP)
                    status.onDisconnect().setValue("offline")
                    // Add this device to my connections list
                    // this value could contain info about the device or a timestamp too

                }
            }

            override fun onCancelled(error: DatabaseError) {
                w("gghh", "Listener was cancelled at .info/connected")
            }
        })

        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        sharedViewModel = ViewModelProviders.of(this).get(SharedViewModel::class.java)
        setSupportActionBar(binding.toolbar)
        //hide toolbar on signup,login fragments
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.label == "SignupFragment" || destination.label == "LoginFragment") {
                binding.toolbar.visibility = View.GONE
            } else {
                binding.toolbar.visibility = View.VISIBLE
            }
        }
        //register to event bus to receive callbacks
        EventBus.getDefault().register(this)
        //hide toolbar on signup,login fragments
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.label == "SignupFragment" || destination.label == "LoginFragment") {
                binding.toolbar.visibility = View.GONE
            } else {
                binding.toolbar.visibility = View.VISIBLE
            }
        }
        //setup toolbar with navigation
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.homeFragment, R.id.loginFragment))
        findViewById<Toolbar>(R.id.toolbar)
            .setupWithNavController(navController, appBarConfiguration)



        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }


    // Show snackbar whenever the connection state changes
    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onConnectionChangeEvent(event: ConnectionChangeEvent): Unit {
        if (!isActivityRecreated) {//to not show toast on configuration changes
            Snackbar.make(binding.coordinator, event.message, Snackbar.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onKeyboardEvent(event: KeyboardEvent) {
        hideKeyboard()
    }


    private fun hideKeyboard() {

        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(binding.toolbar.windowToken, 0)

    }

    fun isValidDestination(destination: Int): Boolean {
        return destination != Navigation.findNavController(this, R.id.nav_host_fragment)
            .currentDestination!!.id
    }




//
//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        return super.onCreateOptionsMenu(menu)
//
//         menuInflater.inflate(R.menu.main_menu,menu)
//        val menuItem = menu.findItem(R.id.action_incoming_requests)
//        val actionView = menuItem?.actionView
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
//
//        R.id.action_add_friend -> {
//            Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.action_homeFragment_to_findUserFragment)
//            true
//        }
//        R.id.action_edit_profile -> {
//            Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.action_homeFragment_to_profileFragment)
//            true
//        }
//        R.id.action_logout -> {
//            logout()
//            true
//        }
//        R.id.action_incoming_requests -> {
//            Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.action_homeFragment_to_incomingRequestsFragment)
//
//
//            true
//        }
//
//        else -> {
//            // If we got here, the user's action was not recognized.
//            // Invoke the superclass to handle it.
//            super.onOptionsItemSelected(item)
//        }
//
//    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.chatFragment -> {
                    d("gghh", "yes group")

                    var navOptions = NavOptions.Builder().setPopUpTo(R.id.nav_graph, true).build()
                    if (isValidDestination(R.id.homeFragment)) {
                        Navigation.findNavController(this, R.id.nav_host_fragment)
                            .navigate(R.id.homeFragment, null, navOptions)
                    }
                }
//                R.id.groupFragment -> {
//                    d("gghh", "yes group")
//                    if (isValidDestination(R.id.groupFragment)) {
//                        Navigation.findNavController(this, R.id.nav_host_fragment)
//                            .navigate(R.id.homeFragmentRoom)
//                    }
//                }
//                R.id.ARselfieFragment -> {
//                    d("gghh", "yes group")
//
//                    if (isValidDestination(R.id.ARselfieFragment)) {
//                        Navigation.findNavController(this, R.id.nav_host_fragment)
//                            .navigate(R.id.ARSelfieFragmentHome)
//                    }
//                }
                R.id.searchFragment -> {
                    d("gghh", "yes group")

                    if (isValidDestination(R.id.searchFragment)) {
                        Navigation.findNavController(this, R.id.nav_host_fragment)
                            .navigate(R.id.findUserFragment)
                    }
                }
                R.id.profileFragment -> {
                    d("gghh", "yes group")

                    if (isValidDestination(R.id.profileFragment)) {
                        Navigation.findNavController(this, R.id.nav_host_fragment)
                            .navigate(R.id.profileFragment)
                    }
                }
            }
            item.isChecked = true
            false
        }
    /////////////////////////////////////////////////////////////////////////

    companion object{
        fun getContext(): Context{
            return this.getContext()
        }
    }
}