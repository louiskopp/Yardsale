package edu.hope.cs.yardsale.View

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import edu.hope.cs.yardsale.Control.HttpUtils
import edu.hope.cs.yardsale.Model.User
import edu.hope.cs.yardsale.R
import java.util.concurrent.Callable
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors


class profile : Fragment() {
    private var firstNameTextView: TextView? = null
    private var emailAddressTextView: TextView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val currentUser = User.getCurrentUser()
        val view: View = inflater.inflate(R.layout.fragment_profile, container, false)
        firstNameTextView = view.findViewById(R.id.userName)
        firstNameTextView!!.text = currentUser.name
        emailAddressTextView = view.findViewById(R.id.userEmail)
        emailAddressTextView!!.text = currentUser.email
        val profileImageView = view.findViewById<ImageView>(R.id.userImage)

        if(currentUser.imageUrl != null){
            Picasso.get().load(currentUser.imageUrl).placeholder(R.drawable.loading).into(profileImageView)
        } else {
            profileImageView.setImageBitmap(BitmapFactory.decodeResource(context!!.resources, R.drawable.profile))
        }

        val prev: TextView? = view.findViewById(R.id.ViewPrevious)
        if (prev != null) {
            prev.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    val intent = Intent(context, DashBoard::class.java)
                    intent.putExtra("board", "userPosts")
                    context!!.startActivity(intent)
                }
            })
        }
        val saved: TextView? = view.findViewById(R.id.ViewSaved)
        if (saved != null) {
            saved.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    val intent = Intent(context, DashBoard::class.java)
                    intent.putExtra("board", "saved")
                    context!!.startActivity(intent)
                }
            })
        }
        return view
    }

    companion object {
        fun newInstance(): profile = profile()
    }
}
