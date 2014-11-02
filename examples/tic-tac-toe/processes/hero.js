Unfluffed.Process(function(app) {
    var interactionSubscription;

    app.subscribe('/move/request/hero', function() {
        interactionSubscription = app.subscribe('/stage/interaction', function(data) {
            app.publish('/move/response/hero', data);
            interactionSubscription.cancel();
        });
    });
});
