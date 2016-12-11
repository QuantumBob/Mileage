package com.smalldoor.rwk.mileage;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

public class MileageAppActivity extends LeftDrawerActivity {

    private DbHelper mDbHelper = new DbHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //mDbHelper.addTestData();

        Fragment fragment = createFragment(0);
        if (fragment != null) {
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
        } else {
            Log.e("Null Value", "MileageAppActivity onCreate fragment is null");
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        DeliveryDepot.get(this).close();
    }
    @Override
    protected Fragment createFragment(int position) {

        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new AndroidMileageFragment();
                break;

            case 1:
                fragment = new DeliveriesFragment();
                break;

            case 2:
                fragment = new MileageFragment();
                break;

            default:
                break;

        }
        return fragment;
    }

    @Override
    protected Bundle putArguments(Fragment fragment) {
        return null;
    }

    @Override
    protected void onDrawerItemClick(AdapterView<?> parent, View view, int position, long id){

        switch (position) {
            case 0:case 1:case 2:
                Bundle args;// = new Bundle();
                Fragment fragment = createFragment(position);
                args = putArguments(fragment);
                fragment.setArguments(args);
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                break;
            case 5:
                mDbHelper.getTableCount(mDbHelper.getReadableDatabase());
                break;
            case 6:
                if(mDbHelper.createTables(mDbHelper.getWritableDatabase())){
                    Log.d("Tables", "Created");
                    Log.d("Table count", Integer.toString(mDbHelper.getTableCount(mDbHelper.getReadableDatabase())));
                } else {
                    Log.e("Tables", "NOT CREATED");
                }
                break;
            case 7:
                if(mDbHelper.deleteTables(mDbHelper.getWritableDatabase())){
                    DeliveryDepot.get(this).deleteDeliveries();
                    DeliveryDepot.get(this).deleteDates();
                    try {
                        DeliveriesFragment deliveriesFragment = (DeliveriesFragment)getFragmentManager().findFragmentById(R.id.fragment_container);
                        deliveriesFragment.buildDateSpinner();
                        Log.d("Tables", "Deleted");
                        Log.d("Table count", Integer.toString(mDbHelper.getTableCount(mDbHelper.getReadableDatabase())));
                    } catch (ClassCastException err) {
                        Log.e("onDrawerItemClick", "deleteTables:" + err.toString());
                    }

                } else {
                    Log.e("Tables", "NOT DELETED");
                }
                break;
            case 8:
                if(deleteDatabase("MileageApp.db")){
                    Log.d("Database", "Deleted");
                } else {
                    Log.e("Database", "NOT DELETED");
                }
                break;
            default:
                //args;// = new Bundle();
                fragment = createFragment(0);
                //args = putArguments(fragment);
                //fragment.setArguments(args);
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                break;
        }
    }
}



