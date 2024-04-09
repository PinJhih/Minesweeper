const sqlite3 = require("sqlite3").verbose();
const fs = require("fs");
const filePath = __dirname + "/../../ntcu-gui.db";

function init() {
  let sql = `CREATE TABLE IF NOT EXISTS record (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_name TEXT,
    score INTEGER
  )`
  query(sql);
  sql = `CREATE TABLE IF NOT EXISTS board (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    content TEXT
  )`
  query(sql);
}

// create file if not exist
fs.access(filePath, fs.constants.F_OK, (err) => {
  if (err) {
    fs.writeFile(filePath, "", (err) => {
      if (err) {
        console.error("Error creating file:", err);
      } else {
        init();
      }
    });
  } else {
    init();
  }
});

function connect() {
  return new Promise((resolve, reject) => {
    const db = new sqlite3.Database(filePath, sqlite3.OPEN_READWRITE, (err) => {
      if (err) {
        console.error(err.message);
        reject(err);
      }
      resolve(db);
    });
  });
}

async function query(sql) {
  const db = await connect();
  return new Promise((resolve, reject) => {
    db.all(sql, [], (err, rows) => {
      if (err) {
        console.error(err.message);
        reject(err);
      }

      db.close((err) => {
        if (err) {
          console.error(err.message);
          reject(err);
        }
      });
      
      resolve(rows);
    });
  });
}

module.exports = {
  query,
};
