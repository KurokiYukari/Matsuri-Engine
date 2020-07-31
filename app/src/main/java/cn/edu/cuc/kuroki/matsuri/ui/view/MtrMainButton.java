package cn.edu.cuc.kuroki.matsuri.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import cn.edu.cuc.kuroki.matsuri.R;
import cn.edu.cuc.kuroki.matsuri.utils.MainUtils;

/**
 * 一个自定义的 Button，在 Matsuri 中主要用于 Title 界面和 Config 界面的各种按钮
 * 提供两个可设置的 Text，要使用在 layout xml 中设置 xmlns:app="http://schemas.android.com/apk/res-auto"
 *      app:mainText 主 Text
 *      app:subText 在 OnTouch ACTION_DOWN 时会显示出的 Text
 * ps: UI 样式设计灵感来自于 「素晴らしき日々　HD」 的按钮 UI。
 */
public class MtrMainButton extends ConstraintLayout {
    private OnClickListener onClickListener;

    // views
    private ConstraintLayout constraintLayout;
    private TextView main_textView;
    private TextView sub_TextView;

    public MtrMainButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setClickable(true);
        setFocusable(true);

        View view = inflate(getContext(), R.layout.mtr_main_button, this);

        constraintLayout = view.findViewById(R.id.mtrMainBtn_constraintLayout);
        main_textView = view.findViewById(R.id.mtrMainBtn_main_textView);
        sub_TextView = view.findViewById(R.id.mtrMainBtn_sub_textView);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.mtrMainButton);
        String mainText = ta.getString(R.styleable.mtrMainButton_mainText);
        String subText = ta.getString(R.styleable.mtrMainButton_subText);
        ta.recycle();

        main_textView.setText(mainText);
        sub_TextView.setText(subText);
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        onClickListener = l;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                MainUtils.performBackgroundTransition(MainUtils.getContext(), R.drawable.mtr_main_btn_normal, R.drawable.mtr_main_btn_pressed, constraintLayout,600);
                sub_TextView.setVisibility(View.VISIBLE);
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                MainUtils.performBackgroundTransition(MainUtils.getContext(), R.drawable.mtr_main_btn_pressed, R.drawable.mtr_main_btn_normal, constraintLayout, 600);
                sub_TextView.setVisibility(View.INVISIBLE);
                if (x + getLeft() < getRight() && y + getTop() < getBottom()) {
                    if (onClickListener != null) {
                        onClickListener.onClick(this);
                    }
                }
                break;
        }
        return true;
    }
}
