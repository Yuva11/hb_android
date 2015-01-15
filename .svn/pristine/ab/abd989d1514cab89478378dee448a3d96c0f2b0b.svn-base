package com.HungryBells.dialog;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.HungryBells.activity.LoggingInActivity;
import com.HungryBells.activity.OrderSummaryActivity;
import com.HungryBells.activity.R;
import com.HungryBells.activity.SignupActivity;
import com.HungryBells.activity.UserActivity;
import com.HungryBells.util.Util;

public class ForgotPasswordDialog extends Dialog implements
        View.OnClickListener {

    UserActivity dialogContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_forgotpassword);
        setCanceledOnTouchOutside(false);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        ((Button) findViewById(R.id.buttondok))
                .setOnClickListener(this);
        ((Button) findViewById(R.id.buttondcancel))
                .setOnClickListener(this);
    }

    public ForgotPasswordDialog(UserActivity context) {
        super(context);
        dialogContext = context;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttondok:
                String editTextComments = ((EditText) findViewById(R.id.editTextdeliveryaddress))
                        .getText().toString();
                if (editTextComments != null && editTextComments.length() == 0) {
                    Util.customToast(dialogContext, dialogContext.getString(R.string.emailempty));
                } else if (!Util.isValidEmailAddress(editTextComments)) {
                    Util.customToast(dialogContext, dialogContext.getString(R.string.emailvalid));
                } else {
                    Log.e("Context", dialogContext.getLocalClassName());
                    ((LoggingInActivity) dialogContext)
                            .submitMailId(editTextComments);

                    dismiss();
                }
                break;
            default:
                dismiss();
                break;
        }
    }
}