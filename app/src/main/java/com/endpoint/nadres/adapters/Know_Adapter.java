package com.endpoint.nadres.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.endpoint.nadres.R;
import com.endpoint.nadres.activities_fragments.knowledge_activity.KnowledegeActivity;
import com.endpoint.nadres.databinding.KnowledegeRowBinding;
import com.endpoint.nadres.databinding.LoadMoreBinding;
import com.endpoint.nadres.models.SingleArticleModel;

import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class Know_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int ITEM_DATA = 1;
    private final int LOAD = 2;
    private List<SingleArticleModel> singleArticleModelList;
    private Context context;
    private LayoutInflater inflater;
    private String lang;

    public Know_Adapter(List<SingleArticleModel> singleArticleModelList, Context context) {
        this.singleArticleModelList = singleArticleModelList;
        this.context = context;
        inflater = LayoutInflater.from(context);
        Paper.init(context);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == ITEM_DATA) {
            KnowledegeRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.knowledege_row, parent, false);
            return new EventHolder(binding);

        } else {
            LoadMoreBinding binding = DataBindingUtil.inflate(inflater, R.layout.load_more, parent, false);
            return new LoadHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SingleArticleModel singleArticle = singleArticleModelList.get(position);
        if (holder instanceof EventHolder) {
            EventHolder eventHolder = (EventHolder) holder;
            // eventHolder.binding.setLang(lang);
            eventHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (context instanceof KnowledegeActivity) {
                        KnowledegeActivity knowledegeActivity = (KnowledegeActivity) context;
                        knowledegeActivity.setItemData(singleArticleModelList.get(eventHolder.getLayoutPosition()));
                    }
                }
            });
            eventHolder.binding.setModel(singleArticle);


        } else {
            LoadHolder loadHolder = (LoadHolder) holder;
            loadHolder.binding.progBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return singleArticleModelList.size();
    }

    public class EventHolder extends RecyclerView.ViewHolder {
        public KnowledegeRowBinding binding;

        public EventHolder(@NonNull KnowledegeRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }

    public class LoadHolder extends RecyclerView.ViewHolder {
        private LoadMoreBinding binding;

        public LoadHolder(@NonNull LoadMoreBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        }

    }

    @Override
    public int getItemViewType(int position) {
        SingleArticleModel order_Model = singleArticleModelList.get(position);
        if (order_Model != null) {
            return ITEM_DATA;
        } else {
            return LOAD;
        }

    }


}
