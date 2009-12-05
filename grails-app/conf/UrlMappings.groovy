class UrlMappings {
    static mappings = {

        // ���胍�O�̃p�[�}�����NURL
        "/viewer/$id"(controller:"viewer", action:"specified") {
            constraints {
                id(matches:/[0-9]+/)
            }
        }

        // ���O�r���[�A
        "/viewer/"(controller:"viewer", action:"index") {
            constraints {
            }
        }

        // �f�t�H���g
        "/$controller/$action?/$id?" {
            constraints {
               // apply constraints here
            }
        }

        "500"(view:'/error')
	}
}
