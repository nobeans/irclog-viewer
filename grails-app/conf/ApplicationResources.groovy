modules = {
    application {
        dependsOn 'jquery'
        resource url: 'coffee/application.coffee'
        resource url: 'js/underscore.js'
        resource url: 'less/main.less'
        resource url: 'css/origin.css'
        resource url: 'less/print.less', attrs: [media: 'print']
    }

    detail {
        dependsOn 'jquery-ui'
        resource url: 'js/knockout-2.2.1.js'
        resource url: 'js/jquery.history.js'
        resource url: 'coffee/detail.coffee'
        resource url: 'js/jquery.dateFormat-1.0.js'
    }

    search {
        dependsOn 'jquery-ui'
        resource url: 'coffee/search.coffee'
        resource url: 'js/jquery.highlight-3.js'
    }

    channel {
        resource url: 'coffee/channel.coffee'
    }
}
