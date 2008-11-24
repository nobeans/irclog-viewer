import org.codehaus.groovy.grails.commons.ApplicationHolder

/**
 * 現在のチャンネル定義を元に、まだチャンネルに関連付けできていないIrclogの関連更新を試みる。
 * エラー処理は手抜き。
 */
class UpdateChannelRelationJob {

    def timeout = 90 * 1000 + 1 // msec
    def channelService

    def execute() {
        channelService.updateAllChannelOfIrclog()
    }

}
