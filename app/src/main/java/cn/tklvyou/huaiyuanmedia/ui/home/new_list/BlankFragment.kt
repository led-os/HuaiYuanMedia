package cn.tklvyou.huaiyuanmedia.ui.home.new_list

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import cn.tklvyou.huaiyuanmedia.R
import cn.tklvyou.huaiyuanmedia.base.NullPresenter
import cn.tklvyou.huaiyuanmedia.base.fragment.BaseFragment
import com.blankj.utilcode.util.ToastUtils
import kotlinx.android.synthetic.main.fragment_blank.*


class BlankFragment : BaseFragment<NullPresenter>() {
    override fun initView() {
    }

    override fun initPresenter(): NullPresenter {
        return NullPresenter()
    }

    override fun getFragmentLayoutID(): Int {
        return R.layout.fragment_blank
    }

    override fun getLoadingView(): View {
        return tvtest
    }

    override fun lazyData() {
        ToastUtils.showShort("111")
        tvtest.text = "-------------------"

    }


}
