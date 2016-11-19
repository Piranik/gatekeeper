package technology.mainthread.apps.gatekeeper.viewModel;

import android.databinding.BaseObservable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import javax.inject.Inject;

import technology.mainthread.apps.gatekeeper.R;
import technology.mainthread.apps.gatekeeper.model.firebase.EventLog;
import technology.mainthread.apps.gatekeeper.view.adapter.LogsAdapter;
import technology.mainthread.apps.gatekeeper.view.adapter.LogsViewHolder;

public class LogsFragmentViewModel extends BaseObservable {

    private final DatabaseReference events;

    @Inject
    LogsFragmentViewModel(FirebaseDatabase database) {
        this.events = database.getReference().child("events");
    }

    public void initialize(RecyclerView logsRecyclerView) {
        logsRecyclerView.setLayoutManager(new LinearLayoutManager(logsRecyclerView.getContext()));
        logsRecyclerView.setHasFixedSize(true);
        Query query = events.orderByChild("sort").limitToFirst(100);
        LogsAdapter logsAdapter = new LogsAdapter(EventLog.class, R.layout.item_log, LogsViewHolder.class, query);
        logsRecyclerView.setAdapter(logsAdapter);
    }

}
