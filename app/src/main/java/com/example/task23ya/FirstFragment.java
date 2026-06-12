package com.example.task23ya;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class FirstFragment extends Fragment {

    private EditText etDescription, etAmount;
    private Spinner spinnerCategory;
    private Button btnDatePicker, btnSaveExpense;
    private TextView tvSelectedDate;

    private DatabaseReference dbRef;
    private Calendar calendar;
    private String formattedDate = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first, container, false);

        dbRef = FirebaseDatabase.getInstance().getReference(Constants.DB_EXPENSES_NODE);

        // קישור רכיבי הממשק
        etDescription = view.findViewById(R.id.et_description);
        etAmount = view.findViewById(R.id.et_amount);
        spinnerCategory = view.findViewById(R.id.spinner_category);
        btnDatePicker = view.findViewById(R.id.btn_date_picker);
        btnSaveExpense = view.findViewById(R.id.btn_save_expense);
        tvSelectedDate = view.findViewById(R.id.tv_selected_date);

        calendar = Calendar.getInstance();

        // הגדרת ספינר הקטגוריות
        String[] categories = {"אוכל", "בילוי", "תחבורה", "אחר"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, categories);
        spinnerCategory.setAdapter(adapter);

        // בחירת תאריך
        btnDatePicker.setOnClickListener(v -> {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view1, selectedYear, selectedMonth, selectedDay) -> {
                calendar.set(Calendar.YEAR, selectedYear);
                calendar.set(Calendar.MONTH, selectedMonth);
                calendar.set(Calendar.DAY_OF_MONTH, selectedDay);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                formattedDate = sdf.format(calendar.getTime());
                tvSelectedDate.setText("תאריך נבחר: " + formattedDate);
            }, year, month, day);
            datePickerDialog.show();
        });

        // לחצני הניווט למסכים האחרים
        view.findViewById(R.id.btn_to_second).setOnClickListener(v ->
                getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, new SecondFragment()).commit());

        view.findViewById(R.id.btn_to_third).setOnClickListener(v ->
                getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, new ThirdFragment()).commit());

        view.findViewById(R.id.btn_to_fourth).setOnClickListener(v ->
                getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, new FourthFragment()).commit());

        // לחצן שמירת ההוצאה
        btnSaveExpense.setOnClickListener(v -> saveExpenseToFirebase());

        return view;
    }

    private void saveExpenseToFirebase() {
        String desc = etDescription.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();

        if (desc.isEmpty() || amountStr.isEmpty() || formattedDate.isEmpty()) {
            Toast.makeText(getContext(), "נא למלא את כל השדות", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);
        String keyID = dbRef.push().getKey();

        if (keyID != null) {
            Expense expense = new Expense(keyID, desc, amount, category, formattedDate);
            dbRef.child(keyID).setValue(expense)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "ההוצאה נשמרה!", Toast.LENGTH_SHORT).show();
                        etDescription.setText("");
                        etAmount.setText("");
                        tvSelectedDate.setText("לא נבחר תאריך");
                        formattedDate = "";
                    })
                    .addOnFailureListener(e -> Log.e("FirebaseError", "שגיאה בשמירה", e));
        }
    }
}