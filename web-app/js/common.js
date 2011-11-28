jQuery(function($) {

    // Help caption
    $(".help-button").click(function() {
        $("#" + this.id + "-caption").slideToggle(100);
    });

    // Default focus
    $(".defaultFocus").focus();
});
