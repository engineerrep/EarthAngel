package com.earth.libbase.util

object CheckPwdUtils {
    //数字
    const val REG_NUMBER = "^[a-zA-Z0-9]{6,20}\$"





    /**
     * 长度至少minLength位,且最大长度不超过maxlength,须包含大小写字母,数字,特殊字符matchCount种以上组合
     * @param password 输入的密码
     * @param minLength 最小长度
     * @param maxLength 最大长度
     * @param matchCount 次数
     * @return 是否通过验证
     */
    fun checkPwd(password: String): Boolean {
        //密码为空或者长度小于8位则返回false
        if (password.matches(Regex(REG_NUMBER))) return true

        return false
    }
}