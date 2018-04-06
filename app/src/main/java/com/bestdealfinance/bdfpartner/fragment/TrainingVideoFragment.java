package com.bestdealfinance.bdfpartner.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.activity.YoutubeActivity;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * A simple {@link Fragment} subclass.
 */
public class TrainingVideoFragment extends Fragment {


    private ListView listView;

    public TrainingVideoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_training_video, container, false);




    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle bundle = getArguments();
        try {
            final JSONArray data = new JSONArray(bundle.getString("data"));
            listView = (ListView) getActivity().findViewById(R.id.listview_training_video);
            listView.setAdapter(new ListViewAdapter(data));
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    try {
                        Intent intent = new Intent(getActivity(), YoutubeActivity.class);
                        intent.putExtra("link",data.getJSONObject(i).getString("source_url"));
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    class ListViewAdapter extends BaseAdapter {

        private JSONArray data;

        public ListViewAdapter(JSONArray data) {
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.length();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater inflater = (LayoutInflater) getActivity()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView;
            if(view!=null)
            {
                rowView = view;
            }
            else
            {
                rowView = inflater.inflate(R.layout.list_item_training_video, viewGroup, false);
            }

            TextView title = rowView.findViewById(R.id.list_title);
            TextView description = rowView.findViewById(R.id.list_description);
            ImageView imageView = rowView.findViewById(R.id.list_thumbnail);

            try {
                title.setText(data.getJSONObject(i).getString("title"));
                description.setText(data.getJSONObject(i).getString("description"));
                Glide.with(getActivity()).load(data.getJSONObject(i).getString("image_url")).into(imageView);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return rowView;
        }
    }

}
