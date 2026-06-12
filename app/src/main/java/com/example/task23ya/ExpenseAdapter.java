package com.example.task23ya;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    private List<Expense> expenseList;
    private OnItemLongClickListener longClickListener;

    // ממשק אינטרפייס לטיפול בלחיצות ארוכות מחוץ לאדפטר אם נרצה
    public interface OnItemLongClickListener {
        void onItemLongClick(Expense expense);
    }

    public ExpenseAdapter(List<Expense> expenseList, OnItemLongClickListener longClickListener) {
        this.expenseList = expenseList;
        this.longClickListener = longClickListener;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_item, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense expense = expenseList.get(position);

        holder.tvDescription.setText(expense.getDescription());
        holder.tvCategory.setText(expense.getCategory());
        holder.tvDate.setText(expense.getDate());
        // הצגת סימן השקל בצורה נכונה מימין למספר
        holder.tvAmount.setText(String.format("%.2f ₪", expense.getAmount()));

        // הגדרת לחיצה ארוכה למחיקה מתוך Firebase
        holder.itemView.setOnLongClickListener(v -> {
            showDeleteDialog(holder.itemView.getContext(), expense);
            if (longClickListener != null) {
                longClickListener.onItemLongClick(expense);
            }
            return true; // מחזיר true כדי לסמן שהאירוע טופל במלואו ולא יפעיל קליק רגיל
        });
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    // פונקציה שמציגה דיאלוג אישור מחיקה מודרני
    private void showDeleteDialog(Context context, Expense expense) {
        new AlertDialog.Builder(context)
                .setTitle("מחיקת הוצאה")
                .setMessage("האם אתה בטוח שברצונך למחוק את '" + expense.getDescription() + "'?")
                .setPositiveButton("מחק", (dialog, which) -> {
                    // התחברות ל-Firebase ומחיקת הפריט לפי ה-Key הייחודי שלו
                    DatabaseReference dbRef = FirebaseDatabase.getInstance()
                            .getReference(Constants.DB_EXPENSES_NODE)
                            .child(expense.getKeyID());

                    dbRef.removeValue().addOnSuccessListener(aVoid ->
                            Toast.makeText(context, "ההוצאה נמחקה בהצלחה", Toast.LENGTH_SHORT).show()
                    ).addOnFailureListener(e ->
                            Toast.makeText(context, "המחיקה נכשלה", Toast.LENGTH_SHORT).show()
                    );
                })
                .setNegativeButton("ביטול", (dialog, which) -> dialog.dismiss())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView tvDescription, tvAmount, tvCategory, tvDate;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDescription = itemView.findViewById(R.id.tv_item_description);
            tvAmount = itemView.findViewById(R.id.tv_item_amount);
            tvCategory = itemView.findViewById(R.id.tv_item_category);
            tvDate = itemView.findViewById(R.id.tv_item_date);
        }
    }
}