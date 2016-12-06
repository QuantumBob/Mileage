package layout;

import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import com.smalldoor.rwk.mileage.DeliveryDepot;
import com.smalldoor.rwk.mileage.DeliveryDetail;
import com.smalldoor.rwk.mileage.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment to show the deliveries screen
 */
public class DeliveriesFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private RecyclerView mRecyclerView;
    private Spinner mDateSpinner;
    private TextView mPriceTotals;
    private TextView mTipsTotal;
    private TextView mWage;
    private TextView mShop;
    private DeliveryDepot mDeliveryDepot;


    /** Required empty public constructor **/
    public DeliveriesFragment() {

    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // An item was selected. You can retrieve the selected item using parent.getItemAtPosition(pos)
        if (parent.getItemAtPosition(pos).toString().toLowerCase().equals("pick")) {
            DialogFragment newFragment = new DatePickerDeliveriesFragment();
            newFragment.show(getFragmentManager(), "datePicker");
        } else {
            mDeliveryDepot.setDeliveries(parent.getItemAtPosition(pos).toString());
        }
        updateUI();
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
    /** create the view for the whole list **/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        /** get the DeliveryDepot instance **/
        mDeliveryDepot = DeliveryDepot.get(getActivity());
        /** Inflate the layout for this fragment **/
        View view = inflater.inflate(R.layout.fragment_deliveries, container, false);
        /** Cache some variables **/
        mPriceTotals = (TextView) view.findViewById(R.id.delivery_list_item_price_total);
        mTipsTotal = (TextView) view.findViewById(R.id.delivery_list_item_tip_total);
        mWage = (TextView) view.findViewById(R.id.delivery_list_item_wage_total);
        mShop = (TextView) view.findViewById(R.id.delivery_list_item_shop_total);


        mDateSpinner = (Spinner) view.findViewById(R.id.spinDeliveryDate);
        mDateSpinner.setOnItemSelectedListener(this);
        buildDateSpinner(view);
        buildDeliveriesRecycler(view);
        /** link DateView to DatePickerDialog **/
//        TextView dateView = (TextView) view.findViewById(R.id.editDeliveryDate);
//        dateView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event){
//                if(MotionEvent.ACTION_UP == event.getAction()){
//                    DialogFragment newFragment = new DatePickerDeliveriesFragment();
//                    newFragment.show(getFragmentManager(), "datePicker");
//                }
//                return false;
//            }
//        });

        view.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent){
                InputMethodManager inputMethodManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return false;
            }
        });

        updateUI();
        view.requestFocus();
        return view;
    }
    /** sets up the spinner and populates it with the date info from the database **/
    private void buildDateSpinner(View view){

        mDateSpinner = (Spinner) view.findViewById(R.id.spinDeliveryDate);
        /** get the list of dates from the depot and add them to the spinner **/
        ArrayList<String> mList = new ArrayList<>();
        mList.add("Today");
        mList.add("Pick");
        mList.addAll(mDeliveryDepot.getDates());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.date_spinner_item, mList);
        /** Specify the layout to use when the list of choices appears **/
        adapter.setDropDownViewResource(R.layout.date_spinner_item);
        /** Apply the adapter to the spinner **/
        mDateSpinner.setAdapter(adapter);
    }
    /** sets up the recycler view for the deliveries list **/
    private void buildDeliveriesRecycler(View view){
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
//            @Override
//            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
//                return true;
//            }
//
//            @Override
//            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
//                Log.d("recycler touched", "yeah");
//                InputMethodManager inputMethodManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//                inputMethodManager.hideSoftInputFromWindow(rv.getWindowToken(), 0);
//            }
//
//            @Override
//            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
//
//            }
//        });
        mRecyclerView.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent){
                if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    Log.d("recycler touched", "yeah");
                }
                InputMethodManager inputMethodManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return false;
            }
        });
    }
    /** updates the list on every onCreateView call **/
    private void updateUI() {

        DeliveriesAdapter mAdapter = new DeliveriesAdapter(mDeliveryDepot.getDeliveries());
        mRecyclerView.setAdapter(mAdapter);

        mPriceTotals.setText(getString(R.string.totalPrice, mDeliveryDepot.getTotalSales()));
        mTipsTotal.setText(getString(R.string.totalTip, mDeliveryDepot.getTotalTips()));

        double totalWage = 40 + (mDeliveryDepot.getDistanceDeliveries() * 1.5) + mDeliveryDepot.getLocalDeliveries() + mDeliveryDepot.getTotalTips();
        mWage.setText(getString(R.string.totalWage, totalWage));

        double totalShop = mDeliveryDepot.getTotalSales() - totalWage;
        mShop.setText(getString(R.string.totalShop, totalShop));
    }
    /** the holder for one item in the list **/
    private class DeliveryHolder extends RecyclerView.ViewHolder {

        private DeliveryDetail mDelivery;
        private TextView mTicketNumView;
        private TextView mPriceTextView;
        private TextView mTipTextView;
        private CheckBox mLocal;

        DeliveryHolder (View itemView) {
            super(itemView);
            mTicketNumView = (TextView) itemView.findViewById(R.id.delivery_list_item_num_view);
            mPriceTextView = (TextView) itemView.findViewById(R.id.delivery_list_item_price_text_view);
            mTipTextView = (TextView) itemView.findViewById(R.id.delivery_list_item_tip_text_view);
            mLocal = (CheckBox) itemView.findViewById(R.id.delivery_list_item_local_checkbox);
        }

        void bindDelivery(DeliveryDetail delivery){

            mDelivery = delivery;
            mTicketNumView.setText(getString(R.string.TicketNumText, mDelivery.getTicketNumber()));
            mPriceTextView.setText(getString(R.string.PriceText, mDelivery.getPrice()));
            mTipTextView.setText(getString(R.string.TipText, mDelivery.getTip()));
            mLocal.setChecked(mDelivery.isLocal());

        }
    }
    /** the adapter that passes the item holder to the view **/
    private class DeliveriesAdapter extends RecyclerView.Adapter<DeliveryHolder>{

        private List<DeliveryDetail> mDeliveries;

        /** Provide a suitable constructor (depends on the kind of data in the list) **/
        DeliveriesAdapter(List<DeliveryDetail> deliveries) {

            mDeliveries = deliveries;
        }
        /** Create new views (invoked by the layout manager) **/
        @Override
        public DeliveryHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.delivery_list_item, parent, false);

            return new DeliveryHolder(view);
        }
        /** Replace the contents of a view (invoked by the layout manager) **/
        @Override
        public void onBindViewHolder(DeliveryHolder holder, int position) {

            /** get element from your list at this position, replace the contents of the view with that element **/
            DeliveryDetail delivery = mDeliveries.get(position);
            holder.bindDelivery(delivery);

        }
        /** Return the size of your list (invoked by the layout manager) **/
        @Override
        public int getItemCount() {

            return mDeliveries.size();
        }
    }
}
