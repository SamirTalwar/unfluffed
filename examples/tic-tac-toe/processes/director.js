Unfluffed.Process(function(app) {
    var move = 1,
        next = 'hero';

    app.subscribe('/move/response/hero', function(position) {
        app.publish('/stage/place/x', position);

        move += 1;
        app.publish('/move/request/villain', {number: move});
    });

    app.subscribe('/move/response/villain', function(position) {
        app.publish('/stage/place/o', position);

        move += 1;
        app.publish('/move/request/hero', {number: move});
    });

    app.publish('/move/request/hero', {number: move});
});
