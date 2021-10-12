package com.android.productlist.Database;

import android.text.Editable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "product")
public class Product {
    @PrimaryKey
    public int uid;

    @ColumnInfo(name = "productname")
    public String productname;

    @ColumnInfo(name = "productdescription")
    public String productdescription;

    @ColumnInfo(name = "productprice")
    public double productprice;

    @ColumnInfo(name = "providername")
    public String providername;

    @ColumnInfo(name = "provideremail")
    public String provideremail;

    @ColumnInfo(name = "providerphone")
    public long providerphone;

    @ColumnInfo(name = "providerlat")
    public double latitude;

    @ColumnInfo(name = "providerlon")
    public double longitude;

    public Product(int uid, String productname, String productdescription, double productprice,
                   String providername, String provideremail, long providerphone,
                   Double latitude, Double longitude) {
        this.uid = uid;
        this.productname = productname;
        this.productdescription = productdescription;
        this.productprice = productprice;
        this.providername = providername;
        this.provideremail = provideremail;
        this.providerphone = providerphone;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getUid() {
        return uid;
    }

    public String getProductname() {
        return productname;
    }

    public String getProductdescription() {
        return productdescription;
    }

    public double getProductprice() {
        return productprice;
    }

    public String getProvidername() {
        return providername;
    }

    public String getProvideremail() {
        return provideremail;
    }

    public long getProviderphone() {
        return providerphone;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
