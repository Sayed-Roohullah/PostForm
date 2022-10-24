package com.acclivousbyte.postform.koinDI

import com.acclivousbyte.postform.viewModel.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mainModule = module {
    viewModel {
        MainViewModel(
            get()
         )
    }
}