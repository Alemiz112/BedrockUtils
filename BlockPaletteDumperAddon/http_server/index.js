const http = require("http");
const fs = require("fs");
const host = 'localhost';
const port = 2001;

const requestListener = function (req, res) {
    console.log(req.method, req.url, req.headers);
    let body = '';
    req.on('data', (chunk) => {
        body += chunk;
    });
    req.on('end', () => {
        console.log(body);
        res.writeHead(200);
        res.end("Ok!");
        fs.writeFile("block_states.json", body, (err) => {
            console.log(err)
        })
    });
};

const server = http.createServer(requestListener);
server.listen(port, host, () => {
    console.log(`Server is running on http://${host}:${port}`);
});