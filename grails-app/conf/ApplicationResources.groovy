modules = {
    application {
        dependsOn 'jquery'
        dependsOn 'my-less'
        resource url: 'js/application.js'
        resource url: 'js/common.js'
        resource url: 'js/jquery_kill_referrer.js'
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
        resource url: 'js/singleViewer.js'
    }

    mixedViewer {
        dependsOn 'jquery-ui'
        resource url: 'cs/mixedViewer.coffee'
        resource url: 'js/jquery.highlight-3.js'
    }

    channel {
        resource url: 'js/channel.js'
    }
}
