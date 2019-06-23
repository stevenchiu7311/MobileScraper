package com.steven.keepscrap.nestedlist.adapter;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import com.steven.keepscrap.R;
import com.steven.keepscrap.nestedlist.model.Child;

public class ChildAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Child> childData;
    private List<Child> childDataBk;

    public ChildAdapter(List<Child> childData) {
        this.childData = childData;
        childDataBk = new ArrayList<>(childData);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvChild;
        ImageView tvExpandCollapseToggle;

        public ViewHolder(View itemView) {
            super(itemView);
            tvChild = (TextView) itemView.findViewById(R.id.tv_child);
            tvExpandCollapseToggle = (ImageView) itemView.findViewById(R.id.iv_expand_collapse_toggle);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.nested_recycler_item_child, parent, false);

        ChildAdapter.ViewHolder cavh = new ChildAdapter.ViewHolder(itemLayoutView);
        return cavh;
    }


    final Handler handler = new Handler();
    Runnable collapseList = new Runnable() {
        @Override
        public void run() {
            if (getItemCount() > 1) {
                childData.remove(childData.size() - 1);
                notifyDataSetChanged();
                handler.postDelayed(collapseList, 50);
            }
        }
    };

    Runnable expandList = new Runnable() {
        @Override
        public void run() {
            int currSize = childData.size();
            if (currSize == childDataBk.size()) return;

            if (currSize == 0) {
                childData.add(childDataBk.get(currSize));
            } else {
                childData.add(childDataBk.get(currSize));
            }
            notifyDataSetChanged();

            handler.postDelayed(expandList, 50);
        }
    };


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ViewHolder vh = (ViewHolder) holder;

        if (position == 0) {
            vh.tvExpandCollapseToggle.setImageResource((getItemCount() == 1) ? R.drawable.baseline_expand_more_black_24: R.drawable.baseline_expand_less_black_24);
            vh.tvExpandCollapseToggle.setVisibility(View.VISIBLE);
        } else {
            vh.tvExpandCollapseToggle.setVisibility(View.GONE);
        }

        Child c = childData.get(position);
        vh.tvChild.setText(c.getmText());

        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacksAndMessages(null);
                if (getItemCount() > 1) {
                    handler.post(collapseList);
                } else {
                    handler.post(expandList);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return childData.size();
    }
}
