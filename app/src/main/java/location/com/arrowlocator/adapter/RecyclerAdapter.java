package location.com.arrowlocator.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import location.com.arrowlocator.R;
import location.com.arrowlocator.models.PrimaryLocation;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ListViewHolder> {

    List<PrimaryLocation> mLocation;
    private OnItemClickListner mListner;

    public RecyclerAdapter(List<PrimaryLocation> location){
        this.mLocation = location;
    }

    public interface OnItemClickListner{
        void OnItemClick(int position);
    }
    public void setOnItemClickListner(OnItemClickListner listner){
        this.mListner = listner;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_element, viewGroup, false);

        ListViewHolder vh = new ListViewHolder(view,mListner);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder listViewHolder, int i) {
        listViewHolder.textView.setText(mLocation.get(i).getName());
    }

    @Override
    public int getItemCount() {
        return mLocation.size();
    }

    public static class ListViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView textView;
        public ListViewHolder(View itemView,final OnItemClickListner listner) {
            super(itemView);
            textView = itemView.findViewById(R.id.recycle_item);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listner!= null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listner.OnItemClick(position);
                        }
                    }
                }
            });
        }
    }


}
