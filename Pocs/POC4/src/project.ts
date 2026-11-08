import { Event, MatchState } from "./types";

export function project(events: Event[]): MatchState {
    const state: MatchState = { robots: {} };
    let previousSequence = -1;

    for (const event of events) {
        if (!Number.isInteger(event.seq) || event.seq <= previousSequence) {
            throw new Error("Les événements doivent avoir des numéros seq croissants.");
        }
        previousSequence = event.seq;

        if (event.type === "MATCH_STARTED") {
            state.robots = {};
            for (const id of event.robots) {
                if (id === "__proto__") {
                    throw new Error("Identifiant de robot réservé : " + id);
                }
                state.robots[id] = { x: 0, y: 0, hp: 100, alive: true };
            }
        } else {
            if (!Object.hasOwn(state.robots, event.robot)) {
                throw new Error("Robot inconnu : " + event.robot);
            }
            const robot = state.robots[event.robot];

            if (event.type === "ROBOT_MOVED") {
                robot.x = event.x;
                robot.y = event.y;
            } else if (event.type === "ROBOT_DAMAGED") {
                robot.hp = event.hp;
                robot.alive = event.hp > 0;
                if (event.alive !== undefined) {
                    robot.alive = event.alive;
                }
            }
        }
    }
    return state;
}
