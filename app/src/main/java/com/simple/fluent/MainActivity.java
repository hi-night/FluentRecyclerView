package com.simple.fluent;

import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.simple.fluentre.FluentBaseAdapter;
import com.simple.fluentre.FluentRecyclerView;
import com.simple.fluentre.FluentRefreshListenerImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private FluentRecyclerView recyclerView;
    private Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recycler_view);

        initRecyclerView();
    }

    private void initRecyclerView() {
        adapter = new Adapter(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setOnRefreshListener(new FluentRefreshListenerImpl(){
            @Override
            public void onLoadMoreRefresh(int page) {
                setData(page);
            }
        });


        recyclerView.setAdapter(adapter);
        recyclerView.setRefreshing();
    }

    private void setData(final int page) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                List<String> randomSublist = getRandomSublist(Cheeses.sCheeseStrings, 20);
                recyclerView.setData(randomSublist, page);
            }
        }, 1000);
    }

    private List<String> getRandomSublist(String[] array, int amount) {
        ArrayList<String> list = new ArrayList<>(amount);
        Random random = new Random();
        while (list.size() < amount) {
            list.add(array[random.nextInt(array.length)]);
        }
        return list;
    }

    private class Adapter extends FluentBaseAdapter<String> {

        public Adapter(Context context) {
            super(context, null);
        }

        @Override
        protected void onAdapterBindViewHolder(RecyclerView.ViewHolder holder, String item) {
            ViewHolder holder1 = (ViewHolder) holder;
            holder1.mTextView.setText(item);
        }

        @Override
        protected RecyclerView.ViewHolder onCreateNormalViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
            return new ViewHolder(view);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {


            public final View mView;
            public final ImageView mImageView;
            public final TextView mTextView;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = (ImageView) view.findViewById(R.id.avatar);
                mTextView = (TextView) view.findViewById(android.R.id.text1);
            }
        }
    }

}
