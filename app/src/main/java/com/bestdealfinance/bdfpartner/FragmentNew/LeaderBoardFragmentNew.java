package com.bestdealfinance.bdfpartner.FragmentNew;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.AdapterNew.LeaderBoardAdapterNew;
import com.bestdealfinance.bdfpartner.application.Constant;
import com.bestdealfinance.bdfpartner.application.Helper;
import com.bestdealfinance.bdfpartner.application.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LeaderBoardFragmentNew extends Fragment {

    private RecyclerView recyclerView;
    private LeaderBoardAdapterNew adapter;
    private RequestQueue queue;

    private JSONArray leaderBoardJsonArray;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_leader_board, container, false);


        queue = Volley.newRequestQueue(getActivity());

        recyclerView = (RecyclerView) rootView.findViewById(R.id.leader_board_recycler_view_top_associates);
        adapter = new LeaderBoardAdapterNew(getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        String leaderBoard = getArguments().getString("leaderBoardJsonArray");

        if (leaderBoard == null || leaderBoard.isEmpty()) {
            getLeaderBoardFromServer();
        } else {
            try {
                leaderBoardJsonArray = new JSONArray(leaderBoard);
                adapter.updateData(leaderBoardJsonArray, -1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }




        return rootView;

    }


    private void getLeaderBoardFromServer() {

        try {

            JSONObject reqObject = new JSONObject();
            reqObject.put("utoken", Helper.getStringSharedPreference(Constant.UTOKEN, getActivity()));
            Helper.showLog(URL.LEADER_BOARD,reqObject.toString());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL.LEADER_BOARD, reqObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response.opt(Constant.STATUS_CODE) != null && response.opt(Constant.MSG) != null) {
                        try {
                            String status, msg;
                            status = response.getString(Constant.STATUS_CODE);
                            msg = response.getString(Constant.MSG);
                            if (status.equals(Constant.STATUS_2000) && msg.equals(Constant.SUCCESS)) {

                                JSONObject body = response.optJSONObject(Constant.BODY);
                                body = body.optJSONObject("detail");
                                leaderBoardJsonArray = body.optJSONArray("data");

                                adapter.updateData(leaderBoardJsonArray, -1);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            queue.add(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
