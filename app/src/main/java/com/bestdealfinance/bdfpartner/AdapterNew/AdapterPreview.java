package com.bestdealfinance.bdfpartner.AdapterNew;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bestdealfinance.bdfpartner.R;
import com.bestdealfinance.bdfpartner.model.ModelPreview;

import java.util.List;

/**
 * Created by siba.prasad on 22-12-2016.
 */

public class AdapterPreview extends RecyclerView.Adapter<AdapterPreview.ViewHolderMain> {
    public static final int ROW_TYPE_TITLE = 1;
    public static final int ROW_TYPE_VALUE = 2;
    private static final String TAG = "AdapterPreview";
    private Context context;
    private List<ModelPreview> listPreview;
    private LayoutInflater layoutInflater;

    public AdapterPreview(Context context, List<ModelPreview> listPreview) {
        this.context = context;
        this.listPreview = listPreview;
        this.layoutInflater = LayoutInflater.from(context);
        Log.i(TAG, "AdapterPreview:  size " + listPreview.size());
    }

    @Override
    public ViewHolderMain onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewRow = layoutInflater.inflate(R.layout.list_item_preview, parent, false);
        ViewHolderMain viewHolderMain = new ViewHolderMain(viewRow);
        return viewHolderMain;
    }

    @Override
    public void onBindViewHolder(ViewHolderMain holder, int position) {
        ModelPreview modelPreview = listPreview.get(position);
        switch (modelPreview.rowType) {
            case ROW_TYPE_TITLE:
                holder.textViewItemPreviewFieldName.setTextSize(16);
                holder.textViewItemPreviewFieldName.setTypeface(Typeface.DEFAULT_BOLD);
                holder.textViewItemPreviewFieldValue.setVisibility(View.GONE);
                holder.textViewItemPreviewFieldName.setText(modelPreview.field_name);
//                holder.linearLayoutItemPreviewRoot.setPadding(0,10,0,0);
               /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    holder.linearLayoutItemPreviewRoot.setBackground(ContextCompat.getDrawable(context,R.drawable.boarder_top_side));
                else
                    holder.linearLayoutItemPreviewRoot.setBackgroundDrawable(ContextCompat.getDrawable(context,R.drawable.boarder_top_side));
*/                break;
            case ROW_TYPE_VALUE:
                holder.textViewItemPreviewFieldName.setTextSize(14);
                holder.textViewItemPreviewFieldName.setTypeface(Typeface.DEFAULT);
                holder.textViewItemPreviewFieldValue.setVisibility(View.VISIBLE);
                holder.textViewItemPreviewFieldName.setText(modelPreview.field_name);
                holder.textViewItemPreviewFieldValue.setText(modelPreview.field_value);
                break;

        }
    }

    @Override
    public int getItemCount() {
        return listPreview.size();
    }

    public class ViewHolderMain extends RecyclerView.ViewHolder {
        private AppCompatTextView textViewItemPreviewFieldName;
        private AppCompatTextView textViewItemPreviewFieldValue;
        private LinearLayout linearLayoutItemPreviewRoot;

        public ViewHolderMain(View itemView) {
            super(itemView);
            textViewItemPreviewFieldName = (AppCompatTextView) itemView.findViewById(R.id.textViewItemPreviewFieldName);
            textViewItemPreviewFieldValue = (AppCompatTextView) itemView.findViewById(R.id.textViewItemPreviewFieldValue);
            linearLayoutItemPreviewRoot = (LinearLayout) itemView.findViewById(R.id.linearLayoutItemPreviewRoot);
        }
    }
}
