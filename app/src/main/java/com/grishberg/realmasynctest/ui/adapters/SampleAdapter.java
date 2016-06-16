package com.grishberg.realmasynctest.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.grishberg.realmasynctest.R;
import com.grishberg.realmasynctest.data.models.SampleModel;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Created by grishberg on 16.06.16.
 */
public class SampleAdapter extends RecyclerView.Adapter<SampleAdapter.SampleViewHolder> {
    private static final String TAG = SampleAdapter.class.getSimpleName();
    private final Context context;
    // realm results
    private final RealmResults<SampleModel> realmResults;
    // observer for handling data change in request
    private RealmChangeListener callback = new RealmChangeListener() {
        @Override
        public void onChange(Object element) {
            Log.d(TAG, "onChange: " + element);
            Toast.makeText(context, context.getString(R.string.data_changed), Toast.LENGTH_SHORT)
                    .show();
            notifyDataSetChanged();
        }
    };

    public SampleAdapter(Context context, RealmResults<SampleModel> realmResults) {
        this.context = context;
        this.realmResults = realmResults;
        realmResults.addChangeListener(callback);
    }

    @Override
    public SampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SampleViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_cell, parent, false));
    }

    @Override
    public void onBindViewHolder(SampleViewHolder holder, int position) {
        SampleModel item = realmResults.get(position);
        if (!item.isValid()) {
            Log.e(TAG, "onBindViewHolder: item is invalid pos = " + position);
            return;
        }
        holder.textView.setText(item.getName());
    }

    @Override
    public int getItemCount() {
        return realmResults.size();
    }

    /**
     * remove change data observer
     */
    public void release() {
        realmResults.removeChangeListener(callback);
    }

    /**
     * View holder for cell view
     */
    public static class SampleViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public SampleViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.tvText);
        }
    }
}
