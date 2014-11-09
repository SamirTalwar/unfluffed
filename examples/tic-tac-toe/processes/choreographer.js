Unfluffed.Process(function(app) {
    var positions = [
            {x: 0, y: 0}, {x: 1, y: 0}, {x: 2, y: 0},
            {x: 0, y: 1}, {x: 1, y: 1}, {x: 2, y: 1},
            {x: 0, y: 2}, {x: 1, y: 2}, {x: 2, y: 2},
        ]

        hero = 'hero',
        villain = 'villain',

        winConditions = [
        ],

        board = [[], [], []];

    function place(player, position) {
        board[position.y][position.x] = player;
    }

    function checkMovement(player) {
        var won = winConditions.some(function(winCondition) {
            return winCondition.every(function(position) {
                return board[position.y][position.x] == player;
            })
        });

        if (won) {
            app.publish('/game/state', {state: 'won', winner: player});
            return;
        }

        var draw = positions.every(function(position) {
            return board[position.y][position.x];
        });

        if (draw) {
            app.publish('/game/state', {state: 'draw'});
            return;
        }

        app.publish('/game/state', {state: 'running'});
    }

    app.subscribe('/move/response/hero', function(position) {
        place(hero, position);
        checkMovement(hero);
    });

    app.subscribe('/move/response/villain', function(position) {
        place(villain, position);
        checkMovement(villain);
    });
});
