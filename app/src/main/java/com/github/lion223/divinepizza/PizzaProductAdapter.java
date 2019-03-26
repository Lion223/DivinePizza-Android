package com.github.lion223.divinepizza;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class PizzaProductAdapter extends FirestoreRecyclerAdapter<PizzaProduct, PizzaProductAdapter.PizzaProductHolder> {

    public PizzaProductAdapter(@NonNull FirestoreRecyclerOptions<PizzaProduct> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull PizzaProductHolder holder, int position, @NonNull PizzaProduct model) {
        holder.rating.setText(model.getRating());
        holder.name.setText(model.getName());

        holder.price.setText("₴" + model.getPrice());


        if(model.getTypes().get(0) == false){
            holder.meat_type.setVisibility(View.GONE);
        }
        else{
            holder.meat_type.setVisibility(View.VISIBLE);
        }
        if(model.getTypes().get(1) == false){
            holder.cheese_type.setVisibility(View.GONE);
        }
        else{
            holder.cheese_type.setVisibility(View.VISIBLE);
        }
        if(model.getTypes().get(2) == false){
            holder.vegetable_type.setVisibility(View.GONE);
        }
        else{
            holder.vegetable_type.setVisibility(View.VISIBLE);
        }
        if(model.getTypes().get(3) == false){
            holder.seafood_type.setVisibility(View.GONE);
        }
        else{
            holder.seafood_type.setVisibility(View.VISIBLE);
        }
    }

    @NonNull
    @Override
    public PizzaProductHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.product_item,
                viewGroup, false);

        return new PizzaProductHolder(v);
    }

    class PizzaProductHolder extends RecyclerView.ViewHolder{

        TextView name, rating, price;
        ImageView image;
        LinearLayout meat_type, cheese_type, vegetable_type, seafood_type;


        public PizzaProductHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.pizza_name);
            rating = itemView.findViewById(R.id.pizza_rating);
            price = itemView.findViewById(R.id.pizza_price);
            image = itemView.findViewById(R.id.pizza_image);
            meat_type = itemView.findViewById(R.id.meat_type);
            cheese_type = itemView.findViewById(R.id.cheese_type);
            vegetable_type = itemView.findViewById(R.id.vegetable_type);
            seafood_type = itemView.findViewById(R.id.seafood_type);
        }
    }
}
