package com.bestdealfinance.bdfpartner.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.activity.PdfActivity;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * A simple {@link Fragment} subclass.
 */
public class TrainingDocumentFragment extends Fragment {


    private RecyclerView recyclerView;

    public TrainingDocumentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_training_document, container, false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle bundle = getArguments();
        try {
            final JSONArray data = new JSONArray(bundle.getString("data"));
            recyclerView = (RecyclerView) getActivity().findViewById(R.id.listview_training_doc);
            recyclerView.setAdapter(new TrainingDocumentAdapter(data));
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    public class TrainingDocumentAdapter extends RecyclerView.Adapter<TrainingDocumentAdapter.MyViewHolder> {

        private JSONArray data;

        private LayoutInflater inflater;

        public TrainingDocumentAdapter(JSONArray data) {
            inflater = LayoutInflater.from(getActivity());
            this.data = data;
        }


        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.list_item_training_articles, parent, false);
            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            try {
                holder.title.setText(data.getJSONObject(position).getString("title"));
                holder.description.setText(data.getJSONObject(position).getString("description"));
                Glide.with(getActivity()).load(data.getJSONObject(position).getString("image_url")).placeholder(R.drawable.app_icon).into(holder.imageView);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


        @Override
        public int getItemCount() {
            return data.length();
        }

        class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            TextView title, description;
            ImageView imageView;

            public MyViewHolder(View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.list_title);
                description = itemView.findViewById(R.id.list_description);
                imageView = itemView.findViewById(R.id.list_thumbnail);
                imageView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                int i = getAdapterPosition();

                Intent intent = new Intent(getActivity(), PdfActivity.class);
                try {
                    intent.putExtra("title", data.getJSONObject(i).getString("title"));
                    intent.putExtra("link", data.getJSONObject(i).getString("source_url"));
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
