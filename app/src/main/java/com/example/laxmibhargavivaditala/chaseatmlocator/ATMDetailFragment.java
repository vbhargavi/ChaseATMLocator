package com.example.laxmibhargavivaditala.chaseatmlocator;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.laxmibhargavivaditala.chaseatmlocator.Modal.ChaseATMResponse;

import java.net.URLEncoder;
import java.util.List;

/**
 * ATMDetailFragment is reponsible for displaying details of ATM/Branch.
 */
public class ATMDetailFragment extends Fragment {

    private ChaseATMResponse.Location mAtmLocation;

    public ATMDetailFragment() {

    }

    public static ATMDetailFragment newInstance(ChaseATMResponse.Location atmLocation) {
        ATMDetailFragment fragment = new ATMDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ATMDetailActivity.EXTRA_ATM, atmLocation);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(ATMDetailActivity.EXTRA_ATM, mAtmLocation);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);// Set this to true so that the fragment can have its own menu in the toolbar.
        return inflater.inflate(R.layout.fragment_atmdetail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAtmLocation = (ChaseATMResponse.Location) (savedInstanceState != null ? savedInstanceState.getParcelable(ATMDetailActivity.EXTRA_ATM) : getArguments().getParcelable(ATMDetailActivity.EXTRA_ATM));

        TextView titleTxtView = (TextView) view.findViewById(R.id.title_txt);
        TextView addressTxtView = (TextView) view.findViewById(R.id.address_txt);
        TextView phoneTxtView = (TextView) view.findViewById(R.id.phone_txt);
        TextView distanceTxtView = (TextView) view.findViewById(R.id.distance_txt);
        TextView atmCntTxtView = (TextView) view.findViewById(R.id.atm_cnt_txt);
        TextView driveUpHrsTxtView = (TextView) view.findViewById(R.id.driveup_txt_view);
        TextView lobbyHrsTxtView = (TextView) view.findViewById(R.id.lobby_hrs_txt);
        TextView serviceTxtView = (TextView) view.findViewById(R.id.services_txt_view);
        View phoneLayout = view.findViewById(R.id.phone_layout);
        View driveUpHrsLayout = view.findViewById(R.id.driveup_layout);
        View lobbyHrsLayout = view.findViewById(R.id.lobby_layout);
        View serviceLayout = view.findViewById(R.id.service_layout);
        View callBtn = view.findViewById(R.id.call_btn);
        View directionsBtn = view.findViewById(R.id.directions_btn);

        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + mAtmLocation.getPhone()));
                if (isIntentAvailable(intent)) {
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), R.string.telephone_not_available, Toast.LENGTH_LONG).show();
                }
            }
        });

        directionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(getString(R.string.maps_geo_string), mAtmLocation.getLat(), mAtmLocation.getLng(),
                            URLEncoder.encode(String.format(getString(R.string.address), mAtmLocation.getAddress(), mAtmLocation.getCity(), mAtmLocation.getState(), mAtmLocation.getZip()), "UTF-8"))));
                    if (isIntentAvailable(intent)) {
                        startActivity(intent);
                    } else {
                        Toast.makeText(getActivity(), R.string.maps_not_available, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        titleTxtView.setText(mAtmLocation.getLabel());
        addressTxtView.setText(String.format(getString(R.string.address), mAtmLocation.getAddress(), mAtmLocation.getCity(), mAtmLocation.getState(), mAtmLocation.getZip()));
        if (!TextUtils.isEmpty(mAtmLocation.getPhone())) {
            phoneLayout.setVisibility(View.VISIBLE);
            phoneTxtView.setText(PhoneNumberUtils.formatNumber(mAtmLocation.getPhone()));
        } else {
            phoneLayout.setVisibility(View.GONE);
        }

        distanceTxtView.setText(String.format(getString(R.string.miles), mAtmLocation.getDistance()));
        atmCntTxtView.setText(String.valueOf(mAtmLocation.getAtms()));

        if (mAtmLocation.getDriveUpHrs() != null && mAtmLocation.getDriveUpHrs().length > 0) {
            driveUpHrsTxtView.setText(buildHoursTxt(mAtmLocation.getDriveUpHrs()));
            driveUpHrsLayout.setVisibility(View.VISIBLE);
        } else {
            driveUpHrsLayout.setVisibility(View.GONE);
        }

        if (mAtmLocation.getLobbyHrs() != null && mAtmLocation.getLobbyHrs().length > 0) {
            lobbyHrsTxtView.setText(buildHoursTxt(mAtmLocation.getLobbyHrs()));
            lobbyHrsLayout.setVisibility(View.VISIBLE);
        } else {
            lobbyHrsLayout.setVisibility(View.GONE);
        }

        if (mAtmLocation.getServices() != null && mAtmLocation.getServices().length > 0) {
            StringBuilder servicesStrBuilder = new StringBuilder();
            for (int i = 0; i < mAtmLocation.getServices().length; i++) {
                servicesStrBuilder.append(mAtmLocation.getServices()[i]);
                if (i != mAtmLocation.getServices().length - 1) {
                    servicesStrBuilder.append("\n");
                }
            }
            serviceTxtView.setText(servicesStrBuilder.toString());
            serviceLayout.setVisibility(View.VISIBLE);
        } else {
            serviceLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_atmdetail, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_share) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, getShareTxt());
            sendIntent.setType("text/plain");
            if (isIntentAvailable(sendIntent)) {
                startActivity(sendIntent);
            } else {
                Toast.makeText(getActivity(), R.string.share_not_available, Toast.LENGTH_LONG).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Build the string data to share.
     *
     * @return
     */
    private String getShareTxt() {
        StringBuilder shareTxtBuilder = new StringBuilder();
        shareTxtBuilder.append(getString(R.string.address_title)).append("\n\n");
        shareTxtBuilder.append(String.format(getString(R.string.address), mAtmLocation.getAddress(), mAtmLocation.getCity(), mAtmLocation.getState(), mAtmLocation.getZip()));

        if (!TextUtils.isEmpty(mAtmLocation.getPhone())) {
            shareTxtBuilder.append("\n\n").append(getString(R.string.phone_title));
            shareTxtBuilder.append("\n\n").append(PhoneNumberUtils.formatNumber(mAtmLocation.getPhone()));
        }

        return shareTxtBuilder.toString();
    }

    /**
     * Build the hours string.
     *
     * @param hrs
     * @return
     */
    private String buildHoursTxt(String[] hrs) {
        String[] hrsArray = getResources().getStringArray(R.array.hours_array);

        StringBuilder hrsBuilder = new StringBuilder();
        for (int i = 0; i < hrsArray.length; i++) {
            hrsBuilder.append(String.format(hrsArray[i], getHoursTxt(hrs[i])));
            if (i != hrsArray.length - 1) {
                hrsBuilder.append("\n");
            }
        }

        return hrsBuilder.toString();
    }

    /**
     * If hour is empty or null we return Closed. If not return the hr.
     *
     * @param hr
     * @return
     */
    private String getHoursTxt(String hr) {
        if (!TextUtils.isEmpty(hr)) {
            return hr;
        }

        return getString(R.string.closed);
    }

    /**
     * This method checks to see if the device has any app that can handle this intent.
     *
     * @param intent
     * @return
     */
    private boolean isIntentAvailable(Intent intent) {
        final PackageManager mgr = getContext().getPackageManager();
        List<ResolveInfo> list =
                mgr.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }
}
