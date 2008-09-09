class UrlMappings {
    static mappings = {

        // 特定ログのパーマリンクURL
        "/viewer/$id"(controller:"viewer", action:"specified") {
            constraints {
                id(matches:/[0-9]+/)
            }
        }

        // ログビューア
        "/viewer/"(controller:"viewer", action:"index") {
            constraints {
            }
        }

        // デフォルト
        "/$controller/$action?/$id?" {
            constraints {
               // apply constraints here
            }
        }

        "500"(view:'/error')
	}
}
