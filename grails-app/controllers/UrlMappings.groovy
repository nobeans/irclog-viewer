class UrlMappings {
    static mappings = {
        // Welcome
        "/"(controller: 'summary')

        // Mixed viewer
        "/search"(controller: "search", action: "index")

        // Detail viewer
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

        "403"(view: '/notFound')
        "404"(view: '/notFound')
        "405"(view: '/notFound')
        "500"(view: '/error')
    }
}
