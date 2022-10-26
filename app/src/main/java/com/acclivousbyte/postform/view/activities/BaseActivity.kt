package com.acclivousbyte.postform.view.activities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.acclivousbyte.postform.extensions.AlertMessageDialog

open class BaseActivity: AppCompatActivity() {

    lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    fun showAlertDialog(msg: String, positiveButtonCallback: AlertMessageDialog.PositiveButtonCallback? = null) {
        AlertMessageDialog.newInstance(msg, positiveButtonCallback)
            .show(this.supportFragmentManager, AlertMessageDialog.TAG)
    }

    fun checkPermission(activity: MainActivity){
        val permissionsStorage = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        val requestExternalStorage = 1
        val permission =
            ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, permissionsStorage, requestExternalStorage);
        }
    }



}