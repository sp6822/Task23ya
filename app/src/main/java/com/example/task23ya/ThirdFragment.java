package com.example.task23ya;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.List;

public class ThirdFragment extends Fragment {

    private EditText etSearchDesc, etMinPrice, etMaxPrice;
    private Button btnSearchFilter;
    private RecyclerView rvResults;

    private List<Expense> allExpenses = new ArrayList<>();
    private List<Expense> filteredList = new ArrayList<>();
    private ExpenseAdapter adapter;
    private DatabaseReference dbRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_third, container, false);

        etSearchDesc = view.findViewById(R.id.et_search_desc);
        etMinPrice = view.findViewById(R.id.et_min_price);
        etMaxPrice = view.findViewById(R.id.et_max_price);
        btnSearchFilter = view.findViewById(R.id.btn_filter);
        rvResults = view.findViewById(R.id.rv_search_results);

        rvResults.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ExpenseAdapter(filteredList, context -> {});
        rvResults.setAdapter(adapter);

        dbRef = FirebaseDatabase.getInstance().getReference(Constants.DB_EXPENSES_NODE);

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allExpenses.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Expense e = ds.getValue(Expense.class);
                    if (e != null) allExpenses.add(e);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        btnSearchFilter.setOnClickListener(v -> performFiltering());

        view.findViewById(R.id.btn_to_first).setOnClickListener(v ->
                getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, new FirstFragment()).commit());

        view.findViewById(R.id.btn_to_second).setOnClickListener(v ->
                getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, new SecondFragment()).commit());

        view.findViewById(R.id.btn_to_fourth).setOnClickListener(v ->
                getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, new FourthFragment()).commit());

        return view;
    }

    private void performFiltering() {
        String searchDesc = etSearchDesc.getText().toString().trim().toLowerCase();
        String minStr = etMinPrice.getText().toString().trim();
        String maxStr = etMaxPrice.getText().toString().trim();

        double minPrice = minStr.isEmpty() ? 0 : Double.parseDouble(minStr);
        double maxPrice = maxStr.isEmpty() ? Double.MAX_VALUE : Double.parseDouble(maxStr);

        filteredList.clear();

        for (Expense exp : allExpenses) {
            boolean matchesDesc = exp.getDescription().toLowerCase().contains(searchDesc);
            boolean matchesPrice = exp.getAmount() >= minPrice && exp.getAmount() <= maxPrice;

            if (matchesDesc && matchesPrice) {
                filteredList.add(exp);
            }
        }
        adapter.notifyDataSetChanged();
    }
}