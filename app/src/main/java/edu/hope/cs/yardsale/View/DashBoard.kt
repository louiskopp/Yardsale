package edu.hope.cs.yardsale.View

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import edu.hope.cs.yardsale.R

class DashBoard : AppCompatActivity() {

    var type: String? = null
    var bundle = Bundle()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dash_board)
        if(intent!=null) {
            type = intent.getStringExtra("board")
        }
       if(type==null){
           type="all"
       }
        val bottomNavigation: BottomNavigationView = findViewById(R.id.navigationView)
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        val firstFragment = feed.newInstance(type!!)
        openFragment(firstFragment)
    }
    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_feed -> {
                val fragment = feed.newInstance("all")
                openFragment(fragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_boards -> {
                val fragment = boards.newInstance()
                openFragment(fragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_profile -> {
                val fragment = profile.newInstance()
                openFragment(fragment)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}