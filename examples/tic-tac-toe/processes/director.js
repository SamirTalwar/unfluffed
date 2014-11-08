Unfluffed.Process(function(app) {
    var move = 1;

    function play(piece, position) {
        app.publish('/stage/place/' + piece, position);
        move += 1;
    }

    function next(player) {
        app.publish('/move/request/' + player, {number: move});
    }

    app.subscribe('/move/response/hero', function(position) {
        play('x', position);
        next('villain');
    });

    app.subscribe('/move/response/villain', function(position) {
        play('o', position);
        next('hero');
    });

    next('hero');
});
