class DateUtils {

    private static final FORMAT = new java.text.SimpleDateFormat("yyyy/MM/dd hh:mm:ss")

    static parse(input) {
        FORMAT.parse(input)
    }

    static format(input) {
        FORMAT.format(input)
    }
}
