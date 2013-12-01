import grails.util.Holders
import irclog.helper.SqlHelper
import irclog.ircbot.Ircbot
import irclog.search.SearchCriteriaStore
import irclog.vertx.DetailPushServer
import org.jggug.kobo.gircbot.builder.GircBotBuilder
import org.vertx.groovy.core.Vertx

beans = {
    searchCriteriaStore(SearchCriteriaStore) { bean ->
        bean.scope = 'session'
    }

    sqlHelper(SqlHelper) { bean ->
        bean.scope = 'prototype'
        sessionFactory = ref('sessionFactory')
    }

    ircbot(Ircbot) { bean ->
        gircBotBuilder = new GircBotBuilder(config: Holders.config.irclog.ircbot.flatten())

        if (Holders.config.irclog.ircbot.enable) {
            bean.constructorArgs = [
                Holders.config.irclog.ircbot.enable
            ]
        }

        limitOfSavedStates = Holders.config.irclog.ircbot.limitOfSavedStates
    }

    // cluster available
    vertx(Vertx, "127.0.0.1") { bean ->
        bean.factoryMethod = "newVertx"
    }

    detailPushServer(DetailPushServer) {
        vertx = ref('vertx')
        channelService = ref('channelService')
    }
}
