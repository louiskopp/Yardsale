package edu.hope.cs.yardsale.View

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import edu.hope.cs.yardsale.R


class boards : Fragment() {

    var listView: ListView? = null
    var adapter: BoardsAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_boards, container, false)

        listView = view.findViewById(R.id.boards_listview) as ListView
        adapter = BoardsAdapter(context)
        (listView as ListView).adapter = adapter
        return view

    }

    companion object {
        fun newInstance(): boards = boards()
    }
}