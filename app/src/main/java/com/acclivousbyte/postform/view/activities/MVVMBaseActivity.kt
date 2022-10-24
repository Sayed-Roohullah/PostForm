package com.acclivousbyte.postform.view.activities

import androidx.lifecycle.ViewModel
import org.koin.core.parameter.ParametersDefinition
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.reflect.KClass

abstract class MVVMBaseActivity<out VM : ViewModel> (
    viewModelClass: KClass<VM>,
    viewmodelParameters: ParametersDefinition? = null
): BaseActivity(){
    protected open val viewModel: VM by viewModel(
        clazz = viewModelClass,
        parameters = viewmodelParameters
    )
}