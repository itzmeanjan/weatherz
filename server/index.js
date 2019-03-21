const express = require('express');
const app = express();
const path = require('path');

app.get(
    '/cityNames', (req, res) => {
        console.log(`\t[+]Incoming request from ${req.ip} ...`);
        res.download(
            path.join(
                __dirname, 'city.list.json'
            )
        );
    }
)

app.listen(8000, '0.0.0.0', () => {
    console.log("[+]Server listening at 0.0.0.0:8000");
});
