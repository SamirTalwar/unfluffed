Unfluffed.Process(function(app) {
    var interactionSubscription;

    app.subscribe('/move/request/villain', function() {
        interactionSubscription = app.subscribe('/stage/interaction', function(data) {
            app.publish('/move/response/villain', data);
            interactionSubscription.cancel();
        });
    });
});
