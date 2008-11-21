class ImportCompletedFile {

    String logFilePath

    static constraints = {
        logFilePath(blank:false, unique:true) // 手っ取り早くIndexを効かせるためにuniqueをつける
    }

}
