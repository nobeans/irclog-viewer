// �Ѵ��Τ���Υ桼�ƥ���ƥ����饹
class ConvertUtils {

    static toInteger(value, defaultValue=null) {
        (value?.isInteger()) ? value.toInteger() : defaultValue
    }

    static toLong(value, defaultValue=null) {
        (value?.isLong()) ? value.toLong() : defaultValue
    }

}