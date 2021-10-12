package com.android.productlist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.productlist.Adapters.productAdapter;
import com.android.productlist.Adapters.providerAdapter;
import com.android.productlist.Database.Product;
import com.android.productlist.Database.ProductDatabase;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class ProductListActivity extends AppCompatActivity {

    TabLayout tabLayout;
    LinearLayout ll_provider,ll_product;
    FloatingActionButton fab_addproduct;
    SearchView sv_product;
    ConstraintLayout cl_parent;

    RecyclerView rv_provider,rv_product;
    productAdapter productAda;
    providerAdapter providerAda;
    ProductDatabase pdtdb;

    List<String> product_name = new ArrayList<String>();
    List<Double> product_price = new ArrayList<Double>();
    List<String> provider_name = new ArrayList<String>();
    List<Integer> uid = new ArrayList<Integer>();

    List<String> unique_provider = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        getSupportActionBar().setTitle("Providers"); // setting activity's appbar title

        //Initializing all Views and ViewGroups
        tabLayout = findViewById(R.id.tablayout);
        ll_provider = findViewById(R.id.ll_provider);
        ll_product = findViewById(R.id.ll_product);
        fab_addproduct = findViewById(R.id.fab_addproduct);
        sv_product = findViewById(R.id.sv_product);
        cl_parent = findViewById(R.id.cl_parent);

        rv_provider = findViewById(R.id.rv_provider);
        rv_product = findViewById(R.id.rv_product);

        ll_product.setVisibility(View.GONE); //hiding product layout

        pdtdb = Room.databaseBuilder(getApplicationContext(), ProductDatabase.class,"product-database").allowMainThreadQueries().build();

        populatedata();

        System.out.println(pdtdb.productDao().getAll());
        loadProvider();

        //  setting functionality to Tablayout
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == 0) // when provider tab is selected
                {
                    // hiding product layout and making provider layout visible
                    ll_provider.setVisibility(View.VISIBLE);
                    ll_product.setVisibility(View.GONE);
                    getSupportActionBar().setTitle("Providers"); // setting activity's appbar title

                    loadProvider();

                }
                else if(tab.getPosition() == 1) // when product tab is selected
                {
                    // hiding provider layout and making product layout visible
                    ll_provider.setVisibility(View.GONE);
                    ll_product.setVisibility(View.VISIBLE);
                    getSupportActionBar().setTitle("Products"); // setting activity's appbar title


                   loadProduct();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        fab_addproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductListActivity.this,ProductEntryActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alert = new AlertDialog.Builder(ProductListActivity.this);
        alert.setTitle("Exit ?");
        alert.setMessage("Do you want to exit this Application ?");
        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishAffinity();
            }
        });
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert.create().show();
    }

    public void loadProvider()
    {
        // initializing room database
        pdtdb = Room.databaseBuilder(getApplicationContext(),ProductDatabase.class,"product-database").allowMainThreadQueries().build();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);

        // getting all provider names from room database
        provider_name = pdtdb.productDao().getallproviders();

        Set<String> unpv = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER); //getting unique provider names using Set
        unpv.addAll(provider_name); // adding all unique value from provider list to newly created set
        unique_provider.clear();
        unique_provider.addAll(unpv); // adding values from set back to unique_provider list

        // initializing provider Adapter
        providerAda = new providerAdapter(provider_name,unique_provider);

        // setting layout manager to Provider recyclerview
        rv_provider.setLayoutManager(layoutManager);

        // setting adapter to Provider recyclerview
        rv_provider.setAdapter(providerAda);

        //deleting provider item on swipe
        ItemTouchHelper.SimpleCallback itscprovider = new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                TextView tv_title = viewHolder.itemView.findViewById(R.id.tv_title);
                String provname = tv_title.getText().toString();

                List<Product> allproducts = pdtdb.productDao().loadAllByProvidernames(provname);

                pdtdb.productDao().deleteproductbyprovider(provname);

                provider_name.remove(provname);
                unique_provider.remove(provname);
                final int index = viewHolder.getAdapterPosition();

                providerAda.notifyItemRemoved(index);

                Snackbar.make(cl_parent,"Provider Deleted: "+provname,5000).setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pdtdb.productDao().insertProducts(allproducts);
                        provider_name = pdtdb.productDao().getallproviders();
                        unique_provider.add(index,provname);

                        providerAda.notifyItemInserted(index);
                    }
                }).show();

            }
        };

        ItemTouchHelper ithprov = new ItemTouchHelper(itscprovider);
        ithprov.attachToRecyclerView(rv_provider); // attaching itemtouchhelper to provider recyclerview


    }

    public void loadProduct()
    {
        // initializing room database
        pdtdb = Room.databaseBuilder(getApplicationContext(),ProductDatabase.class,"product-database").allowMainThreadQueries().build();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);

        // getting all product names from room database
        product_name = pdtdb.productDao().getallproducts();

        // getting all products descriptions from room database
        product_price = pdtdb.productDao().getallprodprice();

        // getting all product ids from room database
        uid = pdtdb.productDao().getallids();

        // initializing Product Adapter
        productAda = new productAdapter(product_name,product_price,uid);

        // setting layout manager to Product recyclerview
        rv_product.setLayoutManager(layoutManager);

        // setting adapter to Product recyclerview
        rv_product.setAdapter(productAda);

        // setting search functionality to Product recyclerview
        sv_product.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                productAda.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                productAda.getFilter().filter(newText);
                return false;
            }
        });



        //deleting product on swipe
        ItemTouchHelper.SimpleCallback itscproduct = new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                TextView tv_title = viewHolder.itemView.findViewById(R.id.tv_title);
                TextView tv_subtitle = viewHolder.itemView.findViewById(R.id.tv_subtitle);
                TextView tv_prodid = viewHolder.itemView.findViewById(R.id.tv_prodid);
                int produid = Integer.parseInt(tv_prodid.getText().toString());
                String prodname = tv_title.getText().toString();
                Double prodprice = Double.valueOf(tv_subtitle.getText().toString().replace("Price : $",""));

                Product product = pdtdb.productDao().loadAllByProductids(produid);

                pdtdb.productDao().deleteproductbyuid(produid);

                product_name.remove(prodname);
                product_price.remove(prodprice);
                uid.remove(Integer.valueOf(produid));

                final int index = viewHolder.getAdapterPosition();

                productAda.notifyItemRemoved(index);

                Snackbar.make(cl_parent,"Product Deleted: "+prodname,5000).setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pdtdb.productDao().insertProduct(product);
                        product_name.add(index,prodname);
                        product_price.add(index,prodprice);
                        uid.add(index,produid);

                        productAda.notifyItemInserted(index);

                    }
                }).show();

            }
        };

        ItemTouchHelper ithprod = new ItemTouchHelper(itscproduct);
        ithprod.attachToRecyclerView(rv_product); // attaching itemtouchhelper to product recyclerview
    }

    public void populatedata() {

        SharedPreferences pref = getSharedPreferences("pref_first", MODE_PRIVATE);
        int first = pref.getInt("first", 1);

        if (first == 1) {
            List<Product> products = new ArrayList<>();

            Product product = new Product(1, "Brazilian Dry Oil",
                    "Brazilian Dry Oil is a fast-absorbing, lightweight oil that can be used daily to smooth",
                    26.0, "Brazilian", "john@Brazilian.site", 6135550154L, 53.534444, -113.490278);
            products.add(product);

            product = new Product(2	,"Ionic Color Lock"	," Lock in week one color vibrancy for all shades, colors, and blonde tones.",
                    38.0	,"Ionic"	,"andy@Ionic.com"	,
                    7805550198L	,45.424722,	-75.695);
            products.add(product);

            product = new Product(3	,"Professional Styling"	,"Smooth or style with this versatile, lightweight, ergonomic styling iron. Far infrared heat penetra",
                    175.0,	"Professional"	,"jason@Professional.info"	,
                    7805550106L,	49.260833,	-123.113889);
            products.add(product);

            product = new Product(4	,"Hydrate Conditioning Masque"	,"AVOCADO OILAmong the healthiest natural ingredients on the planet, avocado oil is beneficial for hair",
                    20.0,	"Hydrate",	"duford@Hydrate.com"	,
                    4165550186L,	43.741667	,-79.373333);
            products.add(product);

            product = new Product(5	,"Smooth Conditioner"	,"AVOCADO OILAmong the healthiest natural ingredients on the planet, avocado oil is beneficial for hair"	,
                    20.0,	"Smooth Conditioner"	,"isabella@SmoothConditioner.ca"	,
                    4165550176L,	49.884444,	-97.146389);
            products.add(product);

            product = new Product(6,	"Clean Shampoo"	,"AVOCADO OILAmong the healthiest natural ingredients on the planet, avocado oil is beneficial for hair"	,
                    50.0,	"Clean Shampoo"	,"Doof@CleanShampoo.ca"	,
                    2045550173,	53.534444	,-113.490278);
            products.add(product);

            product = new Product(	7,	"Instant Damage Correction Shampoo","The exclusive LEAF & FLOWER CBD Corrective Complex elicits an entourage effect by combining key cann.",
                    37.0,	"Correction Shampoo"	,"Candice@CorrectionShampoo.ca"	,
                    2045550179L,	45.424722,	-75.695);
            products.add(product);

            product = new Product(8,	"SPECIAL SHAMPOO"	,"Special ingredients and tea tree oil rid hair of impurities and leave hair full of vitality and lust."	,
                    15.0,	"SPECIAL SHAMPOO"	,"Linda@SPECIALSHAMPOO.ca",
                    8675550120L,	43.741667,	-79.373333);
            products.add(product);

            product = new Product(9,	"MOISTURE SHAMPOO",	" Nourish tresses with lightweight moisture while gently cleansing. This breakthrough formula envelo.."
                    ,50.0,	"MOISTURE SHAMPOO"	,"Ferb@MOISTURESHAMPOO.ca"	,
                    8675550163L,	49.260833,	-123.113889);
            products.add(product);

            product = new Product(10,	"Bombshell Shine Mist"	,"Steal the spotlight in an instant with this weightless, superfine mist. Provides frizz and flyaway .",
                    10.0	,"Bombshell Shine Mist"	,"Phineas@BombshellShineMist.ca",
                6135550169L	,49.884444,-97.146389);
            products.add(product);

            pdtdb.productDao().insertProducts(products);

            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("first",0);
            editor.apply();
        }
    }
}