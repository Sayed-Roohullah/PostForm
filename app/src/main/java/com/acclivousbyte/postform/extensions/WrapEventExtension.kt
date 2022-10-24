package com.acclivousbyte.postform.extensions

import com.acclivousbyte.postform.utils.Event

fun <T> T.wrapWithEvent() = Event(this)
