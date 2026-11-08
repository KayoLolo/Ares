import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class Engine {
    private final GameState state;
    private final Gson gson = new GsonBuilder().serializeNulls().create();
    private int sequence = 0;

    public Engine(GameState state) {
        this.state = state;
    }

    public void apply(String line) {
        if (line.isBlank()) {
            return;
        }

        String[] parts = line.trim().split("\\s+");
        String id = parts[0];
        if (parts.length < 2) {
            reject(id, "INVALID_COMMAND");
            return;
        }

        Robot robot = null;
        for (Robot candidate : state.robots) {
            if (candidate.id.equals(id)) {
                robot = candidate;
                break;
            }
        }
        if (robot == null) {
            reject(id, "UNKNOWN_ROBOT");
            return;
        }

        if (robot.hp == 0) {
            reject(id, "ROBOT_DEAD");
            return;
        }

        if (parts[1].equals("MINE")) {
            if (parts.length != 2) {
                reject(id, "INVALID_COMMAND");
                return;
            }
            mine(robot);
            return;
        }

        if (parts[1].equals("WAIT")) {
            if (parts.length != 2) {
                reject(id, "INVALID_COMMAND");
                return;
            }
            waitTurn(robot);
            return;
        }

        if (!parts[1].equals("MOVE") && !parts[1].equals("SHOOT")) {
            reject(id, "UNKNOWN_ACTION");
            return;
        }
        if (parts.length != 3) {
            reject(id, "INVALID_COMMAND");
            return;
        }

        double angle;
        try {
            angle = Double.parseDouble(parts[2]);
        } catch (NumberFormatException exception) {
            reject(id, "INVALID_NUMBER");
            return;
        }
        if (!Double.isFinite(angle)) {
            reject(id, "INVALID_NUMBER");
            return;
        }
        if (parts[1].equals("MOVE")) {
            move(robot, angle);
        } else {
            shoot(robot, angle);
        }
    }

    private void move(Robot robot, double angle) {
        if (robot.energy < 1) {
            reject(robot.id, "NO_ENERGY");
            return;
        }

        double x = robot.x + state.moveSpeed * Math.cos(angle);
        double y = robot.y + state.moveSpeed * Math.sin(angle);
        if (!Double.isFinite(x) || !Double.isFinite(y)
                || x < 0 || x > state.world.w || y < 0 || y > state.world.h) {
            reject(robot.id, "OUT_OF_BOUNDS");
            return;
        }
        if (hasCollision(robot, x, y)) {
            reject(robot.id, "COLLISION");
            return;
        }

        robot.x = x;
        robot.y = y;
        robot.heading = angle;
        robot.energy--;

        JsonObject event = event("ROBOT_MOVED", robot.id);
        event.addProperty("x", robot.x);
        event.addProperty("y", robot.y);
        write(event);
    }

    private boolean hasCollision(Robot robot, double x, double y) {
        double dx = x - robot.x;
        double dy = y - robot.y;
        double length = Math.hypot(dx, dy);

        for (Robot other : state.robots) {
            if (other == robot || other.hp == 0) {
                continue;
            }
            double nearestX = robot.x;
            double nearestY = robot.y;
            if (length > 0) {
                double distance = (other.x - robot.x) * (dx / length)
                        + (other.y - robot.y) * (dy / length);
                distance = Math.max(0, Math.min(length, distance));
                nearestX += distance * (dx / length);
                nearestY += distance * (dy / length);
            }
            if (Math.hypot(other.x - nearestX, other.y - nearestY) < 1) {
                return true;
            }
        }
        return false;
    }

    private JsonObject event(String type, String robot) {
        JsonObject event = new JsonObject();
        event.addProperty("seq", sequence);
        event.addProperty("type", type);
        event.addProperty("robot", robot);
        sequence++;
        return event;
    }

    private void reject(String robot, String reason) {
        JsonObject event = event("ACTION_REJECTED", robot);
        event.addProperty("reason", reason);
        write(event);
    }

    private void write(JsonObject event) {
        System.out.println(gson.toJson(event));
        System.out.flush();
    }

    private void waitTurn(Robot robot) {
        robot.energy = Math.min(20, robot.energy + 2);

        JsonObject event = event("ROBOT_WAITED", robot.id);
        event.addProperty("energy", robot.energy);
        write(event);
    }

    private void mine(Robot robot) {
        for (int i = 0; i < state.crystals.size(); i++) {
            Crystal crystal = state.crystals.get(i);
            double distance = Math.hypot(crystal.x - robot.x, crystal.y - robot.y);
            if (distance <= 1) {
                state.crystals.remove(i);
                robot.energy = Math.min(20, robot.energy + 5);

                JsonObject event = event("CRYSTAL_MINED", robot.id);
                event.addProperty("x", crystal.x);
                event.addProperty("y", crystal.y);
                event.addProperty("energy", robot.energy);
                write(event);
                return;
            }
        }
        reject(robot.id, "NO_CRYSTAL");
    }

    private void shoot(Robot robot, double angle) {
        if (robot.energy < 3) {
            reject(robot.id, "NO_ENERGY");
            return;
        }

        Robot target = findTarget(robot, angle);
        robot.energy -= 3;
        robot.heading = angle;

        JsonObject shot = event("ROBOT_SHOT", robot.id);
        shot.addProperty("angle", angle);
        if (target == null) {
            shot.add("hit", null);
        } else {
            shot.addProperty("hit", target.id);
        }
        write(shot);

        if (target != null) {
            target.hp = Math.max(0, target.hp - 25);
            JsonObject damage = event("ROBOT_DAMAGED", target.id);
            damage.addProperty("hp", target.hp);
            damage.addProperty("alive", target.hp > 0);
            write(damage);
        }
    }

    private Robot findTarget(Robot robot, double angle) {
        double directionX = Math.cos(angle);
        double directionY = Math.sin(angle);
        Robot target = null;
        double nearestImpact = Double.POSITIVE_INFINITY;

        for (Robot other : state.robots) {
            if (other == robot || other.hp == 0) {
                continue;
            }
            double dx = other.x - robot.x;
            double dy = other.y - robot.y;
            double forward = dx * directionX + dy * directionY;
            double sideways = Math.abs(dx * directionY - dy * directionX);

            if (forward <= 0 || sideways > 0.5) {
                continue;
            }
            double impact = forward - Math.sqrt(Math.max(0, 0.25 - sideways * sideways));
            if (impact >= 0 && impact < nearestImpact) {
                target = other;
                nearestImpact = impact;
            }
        }
        return target;
    }
}
