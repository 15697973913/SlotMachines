package dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.widget.TextView;

import slotmachinse.ncrf.jiege.slotmachines.R;


/**
 * Created by Administrator on 2017062017/6/23 0023下午 1:53.
 * sub: 司机刷卡提示的dialog信息
 */

public class Card_dialog extends Dialog {
    private TextView tv ;
    private String text;
    private Context mContext;

    public Card_dialog(@NonNull Context context, String tv) {
        super(context);
        this.mContext = context;
        this.text = tv;
    }

    public Card_dialog(@NonNull Context context, @StyleRes int theme) {
        super(context, theme);
    }

    protected Card_dialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.dialog_card_succesed);
        tv = (TextView) this.findViewById(R.id.dialog_success_name);
        tv.setText(mContext.getString(R.string.dialog_card_tip_name,text));
    }
}
