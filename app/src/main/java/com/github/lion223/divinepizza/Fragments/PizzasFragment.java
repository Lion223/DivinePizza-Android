package com.github.lion223.divinepizza.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.lion223.divinepizza.Activities.PizzaProductActivity;
import com.github.lion223.divinepizza.CustomToast;
import com.github.lion223.divinepizza.Models.PizzaProductModel;
import com.github.lion223.divinepizza.Adapters.PizzaProductAdapter;
import com.github.lion223.divinepizza.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;



public class PizzasFragment extends Fragment {

    private View view;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();;
    private CollectionReference pizzasRef = db.collection("pizzas");
    private PizzaProductAdapter adapter;

    private CustomToast cToast;

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public PizzasFragment() {
        // Required empty public constructor
    }

    public static PizzasFragment newInstance(String param1, String param2) {
        PizzasFragment fragment = new PizzasFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_pizzas, container, false);
        setUpRecyclerView();
        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void setUpRecyclerView(){
        Query query = pizzasRef.orderBy("price", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<PizzaProductModel> options = new FirestoreRecyclerOptions.Builder<PizzaProductModel>()
                .setQuery(query, PizzaProductModel.class)
                .build();

        adapter = new PizzaProductAdapter(options, getActivity());
        RecyclerView recyclerView = view.findViewById(R.id.pizza_recycler_view);
        if(recyclerView == null){
            return;
        }
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new PizzaProductAdapter.OnItemClickListener() {
            @Override
            public void OnImageClick(DocumentSnapshot documentSnapshot, int position) {
                Intent intent = new Intent(getActivity(), PizzaProductActivity.class);
                intent.putExtra("pid", documentSnapshot.getId());
                startActivity(intent);
            }

            @Override
            public void OnBuyBtnClick(DocumentSnapshot documentSnapshot, int quantity) {
                String id = documentSnapshot.getId();
                Toast.makeText(getContext(), id + " , q: " + quantity, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}