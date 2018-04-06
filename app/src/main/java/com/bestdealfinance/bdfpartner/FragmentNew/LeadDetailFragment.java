package com.bestdealfinance.bdfpartner.FragmentNew;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bestdealfinance.bdfpartner.AdapterNew.AllLeadsRowAdapter;
import com.bestdealfinance.bdfpartner.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class LeadDetailFragment extends Fragment {

    private RecyclerView leadListRecyclerView;
    public AllLeadsRowAdapter allLeadsRowAdapter;
    private JSONArray leadList;
    private JSONArray productListJsonArray;
    private TextView noDataText;

    public static LeadDetailFragment newInstance(JSONArray leadList, String productListJsonArray) {
        Bundle bundle = new Bundle();
        bundle.putString("leadList", leadList.toString());
        bundle.putString("productListJsonArray", productListJsonArray);
        LeadDetailFragment leadDetailFragment = new LeadDetailFragment();
        leadDetailFragment.setArguments(bundle);
        return leadDetailFragment;
    }

    public LeadDetailFragment() {
    }

    /*public LeadDetailFragment(List<JSONObject> leadList, JSONArray productListJsonArray) {
        this.leadList = leadList;
        this.productListJsonArray = productListJsonArray;
    }*/

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle mBundle = getArguments();
        try {
            this.leadList = new JSONArray(getArguments().getString("leadList"));
            String jsonArr = getArguments().getString("productListJsonArray");
            productListJsonArray = new JSONArray(jsonArr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_lead_in_progress, container, false);
        leadListRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_lead_list);
        noDataText = (TextView) rootView.findViewById(R.id.lead_no_data_available);

        if (leadList == null || leadList.length() <= 0) {
            leadListRecyclerView.setVisibility(View.GONE);
            noDataText.setVisibility(View.VISIBLE);
        } else {
            leadListRecyclerView.setVisibility(View.VISIBLE);
            noDataText.setVisibility(View.GONE);

            ArrayList<JSONObject> listdata = new ArrayList<>();

            try {
                for (int i = 0; i < leadList.length(); i++) {
                    listdata.add(leadList.getJSONObject(i));
                }
            }
            catch (JSONException e){
                e.printStackTrace();
            }

            allLeadsRowAdapter = new AllLeadsRowAdapter(getActivity(), listdata, productListJsonArray);

            leadListRecyclerView.setAdapter(allLeadsRowAdapter);
            leadListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
        return rootView;
    }
}
