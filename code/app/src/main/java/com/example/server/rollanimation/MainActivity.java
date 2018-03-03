package com.example.server.rollanimation;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.rect_1)
    LinearLayout rect_1;
    @BindView(R.id.rect_2)
    LinearLayout rect_2;
    @BindView(R.id.rect_3)
    LinearLayout rect_3;
    @BindView(R.id.rect_4)
    LinearLayout rect_4;
    @BindView(R.id.rect_5)
    LinearLayout rect_5;
    @BindView(R.id.rect_6)
    LinearLayout rect_6;
    @BindView(R.id.rect_7)
    LinearLayout rect_7;
    @BindView(R.id.rect_8)
    LinearLayout rect_8;

    @BindView(R.id.rect_center)
    LinearLayout rect_center;

    @BindView(R.id.editText)
    EditText editText;

    Context mContext;
    List<LinearLayout> viewList;

    LinearLayout currentView;

    private Timer mTimer;
    private int iNowCount = 0;
    private boolean isRolling = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lucky_roll);
        ButterKnife.bind(this);
        mContext = this;

        viewList = new ArrayList<>();
        initList();
        mTimer = new Timer();


    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // cancel timer
        mTimer.cancel();
    }

    //初始化list，里面放着所有的要roll的view
    private void initList () {
        viewList.add(rect_1);
        viewList.add(rect_2);
        viewList.add(rect_3);
        viewList.add(rect_4);
        viewList.add(rect_5);
        viewList.add(rect_6);
        viewList.add(rect_7);
        viewList.add(rect_8);
    }

    //设置所有的view为初始状态
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void resetListDisplay () {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (LinearLayout view : viewList) {
                    view.setBackground(getDrawable(R.drawable.item_rect));
                }
            }
        });
    }

    //开始转 判断输入是否合法 可以用来检测返回结果
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @OnClick(R.id.rect_center)
    public void startRoll() {
        String strResult = editText.getText().toString();
        if (strResult.length() <= 0) {
            Toast.makeText(mContext,"请输入要结束的位置",Toast.LENGTH_LONG).show();
            return;
        }
        int iResult = Integer.parseInt(strResult);
        if (iResult > 8 || iResult < 0){
            Toast.makeText(mContext,"结束位置不合法",Toast.LENGTH_LONG).show();
            return;
        }
        Toast.makeText(mContext,"开始roll",Toast.LENGTH_LONG).show();
        if (isRolling) {
            changeSpeed(iResult);
        } else {
            isRolling = true;
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    roll();
                }
            },0,100);
        }
    }

    //转，按位置转
    private void roll () {
        if (currentView == null) {
            currentView = viewList.get(0);
        } else if (currentView != viewList.get(viewList.size()-1)){
            currentView = viewList.get(viewList.indexOf(currentView) + 1);
        } else {
            currentView = viewList.get(0);
        }
        iNowCount = viewList.indexOf(currentView);
        resetListDisplay();
        currentView.post(new Runnable() {
            @Override
            public void run() {
                currentView.setBackground(getDrawable(R.drawable.item_selected));
            }
        });
    }
    //有返回结果时，从当前位置转到结果的位置
    private void rollTo (int iStart, int iStop) {
        if (currentView == null) {
            currentView = viewList.get(iStart);
        } else if (currentView != viewList.get(viewList.size()-1)){
            if (currentView.equals(viewList.get(iStop-1))) {
                mTimer.cancel();
                isRolling = false;
            } else {
                currentView = viewList.get(viewList.indexOf(currentView) + 1);
            }
        } else {
            currentView = viewList.get(0);
        }
        resetListDisplay();
        currentView.post(new Runnable() {
            @Override
            public void run() {
                currentView.setBackground(getDrawable(R.drawable.item_selected));
            }
        });
    }
    //出了结果后，执行rollTo方法，重新执行timer以控制速度
    private void changeSpeed(final int iStop){
        mTimer.cancel();
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                rollTo(iNowCount,iStop);
            }
        },0,300);
    }
}
