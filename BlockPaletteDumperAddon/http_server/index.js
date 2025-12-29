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
        
        let filename;
        if (req.url === '/blocks') {
            filename = 'blocks.json';
        } else if (req.url === '/items') {
            filename = 'items.json';
        } else {
            res.writeHead(404);
            res.end("Not Found");
            return;
        }
        
        fs.writeFile(filename, body, (err) => {
            if (err) {
                console.error(err);
                res.writeHead(500);
                res.end("Error writing file");
            } else {
                console.log(`Saved to ${filename}`);
                res.writeHead(200);
                res.end("OK");
            }
        });
    });
};

const server = http.createServer(requestListener);
server.listen(port, host, () => {
    console.log(`Server is running on http://${host}:${port}`);
});