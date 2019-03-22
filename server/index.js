const express = require('express');
const app = express();
const path = require('path');
const fs = require('fs');

app.get(
    '/cityNames', (req, res) => {
        console.log(`\t[+]Incoming request from ${req.ip} ...`);
        fs.readFile(
            path.join(
                __dirname, 'city.list.json'
            ), (err, data) => {
                if(err){
                    res.type('application/json');
                    res.end({'error': err.message});
                }
                else{
                    res.type('application/json');
                    res.json(JSON.parse(data));
                    res.end();
                }
            }
        );
    }
)

app.listen(8000, '0.0.0.0', () => {
    console.log("[+]Server listening at 0.0.0.0:8000");
});
