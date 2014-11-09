Unfluffed.Process(function(app) {
    var move = 1,
        next = 'hero';

    function play(piece, position) {
        app.publish('/stage/place/' + piece, position);
        move += 1;
    }

    function requestNext(player) {
        app.publish('/move/request/' + player, {});
    }

    app.subscribe('/move/response/hero', function(position) {
        play('x', position);
        next = 'villain';
    });

    app.subscribe('/move/response/villain', function(position) {
        play('o', position);
        next = 'hero';
    });

    app.subscribe('/game/state', function(state) {
        if (state.state == 'running') {
            requestNext(next);
        }
    });

    requestNext(next);
});
