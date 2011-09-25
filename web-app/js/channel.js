jQuery(function($) {

    // Button to join a channel
    $("#toggleJoinToSecretChannel").toggle(function() {
        $("#joinToSecretChannel").slideDown(200);
        $("#join").addClass("active");
        $("#channelName").focus();
    }, function() {
        $("#joinToSecretChannel").slideUp(100);
        $("#join").removeClass("active");
    });
});
