Unfluffed.Process(function(app) {
    var interactionSubscription;

    app.subscribe('/move/request/hero', function() {
        interactionSubscription = app.subscribe('/stage/interaction', function(position) {
            app.publish('/move/response/hero', position);
            interactionSubscription.cancel();
        });
    });
});
