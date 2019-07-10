package edu.hope.cs.yardsale.View

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import edu.hope.cs.yardsale.R

import android.util.Log;
import android.widget.TextView

class feed : Fragment() {

    var listView: ListView? = null
    var adapter: FeedAdapter? = null
    var makePost: Button? = null
    var type: String? = null
    var topText: TextView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val view: View = inflater.inflate(R.layout.fragment_feed, container, false)
            topText = view.findViewById(R.id.settingsBtn)
            type = arguments!!.getString("board")

            if(type==null){
              type = "all"
            }
            if(type.equals("saved")){
                topText!!.setText("Saved Posts")
            }
            if(type.equals("userPosts")){
                topText!!.setText("My Posts")
            }
            if(type.equals("Books")){
                topText!!.setText("Books")
            }
            if(type.equals("Bikes")){
                topText!!.setText("Bikes")
            }
            listView = view.findViewById(R.id.feed_listview) as ListView
            adapter = FeedAdapter(context, type)

            (listView as ListView).adapter = adapter

            makePost = view.findViewById(R.id.feed_add) as Button
            makePost!!.setOnClickListener {
                val intent = Intent(context, MakePost::class.java)
                startActivity(intent)
            }
        return view
    }

    override fun onResume() {
      super.onResume();

      // tell the list view to reload it's data
      ((listView as ListView).adapter as FeedAdapter).reloadData();
    }

    companion object {
        fun newInstance(type: String): feed {
            val args = Bundle()
            args.putString("board", type)
            val fragment = feed()
            fragment.arguments = args
            return fragment
        }
    }
}




