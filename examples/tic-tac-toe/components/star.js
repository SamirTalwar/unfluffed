Unfluffed.Component(function(app) {
    var interactionSubscription;

    app.client.id(function(id) {
        app.subscribe('/move/request/' + id, function() {
            interactionSubscription = app.subscribe('/stage/interaction', function(position) {
                app.publish('/move/response/' + id, position);
                interactionSubscription.cancel();
            });
        });

        app.subscribe('/move/request/' + id + '!', function() {
            interactionSubscription = app.subscribe('/stage/interaction', function(position) {
                app.publish('/move/response/' + id + '!', position);
                interactionSubscription.cancel();
            });
        });
    });
});
