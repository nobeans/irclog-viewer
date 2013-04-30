import grails.util.Holders
import irclog.search.SearchCriteriaStore
import org.jggug.kobo.gircbot.builder.GircBotBuilder

beans = {
    searchCriteriaStore(SearchCriteriaStore) { bean ->
        bean.scope = 'session'
    }

    sqlHelper(irclog.helper.SqlHelper) { bean ->
        bean.scope = 'prototype'
        sessionFactory = ref('sessionFactory')
    }

    ircbot(irclog.ircbot.Ircbot) { bean ->
        gircBotBuilder = new GircBotBuilder(config: Holders.config.irclog.ircbot.flatten())

        if (Holders.config.irclog.ircbot.enable) {
            bean.constructorArgs = [
                Holders.config.irclog.ircbot.enable
            ]
        }

        limitOfSavedStates = Holders.config.irclog.ircbot.limitOfSavedStates
    }
}
