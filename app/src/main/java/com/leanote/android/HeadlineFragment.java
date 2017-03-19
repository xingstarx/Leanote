package com.leanote.android;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.leanote.android.base.BaseFragment;
import com.leanote.android.database.AppDataBase;
import com.leanote.android.model.Account;
import com.leanote.android.model.Note;
import com.leanote.android.rxbus.RxBus;
import com.leanote.android.rxbus.SyncEvent;
import com.leanote.android.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.functions.Action1;

/**
 * Created by xiongxingxing on 17/1/10.
 */

public class HeadlineFragment extends BaseFragment {
    public static final String TAG = "HeadlineFragment";
    @BindView(R.id.recycler_view)
    XRecyclerView mRecyclerView;
    @BindView(android.R.id.empty)
    View mEmptyView;
    HeadLineAdapter mHeadLineAdapter;
    private Account mCurrentAccount;

    public static HeadlineFragment newInstance() {
        Bundle args = new Bundle();
        HeadlineFragment fragment = new HeadlineFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentAccount = AppDataBase.getAccountWithToken();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_head_line, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
        mRecyclerView.setArrowImageView(R.drawable.ic_font_downgrey);
        mRecyclerView.setEmptyView(mEmptyView);
        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                loadData(null);
            }

            @Override
            public void onLoadMore() {
            }
        });

        RxBus.getInstance().toObservable().subscribe(new Action1<Object>() {
            @Override
            public void call(final Object event) {
                if (event instanceof SyncEvent) {
                    mRecyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            loadData(event);
                        }
                    });
                }
            }
        });
        mHeadLineAdapter = new HeadLineAdapter(new ArrayList<Note>());
        mRecyclerView.setAdapter(mHeadLineAdapter);
    }

    private void loadData(final Object event) {
        if (event == null) {
            mRecyclerView.refreshComplete();
        }
        mHeadLineAdapter.setNotes(AppDataBase.getAllNotes(mCurrentAccount.getUserId()));
        mHeadLineAdapter.notifyDataSetChanged();
    }


    static class HeadLineAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        List<Note> notes;

        public HeadLineAdapter(List<Note> notes) {
            this.notes = notes;
        }

        public void setNotes(List<Note> notes) {
            this.notes = notes;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_head_line_list_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            final ViewHolder viewHolder = (ViewHolder) holder;
            final Note note = notes.get(position);
            viewHolder.title.setText(note.title);
            if (note.isMarkDown) {
                viewHolder.content.setText(note.noteAbstract);
            } else {
                viewHolder.content.setText(Html.fromHtml(note.noteAbstract).toString());
            }
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NoteActivity.showPreNote(viewHolder.itemView.getContext(), note);
                }
            });
            viewHolder.updateTime.setText(TimeUtils.toYearFormat(TimeUtils.toTimestamp(note.updatedTime)));
        }

        @Override
        public int getItemCount() {
            return notes.size();
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.content)
        TextView content;
        @BindView(R.id.update_time)
        TextView updateTime;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
