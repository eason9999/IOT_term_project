/*
	此類別為建立 fragement 類別
*/


package com.example.luolab.measureppg;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
//Justin Add
import com.clj.fastble.BleManager;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private TabLayout tabLayout;

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_FINE_LOCATION_PERMISSION = 2;

    private BluetoothManager bluetoothManager;

    private BluetoothLeScanner bluetoothLeScanner;

    private BluetoothGatt bluetoothGatt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 2) {
                    mSectionsPagerAdapter.notifyDataSetChanged();
                    createTabIcons();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        createTabIcons();

        // Initialize Bluetooth
        initBluetooth();

        // Request Location Permission
        requestLocationPermission();

    }

    private void initBluetooth() {
        BluetoothManager bluetoothManager = getSystemService(BluetoothManager.class);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null) {
            Log.d("msg", "BluetoothAdapter 未初始化");
        }
        // Check if BLE is supported on the device
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE is not supported on this device", Toast.LENGTH_SHORT).show();
            finish();
        }


    }

    // Request Location Permission for BLE Scanning
    private void requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Check if permission is not granted
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // Request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_FINE_LOCATION_PERMISSION);
            } else {
                // Permission already granted, start BLE operations
                // startBleOperations(); // Optional
            }
        } else {
            // Permissions are automatically granted on older versions
            // startBleOperations(); // Optional
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_FINE_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start BLE operations
            } else {
                // Permission denied, inform the user and disable BLE features
                Toast.makeText(this, "Location permission is required for BLE scanning", Toast.LENGTH_SHORT).show();
                // Optionally, disable BLE features or close the app
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    private void createTabIcons()
    {

        TextView tabFour = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabFour.setText("註冊Firebase");
        //tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ppg, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabFour);

        TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabTwo.setText("生物回饋訓練");
        //tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ppg, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabTwo);

        TextView tabFive = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabFive.setText("訓練紀錄");
        //tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ppg, 0, 0);
        tabLayout.getTabAt(2).setCustomView(tabFive);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        public static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView;
            if(getArguments().getInt(ARG_SECTION_NUMBER) == 1)
            {
                rootView = (new Register()).onCreateView(inflater, container, savedInstanceState);
            }
            else if(getArguments().getInt(ARG_SECTION_NUMBER) == 2)
            {
                rootView = (new GuanView()).onCreateView(inflater, container, savedInstanceState);
            }
            else
                rootView = (new ShowData()).onCreateView(inflater, container, savedInstanceState);
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private int mChildCount = 0;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getItemPosition(Object object) {

            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }
}
