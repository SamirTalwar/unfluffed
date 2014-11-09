Unfluffed.Process(function (app) {
    var BOARD_SIZE = 600,
        BOX_SIZE = BOARD_SIZE / 3,
        LINE_WIDTH = 20,
        PADDING = 30,

        stage = $('<div>').addClass('stage'),
        canvas = $('<canvas>').attr('width', BOARD_SIZE).attr('height', BOARD_SIZE),
        canvasContext = canvas[0].getContext('2d');

    $(document.body)
        .append(stage.append(canvas));

    canvasContext.fillRect(0, BOX_SIZE - LINE_WIDTH / 2, BOARD_SIZE, LINE_WIDTH);
    canvasContext.fillRect(BOX_SIZE - LINE_WIDTH / 2, 0, LINE_WIDTH, BOARD_SIZE);
    canvasContext.fillRect(0, BOX_SIZE * 2 - LINE_WIDTH / 2, BOARD_SIZE, LINE_WIDTH);
    canvasContext.fillRect(BOX_SIZE * 2 - LINE_WIDTH / 2, 0, LINE_WIDTH, BOARD_SIZE);

    canvas.click(function(event) {
        var clickX = event.clientX - this.offsetLeft,
            clickY = event.clientY - this.offsetTop,

            x = clickX / (BOARD_SIZE / 3) | 0,
            y = clickY / (BOARD_SIZE / 3) | 0;

        app.publish('/stage/interaction', {
            x: x,
            y: y
        });
    });

    app.subscribe('/game/state', function(state) {
        if (state.state != 'won') {
            return;
        }

        var start = state.positions[0],
            end = state.positions[2],

            vertices = [];

        if (start.x == end.x) {
            draw(
                {x: start.x * BOX_SIZE + (BOX_SIZE - LINE_WIDTH) / 2, y: 0},
                {x: start.x * BOX_SIZE + (BOX_SIZE + LINE_WIDTH) / 2, y: 0},
                {x: start.x * BOX_SIZE + (BOX_SIZE + LINE_WIDTH) / 2, y: BOARD_SIZE},
                {x: start.x * BOX_SIZE + (BOX_SIZE - LINE_WIDTH) / 2, y: BOARD_SIZE});
        } else if (start.y == end.y) {
            draw(
                {x: 0, y: start.y * BOX_SIZE + (BOX_SIZE + LINE_WIDTH) / 2},
                {x: 0, y: start.y * BOX_SIZE + (BOX_SIZE - LINE_WIDTH) / 2},
                {x: BOARD_SIZE, y: start.y * BOX_SIZE + (BOX_SIZE - LINE_WIDTH) / 2},
                {x: BOARD_SIZE, y: start.y * BOX_SIZE + (BOX_SIZE + LINE_WIDTH) / 2});
        } else {
            if (start.x == 0) {
                draw(
                    {x: 0, y: LINE_WIDTH},
                    {x: LINE_WIDTH, y: 0},
                    {x: BOARD_SIZE, y: BOARD_SIZE - LINE_WIDTH},
                    {x: BOARD_SIZE - LINE_WIDTH, y: BOARD_SIZE});
            } else {
                draw(
                    {x: BOARD_SIZE - LINE_WIDTH, y: 0},
                    {x: BOARD_SIZE, y: LINE_WIDTH},
                    {x: LINE_WIDTH, y: BOARD_SIZE},
                    {x: 0, y: BOARD_SIZE - LINE_WIDTH});
            }
        }
    });

    app.subscribe('/move/accept/hero', function(position) {
        var offsetX = BOX_SIZE * position.x,
            offsetY = BOX_SIZE * position.y,
            positions = {
                tl: {
                    a: {
                        x: offsetX + PADDING,
                        y: offsetY + PADDING + LINE_WIDTH / 2
                    },
                    b: {
                        x: offsetX + PADDING + LINE_WIDTH / 2,
                        y: offsetY + PADDING
                    }
                },
                tr: {
                    a: {
                        x: offsetX + BOX_SIZE - PADDING - LINE_WIDTH / 2,
                        y: offsetY + PADDING
                    },
                    b: {
                        x: offsetX + BOX_SIZE - PADDING,
                        y: offsetY + PADDING + LINE_WIDTH / 2
                    }
                },
                br: {
                    a: {
                        x: offsetX + BOX_SIZE - PADDING,
                        y: offsetY + BOX_SIZE - PADDING - LINE_WIDTH / 2
                    },
                    b: {
                        x: offsetX + BOX_SIZE - PADDING - LINE_WIDTH / 2,
                        y: offsetY + BOX_SIZE - PADDING
                    }
                },
                bl: {
                    a: {
                        x: offsetX + PADDING + LINE_WIDTH / 2,
                        y: offsetY + BOX_SIZE - PADDING
                    },
                    b: {
                        x: offsetX + PADDING,
                        y: offsetY + BOX_SIZE - PADDING - LINE_WIDTH / 2
                    }
                }
            };

        draw(positions.tl.a, positions.tl.b, positions.br.a, positions.br.b);
        draw(positions.tr.a, positions.tr.b, positions.bl.a, positions.bl.b);
    });

    app.subscribe('/move/accept/villain', function(position) {
        canvasContext.beginPath();
        canvasContext.arc(
            BOX_SIZE * position.x + BOX_SIZE / 2,
            BOX_SIZE * position.y + BOX_SIZE / 2,
            BOX_SIZE / 2 - PADDING,
            0,
            Math.PI * 2
        );
        canvasContext.fill();

        canvasContext.fillStyle = 'white';
        canvasContext.beginPath();
        canvasContext.arc(
            BOX_SIZE * position.x + BOX_SIZE / 2,
            BOX_SIZE * position.y + BOX_SIZE / 2,
            BOX_SIZE / 2 - PADDING - LINE_WIDTH,
            0,
            Math.PI * 2
        );
        canvasContext.fill();
        canvasContext.fillStyle = 'black';
    });

    function draw() {
        var start = arguments[arguments.length - 1];

        canvasContext.beginPath();
        canvasContext.moveTo(start.x, start.y);
        Array.prototype.slice.call(arguments).forEach(function(position) {
            canvasContext.lineTo(position.x, position.y);
        });
        canvasContext.fill();
    }
});
