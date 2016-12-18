package com.smalldoor.rwk.mileage;

import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/** Fragment to show the deliveries screen */
public class DeliveriesFragment extends Fragment implements OnItemLongClickListener{

    /* constants */
    public static final int DATE_PICKED_RESULT_CODE = 123;
    public static final String RETURN_DATE = "Return Date";
    public static final int EDIT_UPDATE_RESULT_CODE = 789;
    public static final String RETURN_NUM = "Return Num";
    public static final String RETURN_PRICE = "Return Price";
    public static final String RETURN_TIP = "Return Tip";
    public static final String RETURN_LOCAL = "Return Local";
    /* member variables **/
    private RecyclerView mRecyclerView;
    private Spinner mDateSpinner;
    private EditText mNum;
    private EditText mPrice;
    private EditText mTip;
    private CheckBox mLocal;
    ImageButton mImageButton;
    private TextView mPriceTotals;
    private TextView mTipsTotal;
    private TextView mWage;
    private TextView mShop;
    private String mCurrentDate;
    private DeliveryDepot mDeliveryDepot;
    private DeliveriesFragment mDeliveriesFragment;

    /** the listeners for the ticketNumber EditText*/
    private final TextWatcher ticketNumWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
    /** the listeners for the date spinner */
    private final AdapterView.OnItemSelectedListener dateSpinnerSelectedListener = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            if (adapterView.getItemAtPosition(i).toString().toLowerCase().equals("pick")) {
                DialogFragment dateFragment = new DatePickerDeliveriesFragment();
                dateFragment.setTargetFragment(mDeliveriesFragment, DATE_PICKED_RESULT_CODE);
                dateFragment.show(getFragmentManager(), "datePicker");
            } else {
                mDeliveryDepot.buildDeliveriesListFromDb(adapterView.getItemAtPosition(i).toString(), null);
            }
            updateUI();

            String item = adapterView.getItemAtPosition(i).toString();
            if (!item.equals("Pick") && !item.equals("Today") && !mCurrentDate.equals(item)) {
                Toast.makeText(adapterView.getContext(), "Date selected : " + adapterView.getItemAtPosition(i).toString(), Toast.LENGTH_SHORT).show();
                mCurrentDate = item;
            }
        }
        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };
    /** the listener for the add delivery button */
    private final ImageButton.OnTouchListener addButtonTouchListener = new ImageButton.OnTouchListener() {

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

            if (MotionEvent.ACTION_UP == motionEvent.getAction()){

                DeliveryDetail delivery = new DeliveryDetail();

                String ticketNumber = mNum.getText().toString();
                String price = mPrice.getText().toString();
                String tip = mTip.getText().toString();
                String date = getDate();

                if (ticketNumber.isEmpty() || price.isEmpty() || date.isEmpty()) return false;
                if (tip.isEmpty()) {
                    tip = "0";
                }

                /* need to test for existing ticket number or auto add a number */
                if (mDeliveryDepot.ticketNumExistsInList(Integer.valueOf(ticketNumber, 10))) {
                    Toast.makeText(getActivity(), "Ticket number : " + ticketNumber + " already exists.", Toast.LENGTH_SHORT).show();
                    return false;
                }

                delivery.setDate(date);
                delivery.setTicketNumber(Integer.valueOf(ticketNumber, 10));
                delivery.setPrice(Integer.valueOf(price, 10));
                delivery.setTip(Integer.valueOf(tip, 10));
                delivery.setLocal(mLocal.isChecked());

                mDeliveryDepot.addDateToDb(date);
                buildDateSpinner(null);
                updateDateSpinner(date);
                mDeliveryDepot.incrementTotalSales(delivery.getPrice());
                mDeliveryDepot.incrementTotalTips(delivery.getTip());
                mDeliveryDepot.addNewDeliveryToDb(delivery, null);

                clearNewDeliveryEditTexts();

                /* need to close soft keyboard after delivery added */
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

                updateUI();
            }
            return false;
        }
    };
    /** listener for RecyclerView to hide soft keyboard */
    private final RecyclerView.OnTouchListener recyclerTouchListener = new RecyclerView.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                Log.d("recycler touched", "yeah");
            }
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
            return false;
        }
    };

    /** Required empty public constructor **/
    public DeliveriesFragment() {
    }

    /** create the view for the whole list **/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        /* get the DeliveryDepot instance. Should be first call to it */
        mDeliveryDepot = DeliveryDepot.get(getActivity());
        mDeliveriesFragment = this;

        /* Inflate the layout for this fragment */
        View view = inflater.inflate(R.layout.fragment_deliveries, container, false);
        /* Cache some variables */
        mPriceTotals = (TextView) view.findViewById(R.id.delivery_list_item_price_total);
        mTipsTotal = (TextView) view.findViewById(R.id.delivery_list_item_tip_total);
        mWage = (TextView) view.findViewById(R.id.delivery_list_item_wage_total);
        mShop = (TextView) view.findViewById(R.id.delivery_list_item_shop_total);
        mNum = (EditText) view.findViewById(R.id.delivery_list_new_num);
        mPrice = (EditText) view.findViewById(R.id.delivery_list_new_price);
        mTip = (EditText) view.findViewById(R.id.delivery_list_new_tip);
        mLocal = (CheckBox) view.findViewById(R.id.delivery_list_item_local_checkbox);

        /* add watcher to ticket number field */
        mNum.addTextChangedListener(ticketNumWatcher);

        buildAddButton(view);

        mDateSpinner = (Spinner) view.findViewById(R.id.spinDeliveryDate);
        mDateSpinner.setOnItemSelectedListener(dateSpinnerSelectedListener);
        buildDateSpinner(view);
        mCurrentDate = getDate();

        buildDeliveriesRecycler(view);

        updateUI();
        view.requestFocus();
        return view;
    }
    /** edit the item that is touched */
    @Override
    public void onLongClick(View view, RecyclerView.ViewHolder viewHolder) {

        Log.d("item long clicked", String.valueOf(viewHolder.getLayoutPosition()));
        DialogFragment itemEditDialog = new ItemEditDialog();

        Bundle args = new Bundle();
        args.putString("ticketNum", String.valueOf(mDeliveryDepot.getDeliveryByPosition(viewHolder.getLayoutPosition()).getTicketNumber()));
        args.putString("Price", String.valueOf(mDeliveryDepot.getDeliveryByPosition(viewHolder.getLayoutPosition()).getPrice()));
        args.putString("Tip", String.valueOf(mDeliveryDepot.getDeliveryByPosition(viewHolder.getLayoutPosition()).getTip()));
        args.putBoolean("Local", mDeliveryDepot.getDeliveryByPosition(viewHolder.getLayoutPosition()).isLocal());
        itemEditDialog.setArguments(args);

        itemEditDialog.setTargetFragment(mDeliveriesFragment, EDIT_UPDATE_RESULT_CODE);
        itemEditDialog.show(getFragmentManager(), "ItemEdit");

    }
    /** sets the image button up */
    private void buildAddButton(View view) {

        mImageButton = (ImageButton) view.findViewById(R.id.delivery_list_item_add);
        mImageButton.setOnTouchListener(addButtonTouchListener);
    }

    /** sets up the spinner and populates it with the date info from the database **/
    public void buildDateSpinner(@Nullable View view) {

        if (view == null) {
            view = getView();
        }

        try {
            if (view != null) {
                mDateSpinner = (Spinner) view.findViewById(R.id.spinDeliveryDate);
            } else {
                return;
            }
            /** get the list of dates from the depot and add them to the spinner **/
            mDeliveryDepot.buildDatesListFromDb(null);
            ArrayList<String> mList = new ArrayList<>();
            mList.addAll(mDeliveryDepot.getDates());
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.date_spinner_item, mList);
            /** Specify the layout to use when the list of choices appears **/
            adapter.setDropDownViewResource(R.layout.date_spinner_item);

            /** Apply the adapter to the spinner **/
            mDateSpinner.setAdapter(adapter);

        } catch (NullPointerException err) {
            Log.e("buildDateSpinner", err.toString());
        }
    }

    /** updates spinner after a date has been added */
    public void updateDateSpinner(String date) {

        @SuppressWarnings("unchecked")
        ArrayAdapter<String> adapter = (ArrayAdapter<String>)mDateSpinner.getAdapter();

        int i = adapter.getPosition(date);
        mDateSpinner.setSelection(i);
    }
    /** sets up the recycler view for the deliveries list **/
    private void buildDeliveriesRecycler(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        /* improves performance if changes in content do not change layout size of RecyclerView */
        mRecyclerView.setHasFixedSize(true);
        /* use a linear layout manager */
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        /* set the touch listener. See top of class */
        mRecyclerView.setOnTouchListener(recyclerTouchListener);
    }
    /** updates the list on every onCreateView call **/
    private void updateUI() {

        DeliveriesAdapter mAdapter = new DeliveriesAdapter(getActivity().getApplicationContext(), this);
        mRecyclerView.setAdapter(mAdapter);

        mPriceTotals.setText(getString(R.string.totalPrice, mDeliveryDepot.getTotalSales()));
        mTipsTotal.setText(getString(R.string.totalTip, mDeliveryDepot.getTotalTips()));

        double totalWage = 40 + (mDeliveryDepot.getDistanceDeliveries() * 1.5) + mDeliveryDepot.getLocalDeliveries() + mDeliveryDepot.getTotalTips();
        mWage.setText(getString(R.string.totalWage, totalWage));

        double totalShop = mDeliveryDepot.getTotalSales() - totalWage;
        mShop.setText(getString(R.string.totalShop, totalShop));
    }

    /** clear new delivery EditTexts */
    private void clearNewDeliveryEditTexts(){
        mNum.setText("");
        mPrice.setText("");
        mTip.setText("");
        mLocal.setChecked(false);
    }

    /** gets the current visible date from the date spinner */
    private String getDate() {

        String date = mDateSpinner.getSelectedItem().toString();//.getItemAtPosition(0).toString();
        if (date.isEmpty()) {
            return "";
        } else if (date.toLowerCase().equals("today")) {
            Calendar cDate = Calendar.getInstance();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            date = formatter.format(cDate.getTime());
        }
        return date;
    }

    /** the callback from the date picker */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == DATE_PICKED_RESULT_CODE) {
            mDeliveryDepot.addDateToDb(data.getStringExtra(RETURN_DATE));
            buildDateSpinner(getView());
            updateDateSpinner(data.getStringExtra(RETURN_DATE));
        }
        if (resultCode == EDIT_UPDATE_RESULT_CODE) {
            mDeliveryDepot.updateDeliveryInDb(data, null);
        }
    }



}
