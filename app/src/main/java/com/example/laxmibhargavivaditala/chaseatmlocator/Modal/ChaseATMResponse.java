package com.example.laxmibhargavivaditala.chaseatmlocator.Modal;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by laxmibhargavivaditala on 1/31/17.
 */

public class ChaseATMResponse implements Parcelable {
    public ArrayList<Location> locations;

    public static class Location implements Parcelable {
        private String address;
        private String state;
        private String city;
        private double lat;
        private double lng;
        private String locType;
        private double distance;
        private String zip;
        private int atms;
        private String phone;
        private String bank;
        private String label;
        private String type;
        private String[] lobbyHrs;
        private String[] driveUpHrs;
        private String[] services;

        public boolean isBranch() {
            return "branch".equalsIgnoreCase(locType);
        }

        public int getAtms() {
            return atms;
        }

        public String getPhone() {
            return phone;
        }

        public String getBank() {
            return bank;
        }

        public String getLabel() {
            return label;
        }

        public String getType() {
            return type;
        }

        public String[] getLobbyHrs() {
            return lobbyHrs;
        }

        public String[] getDriveUpHrs() {
            return driveUpHrs;
        }

        public String[] getServices() {
            return services;
        }

        public String getZip() {
            return zip;
        }

        public String getAddress() {
            return address;
        }

        public double getDistance() {
            return distance;
        }

        public String getState() {
            return state;
        }

        public String getCity() {
            return city;
        }

        public double getLat() {
            return lat;
        }

        public double getLng() {
            return lng;
        }

        public String getLocType() {
            return locType;
        }

        public Location() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.address);
            dest.writeString(this.state);
            dest.writeString(this.city);
            dest.writeDouble(this.lat);
            dest.writeDouble(this.lng);
            dest.writeString(this.locType);
            dest.writeDouble(this.distance);
            dest.writeString(this.zip);
            dest.writeInt(this.atms);
            dest.writeString(this.phone);
            dest.writeString(this.bank);
            dest.writeString(this.label);
            dest.writeString(this.type);
            dest.writeStringArray(this.lobbyHrs);
            dest.writeStringArray(this.driveUpHrs);
            dest.writeStringArray(this.services);
        }

        protected Location(Parcel in) {
            this.address = in.readString();
            this.state = in.readString();
            this.city = in.readString();
            this.lat = in.readDouble();
            this.lng = in.readDouble();
            this.locType = in.readString();
            this.distance = in.readDouble();
            this.zip = in.readString();
            this.atms = in.readInt();
            this.phone = in.readString();
            this.bank = in.readString();
            this.label = in.readString();
            this.type = in.readString();
            this.lobbyHrs = in.createStringArray();
            this.driveUpHrs = in.createStringArray();
            this.services = in.createStringArray();
        }

        public static final Creator<Location> CREATOR = new Creator<Location>() {
            @Override
            public Location createFromParcel(Parcel source) {
                return new Location(source);
            }

            @Override
            public Location[] newArray(int size) {
                return new Location[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.locations);
    }

    public ChaseATMResponse() {
    }

    protected ChaseATMResponse(Parcel in) {
        this.locations = in.createTypedArrayList(Location.CREATOR);
    }

    public static final Parcelable.Creator<ChaseATMResponse> CREATOR = new Parcelable.Creator<ChaseATMResponse>() {
        @Override
        public ChaseATMResponse createFromParcel(Parcel source) {
            return new ChaseATMResponse(source);
        }

        @Override
        public ChaseATMResponse[] newArray(int size) {
            return new ChaseATMResponse[size];
        }
    };
}
