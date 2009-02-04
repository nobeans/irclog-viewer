class UrlMappings {
    static mappings = {

        // Welcome
        "/"(controller:"top")

        // Mixed viewer
        "/viewer/index?"(controller:"mixedViewer", action:"index") {
            constraints {
            }
        }

        // Single viewer
        "/the/$channel/$date"(controller:"singleViewer", action:"index") {
            constraints {
                channel(matches:/\w+/)
                date(matches:/\d{8}/)
            }
        }

        // デフォルト
        "/$controller/$action?/$id?" {
            constraints {
               // apply constraints here
            }
        }

        "404"(view:'/error/404')
        "405"(view:'/error/405')
        "500"(view:'/error/500')
	}
}
