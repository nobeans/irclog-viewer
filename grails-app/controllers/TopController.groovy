class TopController {

    def index = {
        flash.message = flash.message // ���b�Z�[�W���������ꍇ�͈����p��
        redirect(controller:'mixedViewer')
    }
}
