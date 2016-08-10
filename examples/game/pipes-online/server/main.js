var app = require('express')();
var server = require('http').Server(app);
var io = require('socket.io')(server);

server.listen(6469, function () {
    console.log("Server is now running...");
});

var SIZE = 10;
var CELL_START = 0;
var HORIZONTAL = 0;
var VERTICAL = 1;
var LEFT_TO_DOWN = 2;
var LEFT_TO_UP = 3;
var RIGHT_TO_UP = 4;
var RIGHT_TO_DOWN = 5;
var CROSS = 6;
var CELL_LAST = 6;

var Cells = [
    {support: [1, 1, 0, 0], index: HORIZONTAL, next: VERTICAL},
    {support: [0, 0, 1, 1], index: VERTICAL, next: HORIZONTAL},
    {support: [1, 0, 0, 1], index: LEFT_TO_DOWN, next: LEFT_TO_UP},
    {support: [1, 0, 1, 0], index: LEFT_TO_UP, next: RIGHT_TO_UP},
    {support: [0, 1, 1, 0], index: RIGHT_TO_UP, next: RIGHT_TO_DOWN},
    {support: [0, 1, 0, 1], index: RIGHT_TO_DOWN, next: LEFT_TO_DOWN},
    {support: [1, 1, 1, 1], index: CROSS, next: CROSS}
];
function supportLeft(v)  { return Cells[v].support[0]; };
function supportRight(v) { return Cells[v].support[1]; };
function supportUp(v)    { return Cells[v].support[2]; };
function supportDown(v)  { return Cells[v].support[3]; };

function create2DArray(size, value) {
    var result = new Array(size);
    for (var i = 0; i < size; i++) {
        result[i] = new Array(size);
        for (var j = 0; j < size; j++)
            result[i][j] = value;
    }
    return result;
}

var Board = function (size) {
    var self = {};

    self.board1 = create2DArray(size, CELL_START);
    self.board2 = create2DArray(size, CELL_START);

    self.create = function () {
        for (var i = 0; i < size; i++) {
            for (var j = 0; j < size; j++) {
                var cell = Math.floor(Math.random() * CELL_LAST + 1);
                self.board1[i][j] = cell;
                self.board2[i][j] = cell;
            }
        }
    };

    self.switchCell = function (x, y, isGhost) {
        if (isGhost)
            self.board2[x][y] = Cells[self.board2[x][y]].next;
        else
            self.board1[x][y] = Cells[self.board1[x][y]].next;
    };

    var isConnected = function (board, curX, curY, visited) {
        // If it is a last cell - game is finished
        if ( (curX === SIZE - 1) && (curY === SIZE - 1) ) return true;

        // Already visited - exit
        if (visited[curX][curY]) return false;
        // Mark visited
        visited[curX][curY] = true;
        // Check pipes matching
        var current = board[curX][curY];
        if ( supportLeft(current) && (curX > 0) && (supportRight(board[curX - 1][curY])) ) {
            if (isConnected(board, curX - 1, curY, visited)) return true;
        }
        if ( supportRight(current) && (curX < SIZE - 1) && (supportLeft(board[curX + 1][curY])) ) {
            if (isConnected(board, curX + 1, curY, visited)) return true;
        }
        if ( supportUp(current) && (curY > 0) && (supportDown(board[curX][curY - 1])) ) {
            if (isConnected(board, curX, curY - 1, visited)) return true;
        }
        if ( supportDown(current) && (curY < SIZE - 1) && (supportUp(board[curX][curY + 1])) ) {
            if (isConnected(board, curX, curY + 1, visited)) return true;
        }
        return false;
    };

    var isFinished = function(board) {
        // Start pipe must have left touchpoint
        if (!supportLeft(board[0][0])) return false;
        // Finish pipe - right touchpoint
        if (!supportRight(board[SIZE - 1][SIZE - 1])) return false;

        var visited = new Array(SIZE);
        for (var i = 0; i < SIZE; i++) {
            visited[i] = new Array(SIZE);
            for (var j = 0; j < SIZE; j++) visited[i][j] = false;
        }
        // Recursive traversal from left upper pipe
        return isConnected(board, 0, 0, visited);
    };

    self.isGameFinished = function (isGhost) {
        return isFinished(isGhost ? self.board2 : self.board1);
    };

    return self;
};

var board = {};
var players = [];

io.on('connection', function (socket) {
    if (players.length >= 2) {
        console.log('Sorry, server is full.');
        return;
    }

    socket.playerId = players.length + 1;
    players.push({socket: socket});
    console.log('Player ' + socket.playerId + ' Connected!');

    socket.on('switchCell', function (data) {
        var isGhost = socket.playerId === 2;
        var current = isGhost ? 1 : 0;
        var opposite = isGhost ? 0 : 1;
        board.switchCell(data.x, data.y, isGhost);
        players[opposite].socket.emit('updateGhostCell', data);
        if (board.isGameFinished(isGhost)) {
            players[current].socket.emit('gameFinished', true);
            players[opposite].socket.emit('gameFinished', false);
        } else if (board.isGameFinished(!isGhost)) {
            players[opposite].socket.emit('gameFinished', true);
            players[current].socket.emit('gameFinished', false);
        }
    });
    socket.on('updateCursor', function (data) {
        var isGhost = socket.playerId === 2;
        var opposite = isGhost ? 0 : 1;
        players[opposite].socket.emit('updateGhostCursor', data);
    });
    socket.on('connect_timeout', function (exception) {
        console.log('SOCKET TIMEOUT ' + exception);
        socket.destroy();
    });
    socket.on('disconnect', function () {
        console.log('disconnect Player ' + socket.playerId);
        players.splice(socket.playerId - 1, 1);
    });

    // start game
    if (players.length === 2) {
        board = Board(SIZE);
        board.create();

        players[0].socket.emit('gameStart', board.board1);
        players[1].socket.emit('gameStart', board.board2);
    }
});

/*io.on('connection', function(socket) {
 console.log('New connection');
 socket.on('greetings', function(data) {
 console.log('Got greetings from client');
 socket.emit('pong', "Hello from server");
 });
 socket.on('complex_object', function(data) {
 console.log('Got object: ' + data);
 socket.emit('complex_object', {key1: data.key2, key2: data.key1, arr: [0,1,2,"34"]});
 });
 });*/
/*io.on('connection', function(socket) {
 console.log('New connection');
 socket.emit('pong', "Hello");

 socket.on('ping', function(data) {
 console.log('Got ping from client');
 socket.emit('pong', "Hello from server, " + data);
 });
 socket.on('pong', function(data) {
 console.log('Got pong from client');
 console.log('pong');
 });
 });*/