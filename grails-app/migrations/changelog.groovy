import grails.util.Environment

databaseChangeLog = {

    // production only
    if (Environment.current.name == 'production') {
        include file: 'v0_1.groovy'
        include file: 'v0_2.groovy'
        include file: 'v0_3.groovy'
    }

}
