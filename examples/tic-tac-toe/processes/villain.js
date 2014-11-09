Unfluffed.Process(function(app) {
    var interactionSubscription;

    app.subscribe('/move/request/villain', function() {
        interactionSubscription = app.subscribe('/stage/interaction', function(position) {
            app.publish('/move/response/villain', position);
            interactionSubscription.cancel();
        });
    });
});
