class ImportCompletedFile {

    String filePath

    static constraints = {
        filePath(blank:false, unique:true) // 手っ取り早くIndexを効かせるためにuniqueをつける
    }

}
