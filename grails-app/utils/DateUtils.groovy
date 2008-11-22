class DateUtils {

    private static final FORMAT = new java.text.SimpleDateFormat("yyyy/MM/dd hh:mm:ss")

    synchronized static parse(input) {
        FORMAT.parse(input)
    }

    synchronized static format(input) {
        FORMAT.format(input)
    }
}
