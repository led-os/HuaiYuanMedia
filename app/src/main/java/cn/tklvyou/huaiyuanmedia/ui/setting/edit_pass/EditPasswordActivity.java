package cn.tklvyou.huaiyuanmedia.ui.setting.edit_pass;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;

import cn.tklvyou.huaiyuanmedia.R;
import cn.tklvyou.huaiyuanmedia.base.activity.BaseActivity;
import cn.tklvyou.huaiyuanmedia.helper.AccountHelper;
import cn.tklvyou.huaiyuanmedia.utils.CommonUtil;
import cn.tklvyou.huaiyuanmedia.widget.TimeCount;

import static cn.tklvyou.huaiyuanmedia.common.CommonConstant.TIME_INTERVAL;
import static cn.tklvyou.huaiyuanmedia.common.CommonConstant.TIME_ONE_SECOND;

/**
 * @author :JenkinsZhou
 * @description :
 * @company :途酷科技
 * @date 2019年07月30日10:18
 * @Email: 971613168@qq.com
 */
public class EditPasswordActivity extends BaseActivity<EditPassContract.EditPassPresenter> implements EditPassContract.EditView, View.OnClickListener {
    private TextView tvSendVCode;
    private TextView tvConfirmEdit;
    private EditText etVCode;
    private EditText etPassNew;
    private EditText etPassConfirm;
    private String mobile;
    private TimeCount timeCount;
    public static final String EXTRA_MESSAGE_ID = "EditPasswordActivity";

    @Override
    protected void initView(Bundle savedInstanceState) {
        setTitle("修改密码");
        setNavigationImage();
        setNavigationOnClickListener(v -> finish());

        tvSendVCode = findViewById(R.id.tvSendVCode);
        tvSendVCode.setOnClickListener(this);
        tvConfirmEdit = findViewById(R.id.tvConfirmEdit);
        tvConfirmEdit.setOnClickListener(this);
        etVCode = findViewById(R.id.etVCode);
        etPassNew = findViewById(R.id.etPassNew);
        etPassConfirm = findViewById(R.id.etPassConfirm);
        mobile = AccountHelper.getInstance().getPhone();



    }

    @Override
    protected EditPassContract.EditPassPresenter initPresenter() {
        return new EditPassPresenter() {
        };
    }

    @Override
    protected int getActivityLayoutID() {
        return R.layout.activity_password_edit;
    }


    @Override
    public void getCaptchaSuccess() {
        handleVCodeSendSuccess();
    }

    @Override
    public void editSuccess() {
        ToastUtils.showShort("密码重置成功");
        finish();
    }

    @Override
    public void editFailed() {
        ToastUtils.showShort("密码重置失败");
    }

    private void setTextValue(String value) {
        tvSendVCode.setText(value);
    }

    private void showCountDownTiming(long mMillisUntilFinished) {
        setTextValue(parseSecond(mMillisUntilFinished));
    }

    private String parseSecond(long millisUntilFinished) {
        return (millisUntilFinished / 1000) + "秒";
    }

    private void setClickEnable(boolean enable) {
        tvSendVCode.setEnabled(enable);
        if (enable) {
            tvSendVCode.setTextColor(CommonUtil.getColor(R.color.colorPrimary));
        } else {
            tvSendVCode.setTextColor(CommonUtil.getColor(R.color.grayAAAAAA));
        }
    }

    private void resetCountDownTime() {
        setClickEnable(true);
        setTextValue("发送验证码");
    }

    private void handleVCodeSendSuccess() {
        setClickEnable(false);
        timeCount = new TimeCount(TIME_INTERVAL, TIME_ONE_SECOND, new TimeCount.ITimeCountListener() {
            @Override
            public void onTick(long millisUntilFinished) {
                showCountDownTiming(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                resetCountDownTime();
            }
        });
        timeCount.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvSendVCode:
                handleSend(mobile);
                break;
            case R.id.tvConfirmEdit:
                handleCommit(mobile);
                break;
            default:
                break;
        }
    }

    private void handleSend(String phone) {
        if (TextUtils.isEmpty(phone)) {
            ToastUtils.showShort("未获取到用户手机号");
            return;
        }
        mPresenter.getCaptcha(phone, "resetpwd");
    }

    private String getTextValue(EditText editText) {
        return editText.getText().toString();
    }


    private void handleCommit(String mobile) {
        if (TextUtils.isEmpty(mobile)) {
            ToastUtils.showShort("未获取到手机号");
            return;
        }
        if (TextUtils.isEmpty(getTextValue(etVCode))) {
            ToastUtils.showShort("请输入验证码");
            return;
        }
        if (TextUtils.isEmpty(getTextValue(etPassNew))) {
            ToastUtils.showShort("请输入密码");
            return;
        }
        if (TextUtils.isEmpty(getTextValue(etPassConfirm))) {
            ToastUtils.showShort("请输入确认密码");
            return;
        }
        if (!getTextValue(etPassConfirm).equals(getTextValue(etPassNew))) {
            ToastUtils.showShort("两次密码输入不一致");
            return;
        }
        mPresenter.edit(mobile, getTextValue(etPassNew), getTextValue(etVCode));
    }

    @Override
    protected void onDestroy() {
        if (timeCount != null) {
            timeCount.cancel();
        }
        timeCount = null;
        super.onDestroy();
    }

}
