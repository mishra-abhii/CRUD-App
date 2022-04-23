package com.abhishek.crud_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    EditText etItemTitle;
    EditText etItemDescription;
    TextView btnAddItem;
    ListView listViewItems;
    List<Items> itemsList;

    private FirebaseAuth auth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("CRUD App");

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance()
                .getReference().child("Users").child(Objects.requireNonNull(auth.getUid())).child("Items"); // path is used to create node named Items

        etItemTitle = findViewById(R.id.etTitle);
        etItemDescription = findViewById(R.id.etDescription);
        btnAddItem = findViewById(R.id.btnAddItem);
        listViewItems = findViewById(R.id.listViewItems);
        itemsList = new ArrayList<>();

        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddItem();
            }
        });

        listViewItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Items item = itemsList.get(i);
                showUpdateDialog(item.getItemId() , item.getTitle());
            }
        });
    }

    // code to fetch data from firebase
    @Override
    protected void onStart() {
        super.onStart();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                This method is used to read the value from database as it will fetch all the values inside
//                the specified reference which is Items and it will contain all the data inside this DataSnapshot object

                itemsList.clear(); // this will prevent this method to again and again fetch all the data stored in the database and only new
                // data stored will be fetched every time excluding the ones which are already fetched
                for(DataSnapshot artistSnapshot : snapshot.getChildren()){
                    Items item = artistSnapshot.getValue(Items.class);
                    itemsList.add(item);
                }

                ItemsList_Adapter adapter = new ItemsList_Adapter(MainActivity.this , itemsList);
                listViewItems.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    // code for Update Dialog Box and updating value
    private void showUpdateDialog(String itemId , String title){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.update_dialogbox , null);

        dialogBuilder.setView(dialogView);

        final EditText etNewTitle = dialogView.findViewById(R.id.etNewTitle);
        final EditText etNewDescription = dialogView.findViewById(R.id.etNewDescription);
        final TextView btnUpdate = dialogView.findViewById(R.id.btnUpdate);
        final TextView btnDelete = dialogView.findViewById(R.id.btnDelete);

        dialogBuilder.setTitle("Update Item");
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        // code to update the data stored in firebase database
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String title = etNewTitle.getText().toString().trim();
                String description = etNewDescription.getText().toString().trim();

                if(TextUtils.isEmpty(title)){
                    Toast.makeText(MainActivity.this, "Please fill the boxes", Toast.LENGTH_SHORT).show();
                }
                else{
                    Items item = new Items(itemId, title , description);
                    databaseReference.child(itemId).setValue(item);
                    Toast.makeText(MainActivity.this, "Updated successfully..", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                }

            }
        });

        // code to delete data from firebase database
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                databaseReference.child(itemId).removeValue();
                alertDialog.dismiss();

                Toast.makeText(MainActivity.this, "Deleted successfully..", Toast.LENGTH_SHORT).show();
            }
        });

    }

    // code for adding items
    private void AddItem(){
        String title = etItemTitle.getText().toString().trim();
        String description = etItemDescription.getText().toString().trim();

        if(TextUtils.isEmpty(title) && TextUtils.isEmpty(description)){
            Toast.makeText(this, "Please enter the details", Toast.LENGTH_SHORT).show();
        }
        else{
            // push method creates a unique key inside the node Items
            String id = databaseReference.push().getKey();
            Items item = new Items(id, title , description);
            databaseReference.child(id).setValue(item);  // child(id) is used so that every item created will be assigned a unique key stored in id variable

            etItemTitle.getText().clear();
            etItemDescription.getText().clear();
            Toast.makeText(this, "Items added..", Toast.LENGTH_SHORT).show();

        }

    }

    // code for Logout option
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu , menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            auth.signOut();
            Intent intent = new Intent(MainActivity.this, SignUp_Activity.class);
            finish();
        }
        return true;
    }

}