package jj.zmaj.zmajparle;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import androidx.core.content.ContextCompat;

public class PhraseAdapter extends RecyclerView.Adapter<PhraseAdapter.PhraseViewHolder> {

    private List<Phrase> phraseList;
    private Context context;
    private OnFavoriteClickListener favoriteClickListener;

    public interface OnItemLongClickListener {
        void onItemLongClick(Phrase phrase);
    }
    private OnItemLongClickListener longClickListener;
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longClickListener = listener;
    }


    public interface OnFavoriteClickListener {
        void onFavoriteClick(Phrase phrase);
    }

    public PhraseAdapter(Context context, List<Phrase> phraseList, OnFavoriteClickListener favoriteClickListener) {
        this.context = context;
        this.phraseList = phraseList;
        this.favoriteClickListener = favoriteClickListener;
    }

    public static class PhraseViewHolder extends RecyclerView.ViewHolder {
        TextView phraseText, typeText;
        ImageView favoriteIcon;

        public PhraseViewHolder(View itemView) {
            super(itemView);
            phraseText = itemView.findViewById(R.id.phraseText);
            typeText = itemView.findViewById(R.id.typeText);
            favoriteIcon = itemView.findViewById(R.id.favoriteIcon);
        }
    }

    @Override
    public PhraseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_phrase, parent, false);
        return new PhraseViewHolder(v);
    }


    @Override
    public void onBindViewHolder(PhraseViewHolder holder, int position) {
        final Phrase phrase = phraseList.get(position);
        holder.phraseText.setText(phrase.getText());
        holder.favoriteIcon.setColorFilter(
                ContextCompat.getColor(context, phrase.isFavorite() ? R.color.zmaj_orange : R.color.zmaj_blue)
        );

        holder.typeText.setBackgroundResource(R.drawable.type_chip);
        holder.typeText.setTextColor(ContextCompat.getColor(context, R.color.white));

        holder.typeText.setText(phrase.getType());
        holder.favoriteIcon.setImageResource(
                phrase.isFavorite() ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off
        );

        holder.favoriteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                favoriteClickListener.onFavoriteClick(phrase);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (longClickListener != null) {
                    longClickListener.onItemLongClick(phrase);
                }
                return true;
            }
        });

    }


    @Override
    public int getItemCount() {
        return phraseList.size();
    }

    public void setPhraseList(List<Phrase> list) {
        this.phraseList = list;
        notifyDataSetChanged();
    }
}
