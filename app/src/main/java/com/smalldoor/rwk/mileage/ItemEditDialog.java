package com.smalldoor.rwk.mileage;


import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import static com.smalldoor.rwk.mileage.DeliveriesFragment.EDIT_UPDATE_RESULT_CODE;
import static com.smalldoor.rwk.mileage.DeliveriesFragment.RETURN_LOCAL;
import static com.smalldoor.rwk.mileage.DeliveriesFragment.RETURN_NUM;
import static com.smalldoor.rwk.mileage.DeliveriesFragment.RETURN_PRICE;
import static com.smalldoor.rwk.mileage.DeliveriesFragment.RETURN_TIP;

public class ItemEditDialog extends DialogFragment {

    public Button mCancel;
    Button mUpdate;
    EditText mTicketNum;
    EditText mPrice;
    EditText mTip;
    CheckBox mLocal;

    public ItemEditDialog() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_item_edit_dialog, container, false);
        mUpdate = (Button) view.findViewById(R.id.dialog_update);
        mCancel = (Button) view.findViewById(R.id.dialog_cancel);

        Bundle args = getArguments();
        String ticketNum = args.getString("ticketNum");
        String price = args.getString("Price");
        String tip = args.getString("Tip");
        boolean local = args.getBoolean("Local");

        mTicketNum = (EditText) view.findViewById(R.id.delivery_list_new_num);
        mPrice = (EditText) view.findViewById(R.id.delivery_list_new_price);
        mTip = (EditText) view.findViewById(R.id.delivery_list_new_tip);
        mLocal = (CheckBox) view.findViewById(R.id.delivery_list_item_local_checkbox);

        mTicketNum.setText(ticketNum);
        mPrice.setText(price);
        mTip.setText(tip);
        mLocal.setChecked(local);

        mUpdate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                Log.d("Update", "clicked");

                /* return the date to DeliveriesFragment */
                Intent intent = new Intent();
                intent.putExtra(RETURN_NUM, mTicketNum.getText().toString());
                intent.putExtra(RETURN_PRICE, mPrice.getText().toString());
                intent.putExtra(RETURN_TIP, mTip.getText().toString());
                intent.putExtra(RETURN_LOCAL, mLocal.isChecked());
                getTargetFragment().onActivityResult(getTargetRequestCode(), EDIT_UPDATE_RESULT_CODE, intent);

                ItemEditDialog.this.dismiss();
                return false;
            }
        });

        mCancel.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                ItemEditDialog.this.dismiss();
                return false;
            }
        });

        return view;
    }
}
