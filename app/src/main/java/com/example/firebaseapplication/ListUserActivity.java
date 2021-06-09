package com.example.firebaseapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.firebaseapplication.model.UserResponse;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class ListUserActivity extends AppCompatActivity {

    private ArrayList<UserResponse> listData = new ArrayList<>();
    private RecyclerView rv;
    private UserAdapter adapter;
    private LoadingDialog loadingDialog;
    private FloatingActionButton floatingBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_user);
        loadingDialog = new LoadingDialog(this);
        rv = findViewById(R.id.rv_list_user);
        floatingBtn = findViewById(R.id.floatingActionButton);
        adapter = new UserAdapter(ListUserActivity.this, listData);
        adapter.event = new UserAdapter.onItemClick() {
            @Override
            public void onClick(UserResponse data, int position) {
                //edit data
                showDialogEdit(data, position);
            }

            @Override
            public void onDelete(UserResponse data, int position) {
                //delete data
                deleteApi(Constant.BASE_URL + "user", data, position);
            }
        };
        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rv.setAdapter(adapter);
        floatingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogInsert();
            }
        });
        getArrayJson(Constant.BASE_URL + "user");
    }

    private void getArrayJson(String url) {
        loadingDialog.startLoading();
        JsonArrayRequest jsonArrayRequest =
                new JsonArrayRequest(url,
                        response -> {
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject object = (JSONObject) response.get(i);
                                    UserResponse data = UserResponse.getResponseFromObject(object);
                                    listData.add(data);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            adapter.setListData(listData);
                            loadingDialog.hideLoading();
                        }, error -> Toast.makeText(ListUserActivity.this, "Error by get Json Array!", Toast.LENGTH_SHORT).show());
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    private void putApi(String url, UserResponse user, int position) {
        loadingDialog.startLoading();
        StringRequest stringRequest = new StringRequest(
                Request.Method.PUT, url + '/' + user.getId(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loadingDialog.hideLoading();
                listData.get(position).setEmail(user.getEmail());
                listData.get(position).setName(user.getName());
                adapter.notifyItemChanged(position);
                Toast.makeText(ListUserActivity.this, "Successfully", Toast.LENGTH_SHORT).show();
            }
        }, error -> {
            loadingDialog.hideLoading();
            Toast.makeText(ListUserActivity.this, "Error by Post data!", Toast.LENGTH_SHORT).show();

        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return user.toHashMap();
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void deleteApi(String url, UserResponse user, int position) {
        loadingDialog.startLoading();
        StringRequest stringRequest = new StringRequest(
                Request.Method.DELETE, url + '/' + user.getId(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loadingDialog.hideLoading();
                listData.remove(position);
                adapter.notifyItemRemoved(position);
                Toast.makeText(ListUserActivity.this, "Successfully", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingDialog.hideLoading();
                Toast.makeText(ListUserActivity.this, "Error by Post data!", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void postApi(String url, UserResponse user) {
        loadingDialog.startLoading();
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                url,
                response -> {
                    loadingDialog.hideLoading();
                    listData.add(user);
                    adapter.notifyItemInserted(listData.size());
                    Toast.makeText(ListUserActivity.this, "Successfully", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    loadingDialog.hideLoading();
                    Toast.makeText(ListUserActivity.this, "Error by Post data!", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return user.toHashMap();
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void showDialogEdit(UserResponse uData, int position) {
        EditText email, name;
        Button btn;
        AlertDialog.Builder builder = new AlertDialog.Builder(ListUserActivity.this);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.customview, viewGroup, false);
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        email = dialogView.findViewById(R.id.editTextTextPersonName2);
        name = dialogView.findViewById(R.id.editTextTextPersonName3);
        btn = dialogView.findViewById(R.id.button5);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uData.setName(name.getText().toString());
                uData.setEmail(email.getText().toString());
                putApi(Constant.BASE_URL + "user", uData, position);
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void showDialogInsert() {
        EditText email, name;
        Button btn;
        AlertDialog.Builder builder = new AlertDialog.Builder(ListUserActivity.this);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.customview, viewGroup, false);
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        email = dialogView.findViewById(R.id.editTextTextPersonName2);
        name = dialogView.findViewById(R.id.editTextTextPersonName3);
        btn = dialogView.findViewById(R.id.button5);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserResponse uData =new UserResponse();
                uData.setName(name.getText().toString());
                uData.setEmail(email.getText().toString());
                postApi(Constant.BASE_URL + "user", uData);
                alertDialog.dismiss();
            }
        });
        alertDialog.show();

    }
}