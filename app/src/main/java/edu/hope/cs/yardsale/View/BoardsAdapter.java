package edu.hope.cs.yardsale.View;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.util.Log;

import java.util.ArrayList;

import edu.hope.cs.yardsale.Model.Board;
import edu.hope.cs.yardsale.Control.API;
import edu.hope.cs.yardsale.Control.APIDelegate;
import edu.hope.cs.yardsale.R;

public class BoardsAdapter extends BaseAdapter implements APIDelegate<ArrayList<Board>> {

    private ArrayList<Board> boards = new ArrayList<Board>();
    private Context context;
    private LayoutInflater inflater;

    public BoardsAdapter(Context context){
        // call the api to get all of the boards
        API.getAllBoards(this);

        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return boards.size();
    }

    @Override
    public Object getItem(int i) {
        return boards.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    static class ViewHolder {
        TextView titleView;
        TextView click;
        int ref;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        final BoardsAdapter.ViewHolder holder;

        if (convertView == null) {
            holder = new BoardsAdapter.ViewHolder();
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.board_item, null);
            holder.titleView = convertView.findViewById(R.id.board_name);
            holder.click = convertView.findViewById(R.id.board_click);
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.ref = position;
        holder.titleView.setText(boards.get(position).getTitle());
        holder.click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DashBoard.class);
                intent.putExtra("board", boards.get(position).getTitle());
                context.startActivity(intent);

            }
        });
        holder.click.bringToFront();
        return convertView;
    }

    public void onAPIDataSuccess(ArrayList<Board> boards) {
      this.boards = boards;
      notifyDataSetChanged();

      Log.d("Yardsale/BoardsAdapter.onAPIDataSuccess", ""+boards.size());
  }

  public void onAPIFailure(Error error) {
      Log.e("Yardsale/BoardsAdapter.onAPIDataSuccess", error.toString());
  }
}
