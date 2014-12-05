Unfluffed.Component(function(app) {
    var positions = [
            {x: 0, y: 0}, {x: 1, y: 0}, {x: 2, y: 0},
            {x: 0, y: 1}, {x: 1, y: 1}, {x: 2, y: 1},
            {x: 0, y: 2}, {x: 1, y: 2}, {x: 2, y: 2},
        ]

        hero = 'hero',
        villain = 'villain',

        winConditions = [
            [{x: 0, y: 0}, {x: 1, y: 0}, {x: 2, y: 0}],
            [{x: 0, y: 1}, {x: 1, y: 1}, {x: 2, y: 1}],
            [{x: 0, y: 2}, {x: 1, y: 2}, {x: 2, y: 2}],

            [{x: 0, y: 0}, {x: 0, y: 1}, {x: 0, y: 2}],
            [{x: 1, y: 0}, {x: 1, y: 1}, {x: 1, y: 2}],
            [{x: 2, y: 0}, {x: 2, y: 1}, {x: 2, y: 2}],

            [{x: 0, y: 0}, {x: 1, y: 1}, {x: 2, y: 2}],
            [{x: 2, y: 0}, {x: 1, y: 1}, {x: 0, y: 2}],
        ],

        board = [[], [], []];

    function place(player, position) {
        if (board[position.y][position.x]) {
            app.publish('/move/reject/' + player, {});
            return false;
        }

        board[position.y][position.x] = player;
        app.publish('/move/accept/' + player, position);
        return true;
    }

    function checkMovement(player, nextPlayer, heroId, villainId) {
        var winPositions = winConditions.find(function(winCondition) {
            return winCondition.every(function(position) {
                return board[position.y][position.x] == player;
            });
        });

        if (winPositions) {
            app.publish('/game/state', {
                state: 'won',
                winner: player,
                positions: winPositions
            });
            return;
        }

        var draw = positions.every(function(position) {
            return board[position.y][position.x];
        });

        if (draw) {
            app.publish('/game/state', {state: 'draw'});
            return;
        }

        app.publish('/game/state', {
            state: 'running',
            turn: nextPlayer,
            hero: heroId,
            villain: villainId
        });
    }

    app.subscribe('/game/state', function(data) {
        if (data.state == 'started') {
            board = [[], [], []];

            app.subscribe('/move/response/' + data.hero, function(position) {
                if (place(hero, position)) {
                    checkMovement(hero, villain, data.hero, data.villain);
                }
            });

            app.subscribe('/move/response/' + data.villain, function(position) {
                if (place(villain, position)) {
                    checkMovement(villain, hero, data.hero, data.villain);
                }
            });

            app.publish('/game/state', {
                state: 'running',
                turn: hero,
                hero: data.hero,
                villain: data.villain
            });
        }
    });
});
