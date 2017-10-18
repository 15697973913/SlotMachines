package dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import slotmachinse.ncrf.jiege.slotmachines.R;


/**
 * Created by Administrator on 2017042017/4/19 0019上午 10:53.
 * sub:
 */

public class LoadingDialog extends Dialog {
    private TextView tv;
    private ProgressBar progressBar;
    private String mTvStr = "";
    private Context mContext;



    public LoadingDialog(Context context, String tvStr) {
        super(context, R.style.loadingDialogStyle);
        mContext = context;
        mTvStr = tvStr;
    }


    private LoadingDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading);
        tv = (TextView) this.findViewById(R.id.tv);
        tv.setText(mTvStr);
        progressBar = (ProgressBar) this.findViewById(R.id.progressBar1);
        LinearLayout linearLayout = (LinearLayout) this.findViewById(R.id.LinearLayout);
        linearLayout.getBackground().setAlpha(210);
    }

}
