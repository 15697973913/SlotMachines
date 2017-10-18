package view;



import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;


/**
 * Created by Administrator on 2017062017/6/12 0012下午 4:03.
 * sub: 重写VideoView 获取屏幕的高度和宽度，适应播放效果
 */

public class myVideoview extends VideoView {
    public myVideoview(Context context) {
        super(context);
    }

    public myVideoview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public myVideoview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(getWidth(), widthMeasureSpec);
        int height = getDefaultSize(getHeight(), heightMeasureSpec);
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }
}
