package com.sabo.sabostorev2.ui.Home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.andremion.counterfab.CounterFab;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.sabo.sabostorev2.API.APIRequestData;
import com.sabo.sabostorev2.ui.Account.AccountActivity;
import com.sabo.sabostorev2.Adapter.SliderAdapter;
import com.sabo.sabostorev2.Common.Common;
import com.sabo.sabostorev2.Common.Preferences;
import com.sabo.sabostorev2.Model.ExchangeRates.Currency;
import com.sabo.sabostorev2.Model.ExchangeRates.Rates;
import com.sabo.sabostorev2.Model.ResponseModel;
import com.sabo.sabostorev2.Model.UserModel;
import com.sabo.sabostorev2.R;
import com.sabo.sabostorev2.RoomDB.RoomDBHost;
import com.sabo.sabostorev2.RoomDB.User.LocalUserDataSource;
import com.sabo.sabostorev2.RoomDB.User.User;
import com.sabo.sabostorev2.RoomDB.User.UserDataSource;
import com.sabo.sabostorev2.ui.Categories.Categories;
import com.sabo.sabostorev2.ui.OrderHistory.OrdersHistory;
import com.sabo.sabostorev2.ui.Search.SearchActivity;
import com.sabo.sabostorev2.ui.About.AboutActivity;
import com.sabo.sabostorev2.ui.Cart.TestCart;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

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

    private APIRequestData mService, mExchangeRates;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private UserDataSource userDataSource;
    private String uid;

    private CircleImageView civHeader;
    private TextView tvHeader;
    private ProgressBar progressBar;
    private SliderView sliderView;
    private boolean isSearch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        Window w = getWindow();
//        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);


        mService = Common.getAPI();
        mExchangeRates = Common.getExchangeRatesAPI();
        userDataSource = new LocalUserDataSource(RoomDBHost.getInstance(this).userDAO());
        uid = Preferences.getUID(this);

        CounterFab fab = findViewById(R.id.fab);
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
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home).setDrawerLayout(drawer).build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        initViews();
        initViewsUser();
        initCollapsing();
        initViewPager();
        initExchangeRatesAPI();
   }

    private void initExchangeRatesAPI() {

        mExchangeRates.getExchangeRatesAPI().enqueue(new Callback<Currency>() {
            @Override
            public void onResponse(Call<Currency> call, Response<Currency> response) {
                if (response.isSuccessful() && response.body() != null){
                    Rates rates = response.body().getRates();

                    Common.ratesIDR = rates.getIDR();
                }
            }

            @Override
            public void onFailure(Call<Currency> call, Throwable t) {
            }
        });


    }

    private void initViewPager() {
        sliderView = findViewById(R.id.sliderView);
        sliderView.setIndicatorAnimation(IndicatorAnimations.THIN_WORM);
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
        sliderView.setAutoCycle(true);

        mService.getSlider().enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                int code = response.body().getCode();
                if (code == 1){
                    SliderAdapter sliderAdapter = new SliderAdapter(HomeActivity.this, response.body().getItems());
                    sliderView.setSliderAdapter(sliderAdapter);
                    sliderView.startAutoCycle();
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {

            }
        });


    }

    private void initCollapsing() {
        /**
         * mobile_navigation -> remove title home
         * Android Manifests -> title home set ""
         */
        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsingToolbar);
        AppBarLayout appBarLayout = findViewById(R.id.appBar);
        //RelativeLayout menuSearch = findViewById(R.id.menuSearch);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1){
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0){
                    collapsingToolbarLayout.setTitle("Home");
                    sliderView.setVisibility(View.INVISIBLE);
                    //menuSearch.setVisibility(View.VISIBLE);
                    isSearch = true;
                    isShow = true;
                }
                else if (isShow){
                    collapsingToolbarLayout.setTitle("");
                    sliderView.setVisibility(View.VISIBLE);
                    //menuSearch.setVisibility(View.INVISIBLE);
                    isSearch = false;
                    isShow = false;
                }
                invalidateOptionsMenu();
            }
        });
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
        getMenuInflater().inflate(R.menu.search, menu);
        menu.findItem(R.id.action_search).setVisible(isSearch);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_search) startActivity(new Intent(this, SearchActivity.class));
        return super.onOptionsItemSelected(item);
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
        new Handler().postDelayed(() -> {
            loadData();
        },500);
    }

    private void initViewsUser() {
        SweetAlertDialog mSweetLoading = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        mSweetLoading.getProgressHelper().setBarColor(getResources().getColor(R.color.colorAccent));
        mSweetLoading.setTitleText("Please wait...").setCanceledOnTouchOutside(false);
        mSweetLoading.show();

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
                    user.setNickname(userModel.getNickname());
                    user.setImage(userModel.getImage());
                    user.setPhone(userModel.getPhone());
                    user.setCountryCode(userModel.getCountryCode());
                    user.setGender(userModel.getGender());

                    compositeDisposable.add(userDataSource.insertOrUpdateUser(user)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(() -> {
                                Log.d("user", "Success");
                            },throwable -> {
                                Log.d("user", throwable.getMessage());
                            }));

                    Common.setSpanStringNickname("Welcome ", user.getNickname(), tvHeader);
                    Picasso.get().load(Common.USER_IMAGE_URL + user.getImage()).placeholder(R.drawable.no_profile).into(civHeader);
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                loadData();
                mSweetLoading.setTitleText("Oops!")
                        .setContentText(t.getMessage())
                        .changeAlertType(SweetAlertDialog.WARNING_TYPE);

            }
        });
    }

    private void loadData() {
        compositeDisposable.add(userDataSource.getUser(uid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(user -> {
                    Common.setSpanStringNickname("Welcome ", user.getNickname(), tvHeader);
                    Picasso.get().load(Common.USER_IMAGE_URL + user.getImage()).placeholder(R.drawable.no_profile).into(civHeader);
                }));
    }

    public void onSearch(View view) {
        startActivity(new Intent(this, SearchActivity.class));
    }

    public void onCategories(MenuItem item) {
        drawer.closeDrawers();
        startActivity(new Intent(this, Categories.class));
        CustomIntent.customType(this, Common.LTR);
    }

    public void onCart(MenuItem item) {
        drawer.closeDrawers();
        startActivity(new Intent(this, TestCart.class));
        CustomIntent.customType(this, Common.LTR);
    }

    public void onOrderHistory(MenuItem item) {
        drawer.closeDrawers();
        startActivity(new Intent(this, OrdersHistory.class));
        CustomIntent.customType(this, Common.LTR);
    }

    public void onAccount(MenuItem item) {
        drawer.closeDrawers();
        startActivity(new Intent(this, AccountActivity.class));
        CustomIntent.customType(this, Common.LTR);
    }

    public void onAbout(MenuItem item){
        drawer.closeDrawers();
        startActivity(new Intent(this, AboutActivity.class));
        CustomIntent.customType(this, Common.LTR);
    }

    public void onLogout(MenuItem item) {
        drawer.closeDrawers();
        Common.onLogout(this, userDataSource);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }

    @Override
    protected void onStop() {
        super.onStop();
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