/**
 * 1日の初回のサマリ情報へのアクセスが遅くならないように、深夜帯に前日分のサマリ情報を生成しておく。
 */
class UpdateAllSummaryJob {

    def cronExpression = "0 0 1 * * ?"
    def name = "UpdateAllSummary"

    def summaryService

    def execute() {
        log.info "Begin updating all summary..."
        summaryService.updateAllSummary()
        log.info "End updating all summary..."
    }
}
