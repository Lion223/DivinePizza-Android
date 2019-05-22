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
import com.github.lion223.divinepizza.Activities.DrinkProductActivity;
import com.github.lion223.divinepizza.Activities.SaladProductActivity;
import com.github.lion223.divinepizza.Adapters.DrinkProductAdapter;
import com.github.lion223.divinepizza.Adapters.SaladProductAdapter;
import com.github.lion223.divinepizza.CustomToast;
import com.github.lion223.divinepizza.Models.DrinkProductModel;
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


public class PickupFragment extends Fragment {

   private View view;

   private static final String ARG_PARAM1 = "param1";
   private static final String ARG_PARAM2 = "param2";

   private FirebaseFirestore db = FirebaseFirestore.getInstance();;
   private CollectionReference drinksRef = db.collection("drinks");
   private FirebaseUser currentUser;
   private CollectionReference currentCart;

   private CustomToast cToast;

   private String mParam1;
   private String mParam2;

   private OnFragmentInteractionListener mListener;

   public PickupFragment() {
      // Required empty public constructor
   }

   public static PickupFragment newInstance(String param1, String param2) {
      PickupFragment fragment = new PickupFragment();
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
      view = inflater.inflate(R.layout.fragment_pickup, container, false);
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


   @Override
   public void onStart() {
      super.onStart();
   }

   @Override
   public void onStop() {
      super.onStop();
   }
}
