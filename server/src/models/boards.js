let db = require("../utils/database");

// TODO: load list of map
let boards = [
  `1 C\n4 B\n6 C\n3 D\n7 H\n6 B\n3 A`,
  `2 A\n7 C\n4 C\n3 E\n2 H\n5 A\n4 C`,
];

let index = 0;
setInterval(() => {
  index++;
  index %= boards.length;
}, 1000 * 60);

async function get() {
  return boards[index];
}

async function add(content) {
  let sql = `INSERT INTO board (content) VALUES ("${content}")`;
  try {
    await db.query(sql);
  } catch (e) {
    console.error("[Error] Cannot add boards to SQLite\n", e);
  }
}

module.exports = { get, add };
