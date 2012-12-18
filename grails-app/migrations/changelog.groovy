import grails.util.Environment

databaseChangeLog = {

    // production only
    if (Environment.current.name == 'production') {
        include file: 'schema-v0_1.groovy'
        include file: 'schema-v0_2.groovy'
    }
}
