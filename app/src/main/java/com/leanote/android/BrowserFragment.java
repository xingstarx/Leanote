package com.leanote.android;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.leanote.android.api.ApiProvider;
import com.leanote.android.api.NoteApi;
import com.leanote.android.base.BaseFragment;
import com.leanote.android.database.AppDataBase;
import com.leanote.android.model.Account;
import com.leanote.android.model.FullTree;
import com.leanote.android.model.Note;
import com.leanote.android.model.Notebook;
import com.leanote.android.model.TreeEntry;
import com.leanote.android.rxbus.RxBus;
import com.leanote.android.rxbus.SyncEvent;
import com.leanote.android.utils.TimeUtils;

import java.util.List;
import java.util.Stack;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by xiongxingxing on 17/1/10.
 */

public class BrowserFragment extends BaseFragment {

    private static final String TAG = "BrowserFragment";
    @BindView(R.id.recycler_view)
    XRecyclerView mRecyclerView;
    @BindView(android.R.id.empty)
    View mEmptyView;
    private List<Notebook> mNotebooks;
    private Account mCurrentAccount;
    private NoteApi mNoteApi;
    private FullTree.Folder mRootFolder = new FullTree.Folder();
    private EntryAdapter mEntryAdapter;


    public static BrowserFragment newInstance() {
        Bundle args = new Bundle();
        BrowserFragment fragment = new BrowserFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentAccount = AppDataBase.getAccountWithToken();
        mNotebooks = AppDataBase.getRootNotebooks(mCurrentAccount.userId);
        mNoteApi = ApiProvider.getInstance().getNoteApi();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_browser, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
        mRecyclerView.setArrowImageView(R.drawable.ic_font_downgrey);
        mRecyclerView.setEmptyView(mEmptyView);
        mRecyclerView.setLoadingMoreEnabled(false);
        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                loadEntryData(null);
            }

            @Override
            public void onLoadMore() {
            }
        });
        RxBus.getInstance().toObservable().subscribe(new Action1<Object>() {
            @Override
            public void call(Object event) {
                if (event instanceof SyncEvent) {
                    loadEntryData(event);
                }
            }
        });
        mEntryAdapter = new EntryAdapter();
        mRecyclerView.setAdapter(mEntryAdapter);
    }

    private void loadEntryData(final Object event) {
        Observable.create(new Observable.OnSubscribe<FullTree.Folder>() {
            @Override
            public void call(Subscriber<? super FullTree.Folder> subscriber) {
                mRootFolder = mRootFolder.initFullTree(mNotebooks, mCurrentAccount.userId);
                subscriber.onNext(mRootFolder);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<FullTree.Folder>() {
                    @Override
                    public void call(FullTree.Folder folder) {
                        if (event == null) {
                            mRecyclerView.refreshComplete();
                        }
                        mEntryAdapter.setFolder(folder);
                        mEntryAdapter.notifyDataSetChanged();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
    }

    @Override
    public boolean onBackPressed() {
        if (!mEntryAdapter.isRootFolder()) {
            mEntryAdapter.onBackPressed();
            return true;
        }
        return super.onBackPressed();
    }

    static class EntryAdapter extends RecyclerView.Adapter {
        FullTree.Folder folder = new FullTree.Folder();
        Stack<FullTree.Folder> stack = new Stack<>();
        public static final int VIEW_TYPE_FOLDER = 0;
        public static final int VIEW_TYPE_ENTRY = 1;

        public void setFolder(FullTree.Folder folder) {
            this.folder = folder;
        }

        public boolean isRootFolder() {
            return stack.size() == 0;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == VIEW_TYPE_FOLDER) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_note_list_item, parent, false);
                return new FolderViewHolder(view);
            } else {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_browser_entry_list_item, parent, false);
                return new EntryViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
            if (getItemViewType(position) == VIEW_TYPE_FOLDER) {
                FolderViewHolder folderViewHolder = (FolderViewHolder) holder;
                final TreeEntry entry = folder.folders.get(position).entry;
                folderViewHolder.titleView.setText(entry.title);
                folderViewHolder.updateTimeView.setText(TimeUtils.toYearFormat(TimeUtils.toTimestamp(entry.updateTime)));
                folderViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        stack.push(folder);
                        setFolder(folder.folders.get(position));
                        notifyDataSetChanged();
                    }
                });
            } else {
                final EntryViewHolder entryViewHolder = (EntryViewHolder) holder;
                final TreeEntry entry = folder.files.get(position - folder.folders.size()).entry;
                entryViewHolder.title.setText(entry.title);
                Drawable drawable;
                if (entry.isMarkDown) {
                    entryViewHolder.content.setText(entry.noteAbstract);
                    drawable = ContextCompat.getDrawable(entryViewHolder.title.getContext(), R.drawable.ic_doc_markdown);
                } else {
                    entryViewHolder.content.setText(Html.fromHtml(entry.noteAbstract).toString());
                    drawable = ContextCompat.getDrawable(entryViewHolder.title.getContext(), R.drawable.ic_doc_note);
                }
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                TextViewCompat.setCompoundDrawablesRelative(entryViewHolder.title, drawable, null, null, null);
                entryViewHolder.updateTimeView.setText(TimeUtils.toYearFormat(TimeUtils.toTimestamp(entry.updateTime)));
                entryViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Note note = AppDataBase.getNoteById(entry.noteId);
                        NoteActivity.showPreNote(entryViewHolder.itemView.getContext(), note);
                    }
                });
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position < folder.folders.size()) {
                return VIEW_TYPE_FOLDER;
            }
            return VIEW_TYPE_ENTRY;
        }

        @Override
        public int getItemCount() {
            return folder.folders.size() + folder.files.size();
        }

        public void onBackPressed() {
            FullTree.Folder parentFolder = stack.pop();
            setFolder(parentFolder);
            notifyDataSetChanged();
        }

        static class FolderViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.title)
            TextView titleView;
            @BindView(R.id.update_time)
            TextView updateTimeView;

            public FolderViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }

        static class EntryViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.title)
            TextView title;
            @BindView(R.id.content)
            TextView content;
            @BindView(R.id.update_time)
            TextView updateTimeView;

            public EntryViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }

}
