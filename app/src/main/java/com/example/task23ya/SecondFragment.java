package com.example.task23ya;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SecondFragment extends Fragment {

    private TextView tvTotalSum;
    private RecyclerView recyclerView;
    private ExpenseAdapter adapter;
    private List<Expense> expenseList;
    private DatabaseReference dbRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second, container, false);

        tvTotalSum = view.findViewById(R.id.tv_total_sum);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        expenseList = new ArrayList<>();
        adapter = new ExpenseAdapter(expenseList, context -> { });
        recyclerView.setAdapter(adapter);

        dbRef = FirebaseDatabase.getInstance().getReference(Constants.DB_EXPENSES_NODE);

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                expenseList.clear();
                double totalSum = 0;

                for (DataSnapshot data : snapshot.getChildren()) {
                    Expense exp = data.getValue(Expense.class);
                    if (exp != null) {
                        expenseList.add(exp);
                        totalSum += exp.getAmount();
                    }
                }

                Collections.sort(expenseList, (e1, e2) -> e2.getDate().compareTo(e1.getDate()));
                tvTotalSum.setText(String.format("סך הכל הוצאות: %.2f ₪", totalSum));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("FirebaseLog", "failed", error.toException());
            }
        });

        view.findViewById(R.id.btn_to_first).setOnClickListener(v ->
                getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, new FirstFragment()).commit());

        view.findViewById(R.id.btn_to_third).setOnClickListener(v ->
                getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, new ThirdFragment()).commit());

        view.findViewById(R.id.btn_to_fourth).setOnClickListener(v ->
                getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, new FourthFragment()).commit());

        return view;
    }
}