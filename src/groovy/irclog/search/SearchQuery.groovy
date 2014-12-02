package irclog.search

import grails.plugin.springsecurity.SpringSecurityService
import grails.util.Holders
import grails.validation.Validateable
import irclog.ChannelService
import irclog.IrclogSearchService
import org.codehaus.groovy.grails.web.binding.DataBindingUtils

@Validateable
class SearchQuery {

    private static final SELECTABLE_PERIODS = ['all', 'year', 'halfyear', 'month', 'week', 'today', 'oneday']
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

    transient boolean isNotSpecified = false
    transient IrclogSearchService irclogSearchService
    transient ChannelService channelService
    transient SpringSecurityService springSecurityService

    static constraints = {
        // beforeValidate doesn't executed for CommandObject: http://jira.grails.org/browse/GRAILS-8063
        // This is a hack to execute beforeValidate method forcely.
        // The property needs to be nullable:true to use for this purpose,
        // because validator won't be executed if it's null.
        periodOnedayDate nullable: true, validator: { value, object ->
            object.beforeValidate()
            return true
        }
    }

    def beforeValidate() {
        // To identify that request has effective criteria parameters
        isNotSpecified = (period == null)

        // Resolving default values
        period = period ?: DEFAULT_PERIOD
        channel = channel ?: DEFAULT_CHANNEL
        type = type ?: DEFAULT_TYPE
        periodOnedayDate = (period == 'oneday') ? periodOnedayDate : null
        max = max ? Math.min(max, DEFAULT_MAX) : DEFAULT_MAX
        offset = offset ?: 0
    }

    def search(SearchCriteriaStore searchCriteriaStore) {
        restoreCriteriaIfNotSpecifiedByRequest(searchCriteriaStore)
        def criteriaMap = toMap()
        try {
            return irclogSearchService.search(currentUser, criteriaMap, [max: max, offset: offset])
        } finally {
            searchCriteriaStore.criteria = criteriaMap
        }
    }

    private void restoreCriteriaIfNotSpecifiedByRequest(SearchCriteriaStore searchCriteriaStore) {
        if (isNotSpecified && searchCriteriaStore.stored) {
            DataBindingUtils.bindObjectToInstance(this, searchCriteriaStore.criteria)
            log.debug "Restored criteria from store: ${toMap()}"
        }
    }

    def getPeriodCandidates() { // for view
        return SELECTABLE_PERIODS
    }

    def getChannelCandidates() { // for view
        return ['all'] + channelService.getAccessibleChannelList(currentUser, [sort: 'name', 'order': 'asc']).grep { !it.isArchived }*.name
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

    private getCurrentUser() {
        springSecurityService.currentUser
    }
}
