package irclog

import grails.plugin.springsecurity.SpringSecurityService
import grails.util.Holders
import grails.validation.Validateable
import irclog.search.SearchCriteriaStore
import org.codehaus.groovy.grails.web.binding.DataBindingUtils

class SearchController {

    IrclogSearchService irclogSearchService
    ChannelService channelService
    SpringSecurityService springSecurityService
    SearchCriteriaStore searchCriteriaStore

    def redirectToLatestUrl() {
        redirect action: "index", params: params
    }

    def index(SearchCommand command) {
        def searchResult = search(command)
        return [
            command: command,
            criteriaMap: command.toMap(),
            periodCandidates: SearchCommand.SELECTABLE_PERIODS,
            channelCandidates: channelCandidates,
            irclogList: searchResult.list,
            irclogTotalCount: searchResult.totalCount,
            essentialTypes: Irclog.ESSENTIAL_TYPES,
        ]
    }

    def clearCriteria() {
        searchCriteriaStore.clear()
        redirect(action: 'index')
    }

    private search(SearchCommand command) {
        restoreCriteriaIfNotSpecifiedByRequest(command)
        def criteriaMap = command.toMap()
        try {
            return irclogSearchService.search(springSecurityService.currentUser, criteriaMap, [max: command.max, offset: command.offset])
        } finally {
            searchCriteriaStore.criteria = criteriaMap
        }
    }

    private void restoreCriteriaIfNotSpecifiedByRequest(SearchCommand command) {
        if (command.notSpecified && searchCriteriaStore.stored) {
            DataBindingUtils.bindObjectToInstance(this, searchCriteriaStore.criteria)
            log.debug "Restored criteria from store: ${command.toMap()}"
        }
    }

    private getChannelCandidates() {
        return ['all'] + channelService.getAccessibleChannelList(springSecurityService.currentUser, [sort: 'name', 'order': 'asc']).grep { !it.isArchived }*.name
    }
}

@Validateable
class SearchCommand {

    static final SELECTABLE_PERIODS = ['all', 'year', 'halfyear', 'month', 'week', 'today', 'oneday']
    private static final String DEFAULT_PERIOD = 'today'
    private static final String DEFAULT_CHANNEL = 'all'
    private static final String DEFAULT_TYPE = 'filtered'
    private static final Integer DEFAULT_MAX = Holders.config.irclog.viewer.defaultMax

    String period
    String channel
    String type
    String nick
    String message
    String periodOnedayDate

    Integer max
    Integer offset

    transient boolean notSpecified = false

    static constraints = {
        period inList: SELECTABLE_PERIODS
        channel()
        type()
        nick()
        message()
        periodOnedayDate nullable: true
    }

    def beforeValidate() {
        // To identify that request has effective criteria parameters
        notSpecified = (period == null)

        // Resolving default values
        period = period ?: DEFAULT_PERIOD
        channel = channel ?: DEFAULT_CHANNEL
        type = type ?: DEFAULT_TYPE
        periodOnedayDate = (period == 'oneday') ? periodOnedayDate : null
        max = max ? Math.min(max, DEFAULT_MAX) : DEFAULT_MAX
        offset = offset ?: 0
    }

    Map toMap() {
        def map = [
            period: period,
            channel: channel,
            type: type,
            nick: nick,
            message: message,
            periodOnedayDate: periodOnedayDate,
        ]
        map.remove('')
        return map
    }
}
