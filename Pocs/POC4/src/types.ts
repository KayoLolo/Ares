export type Event =
    | { seq: number; type: "MATCH_STARTED"; robots: string[] }
    | { seq: number; type: "ROBOT_MOVED"; robot: string; x: number; y: number }
    | { seq: number; type: "ROBOT_SHOT"; robot: string; hit?: string | null; angle?: number }
    | { seq: number; type: "ROBOT_DAMAGED"; robot: string; hp: number; alive?: boolean };

export interface RobotState {
    x: number;
    y: number;
    hp: number;
    alive: boolean;
}

export interface MatchState {
    robots: { [id: string]: RobotState };
}
