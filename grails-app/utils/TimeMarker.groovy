import java.text.SimpleDateFormat

public class TimeMarker {

    private String time
    
    public TimeMarker(String time) {
        this.time = time
    }
    
    public Date getTime() { // Date型であるがあえて文脈に合わせてTimeというプロパティ名とした。
        def today = new SimpleDateFormat('yyyy-MM-dd ').format(new Date())
        return new SimpleDateFormat('yyyy-MM-dd HH:mm').parse(today + time)
    }

    public String toString() {
        return time
    }
}
