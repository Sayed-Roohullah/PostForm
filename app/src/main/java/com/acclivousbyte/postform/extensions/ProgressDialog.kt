package com.acclivousbyte.postform.extensions

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.acclivousbyte.postform.utils.DailogHelper
import com.acclivousbyte.postform.utils.Event
import com.acclivousbyte.postform.view.activities.BaseActivity
import com.afollestad.materialdialogs.MaterialDialog

fun BaseActivity.setupProgressDialog(
    showHideProgress: LiveData<Event<Boolean>>,
    dialogHelper: DailogHelper
){
    var mDialog: MaterialDialog? = null
    showHideProgress.observe(this, Observer {
        if (!it.consumed) it.consume()?.let { flag ->
            if (flag)
                mDialog?.show() ?: dialogHelper.showSimpleProgress(this)
                    .also { dialog ->
                        mDialog = dialog
                    }
            else mDialog?.dismiss()
        }
    })
}