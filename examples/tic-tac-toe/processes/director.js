Unfluffed.Process(function(app) {
    var move = 1,
        currentPlayer = 'hero';

    function play(piece, position) {
        move += 1;
    }

    function request(player) {
        app.publish('/move/request/' + player, {});
    }

    app.subscribe('/move/accept/hero', function(position) {
        play('x', position);
        currentPlayer = 'villain';
    });

    app.subscribe('/move/accept/villain', function(position) {
        play('o', position);
        currentPlayer = 'hero';
    });

    app.subscribe('/game/state', function(state) {
        if (state.state == 'running') {
            request(currentPlayer);
        }
    });

    request(currentPlayer);
});
