package com.otacodes.onrentservices.Activity;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.otacodes.onrentservices.BuildConfig;
import com.otacodes.onrentservices.Constants.BaseApp;
import com.otacodes.onrentservices.Constants.Constants;
import com.otacodes.onrentservices.Fragment.HomeFragment;
import com.otacodes.onrentservices.Fragment.MessageFragment;
import com.otacodes.onrentservices.Fragment.PropertyFragment;
import com.otacodes.onrentservices.Models.AboutModels;
import com.otacodes.onrentservices.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.otacodes.onrentservices.Fragment.FavouriteFragment;
import com.otacodes.onrentservices.Fragment.ProfileFragment;
import com.otacodes.onrentservices.Utils.BannerAds;
import com.otacodes.onrentservices.Utils.BottomNavigationViewHelper;


public class MainActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {
    long mBackPressed;


    public static SharedPreferences sharedPreferences;
    public static String user_id;
    public static String user_name;
    public static String image;
    public static String image1;
    public static String token;
    BaseApp baseApp;
    LinearLayout llsearch;
    DatabaseReference rootref;
    com.otacodes.onrentservices.Constants.BaseApp BaseApp;

    AboutModels modelAbout;
    public static String title="none";
    ImageView maps;

    EditText search;

    public  static MainActivity mainActivity;
    private FragmentManager fragmentManager;
    BottomNavigationView navigation;

    int previousSelect = 0;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.home:

                    HomeFragment homeFragment = new HomeFragment();
                    navigationItemSelected(0);
                    loadFrag(homeFragment, getString(R.string.menu_home), fragmentManager);
                    return true;

                case R.id.property:

                    PropertyFragment propertyFragment = new PropertyFragment();
                    navigationItemSelected(1);
                    loadFrag(propertyFragment, getString(R.string.menu_property), fragmentManager);
                    return true;

                case R.id.favourite:


                    FavouriteFragment matchFragment = new FavouriteFragment();
                    navigationItemSelected(2);
                    loadFrag(matchFragment, getString(R.string.menu_favourite), fragmentManager);
                    return true;

                case R.id.chat:

                    MessageFragment messageFragment = new MessageFragment();
                    navigationItemSelected(3);
                    loadFrag(messageFragment, getString(R.string.menu_chat), fragmentManager);
                    return true;

                case R.id.user:
                    ProfileFragment profileFragment = new ProfileFragment();
                    navigationItemSelected(4);
                    loadFrag(profileFragment, getString(R.string.menu_profile), fragmentManager);
                    return true;
            }
            return false;
        }
    };

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle actionBarDrawerToggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);

        BaseApp = com.otacodes.onrentservices.Constants.BaseApp.getInstance();

        ///
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbarnewe);
        setSupportActionBar(toolbar);


        navigationView = (NavigationView)findViewById(R.id.navigation_view);

        drawerLayout = (DrawerLayout)findViewById(R.id.MainFragment);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawer_open, R.string.drawer_close);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        actionBarDrawerToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.black));

        actionBarDrawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);



        LinearLayout mAdViewLayout = findViewById(R.id.adView);
        BannerAds.ShowBannerAds(getApplicationContext(), mAdViewLayout);
        fragmentManager = getSupportFragmentManager();
        llsearch = findViewById(R.id.llsearch);
        baseApp = BaseApp.getInstance();

        navigation = findViewById(R.id.navigation);


        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        maps = findViewById(R.id.maps);
         BottomNavigationViewHelper.disableShiftMode(navigation);
         modelAbout = new AboutModels();
        HomeFragment homeFragment = new HomeFragment();
        loadFrag(homeFragment, getString(R.string.menu_home), fragmentManager);
        mainActivity =this;
        sharedPreferences = getSharedPreferences(Constants.pref_name, MODE_PRIVATE);
        user_id = sharedPreferences.getString(Constants.uid, "null");
        user_name = sharedPreferences.getString(Constants.f_name, "") + " " + sharedPreferences.getString(Constants.l_name, "");
        image =sharedPreferences.getString(Constants.u_pic,"null");
        image1 =sharedPreferences.getString("image1","null");
        token=sharedPreferences.getString(Constants.device_token, FirebaseInstanceId.getInstance().getToken());
        rootref= FirebaseDatabase.getInstance().getReference();

        search = findViewById(R.id.search);

        maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BannerAds.ShowInterstitialAds(MainActivity.this);
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });

        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String sSearch= search.getText().toString().trim();
                if (TextUtils.isEmpty(sSearch)) {

                    Toast.makeText(MainActivity.this, "Column Can't be Empty", Toast.LENGTH_SHORT).show();

                } else {
                    Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                    intent.putExtra("searchtext", sSearch);
                    startActivity(intent);
                    return true;
                }

                return false;
            }

        });

        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Constants.versionname=packageInfo.versionName;
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (baseApp.getIsLogin()) {

            rootref.child("Users").child(user_id).child("token").setValue(token);

        } else {

            rootref.child("Users").child(user_id).child("token").setValue("null");

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        int count = this.getSupportFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            if (mBackPressed + 2000 > System.currentTimeMillis()) {
                super.onBackPressed();
                return;
            } else {
                clickDone();

            }
        } else {

            super.onBackPressed();

        }
    }


    public void loadFrag(Fragment f1, String name, FragmentManager fm) {

        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.Container, f1, name);
        ft.commit();

    }

    public void navigationItemSelected(int position) {
        previousSelect = position;
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        //fragmentManagerHelper.setFragmentManager(fm);

        if (id == R.id.home){

            HomeFragment homeFragment = new HomeFragment();
            loadFrag(homeFragment, getString(R.string.menu_home), fragmentManager);



        }else  if (id == R.id.logout){

            clickDone();

        }else if (id == R.id.user){

            ProfileFragment profileFragment = new ProfileFragment();
            loadFrag(profileFragment, getString(R.string.menu_profile), fragmentManager);


        }else if (id == R.id.property){

            PropertyFragment propertyFragment = new PropertyFragment();
            loadFrag(propertyFragment, getString(R.string.menu_property), fragmentManager);

        }else if (id == R.id.favourite){

            FavouriteFragment matchFragment = new FavouriteFragment();
             loadFrag(matchFragment, getString(R.string.menu_favourite), fragmentManager);

        }else if (id == R.id.chat){

            MessageFragment messageFragment = new MessageFragment();
             loadFrag(messageFragment, getString(R.string.menu_chat), fragmentManager);


        } else if (id == R.id.MyProperty){

            Intent intent = new Intent(this, MyPropertyActivity.class);
            startActivity(intent);

        }else if (id == R.id.Privacypolicy){

            Intent intent = new Intent(this, PrivacyActivity.class);
            startActivity(intent);

        }else if (id == R.id.AboutUs){

            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);

        }else if (id == R.id.ShareApp){

            try {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                String shareMessage= "\nLet me recommend you this application\n\n";
                shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "choose one"));
            } catch(Exception e) {
                //e.toString();
            }



        }else if (id == R.id.RateApp){


            Uri uri = Uri.parse("market://details?id=" + getPackageName());
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            try {
                startActivity(goToMarket);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
            }


        }else {

            HomeFragment homeFragment = new HomeFragment();
            loadFrag(homeFragment, getString(R.string.menu_home), fragmentManager);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.MainFragment);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void Logout(){
        SharedPreferences.Editor editor= MainActivity.sharedPreferences.edit();
        editor.putString(Constants.uid,"").clear();
        editor.commit();
        rootref.child("Users").child(user_id).child("token").setValue("null");
        baseApp.saveIsLogin(false);
        Intent intent = new Intent(this.getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

      //  getFragmentManager().beginTransaction().detach(ProfileFragment.this).attach(ProfileFragment.this).commit();


    }


    public void clickDone() {
        new AlertDialog.Builder(this)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(getString(R.string.app_name))
                .setMessage("Do u want to continue?")
                .setPositiveButton("YES!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Logout();
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

}
