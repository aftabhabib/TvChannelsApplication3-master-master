package com.app.liveplanet_tv;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.app.liveplanet_tv.R;
import com.example.fragment.IntroFragment;


public class IntroActivity extends AppCompatActivity {

    View circle1, circle2, circle3, circle4;
    ViewPager pager;
    PagerAdapter pagerAdapter;
    Button btnSignUp, btnSignIn, btnSkip;
    final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 102;
    String[] PERMISSIONS = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        circle1 = findViewById(R.id.circle1);
        circle2 = findViewById(R.id.circle2);
        circle3 = findViewById(R.id.circle3);
        circle4 = findViewById(R.id.circle4);

        btnSignUp = (Button) findViewById(R.id.button_sign_up);
        btnSignIn = (Button) findViewById(R.id.button_sign_in);
        btnSkip = (Button) findViewById(R.id.button_skip);

        pager = (ViewPager) findViewById(R.id.view_pager);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        setIndicator(0);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setIndicator(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IntroActivity.this, SignUpActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IntroActivity.this, SignInActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IntroActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }

    }

    private void setIndicator(int index) {
        switch (index) {
            case 0:
                circle1.setBackgroundResource(R.drawable.circle_fill);
                circle2.setBackgroundResource(R.drawable.circle);
                circle3.setBackgroundResource(R.drawable.circle);
                circle4.setBackgroundResource(R.drawable.circle);
                break;
            case 1:
                circle2.setBackgroundResource(R.drawable.circle_fill);
                circle1.setBackgroundResource(R.drawable.circle);
                circle3.setBackgroundResource(R.drawable.circle);
                circle4.setBackgroundResource(R.drawable.circle);
                break;
            case 2:
                circle3.setBackgroundResource(R.drawable.circle_fill);
                circle1.setBackgroundResource(R.drawable.circle);
                circle2.setBackgroundResource(R.drawable.circle);
                circle4.setBackgroundResource(R.drawable.circle);
                break;
            case 3:
                circle4.setBackgroundResource(R.drawable.circle_fill);
                circle1.setBackgroundResource(R.drawable.circle);
                circle3.setBackgroundResource(R.drawable.circle);
                circle2.setBackgroundResource(R.drawable.circle);
                break;
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            IntroFragment tp;
            tp = IntroFragment.newInstance(position);
            return tp;
        }

        @Override
        public int getCount() {
            return 4;
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        boolean canUseExternalStorage = false;

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    canUseExternalStorage = true;
                }
                if (!canUseExternalStorage) {
                    Toast.makeText(IntroActivity.this, "You cannot see images without requested permission", Toast.LENGTH_SHORT).show();
                } else {
                    // user now provided permission
                    // perform function for what you want to achieve
                    Log.i("Permission", "granted");
                }
            }
        }
    }
}
