let db = require("../utils/database");

// TODO: load list of map
var boards = [];

let index = 0;
setInterval(() => {
  index++;
  index %= boards.length;
}, 1000 * 60);

async function init() {
  boards = await db.query("SELECT content FROM board");
  console.log(boards);
}

async function get() {
  return boards[index].content;
}

async function add(content) {
  let sql = `INSERT INTO board (content) VALUES ("${content}")`;
  try {
    await db.query(sql);
    boards.push({ content: content });
    console.log(boards);
  } catch (e) {
    console.error("[Error] Cannot add boards to SQLite\n", e);
  }
}

module.exports = { get, add, init };
