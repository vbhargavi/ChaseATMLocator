package com.example.laxmibhargavivaditala.chaseatmlocator;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.laxmibhargavivaditala.chaseatmlocator.Modal.ChaseATMResponse;

/**
 * ATMDetailAcitivty is used to display the details of a specific ATM/Branch. It uses fragment to display the details.
 */
public class ATMDetailActivity extends AppCompatActivity {
    public static final String EXTRA_ATM = "atm_key";

    private ChaseATMResponse.Location mAtmLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atmdetail);

        mAtmLocation = getIntent().getParcelableExtra(EXTRA_ATM);

        initToolbar();

        if (savedInstanceState == null) {
            goToDetailFragment();
        }
    }

    /**
     * Initialize the toolbar and set it as action bar.
     */
    private void initToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setTitle(mAtmLocation.isBranch() ? getString(R.string.branch_details) : getString(R.string.atm_details));
    }

    public void setTitle(String title) {
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    private void goToDetailFragment() {
        String tag = ATMDetailFragment.class.getSimpleName();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        ATMDetailFragment fragment = ATMDetailFragment.newInstance(mAtmLocation);

        fragmentTransaction.add(R.id.container_fragment, fragment, tag);

        fragmentTransaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
