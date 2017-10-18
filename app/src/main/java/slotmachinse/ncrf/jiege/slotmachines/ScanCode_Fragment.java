package slotmachinse.ncrf.jiege.slotmachines;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zbar.lib.http.httpApi;


public class ScanCode_Fragment extends Fragment {
	private TextView mTv_text;
	private ProgressBar mProgress_aliPlay;
	private String mText = "扫描成功，等待支付...";
	public ScanCode_Fragment(){
		super();
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.zhifusend_layout, group, false);
		mTv_text = (TextView) view.findViewById(R.id.tv_text);
		mProgress_aliPlay = (ProgressBar) view.findViewById(R.id.progress_aliPlay);
		Bundle bundle = getArguments();
		if (bundle != null){
			mText = bundle.getString(httpApi.EXTRA_DATA_ZHIFUBAO_FRAGMNET);
		}
		mTv_text.setText(mText);
		return view;
	}

	public void showProgress(){
		mProgress_aliPlay.setVisibility(View.VISIBLE);
	}

}
