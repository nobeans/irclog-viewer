modules = {
    application {
        dependsOn 'jquery'
        dependsOn 'my-less'
        resource url: 'coffee/application.coffee', bundle: 'application'
        resource url: 'coffee/common.coffee', bundle: 'application'
        resource url: 'js/jquery_kill_referrer.js', bundle: 'application'
    }

    'my-less' {
        defaultBundle "application"
        resource url: 'less/main.less'
        resource url: 'less/print.less'
        resource url: 'css/origin.css'
        resource url: 'css/mobile.css'
    }

    singleViewer {
        dependsOn 'jquery-ui'
        resource url: 'js/knockout-2.2.1.js', bundle: 'application'
        resource url: 'coffee/singleViewer.coffee'
    }

    mixedViewer {
        dependsOn 'jquery-ui'
        resource url: 'coffee/mixedViewer.coffee'
        resource url: 'js/jquery.highlight-3.js'
    }

    channel {
        resource url: 'coffee/channel.coffee'
    }
}
