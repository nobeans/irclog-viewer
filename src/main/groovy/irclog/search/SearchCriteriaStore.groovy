package irclog.search

import groovy.transform.ToString

@ToString(includeFields = true)
class SearchCriteriaStore implements Serializable {

    def criteria

    def clear() {
        criteria = null
    }

    boolean isStored() {
        return criteria != null
    }
}
