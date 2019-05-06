package com.github.lion223.divinepizza.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.lion223.divinepizza.App;
import com.github.lion223.divinepizza.Models.PizzaProductModel;
import com.github.lion223.divinepizza.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

public class PizzaProductAdapter extends FirestoreRecyclerAdapter<PizzaProductModel, PizzaProductAdapter.PizzaProductHolder> {

    private Context context;
    private OnItemClickListener listener;

    public PizzaProductAdapter(@NonNull FirestoreRecyclerOptions<PizzaProductModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull PizzaProductHolder holder, int position, @NonNull PizzaProductModel model) {
        holder.rating.setText(model.getRating());
        holder.name.setText(model.getName());
        holder.diameter.setText(model.getDiameter() + " см");
        Picasso.get().load(model.getImg_thumb_url()).into(holder.image);

        /*
        List<String> diameters = new ArrayList<>();

        for(String i : model.getDiameter()){
            diameters.add(i + "см");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, diameters){
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);

                Typeface externalFont= ResourcesCompat.getFont(context.getApplicationContext(), R.font.montserrat_regular2);
                ((TextView) v).setTypeface(externalFont);
                ((TextView) v).setTextSize(16f);

                return v;
            }


            public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
                View v =super.getDropDownView(position, convertView, parent);

                Typeface externalFont= ResourcesCompat.getFont(context.getApplicationContext(), R.font.montserrat_regular);
                ((TextView) v).setTypeface(externalFont);

                return v;
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.spinner.setAdapter(adapter);
        */

        holder.price.setText(App.getContext().getResources().getString(R.string.price, model.getPrice()));

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
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.pizza_product_item,
                viewGroup, false);

        return new PizzaProductHolder(v);
    }

    class PizzaProductHolder extends RecyclerView.ViewHolder{

        TextView name, diameter, rating, price;
        ImageView image;
        LinearLayout meat_type, cheese_type, vegetable_type, seafood_type;
        Button add_btn, minus_btn, buy_btn;
        //Spinner spinner;



        public PizzaProductHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.cart_item_name);
            rating = itemView.findViewById(R.id.pizza_item_rating);
            price = itemView.findViewById(R.id.cart_product_price);
            image = itemView.findViewById(R.id.cart_item_image);
            diameter = itemView.findViewById(R.id.pizza_item_diameter);
            //spinner = itemView.findViewById(R.id.item_spinner);
            meat_type = itemView.findViewById(R.id.pizza_product_meat_type);
            cheese_type = itemView.findViewById(R.id.pizza_product_cheese_type);
            vegetable_type = itemView.findViewById(R.id.pizza_product_vegetable_type);
            seafood_type = itemView.findViewById(R.id.pizza_product_seafood_type);
            add_btn = itemView.findViewById(R.id.cart_product_add_button);
            minus_btn = itemView.findViewById(R.id.cart_product_minus_button);
            buy_btn = itemView.findViewById(R.id.cart_product_quantity_button);


            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION && listener != null){
                        listener.OnImageClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });

            add_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int quantity = Integer.parseInt(buy_btn.getText().toString());
                    if(quantity < 99){
                        quantity++;
                        int prev_price = Integer.parseInt(price.getText().toString().substring(1));
                        buy_btn.setText(Integer.toString(quantity));
                        price.setText(App.getContext().getResources().getString(R.string.price,
                                Integer.toString(prev_price + prev_price / (quantity - 1))));
                    }
                }
            });

            minus_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int quantity = Integer.parseInt(buy_btn.getText().toString());
                    if(quantity > 1){
                        quantity--;
                        int prev_price = Integer.parseInt(price.getText().toString().substring(1));
                        price.setText(App.getContext().getResources().getString(R.string.price,
                                Integer.toString(prev_price - prev_price / (quantity + 1))));
                        buy_btn.setText(Integer.toString(quantity));
                    }
                }
            });

            buy_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    int quantity = Integer.parseInt(buy_btn.getText().toString());
                    listener.OnBuyBtnClick(getSnapshots().getSnapshot(position), quantity);

                }
            });
        }
    }

    public interface OnItemClickListener {
        void OnImageClick(DocumentSnapshot documentSnapshot, int position);
        void OnBuyBtnClick(DocumentSnapshot documentSnapshot, int quantity);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
