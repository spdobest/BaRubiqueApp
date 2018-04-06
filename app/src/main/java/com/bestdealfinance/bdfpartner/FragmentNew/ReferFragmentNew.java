package com.bestdealfinance.bdfpartner.FragmentNew;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bestdealfinance.bdfpartner.AdapterNew.ProductGridAdapter;
import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.application.Constant;
import com.bestdealfinance.bdfpartner.application.ToolbarHelper;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReferFragmentNew extends Fragment {

    public static final String TAG = "ReferFragmentNew";
    JSONObject jsonArrayProduct;
    private Toolbar toolbar;
    private RecyclerView productGrid;
    private ProductGridAdapter adapter;
    private RequestQueue queue;
    private boolean isNewLead = false;
    private String toolBarTitle;
    private DB snappyDB;

    public ReferFragmentNew() {
    }

    public static ReferFragmentNew newInstance(boolean isNewLead) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("isNewLead", isNewLead);
        ReferFragmentNew leadDetailFragment = new ReferFragmentNew();
        leadDetailFragment.setArguments(bundle);
        return leadDetailFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle mBundle = getArguments();
        this.isNewLead = getArguments().getBoolean("isNewLead");
    }

    @Override
    public void onResume() {
        super.onResume();

        if (jsonArrayProduct != null && jsonArrayProduct.length() == 0) {
            Toast.makeText(getActivity(), "Loading Data , Please Wait..", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_refer_fragment_new, container, false);

        queue = Volley.newRequestQueue(getActivity());
        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);

        if (isNewLead) {
            toolBarTitle = "Select Product";
            ToolbarHelper.initializeToolbar(getActivity(), toolbar, toolBarTitle, false, false, false);
        } else {
            toolBarTitle = "Products";
            ToolbarHelper.initializeToolbar(getActivity(), toolbar, toolBarTitle, false, true, true);
        }

        productGrid = (RecyclerView) rootView.findViewById(R.id.product_grid);
        adapter = new ProductGridAdapter(getActivity(), isNewLead);
        productGrid.setAdapter(adapter);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        productGrid.setLayoutManager(layoutManager);
        fetchProductDetails();
        return rootView;
    }

    public void fetchProductDetails() {
        try {
            snappyDB = DBFactory.open(getActivity());
            if (snappyDB.exists(Constant.DB_ALL_PRODUCTS_DETAILS_OBJECT)) {
                jsonArrayProduct = new JSONObject(snappyDB.get(Constant.DB_ALL_PRODUCTS_DETAILS_OBJECT));
                adapter.updatePayoutDetails(jsonArrayProduct);
            }
            snappyDB.close();
        } catch (SnappydbException | JSONException e) {
            e.printStackTrace();
        }
    }
}
