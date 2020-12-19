package com.sabo.sabostorev2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.sabo.sabostorev2.API.APIRequestData;
import com.sabo.sabostorev2.Account.AccountActivity;
import com.sabo.sabostorev2.Common.Common;
import com.sabo.sabostorev2.Common.Preferences;
import com.sabo.sabostorev2.Model.ResponseModel;
import com.sabo.sabostorev2.Model.UserModel;
import com.sabo.sabostorev2.RoomDB.RoomDBHost;
import com.sabo.sabostorev2.RoomDB.User.LocalUserDataSource;
import com.sabo.sabostorev2.RoomDB.User.User;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import maes.tech.intentanim.CustomIntent;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private NavController navController;
    private DrawerLayout drawer;
    private NavigationView navigationView;

    private APIRequestData mService;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private LocalUserDataSource localUserDataSource;

    private CircleImageView civHeader;
    private TextView tvHeader;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mService = Common.getAPI();
        localUserDataSource = new LocalUserDataSource(RoomDBHost.getInstance(this).userDAO());

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_categories, R.id.nav_cart)
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        initViews();
   }

    private void initViews() {
        View headerView = navigationView.getHeaderView(0);
        civHeader = headerView.findViewById(R.id.civHeader);
        tvHeader = headerView.findViewById(R.id.tvHeader);
        progressBar = headerView.findViewById(R.id.progressBar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    /**
     * OnResume
     */
    @Override
    protected void onResume() {
        super.onResume();
        initViewsUser();
    }

    private void initViewsUser() {
        SweetAlertDialog mSweetLoading = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        mSweetLoading.getProgressHelper().setBarColor(getResources().getColor(R.color.colorAccent));
        mSweetLoading.setTitleText("Please wait...").setCanceledOnTouchOutside(false);
        mSweetLoading.show();

        String uid = Preferences.getUID(this);
        mService.getUser(uid).enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                int code = response.body().getCode();
                String message = response.body().getMessage();
                mSweetLoading.dismissWithAnimation();
                progressBar.setVisibility(View.GONE);
                if (code == 1) {
                    Log.d("User", message);
                } else {
                    UserModel userModel = response.body().getUser();
                    Common.currentUser = userModel;

                    User user = new User();
                    user.setUid(userModel.getUid());
                    user.setEmail(userModel.getEmail());
                    user.setUsername(userModel.getUsername());
                    user.setImage(userModel.getImage());
                    user.setPhone(userModel.getPhone());
                    user.setGender(userModel.getGender());

                    compositeDisposable.add(localUserDataSource.insertOrUpdateUser(user)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(() -> {
                                Log.d("user", "Success");
                            },throwable -> {
                                Log.d("user", throwable.getMessage());
                            }));

                    tvHeader.setText(user.getUsername());
                    Picasso.get().load(Common.USER_IMAGE_URL + user.getImage()).placeholder(R.drawable.no_profile).into(civHeader);
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                String message = t.getMessage();
                progressBar.setVisibility(View.GONE);
                if (message.contains("10000ms")){
                    mSweetLoading.dismissWithAnimation();
                    String uid = Preferences.getUID(HomeActivity.this);
                    compositeDisposable.add(localUserDataSource.getUser(uid)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(user -> {
                        tvHeader.setText(user.getUsername());
                        Picasso.get().load(Common.USER_IMAGE_URL + user.getImage()).placeholder(R.drawable.no_profile).into(civHeader);
                    }));
                }

                else
                    mSweetLoading.setTitleText("Oops!")
                            .setContentText(t.getMessage())
                            .changeAlertType(SweetAlertDialog.WARNING_TYPE);
            }
        });
    }

    public void onAccount(MenuItem item) {
        drawer.closeDrawers();
        startActivity(new Intent(this, AccountActivity.class));
        CustomIntent.customType(this, Common.LTR);
    }

    public void onLogout(MenuItem item) {
        drawer.closeDrawers();
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Sign out!")
                .setContentText("Are you sure?")
                .showCancelButton(true)
                .setCancelText("No")
                .setCancelClickListener(sweetAlertDialog -> {
                    sweetAlertDialog.dismissWithAnimation();
                })
                .setConfirmText("Yes")
                .setConfirmClickListener(sweetAlertDialog -> {
                    String uid = Preferences.getUID(this);
                    mService.signOut(uid).enqueue(new Callback<ResponseModel>() {
                        @Override
                        public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                            int code = response.body().getCode();
                            String message = response.body().getMessage();
                            switch (code) {
                                case 0:
                                    sweetAlertDialog.setTitleText("Oops!")
                                            .setContentText(message)
                                            .showCancelButton(false)
                                            .setConfirmText("Close")
                                            .setConfirmClickListener(sweetAlertDialog1 -> {
                                                sweetAlertDialog.dismissWithAnimation();
                                            })
                                            .changeAlertType(SweetAlertDialog.WARNING_TYPE);
                                    break;
                                case 1:
                                    sweetAlertDialog.dismissWithAnimation();
                                    Preferences.clearAllPreferences(HomeActivity.this);
                                    startActivity(new Intent(HomeActivity.this, SignInActivity.class));
                                    finish();
                                    break;
                                default:
                                    break;
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseModel> call, Throwable t) {
                            String message = t.getMessage();
                            if (message.contains("10000ms"))
                                sweetAlertDialog.setTitleText("Oops!")
                                        .setContentText(t.getMessage())
                                        .showCancelButton(false)
                                        .setConfirmText("Close")
                                        .setConfirmClickListener(sweetAlertDialog1 -> {
                                            sweetAlertDialog.dismissWithAnimation();
                                        })
                                        .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                            else
                                sweetAlertDialog.setTitleText("Oops!")
                                        .setContentText(t.getMessage())
                                        .showCancelButton(false)
                                        .setConfirmText("Close")
                                        .setConfirmClickListener(sweetAlertDialog1 -> {
                                            sweetAlertDialog.dismissWithAnimation();
                                        })
                                        .changeAlertType(SweetAlertDialog.WARNING_TYPE);
                        }
                    });
                })
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
        String uid = Preferences.getUID(this);
        mService.signOut(uid).enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                int code = response.body().getCode();
                String message = response.body().getMessage();
                Log.d("onDestroy", code + "\n"+message);
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                Log.d("onDestroy-Failure", t.getMessage());
            }
        });
    }

}