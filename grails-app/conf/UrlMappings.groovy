class UrlMappings {
    static mappings = {
        // Welcome
        "/"(controller: "top")

        // Mixed viewer
        "/viewer/index?"(controller: "search", action: "redirectToLatestUrl")
        "/search"(controller: "search", action: "index")

        // Detail viewer
        "/the/$channel/$date"(controller: "detail", action: "redirectToLatestUrl") {
            constraints {
                date(matches: /\d{8}/)
                channel(matches: /[\w()-]+/)
            }
        }
        "/$date/$channel/$permaId?"(controller: "detail", action: "index") {
            constraints {
                date(matches: /\d{8}|\d{4}-\d{2}-\d{2}/)
                channel(matches: /[\w()-]+/)
            }
        }

        // Basic
        "/$controller/$action?/$id?" {
            constraints {
                // apply constraints here
            }
        }

        "404"(controller: 'login', action: 'denied')
        "405"(controller: 'login', action: 'denied')
        "500"(view: '/error')
    }
}
