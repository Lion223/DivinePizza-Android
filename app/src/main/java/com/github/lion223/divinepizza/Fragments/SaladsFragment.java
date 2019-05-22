package com.github.lion223.divinepizza.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.lion223.divinepizza.Activities.PizzaProductActivity;
import com.github.lion223.divinepizza.Activities.SaladProductActivity;
import com.github.lion223.divinepizza.Adapters.SaladProductAdapter;
import com.github.lion223.divinepizza.CustomToast;
import com.github.lion223.divinepizza.Models.SaladProductModel;
import com.github.lion223.divinepizza.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;


public class SaladsFragment extends Fragment {

    private View view;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();;
    private CollectionReference saladsRef = db.collection("salads");
    private SaladProductAdapter adapter;
    private FirebaseUser currentUser;
    private CollectionReference currentCart;

    private CustomToast cToast;

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public SaladsFragment() {
        // Required empty public constructor
    }

    public static SaladsFragment newInstance(String param1, String param2) {
        SaladsFragment fragment = new SaladsFragment();
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
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_salads, container, false);
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
        Query query = saladsRef.orderBy("price", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<SaladProductModel> options = new FirestoreRecyclerOptions.Builder<SaladProductModel>()
                .setQuery(query, SaladProductModel.class)
                .build();

        adapter = new SaladProductAdapter(options, getActivity());
        RecyclerView recyclerView = view.findViewById(R.id.salad_recycler_view);
        if(recyclerView == null){
            return;
        }
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new SaladProductAdapter.OnItemClickListener() {
            @Override
            public void OnImageClick(DocumentSnapshot documentSnapshot, int position) {
                Intent intent = new Intent(getActivity(), SaladProductActivity.class);
                intent.putExtra("pid", documentSnapshot.getId());
                startActivity(intent);
            }

            @Override
            public void OnBuyBtnClick(DocumentSnapshot documentSnapshot, int quantity, String name, String price, String totalPrice) {
                final String id = documentSnapshot.getId();
                final HashMap<String, Object> product = new HashMap<>();
                product.put("name", name);
                product.put("price", price);
                product.put("quantity", String.valueOf(quantity));
                product.put("total_price", totalPrice);
                if (currentUser != null) {
                    db.collection("users").whereEqualTo("phone_number",
                            currentUser.getPhoneNumber()).get().addOnCompleteListener(getActivity(),
                            new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        QuerySnapshot qs = task.getResult();
                                        DocumentReference docRef = qs.getDocuments().get(0).getReference();
                                        currentCart = docRef.collection("CurrentCart");
                                        currentCart.document("item_" + id).set(product, SetOptions.merge());
                                    }
                                }
                            });
                }
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
