package com.acclivousbyte.postform.koinDI

import com.acclivousbyte.postform.utils.DailogHelper
import com.acclivousbyte.postform.utils.Repository
import org.koin.dsl.module

val utilModule = module {

    single { DailogHelper() }

    single { Repository(get()) }

}