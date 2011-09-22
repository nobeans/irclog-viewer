jQuery(function($) {

    // Button to join a channel
    $("#toggleJoinToSecretChannel").toggle(function() {
        $("#joinToSecretChannel").show().css({"z-index": 999});
        $("#join").addClass("active");
        $("#channelName").focus();
    }, function() {
        $("#joinToSecretChannel").hide();
        $("#join").removeClass("active");
    });
});
