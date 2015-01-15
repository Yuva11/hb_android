package com.HungryBells.dialog;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RatingBar;

import com.HungryBells.DTO.DealSummaryDto;
import com.HungryBells.activity.MyOrdersActivity;
import com.HungryBells.activity.R;
import com.HungryBells.activity.UserActivity;
import com.HungryBells.util.OrderDetailsDTO;

public class RatingsDialog extends Dialog implements
        View.OnClickListener {

    UserActivity dialogContext;
    DealSummaryDto summaryDto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_ratings);
        setCanceledOnTouchOutside(false);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        ((Button) findViewById(R.id.buttonfeedbacksubmit))
                .setOnClickListener(this);
    }

    public RatingsDialog(UserActivity context,DealSummaryDto summaryDto) {
        super(context);
        dialogContext = context;
        this.summaryDto = summaryDto;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonfeedbacksubmit:
                OrderDetailsDTO orderDetails =  summaryDto.getDOrders().getOrderDetails().get(0);
                RatingBar ratingBar = (RatingBar)findViewById(R.id.ratingBar);
                int rate =   (int)ratingBar.getRating();
                if(ratingBar.getRating()!=0.0){
                    orderDetails.setRating(rate);
                    ((MyOrdersActivity)dialogContext).getDeals(orderDetails);}
                dismiss();
                break;
            default:
                break;
        }
    }
}