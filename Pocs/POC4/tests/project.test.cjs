const test = require("node:test");
const assert = require("node:assert/strict");
const fs = require("node:fs");
const path = require("node:path");
const { project } = require("../dist/project.js");

test("journal fourni et absence de modification de l'entrée", () => {
    const events = JSON.parse(fs.readFileSync(path.join(__dirname, "../poc4-logs.json"), "utf8"));
    const original = JSON.stringify(events);
    assert.deepEqual(project(events), {
        robots: {
            A1: { x: 14.2, y: 48, hp: 75, alive: true },
            B2: { x: 61.5, y: 33.1, hp: 0, alive: false }
        }
    });
    assert.equal(JSON.stringify(events), original);
});

test("journal vide", () => {
    assert.deepEqual(project([]), { robots: {} });
});

test("état initial et tir sans dégâts implicites", () => {
    assert.deepEqual(project([
        { seq: 0, type: "MATCH_STARTED", robots: ["A", "B"] },
        { seq: 1, type: "ROBOT_SHOT", robot: "A", hit: "B" }
    ]), { robots: {
        A: { x: 0, y: 0, hp: 100, alive: true },
        B: { x: 0, y: 0, hp: 100, alive: true }
    } });
});

test("déplacements successifs et mort sans champ alive", () => {
    const state = project([
        { seq: 0, type: "MATCH_STARTED", robots: ["A"] },
        { seq: 1, type: "ROBOT_MOVED", robot: "A", x: 10, y: 20 },
        { seq: 2, type: "ROBOT_MOVED", robot: "A", x: 30, y: 40 },
        { seq: 3, type: "ROBOT_DAMAGED", robot: "A", hp: 0 }
    ]);
    assert.deepEqual(state.robots.A, { x: 30, y: 40, hp: 0, alive: false });
});

test("refus d'un journal désordonné ou d'un seq dupliqué", () => {
    for (const sequence of [0, -1, 0.5]) {
        assert.throws(() => project([
            { seq: 0, type: "MATCH_STARTED", robots: ["A"] },
            { seq: sequence, type: "ROBOT_MOVED", robot: "A", x: 1, y: 2 }
        ]));
    }
});

test("refus d'un robot inconnu", () => {
    assert.throws(() => project([
        { seq: 0, type: "MATCH_STARTED", robots: ["A"] },
        { seq: 1, type: "ROBOT_MOVED", robot: "B", x: 1, y: 2 }
    ]), /Robot inconnu/);
});

test("chaque projection produit un état indépendant", () => {
    const events = [{ seq: 0, type: "MATCH_STARTED", robots: ["A"] }];
    const first = project(events);
    first.robots.A.hp = 0;
    assert.equal(project(events).robots.A.hp, 100);
});
