package com.earth.angel.dialog

import com.earth.libbase.entity.MemberEntity


interface MemberInterface {
    fun refresh()
    fun more()
    fun join(userid: Long, bean: MemberEntity)
}