package com.github.lion223.divinepizza.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.lion223.divinepizza.App;
import com.github.lion223.divinepizza.Models.CartProductModel;
import com.github.lion223.divinepizza.R;
import com.google.firebase.firestore.DocumentSnapshot;

public class CartProductAdapter extends FirestoreRecyclerAdapter<CartProductModel, CartProductAdapter.CartProductHolder> {

    private CartProductAdapter.OnItemClickListener listener;

    public CartProductAdapter(@NonNull FirestoreRecyclerOptions<CartProductModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CartProductAdapter.CartProductHolder holder, int position, @NonNull CartProductModel model) {
        holder.name.setText(model.getName());
        holder.qty.setText(model.getQuantity());
        holder.price.setText(App.getContext().getResources().getString(R.string.price,
                model.getTotal_price()));
    }

    @NonNull
    @Override
    public CartProductAdapter.CartProductHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cart_product_item,
                viewGroup, false);

        return new CartProductAdapter.CartProductHolder(v);
    }

    public void deleteItem(int position){
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    class CartProductHolder extends RecyclerView.ViewHolder {

        TextView name, price, qty;
        Button add_btn, minus_btn;

        public CartProductHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.cart_item_name);
            price = itemView.findViewById(R.id.cart_item_price);
            add_btn = itemView.findViewById(R.id.cart_item_add_button);
            minus_btn = itemView.findViewById(R.id.cart_item_minus_button);
            qty = itemView.findViewById(R.id.cart_item_quantity);

            add_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    int quantity = Integer.parseInt(qty.getText().toString());
                    int prev_price = Integer.parseInt(price.getText().toString().substring(1));
                    if (quantity < 99) {
                        quantity++;
                        listener.OnBtnClick(getSnapshots().getSnapshot(position),
                                Integer.toString(quantity),
                                Integer.toString(prev_price + prev_price / (quantity - 1)));
                    }
                }
            });

            minus_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    int quantity = Integer.parseInt(qty.getText().toString());
                    int prev_price = Integer.parseInt(price.getText().toString().substring(1));
                    if (quantity > 1) {
                        quantity--;
                        listener.OnBtnClick(getSnapshots().getSnapshot(position),
                                Integer.toString(quantity),
                                Integer.toString(prev_price - prev_price / (quantity + 1)));
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void OnImageClick(DocumentSnapshot documentSnapshot, int position);
        void OnBtnClick(DocumentSnapshot documentSnapshot, String quantity, String totalPrice);
    }

    public void setOnItemClickListener(CartProductAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }
}
