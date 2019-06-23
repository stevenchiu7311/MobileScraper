package com.steven.keepscrap.nestedlist.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import com.steven.keepscrap.R;
import com.steven.keepscrap.nestedlist.NestedRecyclerLinearLayoutManager;
import com.steven.keepscrap.nestedlist.model.Child;
import com.steven.keepscrap.nestedlist.model.ParentChild;


public class ParentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<ParentChild> parentChildData;
    Context ctx;

    public ParentAdapter(Context ctx, ArrayList<ParentChild> parentChildData) {
        this.ctx = ctx;
        this.parentChildData = parentChildData;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        RecyclerView rv_child;

        public ViewHolder(View itemView) {
            super(itemView);
            rv_child = (RecyclerView) itemView.findViewById(R.id.rv_child);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.nested_recycler_item_parent, parent, false);
        ParentAdapter.ViewHolder pavh = new ParentAdapter.ViewHolder(itemLayoutView);
        return pavh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder vh = (ViewHolder) holder;
        ParentChild p = parentChildData.get(position);
        initChildLayoutManager(vh.rv_child, p.getChild());
    }

    private void initChildLayoutManager(RecyclerView rv_child, List<Child> childData) {
        rv_child.setLayoutManager(new NestedRecyclerLinearLayoutManager(ctx));
        ChildAdapter childAdapter = new ChildAdapter(childData);
        rv_child.setAdapter(childAdapter);
    }

    @Override
    public int getItemCount() {
        return parentChildData.size();
    }
}
