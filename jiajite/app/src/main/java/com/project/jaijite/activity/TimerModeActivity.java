package com.project.jaijite.activity;

import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.project.jaijite.R;
import com.project.jaijite.base.BaseTitleActivity;
import com.project.jaijite.bean.Task;
import com.project.jaijite.dialog.TipsDialog;
import com.project.jaijite.entity.LightInfo;
import com.project.jaijite.greendao.db.LightingDB;
import com.project.jaijite.gui.SwitchButtonView;
import com.project.jaijite.service.MainService;
import com.project.jaijite.util.DataUtil;
import com.project.jaijite.util.ScreenUtils;
import com.project.jaijite.util.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;

public class TimerModeActivity extends BaseTitleActivity implements View.OnClickListener {
    public static final String PARAM = "param";
    private LightInfo lightInfo;
    @BindView(R.id.tvSleepTime)
    TextView tvSleepTime;
    @BindView(R.id.tvTimerTimeOpen)
    TextView tvTimerTimeOpen;
    @BindView(R.id.tvTimerTimeClose)
    TextView tvTimerTimeClose;
    @BindView(R.id.sbDelay)
    SwitchButtonView sbDelay;
    @BindView(R.id.sbTimeOn)
    SwitchButtonView sbTimeOn;
    @BindView(R.id.sbTimeOff)
    SwitchButtonView sbTimeOff;

    @Override
    public int getLayoutId() {
        return R.layout.activity_timer_mode;
    }

    @Override
    public void initView() {
        lightInfo = (LightInfo) getIntent().getSerializableExtra(PARAM);
        setTvTitle("定时模式");
        setTitleLeft(lightInfo.getName());
        sbDelay.setOnClickListener(this);
        sbTimeOn.setOnClickListener(this);
        sbTimeOff.setOnClickListener(this);
        updateUI();
    }

    @OnClick(R.id.btLeft)
    void close() {
        finish();
    }

    @OnClick({R.id.tvSleepTime, R.id.tvTimerTimeOpen, R.id.tvTimerTimeClose})
    void showDialog(final View mView) {
        TipsDialog.getInstance()
                .createDialog(this, R.layout.dialog_set_time)
                .bindClick(R.id.leftButton, null)
                .bindClick(R.id.rightButton, new TipsDialog.TipClickListener() {
                    @Override
                    public void onClick(View v, TipsDialog dialog) {
                        NumberPicker mH = dialog.getView(R.id.npHours);
                        NumberPicker mM = dialog.getView(R.id.npMinute);
                        mH.setMaxValue(23);
                        mH.setMinValue(0);
                        mH.setWrapSelectorWheel(false);
                        mM.setMaxValue(59);
                        mM.setMinValue(0);
                        mM.setWrapSelectorWheel(false);

                        String hourStr = String.valueOf(mH.getValue());
                        String minuteStr = String.valueOf(mM.getValue());
                        if ("".equals(hourStr) && mH.getVisibility() == View.VISIBLE) {
                            ToastUtils.showShortSafe("定时小时不能为空");
                            return;
                        }
                        int h = Integer.valueOf(hourStr);
                        if (h > 23) {
                            ToastUtils.showShortSafe("不能大于23时");
                            return;
                        }
                        int m = Integer.valueOf(minuteStr);
                        if (m > 60) {
                            ToastUtils.showShortSafe("不能大于60分钟");
                            return;
                        }
                        if (h == 0 && m == 0) {
                            ToastUtils.showShortSafe("时间不能定为0");
                            return;
                        }
                        String hours = (h < 10 ? "0" : "") + hourStr;
                        String minutes = (m < 10 ? "0" : "") + minuteStr;
                        switch (mView.getId()) {
                            case R.id.tvSleepTime:
                                lightInfo.setDelayOffSwitch(false);
                                lightInfo.setDelay(minutes);
                                break;
                            case R.id.tvTimerTimeOpen:
                                lightInfo.setTimeOnSwitch(false);
                                lightInfo.setTime_on(hours.concat(":").concat(minutes));
                                break;
                            case R.id.tvTimerTimeClose:
                                lightInfo.setTimeOffSwitch(false);
                                lightInfo.setTime_off(hours.concat(":").concat(minutes));
                                break;
                        }
                        ScreenUtils.hideInput(TimerModeActivity.this);
                        LightingDB.updateLight(lightInfo);
                        updateUI();
                    }
                })
                .setVisibility(R.id.npHours, mView.getId() == R.id.tvSleepTime ? View.GONE : View.VISIBLE)
                .setVisibility(R.id.tvHour, mView.getId() == R.id.tvSleepTime ? View.GONE : View.VISIBLE)
                .show(new TipsDialog.ViewController() {
                    @Override
                    public void view(TipsDialog dialog) {
                        NumberPicker mH = dialog.getView(R.id.npHours);
                        NumberPicker mM = dialog.getView(R.id.npMinute);
                        mH.setMaxValue(23);
                        mH.setMinValue(0);
                        mM.setMaxValue(59);
                        mM.setMinValue(0);

                        mM.setWrapSelectorWheel(true);
                        mH.setWrapSelectorWheel(true);

                        String to = lightInfo.getTime_on();
                        String tf = lightInfo.getTime_off();
                        String dl = lightInfo.getDelay();
                        switch (mView.getId()) {
                            case R.id.tvSleepTime:
                                mM.setValue(Integer.valueOf(dl));
                                break;
                            case R.id.tvTimerTimeOpen:
                                String[] split = to.split(":");
                                mH.setValue(Integer.valueOf(split[0]));
                                mM.setValue(Integer.valueOf(split[1]));
                                break;
                            case R.id.tvTimerTimeClose:
                                split = tf.split(":");
                                mH.setValue(Integer.valueOf(split[0]));
                                mM.setValue(Integer.valueOf(split[1]));
                                break;
                        }

                    }
                });
    }

    private void updateUI() {
        if (lightInfo == null) return;
        tvSleepTime.setText(String.format("%s分钟后关灯", lightInfo.getDelay()));
        tvTimerTimeOpen.setText(String.format("%s分开灯", lightInfo.getTime_on().replace(":", "时")));
        tvTimerTimeClose.setText(String.format("%s分关灯", lightInfo.getTime_off().replace(":", "时")));
        sbDelay.setOpened(lightInfo.getDelayOffSwitch());
        sbTimeOff.setOpened(lightInfo.getTimeOffSwitch());
        sbTimeOn.setOpened(lightInfo.getTimeOnSwitch());
    }

    private void setCurrentTime(final SwitchButtonView sb, final boolean check) {
        Task task = new Task();
        task.setOnlyAttribute(true);
        task.setAttribute("TIME:".concat(DataUtil.getCurrentTime()));
        task.startCommand(new MainService.TaskListener() {
            @Override
            public void taskCallback(Object... obj) {
                timerLightController(sb, check);
            }

            @Override
            public void taskFailed(String msg) {
                ToastUtils.showShort("发送指令失败：02");
                sb.setOpened(false);
            }
        });
    }

    @Override
    public void onClick(View v) {
        SwitchButtonView view = (SwitchButtonView) v;
        ScreenUtils.shake();
        switch (view.getId()) {
            case R.id.sbDelay:
                startDelayCommand(view, view.isOpened());
                break;
            case R.id.sbTimeOn:
                Integer integer = Integer.valueOf(lightInfo.getTime_on().replace(":",""));
                if (integer <= 0) {
                    ToastUtils.showShortSafe("时间不能定为00:00");
                    view.setOpened(false);
                    return;
                }
                setCurrentTime(view, view.isOpened());
                break;
            case R.id.sbTimeOff:
                integer = Integer.valueOf(lightInfo.getTime_off().replace(":",""));
                if (integer <= 0) {
                    ToastUtils.showShortSafe("时间不能定为00:00");
                    view.setOpened(false);
                    return;
                }
                setCurrentTime(view, view.isOpened());
                break;
        }
    }


    private void startDelayCommand(final SwitchButtonView view, final boolean isChecked) {
        Integer integer = Integer.valueOf(lightInfo.getDelay());
        if (integer <= 0) {
            ToastUtils.showShortSafe("时间不能定为0");
            view.setOpened(false);
            return;
        }
        Task task = new Task();
        task.setLedID(lightInfo.getLedId());
        task.setGroupID(lightInfo.getGroupId());
        task.setFunction(Task.LIGHT_DELAY);
        task.setAttribute(integer);
        lightInfo.setDelayOffSwitch(isChecked);
        if (!isChecked) {
            task.setAttribute(0);
            task.setAttribute("");
        }
        task.startCommand(new MainService.TaskListener() {
            @Override
            public void taskCallback(Object... obj) {
                ToastUtils.showLongSafe(obj.toString());
                view.setOpened(true);
                LightingDB.updateLight(lightInfo);
            }

            @Override
            public void taskFailed(String msg) {
                ToastUtils.showShort("发送指令失败：01");
                view.setOpened(false);
            }
        });
    }

    public void timerLightController(final SwitchButtonView view, boolean isChecked) {
        Task task = new Task();
        task.setLedID(lightInfo.getLedId());
        task.setGroupID(lightInfo.getGroupId());
        switch (view.getId()) {
            case R.id.sbDelay:
                task.setFunction(Task.LIGHT_DELAY);
                task.setAttribute(Integer.valueOf(lightInfo.getDelay()));
                lightInfo.setDelayOffSwitch(isChecked);
                break;
            case R.id.sbTimeOn:
                task.setFunction(Task.LIGHT_TIMER_OPEN);
                task.setAttribute(lightInfo.getTime_on().replace(":", "").concat("00"));
                lightInfo.setTimeOnSwitch(isChecked);
                break;
            case R.id.sbTimeOff:
                task.setFunction(Task.LIGHT_TIMER_CLOSE);
                task.setAttribute(lightInfo.getTime_off().replace(":", "").concat("00"));
                lightInfo.setTimeOffSwitch(isChecked);
                break;
        }
        if (!isChecked) {
            task.setAttribute("");
            task.setAttribute(0);
        }
        task.startCommand(new MainService.TaskListener() {
            @Override
            public void taskCallback(Object... obj) {
                ToastUtils.showLongSafe(obj.toString());
                LightingDB.updateLight(lightInfo);
                //为了保证在发送关闭指令的时候 发送失败，这里哪怕发送成功也不勾选 保持一致
                //如果发送关闭指令成功了 ，那么这里的勾选状态保持一致
//                view.setChecked(view.isChecked());
            }

            @Override
            public void taskFailed(String msg) {
                view.setOpened(false);
                ToastUtils.showShort("发送指令失败：03");
            }
        });
    }

}
