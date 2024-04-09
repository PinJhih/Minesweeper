const express = require("express");
const router = express.Router();
const httpError = require("http-errors");

const records = require("../models/records");
const boards = require("../models/boards");

boards.init();

router.get("/", (req, res) => {
  res.send("OK!");
});

router.get("/records", async (req, res, next) => {
  try {
    let data = await records.getAll();
    res.json(data);
  } catch (e) {
    next(httpError(500, "Cannot get records.\n" + e));
  }
});

router.post("/records/:name/:score", async (req, res, next) => {
  try {
    let userName = req.params.name;
    let score = req.params.score;
    await records.add(userName, score);
    res.send("Succeed!");
  } catch (e) {
    next(httpError(500, "Cannot add records.\n" + e));
  }
});

router.get("/board", async (req, res, next) => {
  try {
    let data = await boards.get();
    res.send(data);
  } catch (e) {
    next(httpError(500, "Cannot get board.\n" + e));
  }
});

router.post("/board", async (req, res, next) => {
  try {
    let board = req.body.board;
    await boards.add(board);
    res.send("Succeed!");
  } catch (e) {
    next(httpError(500, "Cannot add board.\n" + e));
  }
});

router.get("/ranks", async (req, res, next) => {
  try {
    let data = await records.getAll();
    res.render("rank", { records: data });
  } catch (e) {
    next(httpError(500, "Cannot add board.\n" + e));
  }
});

module.exports = router;
