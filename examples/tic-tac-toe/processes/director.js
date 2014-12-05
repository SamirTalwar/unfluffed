Unfluffed.Process(function(app) {
    var move = 1;

    function play(piece, position) {
        move += 1;
    }

    function request(playerId) {
        app.publish('/move/request/' + playerId, {});
    }

    app.subscribe('/move/accept/hero', function(position) {
        play('x', position);
    });

    app.subscribe('/move/accept/villain', function(position) {
        play('o', position);
    });

    app.subscribe('/game/state', function(state) {
        if (state.state == 'running') {
            request(state[state.turn]);
        }
    });
});
