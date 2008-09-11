class StringUtils {

    static joinWithoutEmpty(array, delimiter) {
        return array.findAll({it}).join(delimiter)
    }

    static getNotNullString(str) {
        return (str == null) ? '' : str
    }

}
