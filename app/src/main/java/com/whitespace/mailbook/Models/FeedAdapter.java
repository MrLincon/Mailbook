package com.whitespace.mailbook.Models;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.whitespace.mailbook.R;

public class FeedAdapter extends FirestorePagingAdapter<Feed, FeedAdapter.FeedHolder> {

    private OnItemClickListener listener;
    private Context mContext;

    public FeedAdapter(@NonNull FirestorePagingOptions<Feed> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final FeedHolder holder, int position, @NonNull Feed model) {
        holder.Name.setText(model.getName());
        holder.Email.setText(model.getEmail());
        holder.Copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(mContext.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Email", model.getEmail());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(mContext, "Copied", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @NonNull
    @Override
    public FeedHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_layout,
                parent, false);

        return new FeedHolder(view);
    }

    class FeedHolder extends RecyclerView.ViewHolder {
        TextView Name, Email;
        ImageView Copy;

        public FeedHolder(View itemView) {
            super(itemView);
            Name = itemView.findViewById(R.id.name);
            Email = itemView.findViewById(R.id.email);
            Copy = itemView.findViewById(R.id.copy);

            mContext = itemView.getContext();

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getItem(position));
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
