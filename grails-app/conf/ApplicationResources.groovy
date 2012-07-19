modules = {
    application {
        dependsOn 'jquery'
        resource url:'js/application.js'
        resource url:'js/common.js'
        resource url:'js/jquery_kill_referrer.js'
    }
    singleViewer {
        dependsOn 'jquery-ui'
        resource url:'js/singleViewer.js'
    }
    mixedViewer {
        dependsOn 'jquery-ui'
        resource url:'js/mixedViewer.js'
        resource url:'js/jquery.highlight-3.js'
    }
    channel {
        resource url:'js/channel.js'
    }
}
