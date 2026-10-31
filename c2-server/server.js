const http = require('http');
const fs = require('fs');
const path = require('path');

const PORT = 8080;
const LOG_DIR = path.join(__dirname, 'logs');

if (!fs.existsSync(LOG_DIR)) fs.mkdirSync(LOG_DIR);

const server = http.createServer((req, res) => {
    if (req.method === 'POST' && req.url === '/gate') {
        let body = '';
        req.on('data', chunk => body += chunk);
        req.on('end', () => {
            try {
                const data = JSON.parse(body);
                const id = data.id || 'unknown';
                const logFile = path.join(LOG_DIR, `${id}.log`);
                const line = `[${new Date().toISOString()}] ${JSON.stringify(data)}\n`;
                fs.appendFileSync(logFile, line);
                console.log(`Received from ${id}: ${data.type}`);
            } catch (e) {
                console.error('Bad payload', e);
            }
            res.writeHead(200, { 'Content-Type': 'application/json' });
            res.end(JSON.stringify({ status: 'ok' }));
        });
    } else {
        res.writeHead(404);
        res.end();
    }
});

server.listen(PORT, () => {
    console.log(`Lakey C2 listening on http://0.0.0.0:${PORT}`);
});
